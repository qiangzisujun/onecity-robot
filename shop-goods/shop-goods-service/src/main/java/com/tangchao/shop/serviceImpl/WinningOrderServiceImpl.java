package com.tangchao.shop.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tangchao.common.constant.CommonConstant;
import com.tangchao.common.constant.OrderConstant;
import com.tangchao.common.constant.PayStatusConstant;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.common.utils.RandomUtil;
import com.tangchao.shop.mapper.*;
import com.tangchao.shop.pojo.*;
import com.tangchao.shop.service.ConfService;
import com.tangchao.shop.service.WinningOrderService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

@Service
public class WinningOrderServiceImpl implements WinningOrderService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WinningOrderMapper winningOrderMapper;

    @Resource
    private OrderGoodsMapper orderGoodsMapper;

    @Autowired
    private CustomerInfoMapper customerInfoMapper;

    @Autowired
    private CustomerRechargeRecordMapper customerRechargeRecordMapper;

    @Autowired
    private TradeOrderMapper tradeOrderMapper;

    @Autowired
    private ConfService confService;

    @Autowired
    private FastOrderMapper fastOrderMapper;

    @Override
    public Map<String,Object> prizeWinning(Long userCode, Integer pageNo, Integer pageSize) {
        if (userCode == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        PageHelper.startPage(pageNo, pageSize, true);
        //  设置排序
        PageHelper.orderBy(" open_prize_time DESC,id DESC ");
        WinningOrder winningOrder = new WinningOrder();
        winningOrder.setCustomerCode(userCode.toString());
        List<WinningOrder> orderList = winningOrderMapper.select(winningOrder);

        UserConf conf=confService.selectCmsValue("receivables.function");
        PageInfo<WinningOrder> pageInfo = new PageInfo<>(orderList);
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("data",pageInfo);
        if (conf!=null){
            resultMap.put("isSell",conf.getConfValue());
        }else{
            resultMap.put("isSell",0);
        }
        return resultMap;
    }

    @Override
    public void createOrder(Lottery lottery, GoodsStage goodsStage) {
        //  获取中奖商品
        OrderGoods orderGoods = orderGoodsMapper.selectByPrimaryKey(lottery.getOrderGoodsId());
        if (null != orderGoods){
            WinningOrder order = new WinningOrder();
            //  生成订单编号
            String orderNo = RandomUtil.generateLongByDateTime(3);
            order.setOrderNo(orderNo);
            if(goodsStage.getTypeId() == -1) {//虚拟商品
                Long userCode = lottery.getResultUserCode();
                //  订单状态 -->> 完成
                order.setOrderStatus(OrderConstant.END);
                //  给用户充值福分
                CustomerInfo customerInfo = new CustomerInfo();
                customerInfo.setCustomerCode(userCode);
                List<CustomerInfo> customerList = customerInfoMapper.select(customerInfo);
                customerInfo = customerList.get(0);
                customerInfo.setUserMoney(customerInfo.getUserMoney() + goodsStage.getRecoveryPrice());
                customerInfoMapper.updateByPrimaryKeySelective(customerInfo);
                CustomerRechargeRecord record1 = new CustomerRechargeRecord();
                record1.setCustomerCode(userCode);
                record1.setAmount(goodsStage.getRecoveryPrice());
                record1.setType(1);//'充值消费标识{ 1：充值，2：消费 , 3 :佣金提现 ,4佣金充值}',
                record1.setCreateTime(new Date());
                record1.setCreateId(userCode);
                record1.setPayment(PayStatusConstant.PAY_FROM_VIRTUAL);//'支付方式{1：支付宝，2：微信，3：余额 }',
                //会员余额充值记录
                customerRechargeRecordMapper.insertSelective(record1);
            }else {
                //  订单状态 -->> 待发货
                order.setOrderStatus(OrderConstant.NOT_YET_SHIPPED);
            }
            //  商品相关信息
            order.setGoodsNo(orderGoods.getGoodsNo());                  //  商品编号
            order.setStageId(orderGoods.getStageId());                  //  期次Id
            order.setGoodsStage(orderGoods.getGoodsStage());            //  商品期次
            order.setGoodsPrice(orderGoods.getGoodsPrice());            //  商品单价
            order.setGoodsName(orderGoods.getGoodsName());              //  商品名称
            order.setGoodsImg(orderGoods.getGoodsImg());                //  商品图片
            order.setGoodsFirm(orderGoods.getGoodsFirm());              //  商品厂商
            order.setGoodsSpec(orderGoods.getGoodsSpec());              //  商品规格
            order.setIsAllowSunburn(orderGoods.getIsAllowSunburn());    //  是否允许晒单
            order.setIsShowOrder(CommonConstant.NO);                    //  未晒单
            order.setIsRobot(lottery.getIsRobot());                     //  是否为机器人订单
            //  从交易订单中获取会员信息
            // 只查询一条记录
            PageHelper.startPage(1, 1);
            TradeOrder tradeOrder=new TradeOrder();
            tradeOrder.setOrderNo(orderGoods.getOrderNo());
            List<TradeOrder> list = tradeOrderMapper.select(tradeOrder);
            if (list.size() > 0) {
                tradeOrder=list.get(0);
                order.setCustomerCode(tradeOrder.getPurchaseCode());        //  会员编号
                order.setCustomerName(tradeOrder.getPurchaseName());        //  会员名称
                order.setOrderRemarks(tradeOrder.getOrderRemarks());        //  订单备注
            }

            //  中奖信息
            order.setOpenPrizeResult(lottery.getResultCode());          //  开奖结果
            order.setOpenPrizeTime(lottery.getOpenWinningTime());       //  开奖时间
            String lotteryCode = lottery.getLotteryCode();
            if(StringUtils.isEmpty(lotteryCode)) {
                order.setBuyNum(0L);
            }else {
                Integer buyNum = lotteryCode.split(",").length;
                order.setBuyNum(buyNum.longValue());
            }
            //  保存中奖订单
            try{
                this.winningOrderMapper.insertSelective(order);
            }catch (Exception e){
                this.logger.error("创建中奖订单失败：");
                this.logger.error(e.getMessage());
            }
        }else {
            this.logger.error("创建中奖订单失败,订单商品Id：" + lottery.getOrderGoodsId() + ",不存在");
        }
    }

    @Override
    public String getCheckCode(Long userCode, String winOrderId) {
        if (userCode == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        if (StringUtils.isEmpty(winOrderId)) {
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }

        Example example = new Example(FastOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("flag", 0);
        criteria.andEqualTo("winOrderId", winOrderId);
        List<FastOrder> list = this.fastOrderMapper.selectByExample(example);
        if (list.isEmpty() || list.size() == 0) {
            throw new CustomerException(ExceptionEnum.ORDER_NOT_ERROR);
        }
        if (null==list.get(0).getCheckCode()) {
            throw new CustomerException(ExceptionEnum.ORDER_NOT_ERROR);
        }

        WinningOrder winningOrder=winningOrderMapper.selectByPrimaryKey(winOrderId);
        if (winningOrder==null){
            throw new CustomerException(ExceptionEnum.ORDER_NOT_ERROR);
        }
        return list.get(0).getCheckCode();
    }

    @Override
    public void delivery(Long userCode, String orderNo) {
        if (userCode == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        Example example = new Example(WinningOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderNo",orderNo);
        PageHelper.startPage(1,1);
        List<WinningOrder> list = this.winningOrderMapper.selectByExample(example);
        if (list.size()==0){
            throw new CustomerException(ExceptionEnum.ORDER_NOT_ERROR);
        }
        WinningOrder dbOrder=list.get(0);
        if (!dbOrder.getCustomerCode().equals(userCode)){
            throw new CustomerException(ExceptionEnum.UNAUTHORIZED);
        }
        //  订单状态 --->> 完成订单
        dbOrder.setOrderStatus(OrderConstant.END);
        dbOrder.setTakeGoodsTime(new Date());
        int count=this.winningOrderMapper.updateByPrimaryKeySelective(dbOrder);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }
}
