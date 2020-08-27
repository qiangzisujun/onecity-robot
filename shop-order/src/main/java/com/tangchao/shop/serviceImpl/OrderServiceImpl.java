package com.tangchao.shop.serviceImpl;


import com.tangchao.common.constant.*;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.common.utils.*;
import com.tangchao.shop.interceptor.UserInterceptor;
import com.tangchao.shop.mapper.*;
import com.tangchao.shop.pojo.*;
import com.tangchao.shop.service.ConfService;
import com.tangchao.shop.service.OrderService;
import com.tangchao.shop.service.SynchronizedLockService;
import com.tangchao.shop.service.TradeOrderService;
import com.tangchao.shop.vo.OrderVO;
import com.tangchao.user.service.CustomerScoreDetailService;
import com.tangchao.user.service.CustomerService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;


@Service
public class OrderServiceImpl implements OrderService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ShoppingCartMapper cartMapper;

    @Autowired
    private GoodsStageMapper goodsStageMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Resource
    private OrderGoodsMapper orderGoodsMapper;

    @Autowired
    private CustomerInfoMapper customerInfoMapper;

    @Autowired
    private UserConfMapper userConfMapper;

    @Autowired
    private TradeOrderMapper tradeOrderMapper;

    @Autowired
    private GoodsLockingMapper goodsLockingMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsRcmdMapper goodsRcmdMapper;

    @Autowired
    private GoodsRobotSetMapper goodsRobotSetMapper;

    @Autowired
    private GoodsRobotMapper goodsRobotMapper;

    @Autowired
    private LotteryMapper lotteryMapper;

    @Autowired
    private ConfService confService;

    @Autowired
    private CustomerRechargeRecordMapper rechargeRecordMapper;

    @Autowired
    private OrderDistributionMapper orderDistributionMapper;

    @Autowired
    private SynchronizedLockService synchronizedLockService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private TradeOrderService tradeOrderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized OrderVO createOrder(Long userCode,Map<String,Object> paramsMap, HttpServletRequest request) throws IOException {
        if (userCode == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        List<String> GoodsNoList = (List<String>) paramsMap.get("key");
        if (GoodsNoList.isEmpty()) {
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }

        //用户信息
        Customer record = new Customer();
        record.setUserCode(userCode);
        Customer userInfo = customerMapper.selectOne(record);

        Map<String, Object> params = new HashMap<>();
        params.put("customerCode", userCode);
        params.put("list", GoodsNoList);
        List<ShoppingCart> cartList = cartMapper.getShoppingCartByIds(params);
        int count = 0;
        //  全部商品总额
        double allGoodsTotal = 0;
        double minPayMoney = 0;
        int isActivity = 0;

        //生成订单
        TradeOrder order = new TradeOrder();
        // 生成订单编号
        String orderNo = RandomUtil.generateLongByDateTime(3);
        order.setOrderNo(orderNo);
        order.setPurchaseCode(userInfo.getUserCode().toString()); // 会员唯一标识
        order.setPurchaseName(userInfo.getUserName()); // 用户名
        // 待付款状态
        order.setOrderStatus(OrderConstant.UNPAID);
        order.setIsRobot(userInfo.getIsRobot()); // 是否为机器人订单
        order.setConsumeScore(0L); // 消费积分数量
        order.setScoreDeductionMoney(0.0); // 积分抵扣金额
        order.setDelFlag(0); // 删除标记
        order.setUserDelFlag(0); // 会员删除标记
        Date now = new Date();
        order.setDownOrderTime(now); // 下单时间
        order.setCreateTime(now); // 订单创建时间
        order.setIsAutoBuyNext(0); // 是否自动购买下一期
        order.setUserMobile(userInfo.getUserMobile());
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (ShoppingCart cart : cartList) {
            //  获取商品信息
            GoodsStage goodsStage = new GoodsStage();
            goodsStage.setId(cart.getStageId());
            goodsStage = goodsStageMapper.selectOne(goodsStage);
            // 判断库存
            if (goodsStage.getGoodsInv() < cart.getPayNum()) {
                // 库存不足
                cart.setPayNum(goodsStage.getGoodsInv());// 库存不足时修改采购量为最大库存
            }

            //修改购车数量
            count = cartMapper.updateByPrimaryKeySelective(cart);
            if (count != 1) {
                throw new CustomerException(ExceptionEnum.CREATE_ORDER_ERROR);
            }

            //  创建商品信息
            OrderGoods orderGoods = new OrderGoods();
            orderGoods.setGoodsNo(goodsStage.getGoodsNo());         //  商品编号
            orderGoods.setStageId(goodsStage.getId());              //  期次Id
            orderGoods.setGoodsStage(goodsStage.getStageIndex());   //  商品期次
            orderGoods.setGoodsPrice(goodsStage.getGoodsPrice());   //  商品价格
            orderGoods.setPayPrice(goodsStage.getBuyPrice());       //  购买单价
            orderGoods.setPayNum(cart.getPayNum());                 //  购买数量
            Double payMoney = ArithUtil.mul(goodsStage.getBuyPrice(), cart.getPayNum());//  购买价格
            orderGoods.setGoodsTotal(payMoney);                         //  商品价格合计
            orderGoods.setGoodsName(goodsStage.getGoodsName());         //  商品名称
            orderGoods.setGoodsImg(goodsStage.getGoodsPicture());       //  商品图片
            orderGoods.setGoodsFirm(goodsStage.getGoodsBrand());        //  商品厂商
            orderGoods.setGoodsSpec(goodsStage.getGoodsSpec());         //  商品规格
            orderGoods.setIsAllowSunburn(goodsStage.getIsShowOrder());  //  是否允许晒单
            orderGoods.setOrderNo(order.getOrderNo());
            count = orderGoodsMapper.insertSelective(orderGoods);
            if (count != 1) {
                throw new CustomerException(ExceptionEnum.CREATE_ORDER_ERROR);
            }
            allGoodsTotal = ArithUtil.add(orderGoods.getGoodsTotal(), allGoodsTotal);
            //不中全返商品
            if (CommonConstant.YES == goodsStage.getIsActivity()) {
                isActivity = 1;
                minPayMoney = ArithUtil.add(orderGoods.getGoodsTotal(), minPayMoney);
            }

            Map<String, Object> map = new HashMap<>();
            map.put("goodsStage", goodsStage.getStageIndex());
            map.put("goodsName", goodsStage.getGoodsName());
            map.put("goodsTotal", payMoney);
            mapList.add(map);
        }
        order.setGoodsTotal(allGoodsTotal);     //  商品总额
        order.setMinPayMoney(minPayMoney);      //  商品最小支付金额
        order.setIsActivity(isActivity);
        order.setOrderTotal(order.getGoodsTotal());
        order = this.isUseIntegral(order, CommonConstant.NO);
        if (order.getOrderTotal() == 0) {
            order.setOrderTotal(order.getGoodsTotal());
        }
        // 保存订单
        count = tradeOrderMapper.insertSelective(order);
        if (count != 1) {
            throw new CustomerException(ExceptionEnum.CREATE_ORDER_ERROR);
        }

        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setCustomerCode(Long.valueOf(order.getPurchaseCode()));
        List<CustomerInfo> customerList = customerInfoMapper.select(customerInfo);
        customerInfo = customerList.get(0);


        OrderVO orderVO = new OrderVO();
        orderVO.setGoodsList(JsonUtils.serialize(mapList));
        if (null!=customerInfo.getUserScore()&&null!=customerInfo.getRegisterScore()){
            orderVO.setUserScore(customerInfo.getUserScore() + customerInfo.getRegisterScore());
        }
        orderVO.setUserBalance(customerInfo.getUserMoney());
        orderVO.setOrderNo(order.getOrderNo());
        orderVO.setConsumeScore(order.getConsumeScore().toString());

        //  获取积分抵扣配置
        UserConf newConf = new UserConf();
        newConf.setConfKey(ConfigkeyConstant.MALL_ORDER_SCORE_DEDUCTION);
        newConf = userConfMapper.selectOne(newConf);
        String[] scoreConfArray = newConf.getConfValue().split("/");
        orderVO.setScoreMoney(Double.parseDouble(scoreConfArray[1]));
        orderVO.setScore(Double.parseDouble(scoreConfArray[0]));
        orderVO.setTotalMoney(order.getGoodsTotal());
        orderVO.setIsActivity(order.getIsActivity());
        orderVO.setScoreDeductionMoney(order.getScoreDeductionMoney());
        orderVO.setOrderTotal(order.getOrderTotal());

        //  需要订单金额到多少才能开启积分抵扣金额
        newConf.setConfKey(ConfigkeyConstant.MALL_USER_SCORE_NEED);
        newConf = userConfMapper.selectOne(newConf);
        if (newConf != null && !StringUtils.isEmpty(newConf.getConfValue())) {
            orderVO.setNeedMoney(Double.parseDouble(newConf.getConfValue()));
        } else {
            orderVO.setNeedMoney(0.0);
        }
        return orderVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized Double commitOrder(Integer isUse,Long orderNo, Integer isAutoBuyNext, HttpServletRequest request) throws Exception {


        int count = 0;
        UserInfo user = UserInterceptor.getUserInfo();
        if (user == null && user.getUserCode() == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        if (orderNo == null || isAutoBuyNext == null) {
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }

        //获取用户信息
        Customer record = new Customer();
        record.setUserCode(user.getUserCode());
        List<Customer> userList = customerMapper.select(record);
        Customer customer = userList.get(0);

        // 获取订单信息
        TradeOrder orderInfo = new TradeOrder();
        orderInfo.setOrderNo(orderNo.toString());
        orderInfo = tradeOrderMapper.selectOne(orderInfo);
        if (orderInfo == null) {
            throw new CustomerException(ExceptionEnum.ORDER_NOT_ERROR);
        }

        if (orderInfo.getOrderStatus() == OrderConstant.UNPAID) {
            //执行付款逻辑
            orderInfo.setPayIp(IPAddressUtil.getClientIpAddress(request));
            orderInfo.setIsAutoBuyNext(isAutoBuyNext); // 是否自动购买

            // 判断会员余额
            CustomerInfo customerInfo = new CustomerInfo();
            customerInfo.setCustomerCode(user.getUserCode());
            List<CustomerInfo> customerList = customerInfoMapper.select(customerInfo);
            customerInfo = customerList.get(0);

            //是否使用福分抵扣,后续添加的功能
            if(isUse==1){
                // 获取用户的可用积分
                double score = customerInfo.getUserScore();
                // 计算积分抵扣金额，与消费积分
                UserConf conf = this.confService.selectCmsValue(ConfigkeyConstant.MALL_ORDER_SCORE_DEDUCTION);
                if (null != conf) {
                    String[] scoreConfArray = conf.getConfValue().split("/");
                    long value = (long) (Double.parseDouble(scoreConfArray[0]) / Double.parseDouble(scoreConfArray[1]));
                    // 最多可抵扣金额 100
                    long result = (long) score / value;
                    // 订单最多可抵扣金额
                    if(orderInfo.getMinPayMoney() == null) {
                        orderInfo.setMinPayMoney(0.0);
                    }
                    Double maxMoney = ArithUtil.sub(orderInfo.getOrderTotal(), orderInfo.getMinPayMoney());
                    if (result > maxMoney) {
                        result = result - (result - maxMoney.longValue());
                    }
                    // 设置抵扣金额
                    orderInfo.setScoreDeductionMoney((double) result);
                    // 设置需要消费积分
                    orderInfo.setConsumeScore(result * value);
                    count=tradeOrderMapper.updateByPrimaryKeySelective(orderInfo);
                    if (count!=1){
                        throw new CustomerException(ExceptionEnum.ORDER_PAYMENT_ERROR);
                    }
                }
            }

            if (customerInfo.getUserMoney() + orderInfo.getScoreDeductionMoney() < orderInfo.getGoodsTotal()) {
                // 余额不足
                throw new CustomerException(ExceptionEnum.USER_BALANCE_INSUFFICIENT);
            }

            OrderGoods goods = new OrderGoods();
            goods.setOrderNo(orderInfo.getOrderNo());
            List<OrderGoods> orderGoodsList = orderGoodsMapper.select(goods);

            for (OrderGoods orderGoods : orderGoodsList) {
                int payNum = orderGoods.getPayNum();
                if (payNum <= 0)
                    continue;
                orderGoods.setUserCode(customerInfo.getCustomerCode());
                //商品库存同步锁
                this.synchronizedLockService.locking(customer, orderGoods.getStageId(), Long.valueOf(orderGoods.getOrderNo()), Long.valueOf(orderGoods.getGoodsNo()), payNum, 0);
            }

            count=tradeOrderService.robotOrCustomerPayGoods(orderGoodsList,0,null);
            if (count<=0){
                throw new CustomerException(ExceptionEnum.ORDER_PAYMENT_ERROR);
            }

            // 赠送积分
            double score = 0.00;
            //保存消费积分明细 积分抵扣金额大于等于订单金额保存一条记录，积分抵扣金额小于订单金额保存两条记录，收入和支出
            Double orderTotal=orderInfo.getOrderTotal();//支付总金额
            if (null != orderInfo.getConsumeScore() && orderInfo.getConsumeScore() > 0) {
                // TODO 抵扣金额小于订单金额
                //保存一条消费积分明细
                CustomerScoreDetail detail = new CustomerScoreDetail();
                detail.setDataSrc(DataSrcConstant.DEDUCTING);
                detail.setScoreFlag(PayStatusConstant.EXPENDITURE);
                detail.setScore(orderInfo.getConsumeScore().doubleValue()); // 积分
                detail.setCustomerCode(customerInfo.getCustomerCode()); // 会员编码
                detail.setOrderCode(orderInfo.getOrderNo()); // 订单编号
                detail.setScoreDescribe("积分抵扣"); // 描述
                //用户扣减积分
                score= ArithUtil.sub(score, orderInfo.getConsumeScore());
                orderTotal=orderInfo.getOrderTotal()-orderInfo.getScoreDeductionMoney();
                orderInfo.setOrderScore(0.00);
            }else{
                //保存一条消费积分明细
                score=this.payAvailableScore(orderInfo.getOrderTotal());
                CustomerScoreDetail detail = new CustomerScoreDetail();
                detail.setDataSrc(DataSrcConstant.PAY);
                detail.setScoreFlag(PayStatusConstant.INCOME);
                detail.setScore(score); // 积分
                detail.setCustomerCode(customerInfo.getCustomerCode()); // 会员编码
                detail.setOrderCode(orderInfo.getOrderNo()); // 订单编号
                detail.setScoreDescribe("消费赠送积分"); // 描述
                orderInfo.setOrderScore(score);
            }
            // 保存会员信息
            System.out.println("=================:5：修改用户信息");
            //订单总价格======修改用户金额和积分
            CustomerInfo updateCustomer=new CustomerInfo();
            updateCustomer.setUserFlow(0.0);
            updateCustomer.setCustomerCode(customer.getUserCode());
            updateCustomer.setUserMoney(customerInfo.getUserMoney()-orderTotal);

            if (null!=customerInfo.getUserScore()){
                updateCustomer.setUserScore(score+customerInfo.getUserScore());
            }else{
                updateCustomer.setUserScore(score);
            }

            count=customerService.updateCustomerInfo(updateCustomer);
            if (count!=1) {
                throw new CustomerException(ExceptionEnum.ORDER_PAYMENT_ERROR);
            }

            // 生成消费明细
            CustomerRechargeRecord newRecord = new CustomerRechargeRecord();
            newRecord.setCustomerCode(Long.valueOf(orderInfo.getPurchaseCode())); // 会员编号
            newRecord.setAmount(orderTotal); // 消费金额
            newRecord.setIntegral(score); // 赠送积分
            newRecord.setRechargeCode(orderInfo.getOrderNo()); // 订单编号
            newRecord.setType(2); // 消费标记
            newRecord.setCreateTime(new Date()); // 创建时间
            newRecord.setPayment(3); // 支付方式
            newRecord.setDeductionsScore(orderInfo.getConsumeScore().doubleValue());//消费积分
            System.out.println("=================:6：开始生成用户消费明细");
            count= rechargeRecordMapper.insert(newRecord);
            if (count!=1) {
                throw new CustomerException(ExceptionEnum.ORDER_PAYMENT_ERROR);
            }

            // 保存订单
            orderInfo.setOrderTotal(orderTotal);
            orderInfo.setPayTime(new Date()); // 支付时间
            orderInfo.setOrderStatus(OrderConstant.ALREADY_PAID);
            orderInfo.setPayFrom(PayStatusConstant.PAY_FROM_MONEY);
            orderInfo.setGoodsTotal(orderTotal);
            orderInfo.setPayIp(IPAddressUtil.getClientIpAddress(request));
            System.out.println("==================7：开始修改订单状态为已支付");
            count = tradeOrderMapper.updateByPrimaryKeySelective(orderInfo);
            if (count != 1) {
                throw new CustomerException(ExceptionEnum.ORDER_PAYMENT_ERROR);
            }
            return orderInfo.getOrderTotal();
        } else if (orderInfo.getOrderStatus() == OrderConstant.TIME_OUT) {
            //订单超时
            throw new CustomerException(ExceptionEnum.ORDER_TIMEOUT);

        } else if (orderInfo.getOrderStatus() == OrderConstant.USER_CANCEL) {

            throw new CustomerException(ExceptionEnum.ORDER_CANCEL);//用户已取消

        } else if (orderInfo.getOrderStatus() == OrderConstant.ALREADY_PAID) {
            //用户已付款
            throw new CustomerException(ExceptionEnum.ORDER_PAYMENT);
        } else {
            throw new CustomerException(ExceptionEnum.ORDER_NOT_ERROR);
        }
    }

    /**
     * 使用积分抵扣
     *
     * @param order 订单信息
     * @param isUse 1：使用，2：不使用
     * @return TradeOrder
     */
    private TradeOrder isUseIntegral(TradeOrder order, Integer isUse) {
        // 判断是否使用积分抵扣
        if (isUse == CommonConstant.YES && order.getScoreDeductionMoney() <= 0) {
            CustomerInfo userInfo = new CustomerInfo();
            userInfo.setCustomerCode(Long.valueOf(order.getPurchaseCode()));
            List<CustomerInfo> customerList = customerInfoMapper.select(userInfo);
            userInfo = customerList.get(0);

            double score = userInfo.getUserScore();
            // 计算积分抵扣金额，与消费积分

            UserConf confByKey = new UserConf();
            confByKey.setConfKey(ConfigkeyConstant.MALL_ORDER_SCORE_DEDUCTION);
            confByKey = userConfMapper.selectOne(confByKey);
            if (null != confByKey) {
                String[] scoreConfArray = confByKey.getConfValue().split("/");
                long value = (long) (Double.parseDouble(scoreConfArray[0]) / Double.parseDouble(scoreConfArray[1]));
                // 最多可抵扣金额 100
                long result = (long) score / value;
                // 订单最多可抵扣金额
                if (order.getMinPayMoney() == null) {
                    order.setMinPayMoney(0.0);
                }
                Double maxMoney = ArithUtil.sub(order.getOrderTotal(), order.getMinPayMoney());
                if (result > maxMoney) {
                    result = result - (result - maxMoney.longValue());
                }
                // 设置抵扣金额
                order.setScoreDeductionMoney((double) result);
                // 设置需要消费积分
                order.setConsumeScore(result * value);
            }
        } else if (isUse == CommonConstant.NO && order.getScoreDeductionMoney() > 0) {
            // 撤销抵扣价格
            double orderTotal = order.getOrderTotal();
            order.setConsumeScore(0L); // 撤回消费积分
            order.setOrderTotal(orderTotal);
            order.setScoreDeductionMoney(0.0);
        }
        return order;
    }



    /**
     * 计算支付订单赠送金额
     * @param money
     * @return
     */
    public double payAvailableScore(double money) {
        UserConf conf = new UserConf();
        conf.setConfKey(ConfigkeyConstant.MALL_USER_PAY_GIVE_SCORE);
        conf.setFlag(0);
        List<UserConf> newConf = userConfMapper.select(conf);
        conf = newConf.get(0);
        try {
            String[] array = conf.getConfValue().split("/");
            if (array.length != 2) {
                throw new Exception("配置格式：X/Y,X为消费最低金额,Y为赠送福分数量");
            }
            double where = Double.parseDouble(array[0]);
            double score = Double.parseDouble(array[1]);
            if (money >= where) {
                // 计算可获得积分数量
                long x = (long) (money / where);
                return ArithUtil.mul(x, score);
            }
        } catch (Exception e) {
            this.logger.error("配置错误：每消费X元可获赠Y数量福分");
            this.logger.error(e.getMessage());
        }
        return 0;
    }
}

