package com.tangchao.shop.serviceImpl;


import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tangchao.common.constant.CommonConstant;
import com.tangchao.common.constant.OrderConstant;
import com.tangchao.common.constant.PayStatusConstant;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.common.utils.ArithUtil;
import com.tangchao.common.utils.ExportExcelUtil;
import com.tangchao.common.utils.RandomUtil;
import com.tangchao.common.utils.StringUtil;
import com.tangchao.shop.dto.OrderGoodsDTO;
import com.tangchao.shop.dto.TradeOrderDTO;
import com.tangchao.shop.dto.adminDTO.OrderDTO;
import com.tangchao.shop.mapper.*;
import com.tangchao.shop.pojo.*;
import com.tangchao.shop.service.ShoppingCartService;
import com.tangchao.shop.service.TradeOrderService;
import com.tangchao.shop.vo.adminVo.TradeOrderVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TradeOrderServiceImpl implements TradeOrderService {

    @Autowired
    private TradeOrderMapper tradeOrderMapper;

    @Autowired
    private OrderGoodsMapper orderGoodsMapper;

    @Autowired
    private LotteryMapper lotteryMapper;

    @Autowired
    private GoodsStageMapper goodsStageMapper;

    @Autowired
    private FastOrderMapper fastOrderMapper;

    @Autowired
    private WinningOrderMapper winningOrderMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private LogisticsCompanyMapper logisticsCompanyMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private ExpressFormMapper expressFormMapper;

    @Autowired
    private CustomerRechargeRecordMapper customerRechargeRecordMapper;

    @Autowired
    private ShopOrderMapper shopOrderMapper;

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Autowired
    private GoodsLockingMapper goodsLockingMapper;

    @Autowired
    private GoodsRobotMapper goodsRobotMapper;

    @Autowired
    private ShoppingCartService cartService;

    @Autowired
    private GoodsRcmdMapper goodsRcmdMapper;

    @Autowired
    private GoodsRobotSetMapper goodsRobotSetMapper;

    public Map OrderManagement() {
        HashMap map = new HashMap();
        //map.put("Today",tradeOrderMapper.selectToday());  //今日订单
        //map.put("Yesterday",tradeOrderMapper.selectYesterday());    //昨日订单
        map.put("UnDeliverGoodsNum",tradeOrderMapper.selectUnDeliverGoodsNum()); //未收货订单
        map.put("AgentOrderToday",tradeOrderMapper.selectAgentOrderToday()); //今日代理订单
        map.put("AgentOrderYesterday",tradeOrderMapper.selectAgentOrderYesterday()); //昨日代理订单
        map.put("UnCheckAgentOrderNum",tradeOrderMapper.selectUnCheckAgentOrderNum()); //未销订单
        map.put("CustomerToday",tradeOrderMapper.selectCustomerToday()); //今日新增
        map.put("CustomerYesterday",tradeOrderMapper.selectCustomerYesterday()); //昨日新增
        map.put("TodayScoreIncome",tradeOrderMapper.selectTodayScoreIncome()); //今日积分消耗
        map.put("TodayScoreExpenditure",tradeOrderMapper.selectTodayScoreExpenditure()); //今日积分赠送
        map.put("TodayAmountRecharge",tradeOrderMapper.selectTodayAmountRecharge()); //今日充值
        map.put("TodayAmountConsumption",tradeOrderMapper.selectTodayAmountConsumption()); //今日消费
        map.put("TodayShowNum",tradeOrderMapper.selectTodayShowNum()); //今日晒单
        map.put("TodayCommentNum",tradeOrderMapper.selectTodayCommentNum()); //今日评论
        Example example = new Example(ShopOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", 2);
        criteria.andEqualTo("datalevel", 1);
        Integer shopOrderTotal = shopOrderMapper.selectCountByExample(example);
        map.put("shopOrderTotal", shopOrderTotal); //未发货商品总订单

        Example example1 = new Example(ShopOrder.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andEqualTo("datalevel", 1);
        criteria1.andLessThanOrEqualTo("createTime", DateUtil.endOfDay(new Date()));
        criteria1.andGreaterThanOrEqualTo("createTime",  DateUtil.beginOfDay(new Date()));
        Integer couponTotal = userCouponMapper.selectCountByExample(example1);

        Double callChargeTotal=customerRechargeRecordMapper.sumCustomerRechargeByGoodsType();
        map.put("couponTotal", couponTotal); //当天优惠券购买数
        map.put("callChargeTotal", callChargeTotal); //话费充值总金额
        return map;
    }

    @Override
    public Map<String, Object> selectOrderTradeList(Long userId, OrderDTO orderDTO) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        // 查询订单列表
        if (!StringUtils.isBlank(orderDTO.getUserMobile())){
            Customer customer=new Customer();
            customer.setUserMobile(orderDTO.getUserMobile());
            List<Customer> user=customerMapper.select(customer);
            if (!user.isEmpty()){
                orderDTO.setCustomerCode(user.get(0).getUserCode().toString());
            }
        }
        // 设置分页
        PageHelper.startPage(orderDTO.getPageNo(), orderDTO.getPageSize(),false);
        List<TradeOrderVO> tradeOrderList=tradeOrderMapper.selectOrderList(orderDTO);

        List<String> userCodeList=null;
        if (!tradeOrderList.isEmpty()){
            userCodeList=tradeOrderList.stream().map(t->t.getPurchaseCode()).collect(Collectors.toList());
        }


        List<Customer> customerList=null;
        if (userCodeList!=null){
            customerList=customerMapper.getCustomerListByCode(userCodeList);
        }
        if (customerList!=null){
            Map<String,String> map=new HashMap<>();
            for (Customer param:customerList){
                map.put(param.getUserCode().toString(),param.getUserMobile());
            }

            tradeOrderList.forEach(s -> s.setUserMobile(map.get(s.getPurchaseCode())));
        }

        List<Long> customerCodeList=tradeOrderList.stream().map(t->Long.parseLong(t.getPurchaseCode())).collect(Collectors.toList());

        // 查询消费金额
        List<Map<String,Object>>  payAmountSum=null;
        if (!customerCodeList.isEmpty()){
            payAmountSum=customerRechargeRecordMapper.sumCustomerRechargeRecordTotal(customerCodeList);
        }

        if (payAmountSum!=null){
            Map<String,Double>  amountSumMap=new HashMap<>();
            for (Map<String, Object> map : payAmountSum){
                amountSumMap.put(map.get("customerCode").toString(),Double.valueOf(map.get("amount").toString()));
            }
            tradeOrderList.forEach(s -> s.setPayAmountSum(amountSumMap.get(s.getPurchaseCode())));
        }


        // 查询中奖金额
        List<Map<String,Object>>  winningTotal=null;
        if (!customerCodeList.isEmpty()){
            winningTotal=winningOrderMapper.sumWinningPriceTotalByUserCode(customerCodeList);
        }

        if (winningTotal!=null){
            Map<String,Double>  winningOrderMap=new HashMap<>();
            for (Map<String, Object> map : winningTotal){
                winningOrderMap.put(map.get("customerCode").toString(),Double.valueOf(map.get("winningTotal").toString()));
            }
            tradeOrderList.forEach(s -> s.setWinningTotal(winningOrderMap.get(s.getPurchaseCode())));
        }else{
            tradeOrderList.forEach(s -> s.setWinningTotal(0.0));
        }


        int totalRecord=tradeOrderMapper.getOrderSum(orderDTO);

        //获取总页数
        PageInfo pageInfo=new PageInfo();
        pageInfo.setList(tradeOrderList);
        pageInfo.setTotal(totalRecord);
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("data",pageInfo);
        return resultMap;
    }

    @Override
    public TradeOrder getOrderDetailInfo(Long userId, String orderNo) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        if (StringUtils.isBlank(orderNo)){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        TradeOrder order=new TradeOrder();
        order.setOrderNo(orderNo);
        order.setDelFlag(0);
        order=tradeOrderMapper.selectOne(order);

        if (StringUtils.isBlank(orderNo)){
            throw new CustomerException(ExceptionEnum.ORDER_NOT_ERROR);
        }

        Customer customer=new Customer();
        customer.setFlag(0);
        customer.setUserCode(Long.valueOf(order.getPurchaseCode()));
        customer=customerMapper.selectOne(customer);
        order.setPhone(customer.getUserMobile());


        List<String> orderNoList=new ArrayList<>();
        orderNoList.add(order.getOrderNo());
        List<OrderGoods> orderLotteryList=orderGoodsMapper.selectOrderGoodsByOrderNo(orderNoList);

        for (OrderGoods goods:orderLotteryList){
            if (goods.getIsAward()==0&&order.getOrderStatus()==0&&!order.getPurchaseCode().equals(goods.getCustCode())&&!StringUtils.isBlank(order.getPhone())){
                goods.setIsDesignation(1);
            }else if(goods.getIsAward()==0&&order.getOrderStatus()==0&&order.getPurchaseCode().equals(goods.getCustCode())){
                goods.setIsDesignation(2);
            }
        }

        order.setOrderGoodsList(orderLotteryList);

        List<Long> OrderGoodIds=null;
        if (orderLotteryList!=null){
            OrderGoodIds=orderLotteryList.stream().map(OrderGoods::getId).collect(Collectors.toList());
        }

        List<Lottery>  lotteries=null;
        if (OrderGoodIds!=null&&OrderGoodIds.size()>0){
            lotteries=lotteryMapper.getLotteryListByOrderGoodIds(OrderGoodIds);
        }

        if (lotteries!=null){
            Map<Long,List<Lottery>> lotteryMap=new HashMap<>();
            for (Lottery lottery:lotteries){
                if (!lotteryMap.containsKey(lottery.getOrderGoodsId())){
                    lotteryMap.put(lottery.getOrderGoodsId(),new ArrayList<>());
                }
                lotteryMap.get(lottery.getOrderGoodsId()).add(lottery);
            }
            orderLotteryList.forEach(s -> s.setLotteryList(lotteryMap.get(s.getId())));
        }
        return order;
    }

    @Override
    public void setLuckyUser(Long userId, String stageId,String userCode) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        //查找该期，查询开奖状态
        GoodsStage stage= new GoodsStage();
        stage.setId(Long.valueOf(stageId));
        stage.setFlag(0);
        stage=goodsStageMapper.selectOne(stage);
        if (stage==null){
            throw new CustomerException(ExceptionEnum.GOODS_NOT_FOND);
        }
        if (stage.getIsAward()==1){
            throw new CustomerException(ExceptionEnum.GOODS_IS_LOTTERY);
        }
        stage.setCustCode(userCode);
        // 设置修改人Id
        stage.setLastModifyId(userId);
        int count=goodsStageMapper.updateGoodsStageByCustCode(stage);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void closeLuckyUser(Long userId, String stageId, String userCode) {

        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        //查找该期，查询开奖状态
        GoodsStage stage= new GoodsStage();
        stage.setId(Long.valueOf(stageId));
        stage.setFlag(0);
        stage=goodsStageMapper.selectOne(stage);
        if (stage==null){
            throw new CustomerException(ExceptionEnum.GOODS_NOT_FOND);
        }
        if (stage.getIsAward()==1){
            throw new CustomerException(ExceptionEnum.GOODS_IS_LOTTERY);
        }
        stage.setCustCode("");
        // 设置修改人Id
        stage.setLastModifyId(userId);
        int count=goodsStageMapper.updateByPrimaryKeySelective(stage);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public Map<String, Object> getFastOrderList(Long userId, OrderDTO orderDTO) {

        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        PageHelper.startPage(orderDTO.getPageNo(), orderDTO.getPageSize(),false);
        List<Map<String, Object>> fastList = fastOrderMapper.selectFastOrderList(orderDTO);

        int totalRecord=fastOrderMapper.getFastOrderSum(orderDTO);


        PageInfo pageInfo=new PageInfo();
        pageInfo.setList(fastList);
        pageInfo.setTotal(totalRecord);
        Map<String,Object> resultMap=new HashMap<>();
        resultMap=fastOrderMapper.selectPriceTotal(orderDTO);
        if (resultMap==null){
            resultMap=new HashMap<>();
        }
        resultMap.put("data",pageInfo);
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoCompleteCode(Long userId, Map<String, Object> data) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        List<String> list = (List<String>) data.get("data");
        if (list.isEmpty()){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }

        for (String str:list){
            FastOrder fastOrder = fastOrderMapper.selectByPrimaryKey(str);
            fastOrder.setStatusFlag(1);
            fastOrder.setCheckTime(new Date());
            int count=fastOrderMapper.updateByPrimaryKey(fastOrder);
            if (count!=1){
                throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
            }
            Long winOrderId = fastOrder.getWinOrderId();
            WinningOrder winningOrder = winningOrderMapper.selectByPrimaryKey(winOrderId);
            winningOrder.setOrderStatus(6);
            count=winningOrderMapper.updateByPrimaryKey(winningOrder);
            if (count!=1){
                throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
            }
        }
    }

    @Override
    public Map<String, Object> getWinningOrderList(Long userId, OrderDTO orderDTO) {


        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        //手机号转用户标识
        if (!StringUtils.isBlank(orderDTO.getUserMobile())){
            Customer customer=new Customer();
            customer.setUserMobile(orderDTO.getUserMobile());
            customer=customerMapper.selectOne(customer);
            if (customer!=null){
                orderDTO.setCustomerCode(customer.getUserCode().toString());
            }
        }

        PageHelper.startPage(orderDTO.getPageNo(), orderDTO.getPageSize(),false);
        List<WinningOrder>  WinningOrderList=winningOrderMapper.selectWinningOrderList(orderDTO);
        WinningOrderList=WinningOrderList.stream().sorted(Comparator.comparing(WinningOrder::getOpenPrizeTime).reversed()).collect(Collectors.toList());

        int totalNum=winningOrderMapper.countWinningOrderList(orderDTO);

        PageInfo pageInfo=new PageInfo();
        pageInfo.setList(WinningOrderList);
        pageInfo.setTotal(totalNum);
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("data",pageInfo);
        return resultMap;
    }

    @Override
    public Map<String, Object> countOrderTradeList(Long userId, OrderDTO orderDTO) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        //手机号转用户标识
        if (!StringUtils.isBlank(orderDTO.getUserMobile())){
            Customer customer=new Customer();
            customer.setUserMobile(orderDTO.getUserMobile());
            customer=customerMapper.selectOne(customer);
            if (customer!=null){
                orderDTO.setCustomerCode(customer.getUserCode().toString());
            }
        }
        return tradeOrderMapper.selectCountOrderTotal(orderDTO);
    }

    @Override
    public Map<String, Object> countOrderList(Long userId, OrderDTO orderDTO) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        if (!StringUtils.isBlank(orderDTO.getUserMobile())){
            Customer customer=new Customer();
            customer.setUserMobile(orderDTO.getUserMobile());
            customer=customerMapper.selectOne(customer);
            if (customer!=null){
                orderDTO.setCustomerCode(customer.getUserCode().toString());
            }
        }
        return winningOrderMapper.getWinningPriceTotal(orderDTO);
    }

    @Override
    public Map<String, Object> WinningOrderInfo(Long userId, String orderOrder) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        WinningOrder winningOrder=new WinningOrder();
        winningOrder.setOrderNo(orderOrder);
        winningOrder=winningOrderMapper.selectOne(winningOrder);
        if (winningOrder==null){
            throw new CustomerException(ExceptionEnum.ORDER_NOT_ERROR);
        }
        LogisticsCompany logisticsCompany=new LogisticsCompany();
        logisticsCompany.setFlag(0);
        List<LogisticsCompany> logisticsCompanyList =logisticsCompanyMapper.select(logisticsCompany);

        Goods goods=new Goods();
        goods.setGoodsNo(winningOrder.getGoodsNo().toString());
        goods=goodsMapper.selectOne(goods);
        winningOrder.setRecoveryPrice(goods.getRecoveryPrice());
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("data",winningOrder);
        resultMap.put("logisticsCompanyList",logisticsCompanyList);
        return resultMap;
    }

    @Override
    public void updateWinningOrderInfo(Long userId, Map<String, Object> data) {

        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        String orderNo=data.get("orderNo").toString();
        String expressCompany=data.get("expressCompany").toString();
        String expressNo=data.get("expressNo").toString();
        double expressCost=Double.valueOf(data.get("expressCost").toString());


        if (StringUtils.isBlank(orderNo)||StringUtils.isBlank(expressCompany)||StringUtils.isBlank(expressNo)){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }

        //  根据订单编号获取订单
        WinningOrder winningOrder=new WinningOrder();
        winningOrder.setOrderNo(orderNo);
        winningOrder=winningOrderMapper.selectOne(winningOrder);
        if (null == winningOrder){
            //  订单不存在
            throw new CustomerException(ExceptionEnum.ORDER_NOT_ERROR);
        }
        //  设置订单快递公司,及编号,运费
        winningOrder.setExpressCompany(expressCompany);
        winningOrder.setExpressNo(expressNo);
        winningOrder.setExpressCost(expressCost);
        //  发货人,发货时间
        winningOrder.setDeliveryId(userId);
        /*if(winningOrder.getOrderStatus()==1){

        }*/
        winningOrder.setDeliveryTime(new Date());
        //  修改订单状态 --->> 已发货
        winningOrder.setOrderStatus(OrderConstant.ALREADY_SHIPPED);
        int count=winningOrderMapper.updateByPrimaryKeySelective(winningOrder);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public ExpressForm orderShipTemplate(Long userId) {

        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        List<ExpressForm> formList = this.expressFormMapper.selectAll();
        ExpressForm form;
        if (formList.size() > 0){
            form = formList.get(0);
        }else {
            // 初始化数据
            form = new ExpressForm(0);
            this.expressFormMapper.insertSelective(form);
        }
        return form;
    }

    @Override
    public void updateExpressFormTemplate(Long userId, ExpressForm expressForm) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        if (expressForm==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        int count=expressFormMapper.updateByPrimaryKeySelective(expressForm);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void updateOverTimeOrderStatus(Long overTime) {
        if (overTime!=null){
            int count=tradeOrderMapper.updateOrderStatusByOverTime(overTime, OrderConstant.UNPAID, OrderConstant.TIME_OUT);
        }
    }

    @Override
    public void exportWinningOrder(Long userId,String goodsNo,String orderNo,String userName,String userMobile,String createStartTime,String createEndTime,Integer status,HttpServletResponse response) throws UnsupportedEncodingException {
        OrderDTO orderDTO=new OrderDTO();
        orderDTO.setUserMobile(userMobile);
        orderDTO.setGoodsNo(goodsNo);
        orderDTO.setCreateEndTime(createEndTime);
        orderDTO.setCreateEndTime(createStartTime);
        orderDTO.setStatus(status);
        orderDTO.setUserName(userName);
        orderDTO.setOrderNo(orderNo);
        if (!StringUtils.isBlank(orderDTO.getUserMobile())){
            Customer customer=new Customer();
            customer.setUserMobile(orderDTO.getUserMobile());
            customer=customerMapper.selectOne(customer);
            if (customer!=null){
                orderDTO.setCustomerCode(customer.getUserCode().toString());
            }
        }
        String fileName ="中奖订单记录";
        List<Map<String, Object>> list = winningOrderMapper.findWinningOrderByPojo(orderDTO);
        String[] keys = { "order_no", "goods_name", "goods_price", "open_prize_time", "open_prize_result", "customer_name","customer_phone","user_name","user_mobile","user_address"};
        Integer[] widths = { 20 };
        String[] headers = { "订单编号", "商品名称","商品价格","开奖时间", "中奖号码", "中奖会员","会员电话","收件人","收件人手机","收件人地址"};
        ExportExcelUtil.exportExcel(fileName, headers, keys, widths, list, response, null);
    }

    @Override
    public Map<String, Object> winningOrderByGoodsNo(Integer pageNo, Integer pageSize, String goodsNo) {

        PageHelper.startPage(pageNo, pageSize,false);
        List<Map<String, Object>>  WinningOrderList = winningOrderMapper.selectWinningOrderByGoodsNo(goodsNo);

        Integer total = winningOrderMapper.countWinningOrderByGoodsNo(goodsNo);

        PageInfo pageInfo=new PageInfo();
        pageInfo.setList(WinningOrderList);
        pageInfo.setTotal(total);
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("data",pageInfo);
        return resultMap;
    }

    @Override
    public int robotPayGoods(TradeOrderDTO tradeOrder, GoodsRobot goodsRobot) throws Exception {
        int count=0;
        TradeOrder order = new TradeOrder();
        // 生成订单编号
        String orderNo = RandomUtil.generateLongByDateTime(3);
        order.setOrderNo(orderNo);
        order.setPurchaseCode(tradeOrder.getCustomerCode().toString()); // 会员唯一标识
        order.setPurchaseName(tradeOrder.getUserName()); // 用户名
        // 待付款状态
        order.setOrderStatus(OrderConstant.UNPAID);
        order.setIsRobot(1); // 是否为机器人订单
        order.setConsumeScore(0L); // 消费积分数量
        order.setScoreDeductionMoney(0.0); // 积分抵扣金额
        order.setDelFlag(0); // 删除标记
        order.setUserDelFlag(0); // 会员删除标记
        Date now = new Date();
        order.setDownOrderTime(now); // 下单时间
        order.setCreateTime(now); // 订单创建时间
        order.setUserMobile(tradeOrder.getUserMobile()); // 收件人手机
        order.setOrderRemarks(tradeOrder.getOrderRemarks()); // 订单备注
        order.setIsAutoBuyNext(0); // 是否自动购买下一期

        // 创建订单商品
        //  获取期次商品信息
        GoodsStage goodsStage=new GoodsStage();
        goodsStage.setId(tradeOrder.getStageId());
        List<GoodsStage> goodsStageList = goodsStageMapper.select(goodsStage);
        if (goodsStageList.isEmpty()){
            log.error("商品不存在=="+tradeOrder.getStageId());
            return 0;
            //throw new Exception("商品不存在");
        }
        goodsStage=goodsStageList.get(0);
        //  判断库存
        if (goodsStage.getGoodsInv() < tradeOrder.getPayNum()){
            log.error("商品库存不足"+goodsStage.getGoodsName());
            return 0;
            //throw new Exception("商品不存在");
        }
        //  创建商品信息
        OrderGoods orderGoods = new OrderGoods();
        if (null != goodsStage) {
            orderGoods.setGoodsNo(goodsStage.getGoodsNo());         //  商品编号
            orderGoods.setStageId(goodsStage.getId());              //  期次Id
            orderGoods.setGoodsStage(goodsStage.getStageIndex());   //  商品期次
            orderGoods.setGoodsPrice(goodsStage.getGoodsPrice());   //  商品价格
            orderGoods.setPayPrice(goodsStage.getBuyPrice());       //  购买单价
            orderGoods.setPayNum(tradeOrder.getPayNum());                           //  购买数量
            //  购买价格
            Double payMoney = ArithUtil.mul(goodsStage.getBuyPrice(),tradeOrder.getPayNum());
            orderGoods.setGoodsTotal(payMoney);                         //  商品价格合计
            orderGoods.setGoodsName(goodsStage.getGoodsName());         //  商品名称
            orderGoods.setGoodsImg(goodsStage.getGoodsPicture());       //  商品图片
            orderGoods.setIsAllowSunburn(goodsStage.getIsShowOrder());  //  是否允许晒单
            orderGoods.setOrderNo(order.getOrderNo());

            //  保存订单商品信息
            count=orderGoodsMapper.insertSelective(orderGoods);
            if (count!=1){
                log.error("订单保存失败1"+orderGoods.getGoodsNo());
            }
            Double goodsTotal = orderGoods.getGoodsTotal();
            order.setGoodsTotal(goodsTotal);     //  商品总额
            order.setOrderTotal(goodsTotal);
            if(goodsStage.getIsActivity() == CommonConstant.YES) {//不中全返商品
                order.setMinPayMoney(goodsTotal);
            }

            count=tradeOrderMapper.insertSelective(order);
            if (count!=1){
                log.error("订单保存失败2"+orderGoods.getGoodsNo());
            }
        }
        orderGoods.setUserCode(tradeOrder.getCustomerCode());

        List<OrderGoods> orderGoodsList=new ArrayList<>();
        orderGoodsList.add(orderGoods);

        //========
        count=this.robotOrCustomerPayGoods(orderGoodsList,1,goodsRobot);
        //======================================
        // 修改订单状态
        Integer nextInt = new Random().nextInt(30000);//随机毫秒数(0~29.99...s)
        order.setPayTime(new Date(System.currentTimeMillis() - nextInt)); // 支付时间
        order.setOrderStatus(OrderConstant.ALREADY_PAID);
        order.setPayFrom(PayStatusConstant.PAY_FROM_MONEY);
        order.setPayIp(tradeOrder.getPayIp());
        count=tradeOrderMapper.updateByPrimaryKeySelective(order);
        if (count!=1){
            log.error("订单保存失败7"+orderGoods.getGoodsNo());
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int robotOrCustomerPayGoods(List<OrderGoods> orderGoodsList, Integer IsRobot, GoodsRobot goodsRobot) throws Exception {
        int count=0;
        //期数id集合
        List<Long> stageIds=orderGoodsList.stream().map(OrderGoods::getStageId).collect(Collectors.toList());

        //查询商品期数
        Example example=new Example(GoodsStage.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andIn("id",stageIds);
        List<GoodsStage> goodsStageList=goodsStageMapper.selectByExample(example);

        Map<Long,GoodsStage> stageMap=goodsStageList.stream().collect(Collectors.toMap(s->s.getId(),t->t));

        List<ShoppingCart> cartList=new ArrayList<>();
        for (OrderGoods orderGoods : orderGoodsList) {
            int payNum = orderGoods.getPayNum();
            if (payNum <= 0)
                continue;

            // 获取期次商品信息
            GoodsStage goodsStage =goodsStageMapper.selectByPrimaryKey(orderGoods.getStageId());

            // 判断是否已开奖
            if (null != goodsStage.getIsAward() && goodsStage.getIsAward().equals(1)) {
                //已开奖
                throw new Exception("商品" + goodsStage.getGoodsName() + "已开奖");
            }

            Integer goodsLockingNum = goodsLockingMapper.findGoodsLockingCount(orderGoods.getStageId());//查询锁定数量
            if (goodsLockingNum == null) goodsLockingNum = 0;

            // 判断库存
            int stock = goodsStage.getGoodsInv() - payNum;
            if (stock < 0) {
                // 模拟库存不足
                throw new Exception("商品" + goodsStage.getGoodsName() + "库存不足");
            }
            // 扣减库存
            goodsStage.setGoodsInv(stock);
            // 当前购买数
            Integer buyIndex;
            if (null != goodsStage.getBuyIndex()) {
                buyIndex = goodsStage.getBuyIndex();
            } else {
                buyIndex = 0;
            }
            goodsStage.setBuyIndex(buyIndex + payNum);
            goodsStage.setJackPotNow(goodsStage.getJackPotNow() + payNum);
            if (stock == 0) {
                goodsStage.setFullTime(new Date()); // 满团时间
                goodsStage.setIsAward(2); // 开奖中
                //修改商品期数
                count = goodsStageMapper.updateByPrimaryKeySelective(goodsStage);
                count=this.createGoodsStage(goodsStage);
            } else {
                count = goodsStageMapper.updateByPrimaryKeySelective(goodsStage);
                //修改机器人
                if (goodsRobot!=null){
                    count=goodsRobotMapper.updateByPrimaryKeySelective(goodsRobot);
                }
            }
            // 7、解除商品库存锁定
            count = goodsLockingMapper.updateOneGoodsLocking(orderGoods.getStageId(), orderGoods.getUserCode());//解除商品库存锁定
            if (count <=0) {
                log.error("解除商品库存锁定失败!="+orderGoods.getUserCode()+"期数="+orderGoods.getStageId());
                throw new CustomerException(ExceptionEnum.ORDER_PAYMENT_ERROR);
            }
            // 生成幸运号码
            orderGoods.setBuyIndex(buyIndex);
            orderGoods.setPayIp(orderGoods.getPayIp());
            orderGoods.setBuySize(goodsStage.getBuySize());
            OrderGoodsDTO orderGoodsDTO=new OrderGoodsDTO();
            BeanUtils.copyProperties(orderGoods, orderGoodsDTO);
            orderGoodsDTO.setCustCode(orderGoods.getUserCode().toString());
            orderGoodsDTO.setIsRobot(IsRobot);

            //生成幸运号
            this.createLotteryByOrderGoods(orderGoodsDTO);

            //清空购物车 ====集合删除购物车或者通知方式
            if (IsRobot.equals(0)){
                ShoppingCart cart=new ShoppingCart();
                cart.setCustomerCode(orderGoods.getUserCode());
                cart.setGoodsNo(goodsStage.getGoodsNo());
                cartList.add(cart);
            }

        }

        if (IsRobot.equals(0)){
            //清空购物车
            cartService.deleteShopCartByList(cartList);
        }
        return count;
    }

    @Override
    public void proxyCompleteCode(Long userId, String checkCode, Long id) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        FastOrder fastOrder =new  FastOrder();
        fastOrder.setCheckCode(checkCode);
        fastOrder.setId(id);
        fastOrder=fastOrderMapper.selectOne(fastOrder);
        fastOrder.setStatusFlag(1);
        fastOrder.setCheckTime(new Date());
        int count=fastOrderMapper.updateByPrimaryKey(fastOrder);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
        Long winOrderId = fastOrder.getWinOrderId();
        WinningOrder winningOrder = winningOrderMapper.selectByPrimaryKey(winOrderId);
        winningOrder.setOrderStatus(6);
        count=winningOrderMapper.updateByPrimaryKey(winningOrder);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public int createGoodsStage(GoodsStage oldStage) {
        if(ObjectUtils.isEmpty(oldStage)){
            return 0;
        }
        Long goodsId=oldStage.getGoodsId();
        GoodsStage stage = new GoodsStage();
        stage.setIsAward(0);
        stage.setFlag(0);
        stage.setGoodsId(goodsId);
        List<GoodsStage> stageList = goodsStageMapper.select(stage);// 如果查询报错则是有多条正在进行开奖的商品
        if (null!=stageList&&stageList.size()>0) {
            // 已存在正在开奖的商品
            return 0;
        }
        int count=0;
        Goods goods = goodsMapper.selectByPrimaryKey(goodsId);// 查询该商品最新信息
        if (goods == null || goods.getFlag() != 0) {
            Date now = new Date();
            goods.setId(goodsId);// 设置id
            goods.setIsSell(0);// 设置在售状态
            goods.setLastModifyTime(now);// 设置修改时间
            goods.setIsRcmd(0);// 非推荐商品
            goods.setSellEndTime(now);
            goodsMapper.updateByPrimaryKeySelective(goods);
            goodsRcmdMapper.deleteRcmdByGoodsId(goodsId);
        }
        // 判断商品是否上架
        if (goods.getIsSell() == 0) {
            log.info("商品未上架");
            return 1;
            //throw new CustomerException(ExceptionEnum.SELL_IS_DOWN);
        }
        // 判断库存是否为0
        if (goods.getGoodsInv() <= 0) {
            Date now = new Date();
            goods.setId(goodsId);// 设置id
            goods.setIsSell(0);// 设置在售状态
            goods.setLastModifyTime(now);// 设置修改时间
            goods.setIsRcmd(0);// 非推荐商品
            goods.setSellEndTime(now);
            goodsMapper.updateByPrimaryKeySelective(goods);
            goodsRcmdMapper.deleteRcmdByGoodsId(goodsId);
            log.info("库存为0");
            return 0;
            //throw new CustomerException(ExceptionEnum.INV_IS_ZERO);
        }
        // 查询商品最大期数
        Integer index = goodsStageMapper.selectMaxIndexByGoodsId(goodsId);
        if (index == null) {// 无最大期数则是第一期
            index = 1;
        } else {
            index++;
        }
        // 判断是否过期
        if (goods.getSellStage() != null && index > goods.getSellStage()) {
            Date now = new Date();
            goods.setId(goodsId);// 设置id
            goods.setIsSell(0);// 设置在售状态
            goods.setLastModifyTime(now);// 设置修改时间
            goods.setIsRcmd(0);// 非推荐商品
            goods.setSellEndTime(now);
            goodsMapper.updateByPrimaryKeySelective(goods);
            goodsRcmdMapper.deleteRcmdByGoodsId(goodsId);
            log.info("是否过期");
            return 0;
        }
        stage = new GoodsStage();// 创建新的商品期数信息
        Example example = new Example(GoodsRcmd.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("flag", 0);
        criteria.andEqualTo("goodsId", goodsId);
        List<GoodsRcmd> rcmdList = this.goodsRcmdMapper.selectByExample(example);// 商品热度
        if (!rcmdList.isEmpty()) {
            stage.setGoodsHot(rcmdList.get(0).getGoodsHot());
        }

        // 创建商品期次模板
        stage = this.createGoodsStageTemplate(goods, stage);
        stage.setStageIndex(index); // 商品期次
        stage.setBuyIndex(0);
        stage.setIsActivity(goods.getIsActivity()); // 活动商品
        if (CommonConstant.YES == goods.getIsActivity()) {// 活动商品
            stage.setBuySize(goods.getActivityBuyNum());
            stage.setBuyNum(goods.getActivityBuyNum());
        } else {
            stage.setBuySize((int) Math.round(goods.getGoodsPrice() / goods.getBuyPrice()));// 购买总数
            if (stage.getBuyNum() <= 0 || stage.getBuyNum() > stage.getBuySize()) {// 每人限购数不在购买总数范围内，则为不限制购买
                stage.setBuyNum(stage.getBuySize());
            }
        }
        stage.setGoodsInv(stage.getBuySize());// 当前购买的商品库存
        stage.setSellStartTime(goods.getSellStartTime());// 设置上架时间
        stage.setRecoveryPrice(goods.getRecoveryPrice());// 设置商品回收价
        if (oldStage != null) {
            stage.setJackPotAll(oldStage.getJackPotAll());
            stage.setJackPotType(oldStage.getJackPotType());
            //检测上期奖池值是否足够，如果未足够则流入当前期
            if (oldStage.getJackPotNow() < oldStage.getJackPotAll()) {
                stage.setJackPotNow(oldStage.getJackPotNow());
            }
        }
        if (this.goodsStageMapper.insertSelective(stage) > 0) {// 创建商品期数
            Goods _goods = new Goods();
            _goods.setId(goodsId);
            _goods.setGoodsInv(goods.getGoodsInv() - 1);
            this.goodsMapper.updateByPrimaryKeySelective(_goods);// 更新商品库存

            if (CommonConstant.NO == goods.getIsActivity()) {
                GoodsRobotSet goodsRobotSet = new GoodsRobotSet();
                goodsRobotSet.setGoodsId(goodsId);
                goodsRobotSet.setStatus(1);
                List<GoodsRobotSet> goodsRobotSetList = goodsRobotSetMapper.select(goodsRobotSet);
                if (null!=goodsRobotSetList&&goodsRobotSetList.size()>0){
                    goodsRobotSet = goodsRobotSetList.get(0);
                    if (goodsRobotSet != null&& goodsRobotSet.getPeriodsNumber() > goodsRobotSet.getBuyingPeriodsNumber()) {// 如果产品有机器人任务设置，重新生成下一期的机器人任务。
                        GoodsRobot robot = new GoodsRobot();
                        robot.setGoodsId(goodsId);
                        List<GoodsRobot> robotList = goodsRobotMapper.select(robot);
                        robot = robotList.get(0);
                        boolean update = true;
                        if (robot == null) {
                            robot = new GoodsRobot();
                            update = false;
                        }
                        robot.setSetId(goodsRobotSet.getId());
                        robot.setGoodsId(stage.getGoodsId());
                        robot.setGoodsPeriodId(stage.getId());
                        Integer max_m = goodsRobotSet.getMaxPurchasesMinute();
                        Integer min_m = goodsRobotSet.getMinPurchasesMinute();
                        if (max_m == null || min_m == null || max_m <= 0 || min_m > max_m) {// 不符合条件的数值
                            robot.setBuyRateMinute(new Random().nextInt(30));// 30分钟的随机购买时间
                        } else {
                            robot.setBuyRateMinute(new Random().nextInt(max_m - min_m + 1) + min_m);
                        }
                        robot.setGoodsLimitCount(stage.getBuyNum());
                        robot.setMaxCount((int) (goodsRobotSet.getPercentage() * stage.getBuySize()));
                        robot.setBoughtCount(0);
                        robot.setMaxPurchasesConut(goodsRobotSet.getMaxPurchasesCount());
                        robot.setMinPurchasesConut(goodsRobotSet.getMinPurchasesCount());
                        robot.setMaxPurchasesMinute(goodsRobotSet.getMaxPurchasesMinute());
                        robot.setMinPurchasesMinute(goodsRobotSet.getMinPurchasesMinute());
                        if (update) {
                            count=this.goodsRobotMapper.updateByPrimaryKeySelective(robot);// 更新机器人购买任务数据
                        } else {
                            count=this.goodsRobotMapper.insertSelective(robot);// 创建机器人购买任务数据
                        }
                        goodsRobotSet.setBuyingPeriodsNumber(goodsRobotSet.getBuyingPeriodsNumber() + 1);
                        this.goodsRobotSetMapper.updateByPrimaryKeySelective(goodsRobotSet);
                    } else {//已经跑完，重置购买数量
                        GoodsRobot robot = new GoodsRobot();
                        robot.setGoodsId(goodsId);
                        List<GoodsRobot> robotList = goodsRobotMapper.select(robot);
                        if (null!=robotList&&robotList.size()>0) {
                            robot = robotList.get(0);
                            robot.setBoughtCount(0);
                            this.goodsRobotMapper.updateByPrimaryKeySelective(robot);// 更新机器人购买任务数据
                        }
                    }
                }else {//已经跑完，重置购买数量
                    GoodsRobot robot = new GoodsRobot();
                    robot.setGoodsId(goodsId);
                    List<GoodsRobot> robotList = goodsRobotMapper.select(robot);
                    if (null!=robotList&&robotList.size()>0) {
                        robot = robotList.get(0);
                        robot.setBoughtCount(0);
                        this.goodsRobotMapper.updateByPrimaryKeySelective(robot);// 更新机器人购买任务数据
                    }
                }
            }
        }
        return 1;
    }

    private GoodsStage createGoodsStageTemplate(Goods goods, GoodsStage stage) {
        // 创建时间,修改时间
        Date now = new Date();
        stage.setCreateTime(now);
        stage.setLastModifyTime(now);
        // 商品Id
        stage.setGoodsId(goods.getId());
        // 商品类型Id
        stage.setTypeId(goods.getTypeId());
        // 商品编号
        stage.setGoodsNo(goods.getGoodsNo());
        // 商品名称
        stage.setGoodsName(goods.getGoodsName());
        // 商品价格
        stage.setGoodsPrice(goods.getGoodsPrice());
        // 商品图片
        stage.setGoodsPicture(goods.getGoodsPicture());
        // 商品详情图
        stage.setGoodsInfoPicture(goods.getGoodsInfoPicture());
        // 商品编码
        stage.setGoodsCode(goods.getGoodsCode());
        // 是否允许晒单
        stage.setIsShowOrder(goods.getIsShowOrder());
        // 每人限购次数
        stage.setBuyNum(goods.getBuyNum());
        // 每次购买价格
        stage.setBuyPrice(goods.getBuyPrice());
        // 删除标记
        stage.setFlag(0);
        // 是否已开奖
        stage.setIsAward(0);
        return stage;
    }

    @Transactional(rollbackFor = Exception.class)
    public int createLotteryByOrderGoods(OrderGoodsDTO orderGoods) {
        System.out.println("开始生成幸运号"+orderGoods.getPayNum());
        Lottery lottery = new Lottery();
        lottery.setOrderGoodsId(orderGoods.getId());        //  订单商品Id
        lottery.setGoodsNo(orderGoods.getGoodsNo());        //  商品唯一编码
        lottery.setStageId(orderGoods.getStageId());        //  期次Id
        lottery.setGoodsStage(orderGoods.getGoodsStage());  //  商品期次
        lottery.setCustomerCode(Long.valueOf(orderGoods.getCustCode()));    //  会员编号
        lottery.setIsRobot(orderGoods.getIsRobot());          //  是否为机器人
        lottery.setIsWinning(0);                            //  未中奖
        lottery.setCreateTime(new Date());                  //  创建时间
        lottery.setPayIp(orderGoods.getPayIp());            //  支付Ip

        //  需要过滤的幸运号码
        List<Integer> excludedList = new ArrayList<>();
        //  获取当前期次的所有幸运号码
        List<String> list = lotteryMapper.selectLotteryCodeByStageId(orderGoods.getStageId());
        StringBuilder stringBuilder = new StringBuilder();
        for (String item : list) {
            stringBuilder.append(item);
        }
        List<String> itemList = StringUtil.splitString(stringBuilder.toString(), StringUtil.COMMA);
        if (!(itemList.size() == 1 && StringUtils.isEmpty(itemList.get(0)))) {
            for (String num : itemList) {
                excludedList.add(Integer.parseInt(num.trim()));
            }
        }
        StringBuilder sb = new StringBuilder();

        //  从该列表中抽取幸运号码
        int maxSize = orderGoods.getBuySize();
        List<Integer> luckyNumberList = new ArrayList<>(maxSize);
        maxSize = CommonConstant.OPEN_WINNING_SALT + maxSize;
        for (int i = CommonConstant.OPEN_WINNING_SALT; i < maxSize; i++) {
            luckyNumberList.add(i);
        }
        //  过滤
//        luckyNumberList.removeAll(excludedList);//此句消耗大量性能，需替换移除方法
        luckyNumberList = StringUtil.removeAll(luckyNumberList, excludedList);


        for (int i = 0; i < orderGoods.getPayNum(); i++) {
            //  生成幸运号码
            String lotteryCode = StringUtil.createLuckyNumber(luckyNumberList);
            sb.append(lotteryCode).append(",");
        }
        System.out.println("幸运号生成数量"+orderGoods.getPayNum()+"==="+sb.length());
        lottery.setLotteryCode(sb.toString());
        //  保存
        this.lotteryMapper.insertSelective(lottery);
        return 1;
    }
}
