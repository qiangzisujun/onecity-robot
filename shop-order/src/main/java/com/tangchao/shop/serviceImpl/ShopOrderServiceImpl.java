package com.tangchao.shop.serviceImpl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tangchao.common.constant.ConfigkeyConstant;
import com.tangchao.common.constant.PayStatusConstant;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.common.utils.*;
import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.dto.CouponLogDTO;
import com.tangchao.shop.dto.ShopOrderDTO;
import com.tangchao.shop.interceptor.UserInterceptor;
import com.tangchao.shop.mapper.*;
import com.tangchao.shop.params.*;
import com.tangchao.shop.pojo.*;
import com.tangchao.shop.service.ShopOrderService;
import com.tangchao.shop.utils.*;
import com.tangchao.shop.vo.OrderResponse;
import com.tangchao.user.service.CmsConfigService;
import com.tangchao.user.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Encoder;
import tk.mybatis.mapper.entity.Example;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ShopOrderServiceImpl implements ShopOrderService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private ShopOrderMapper shopOrderMapper;

    @Autowired
    private ShopOrderDetailMapper detailMapper;

    @Autowired
    private ShopGoodsMapper goodsMapper;

    @Autowired
    private PayHelper payHelper;

    @Autowired
    private PayConfig config;

    @Autowired
    private ShopSpecParamMapper paramMapper;

    @Autowired
    private ShopUserDetailMapper userDetailMapper;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerInfoMapper customerInfoMapper;

    @Autowired
    private UserConfMapper userConfMapper;

    @Autowired
    private OrderDistributionMapper orderDistributionMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private ShopCartMapper shopCartMapper;

    @Autowired
    private CustomerAddressMapper addressMapper;

    @Autowired
    private CustomerScoreDetailMapper customerScoreDetailMapper;

    @Autowired
    private WxPayService wxService;

    @Autowired
    private CmsConfigService configService;

    @Autowired
    private CustomerRechargeRecordMapper customerRechargeRecordMapper;

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Autowired
    private ShopSpecificationMapper shopSpecificationMapper;

    @Autowired
    private ShopReturnOrderMapper shopReturnOrderMapper;

    @Autowired
    private CustomerPurchasesMapper customerPurchasesMapper;

    @Autowired
    private PaymentChannelMapper paymentChannelMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized OrderResponse createOrder(ShopOrderParam shopOrderParam, HttpServletRequest request) {
        //获取用户登录
        UserInfo user = UserInterceptor.getUserInfo();
        if (user == null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        //获取用户选择的收货地址相关信息
        if (null==shopOrderParam&&null==shopOrderParam.getAddressId()){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        CustomerAddress customerAddress = addressMapper.selectByPrimaryKey(shopOrderParam.getAddressId());
        String zipCode = customerAddress.getZipCode();
        String userName = customerAddress.getUserName();
        String userMobile = customerAddress.getUserMobile();
        StringBuilder address = new StringBuilder();
        address.append(customerAddress.getProvince() != null ? customerAddress.getProvince() : "" )
                .append(customerAddress.getCity() != null ? customerAddress.getCity() : "" )
                .append(customerAddress.getArea() != null ? customerAddress.getArea() : "" )
                .append(customerAddress.getStreet() != null ? customerAddress.getStreet() : "" )
                .append(customerAddress.getDetailed() != null ? customerAddress.getDetailed() : "" );
        //获取用户购物车的信息
        String orderId = String.valueOf(idWorker.nextId());
        Example exampleCart = new Example(ShopCart.class);
        Example.Criteria criteria = exampleCart.createCriteria();
        criteria.andEqualTo("userCode", user.getUserCode());
        criteria.andEqualTo("status", 1);
        List<ShopCart> shopCarts = shopCartMapper.selectByExample(exampleCart);
        Long totalPrice = 0l;
        Long totalIntegral = 0l;
        for ( ShopCart shopCart : shopCarts) {
            ShopOrderDetail shopOrderDetail = new ShopOrderDetail();
            shopOrderDetail.setOrderId(orderId);
            //生成订单详情数据
            shopOrderDetail.setGoodsId(shopCart.getGoodsId());
            shopOrderDetail.setNum(shopCart.getNumber());
            shopOrderDetail.setTitle(shopCart.getGoodsName());
            shopOrderDetail.setPrice(shopCart.getPrice());
            shopOrderDetail.setIntegral(shopCart.getIntegral());
            shopOrderDetail.setImage(shopCart.getImage());
            detailMapper.insertSelective(shopOrderDetail);
            //清楚购物车的商品
            shopCart.setStatus(0);
            shopCartMapper.updateByPrimaryKeySelective(shopCart);
            totalPrice += (shopCart.getPrice() * shopCart.getNumber());
            totalIntegral += (shopCart.getIntegral() * shopCart.getNumber());
        }

        // TODO: 2020.2.18 抵消优惠券码
        String couponCode = shopOrderParam.getCouponCode();
        if (couponCode != null) {
            Example exampleUserCoupon = new Example(UserCoupon.class);
            Example.Criteria criteriaUserCoupon = exampleUserCoupon.createCriteria();
            criteriaUserCoupon.andEqualTo("couponCode", couponCode);
            criteriaUserCoupon.andEqualTo("couponStatus", 0);
            criteriaUserCoupon.andEqualTo("datalevel", 1);
            criteriaUserCoupon.andLessThan("efectiveTime", new Date());
            UserCoupon userCoupon = userCouponMapper.selectOneByExample(criteriaUserCoupon);
            if (userCoupon == null) {
                throw new CustomerException(ExceptionEnum.CREATE_ORDER_ERROR);
            }
            BigDecimal multiply = userCoupon.getCouponAmount().multiply(new BigDecimal("100"));
            totalPrice -= multiply.longValue();
        }

        //生成订单
        ShopOrder order = new ShopOrder();
        order.setOrderId(orderId);
        order.setOrderNo(String.valueOf(idWorker.nextId()));
        order.setUserCode(user.getUserCode());
        order.setBuyerMessage(shopOrderParam.getBuyerMessage());
        order.setBuyerNick(user.getName());
        order.setReceiverAddress(address.toString());
        order.setUserName(userName);
        order.setUserMobile(userMobile);
        order.setZipCode(zipCode);
        // TODO: 2020.2.18 抵消优惠券
        order.setTotalPay(totalPrice);
        order.setTotalIntegral(totalIntegral);
        Integer count = shopOrderMapper.insertSelective(order);
        if (count != 1) throw new CustomerException(ExceptionEnum.CREATE_ORDER_ERROR);

        // UnifiedOrderResponse unifiedOrderResponse = new UnifiedOrderResponse();
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderNo(order.getOrderNo());
        orderResponse.setOrderId(order.getOrderId());
        orderResponse.setTotal(order.getTotalPay());
        return orderResponse;
    }

    @Override
    @Transactional
    public ResponseEntity payNotify(String orderId, String resultCode, String money, String type, String sign, String timeEnd,String outTradeNo) throws Exception {
        Boolean aBoolean = WXPayUtil.checkSign(sign, money, orderId);
        if (!aBoolean && !resultCode.equals("SUCCESS")) {
            log.error( orderId + "订单号回调处理失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("回调失败");
        }
        log.warn("回调处理，订单号：" + orderId);
        ShopOrder shopOrder = shopOrderMapper.selectByPrimaryKey(orderId);
        if (shopOrder == null && shopOrder.getStatus() != 1) {
            log.error("没有该订单号或者该订单号已经支付");
            throw new Exception("没有该订单号或者该订单号已经支付");
        }

        // TODO: 2020/1/17 给购物的商品添加销量
        Example example = new Example(ShopOrderDetail.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", shopOrder.getOrderId());
        List<ShopOrderDetail> shopOrderDetails = detailMapper.selectByExample(example);

        //商品分销提点
        Double remindSpec=0.0;
        Integer goodsType=0;
        for ( ShopOrderDetail shopOrderDetail : shopOrderDetails ) {
            Long goodsId = shopOrderDetail.getGoodsId();
            ShopGoods shopGoods = goodsMapper.selectByPrimaryKey(goodsId);
            Integer salesVolume = shopGoods.getSalesVolume();
            Integer total = salesVolume + shopOrderDetail.getNum();
            shopGoods.setSalesVolume(total);
            goodsMapper.updateByPrimaryKeySelective(shopGoods);
            //只有一条记录时可以使用
            remindSpec=shopGoods.getCommission().doubleValue();
            if(shopGoods.getGoodsTypeId().equals(1)){//购买特定商品充值20元话费
                customerService.mobileRecharge(shopOrder.getUserCode(),20);
                //查询上级给上级赠送抽奖机会
                Customer customer =new Customer();
                customer.setUserCode(shopOrder.getUserCode());
                customer.setFlag(0);
                customer=customerMapper.selectOne(customer);

                CustomerInfo superior =new CustomerInfo();
                superior.setInviteCode(customer.getInviteId());
                superior=customerInfoMapper.selectOne(superior);
                if (superior!=null){
                    int rowCount = customerInfoMapper.addAmount(superior.getCustomerCode(), 2.0, 0.00, 1L);
                    if (rowCount > 0) {
                        CustomerRechargeRecord customerRechargeRecord = new CustomerRechargeRecord();
                        customerRechargeRecord.setAmount(2.0);
                        customerRechargeRecord.setCustomerCode(superior.getCustomerCode());
                        customerRechargeRecord.setIntegral(0.00);
                        customerRechargeRecord.setType(1);//'充值消费标识{ 1：充值，2：消费 , 3 :佣金提现 ,4佣金充值}',
                        customerRechargeRecord.setPayment(PayStatusConstant.PAY_FROM_WECHAT);//微信
                        customerRechargeRecord.setCreateTime(new Date());
                        customerRechargeRecord.setGoodsTypeId(goodsType);
                        int rowResult = this.customerRechargeRecordMapper.insertSelective(customerRechargeRecord);
                        if (rowResult== 1) {
                            logger.info("会员余额充值记录");
                        }
                    }
                }
            }
            goodsType=shopGoods.getGoodsTypeId();
        }

        // TODO: 2020/4/2 虚拟订单的订单支付完成后直接修改订单状态为 已发货未确认状态
        if (shopOrder.getOrderType() == 1) {
            shopOrder.setStatus(3);
        } else {
            shopOrder.setStatus(2);
        }
        if(goodsType.equals(1)){
            shopOrder.setStatus(4);
        }
        shopOrder.setPaymentType(1);
        BigDecimal actualPay = new BigDecimal(money);
        BigDecimal multiply = actualPay.multiply(new BigDecimal("100"));
        shopOrder.setActualPay(multiply.longValue());
        Date date = DateUtil.parse(timeEnd, "yyyyMMddHHmmss");
        shopOrder.setPaymentTime(date);
        shopOrder.setPlatformOrderNo(outTradeNo);
        Integer count = shopOrderMapper.updateByPrimaryKeySelective(shopOrder);
        if (count != 1) {
            log.error("修改订单状态失败");
            throw new Exception("修改订单状态失败");
        }

        // TODO: 2020/3/12 给规格商品减库存
        if (shopOrder.getSpecId()>0){
            ShopSpecification shopSpecification = shopSpecificationMapper.selectByPrimaryKey(shopOrder.getSpecId());
            Integer stock = shopSpecification.getStock();
            shopSpecification.setStock(stock - 1);
            shopSpecificationMapper.updateByPrimaryKey(shopSpecification);
        }

        //  给用户充值福分
        CustomerInfo payCustomer = new CustomerInfo();
        payCustomer.setCustomerCode(shopOrder.getUserCode());
        payCustomer=customerInfoMapper.selectOne(payCustomer);
        UserConf rechargeConf = configService.selectCmsValue(ConfigkeyConstant.MALL_USER_RECHARGE_GIVE);
        double userMoney=shopOrder.getTotalIntegral();
        if (rechargeConf != null && !StringUtils.isEmpty(rechargeConf.getConfValue())) {
            String[] array1 = rechargeConf.getConfValue().split("/");
            if (array1.length == 2) {
                double amountX = Double.parseDouble(array1[0]);
                double amountY = Double.parseDouble(array1[1]);
                if (userMoney >= amountX) {// 判断开关 金额是否已达到可以送金额的数量
                    long x = (long) (userMoney / amountX);
                    Double userAmount = ArithUtil.mul(x, amountY);
                    userMoney = userMoney + userAmount;
                }
            } else {
                logger.error("配置格式：X/Y,X为充值金额,Y为赠送金额");
            }

        }
        int rowCount = customerInfoMapper.addAmount(shopOrder.getUserCode(), userMoney, 0.00, 1L);
        if (rowCount > 0) {
            CustomerRechargeRecord customerRechargeRecord = new CustomerRechargeRecord();
            customerRechargeRecord.setAmount(userMoney);
            customerRechargeRecord.setCustomerCode(payCustomer.getCustomerCode());
            customerRechargeRecord.setIntegral(0.00);
            customerRechargeRecord.setType(1);//'充值消费标识{ 1：充值，2：消费 , 3 :佣金提现 ,4佣金充值}',
            customerRechargeRecord.setPayment(PayStatusConstant.PAY_FROM_WECHAT);//微信
            customerRechargeRecord.setCreateTime(new Date());
            customerRechargeRecord.setGoodsTypeId(goodsType);
            int rowResult = this.customerRechargeRecordMapper.insertSelective(customerRechargeRecord);
            if (rowResult== 1) {
                logger.info("会员余额充值记录");
            }
        }

        Customer customer=new Customer();
        customer.setUserCode(shopOrder.getUserCode());
        customer=customerMapper.selectOne(customer);
        if (!goodsType.equals(1)){
            log.info("==================================三级分佣开始===========");
            this.threeLevelDistribution(shopOrder, customer.getInviteId(),remindSpec);
        }
        log.warn(orderId + "回调处理成功！");
        return ResponseEntity.ok( orderId + "回调处理成功！");
    }


    @Override
    public PageResult<ShopOrderDTO> getShopOrderList(Integer status, Integer pageNo, Integer pageSize) {
        //获取用户登录
        UserInfo user = UserInterceptor.getUserInfo();
        if (user== null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        PageHelper.startPage(pageNo, pageSize, true);
        PageHelper.orderBy("create_time desc");

        Example example=new Example(ShopOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userCode",user.getUserCode());
        criteria.andNotEqualTo("receiverAddress","");
        if (status != null&&status.equals(8)){
            criteria.andIn("status", Arrays.asList(8,9));
        }else{
            criteria.andEqualTo("status", status);
        }
        List<ShopOrder> list = shopOrderMapper.selectByExample(example);
        List<ShopOrderDTO> resultList = new ArrayList<>();
        for (ShopOrder o : list) {
            ShopOrderDTO shopOrderDTO = new ShopOrderDTO();
            BeanUtils.copyProperties(o, shopOrderDTO);
            ShopOrderDetail detail = new ShopOrderDetail();
            detail.setOrderId(o.getOrderId());
            List<ShopOrderDetail> select = detailMapper.select(detail);
            shopOrderDTO.setOrderDetails(select);
            resultList.add(shopOrderDTO);
        }
        PageInfo<ShopOrderDTO> pageInfo = new PageInfo<>(resultList);
        return new PageResult<>(pageInfo.getTotal(), resultList);
    }


    @Override
    public ResponseEntity payOrder(String orderId, HttpServletRequest request) {
        //获取用户登录
        UserInfo user = UserInterceptor.getUserInfo();
        if (user== null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        ShopOrder order = new ShopOrder();
        order.setStatus(1);
        order.setOrderId(orderId);
        ShopOrder newOrder = shopOrderMapper.selectOne(order);
        if (newOrder == null) {//订单不存在
            throw new CustomerException(ExceptionEnum.ORDER_NOT_ERROR);
        }
        BigDecimal totalPay = new BigDecimal(String.valueOf(newOrder.getTotalPay()));
        BigDecimal money = totalPay.divide(new BigDecimal("100"));
        String payUrl = WXPayUtil.pay(String.valueOf(money), IdUtil.simpleUUID(), "http://api.banmatongxiao.com/api/order/payNotify/"+ order.getOrderId(), "http://hao.banmatongxiao.com");
        return ResponseEntity.ok(payUrl);
    }

    @Override
    public ResponseEntity cancelOrder(String orderId) {
        //获取用户登录
        UserInfo user = UserInterceptor.getUserInfo();
        if (user == null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        ShopOrder newOrder = shopOrderMapper.selectByPrimaryKey(orderId);
        if (newOrder == null || newOrder.getStatus() != 1) {//订单不存在
            throw new CustomerException(ExceptionEnum.ORDER_NOT_ERROR);
        }
        newOrder.setStatus(7);
        Integer count = shopOrderMapper.updateByPrimaryKeySelective(newOrder);
        if (!count.toString().equals("1")) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity getBy(String orderId) {
        UserInfo user = UserInterceptor.getUserInfo();
        if (user == null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        ShopOrder shopOrder = shopOrderMapper.selectByPrimaryKey(orderId);
        ShopOrderDTO shopOrderDTO = new ShopOrderDTO();
        BeanUtils.copyProperties(shopOrder, shopOrderDTO);
        ShopOrderDetail detail = new ShopOrderDetail();
        detail.setOrderId(shopOrder.getOrderId());
        List<ShopOrderDetail> select = detailMapper.select(detail);
        shopOrderDTO.setOrderDetails(select);
        return ResponseEntity.ok(shopOrderDTO);
    }

    @Override
    public ResponseEntity list(Integer pageNo, Integer pageSize, Integer status, String orderNo, String buyerNick, String username, String userCode, String phone, Long beforeDate, Long rearDate, Integer orderType) {
        Example example = new Example(ShopOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if (status != null) criteria.andEqualTo("status", status);
        if (orderType != null) criteria.andEqualTo("orderType", orderType);
        if (StringUtils.isNotBlank(orderNo)) criteria.andLike("orderNo", "%" +  orderNo + "%");
        if (StringUtils.isNotBlank(buyerNick)) criteria.andLike("buyerNick", "%" + buyerNick + "%");
        if (StringUtils.isNotBlank(username)) criteria.andLike("userName", "%" + username + "%");
        if (StringUtils.isNotBlank(userCode)) criteria.andLike("userCode", "%" + userCode + "%");
        if (beforeDate != null) {
            Timestamp t = new Timestamp(beforeDate);
            Date d = new Date(t.getTime());
            criteria.andLessThanOrEqualTo("createTime", d);
        }
        if (rearDate != null) {
            Timestamp t2 = new Timestamp(rearDate);
            Date d2 = new Date(t2.getTime());
            criteria.andGreaterThanOrEqualTo("createTime", d2);
        }
        if (StringUtils.isNotBlank(phone)) {
            Example example1 = new Example(Customer.class);
            Example.Criteria criteria1 = example1.createCriteria();
            criteria1.andEqualTo("userMobile", phone);
            Customer customer = customerMapper.selectOneByExample(example1);
            if (customer != null) {
                criteria.andEqualTo("userCode", String.valueOf(customer.getUserCode()));
            }
        }
        criteria.andEqualTo("datalevel", 1);
        PageHelper.startPage(pageNo, pageSize);
        PageHelper.orderBy("create_time desc");
        List<ShopOrder> shopOrders = shopOrderMapper.selectByExample(example);

        BigDecimal actualPay = BigDecimal.ZERO;
        List<ShopOrder> countList = shopOrderMapper.selectByExample(example);
        for (ShopOrder shopOrder : countList) {
            actualPay = actualPay.add(new BigDecimal(shopOrder.getTotalPay()));
        }
        BigDecimal totalAmount = actualPay.divide(new BigDecimal("100"));


        List<String> orderIdS=shopOrders.stream().map(ShopOrder::getOrderId).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(orderIdS)){
            Example example1 = new Example(ShopOrderDetail.class);
            example1.createCriteria().andIn("orderId",orderIdS);
            List<ShopOrderDetail> details=detailMapper.selectByExample(example1);
            Map<String,ShopOrderDetail> detailMap=details.stream().collect(Collectors.toMap(s->s.getOrderId(),t->t));
            shopOrders.forEach(s->s.setShopOrderDetail(detailMap.get(s.getOrderId())));

            List<Long> userCodes=shopOrders.stream().map(ShopOrder::getUserCode).collect(Collectors.toList());
            Example example2 = new Example(Customer.class);
            example2.createCriteria().andIn("userCode",userCodes);
            List<Customer> customerList=customerMapper.selectByExample(example2);
            Map<Long,String> customerMap=customerList.stream().collect(Collectors.toMap(s->s.getUserCode(),t->t.getUserMobile()));
            shopOrders.forEach(s->s.setPhone(customerMap.get(s.getUserCode())));
        }

        PageInfo<ShopOrder> goodsPageInfo = new PageInfo<>(shopOrders);
        PageResult<ShopOrder> pageResult = new PageResult<>(goodsPageInfo.getTotal(), totalAmount, shopOrders);
        return ResponseEntity.ok(pageResult);
    }

    @Override
    public ResponseEntity delivery(DeliveryParam deliveryParam) {
        String orderId = deliveryParam.getOrderId();
        ShopOrder shopOrder = shopOrderMapper.selectByPrimaryKey(orderId);
        shopOrder.setStatus(3);
        shopOrder.setCourierCompany(deliveryParam.getCourierCompany());
        shopOrder.setTrackingNumber(deliveryParam.getTrackingNumber());
        Integer count = shopOrderMapper.updateByPrimaryKeySelective(shopOrder);
        if (count != 1) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity changeAddress(ChangeAddressParam changeAddressParam) {
        String orderId = changeAddressParam.getOrderId();
        ShopOrder shopOrder = shopOrderMapper.selectByPrimaryKey(orderId);
        if (StringUtils.isNotBlank(changeAddressParam.getReceiverAddress())) shopOrder.setReceiverAddress(changeAddressParam.getReceiverAddress());
        if (StringUtils.isNotBlank(changeAddressParam.getUserName())) shopOrder.setUserName(changeAddressParam.getUserName());
        if (StringUtils.isNotBlank(changeAddressParam.getUserMobile())) shopOrder.setUserMobile(changeAddressParam.getUserMobile());
        if (StringUtils.isNotBlank(changeAddressParam.getZipCode())) shopOrder.setZipCode(changeAddressParam.getZipCode());
        Integer count = shopOrderMapper.updateByPrimaryKeySelective(shopOrder);
        if (count != 1) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity getById(String id) {
        ShopOrder shopOrder = shopOrderMapper.selectByPrimaryKey(id);
        ShopOrderDTO shopOrderDTO = new ShopOrderDTO();
        BeanUtils.copyProperties(shopOrder, shopOrderDTO);
        ShopOrderDetail detail = new ShopOrderDetail();
        detail.setOrderId(shopOrder.getOrderId());
        List<ShopOrderDetail> select = detailMapper.select(detail);
        shopOrderDTO.setOrderDetails(select);
        return ResponseEntity.ok(shopOrderDTO);
    }

    @Override
    public ResponseEntity testPay(ShopPayOrderParam payOrderParam) {
        ShopOrder shopOrder = shopOrderMapper.selectByPrimaryKey(payOrderParam);
        if (shopOrder == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("没有找到订单号：" + payOrderParam.getOrderId());
        }
        Long totalIntegral = shopOrder.getTotalIntegral();
        // Long totalPay = shopOrder.getTotalPay();
        ShopOrder order = new ShopOrder();
        order.setOrderId(shopOrder.getOrderId());
        order.setStatus(2);
        Integer i1 = shopOrderMapper.updateByPrimaryKeySelective(order);
        if (i1 != 1) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("插入钻石明细失败");
        }
        //插入钻石明细
        CustomerScoreDetail customerScoreDetail = new CustomerScoreDetail();
        customerScoreDetail.setCustomerCode(shopOrder.getUserCode());
        customerScoreDetail.setScore(totalIntegral.doubleValue());
        customerScoreDetail.setDataSrc(2);
        customerScoreDetail.setOrderCode(shopOrder.getOrderId());
        customerScoreDetail.setScoreDescribe("钻石商城购买商品");
        customerScoreDetail.setScoreFlag(1);
        Integer i2 = customerScoreDetailMapper.insertSelective(customerScoreDetail);
        if (i2 != 1) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("插入钻石明细失败");
        }
        //插入钻石到用户的账号里
        Example example = new Example(CustomerInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("customerCode", shopOrder.getUserCode());
        CustomerInfo customerInfo = customerInfoMapper.selectOneByExample(example);
        Double userScore = customerInfo.getUserScore();
        customerInfo.setUserScore(userScore + totalIntegral);
        Integer count = customerInfoMapper.updateByPrimaryKeySelective(customerInfo);
        if (count != 1) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("插入钻石数量失败");
        }
        return ResponseEntity.ok().body("模拟付款成功");
    }


    private void threeLevelDistribution(ShopOrder order, String inviteId,Double remindSpec){

        BigDecimal totalMoney=new BigDecimal(order.getActualPay());

        BigDecimal  money=new BigDecimal("100");
        BigDecimal num=totalMoney.divide(money);
        OrderDistribution distribution = new OrderDistribution();
        distribution.setOrderNo(order.getOrderNo());            //  订单编号
        //  订单总额（扣除最小支付金额）
        distribution.setOrderTotal(num.doubleValue());
        distribution.setRemindLayer(1);                         //  提点层级
        distribution.setPurchaserCode(Long.valueOf(order.getUserCode())); //  订单消费者编号
        distribution.setCreateTime(new Date());                 //  创建时间
        //  创建分销提点
        this.createDistribution(distribution,inviteId,remindSpec);
    }

    private void createDistribution(OrderDistribution distribution,String beneficiaryCode,Double remindSpec){
        //  判断有收益人
        if (null == beneficiaryCode){
            return;
        }
        //  查询相应层级的提点比例
        /*int layer = distribution.getRemindLayer();
        String confKey = ConfigkeyConstant.MALL_ORDER_DISTRIBUTION_REMIND + layer;
        UserConf conf=new UserConf();
        conf.setConfKey(confKey);
        conf.setFlag(0);
        conf = userConfMapper.selectOne(conf);*/
        if (null == remindSpec||remindSpec==0){
            return;
        }

        try{
            //  获取提点比例
            //Double remindSpec = Double.parseDouble(conf.getConfValue());
            distribution.setRemindSpec(remindSpec);
            //  计算提点金额
            Double remindMoney = ArithUtil.mul(remindSpec,distribution.getOrderTotal());
            distribution.setRemindMoney(remindMoney);
            //  获取收益人信息
            CustomerInfo customerInfo = new CustomerInfo();
            customerInfo.setInviteCode(beneficiaryCode);
            List<CustomerInfo> customerList = customerInfoMapper.select(customerInfo);
            if (customerList.isEmpty()){
                return;
            }
            customerInfo = customerList.get(0);//chang
            distribution.setBeneficiaryCode(customerInfo.getCustomerCode());
            distribution.setId(null);
            //  保存提点记录
            this.orderDistributionMapper.insertSelective(distribution);
            //  更新收益人佣金余额
            double userMoney = ArithUtil.add(customerInfo.getEmployMoney(),remindMoney);
            customerInfo.setEmployMoney(userMoney);
            this.customerInfoMapper.updateByPrimaryKeySelective(customerInfo);

            //  创建下一级分销记录
            /*layer++;
            //  判断层级
            if (layer > 3){
                return;
            }
            distribution.setRemindLayer(layer);

            Customer customer=new Customer();
            customer.setUserCode(customerInfo.getCustomerCode());
            List<Customer> customerInfoList = customerMapper.select(customer);
            if(customerInfoList.isEmpty()) {
                return;
            }
            customer=customerInfoList.get(0);
            this.createDistribution(distribution,customer.getInviteId() == null ? null : customer.getInviteId());*/
        }catch (NumberFormatException e){
            //this.logger.error("配置：\"" + confKey + "\" 的值是个字符串");
            this.logger.error(e.getMessage());
        }
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }


    @Override
    public ResponseEntity buy(BuyParam buyParam) {

        //获取用户登录
        UserInfo user = UserInterceptor.getUserInfo();
        if (user == null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);

        ShopGoods shopGoods = goodsMapper.selectByPrimaryKey(buyParam.getGoodsId());

        // TODO: 2020/3/18 Aquan 商品限购判断
        Integer limitEnough = shopGoods.getLimitEnough();
        if (limitEnough != 0) {
            // 查询用户是否已购买过商品
            Integer count = shopOrderMapper.checkLimitEnough(user.getUserCode(), buyParam.getGoodsId());
            if (count >= limitEnough) {
                return ResponseEntity.ok("该商品只能购买" + limitEnough + "次");
            }
        }

        BigDecimal totalPay;
        Long specId = 0l;
        String specName = "";
        // TODO: 2020/3/13 判断是否有规格ID
        if (!org.springframework.util.StringUtils.isEmpty(buyParam.getSpecificationId())) {
            // 不为空时处理 有规格
            ShopSpecification shopSpecification = shopSpecificationMapper.selectByPrimaryKey(buyParam.getSpecificationId());
            totalPay = shopSpecification.getPrice().multiply(new BigDecimal("100"));
            specId = shopSpecification.getId();
            specName = shopSpecification.getGoodsSpecs();
        } else {
            totalPay = new BigDecimal(shopGoods.getPrice());
        }

        //生成订单
        ShopOrder order = new ShopOrder();
        String orderId = String.valueOf(idWorker.nextId());
        order.setOrderId(orderId);
        order.setOrderNo(String.valueOf(idWorker.nextId()));
        order.setUserCode(user.getUserCode());
        order.setBuyerNick(user.getName());
        order.setTotalPay(totalPay.longValue());
        order.setTotalIntegral(shopGoods.getIntegral());
        order.setSpecId(specId);
        order.setSpecName(specName);
        order.setVirtualMessage(shopGoods.getVirtualMessage());
        // TODO: 2020/4/2 虚拟商品生成对应的虚拟类型订单
        if (shopGoods.getIsVirtual() == 1) {
            order.setOrderType(1);
        }
        Integer count = shopOrderMapper.insertSelective(order);
        if (count != 1) throw new CustomerException(ExceptionEnum.CREATE_ORDER_ERROR);

        ShopOrderDetail shopOrderDetail = new ShopOrderDetail();
        shopOrderDetail.setOrderId(orderId);
        //生成订单详情数据
        shopOrderDetail.setGoodsId(buyParam.getGoodsId());
        shopOrderDetail.setNum(1);
        shopOrderDetail.setTitle(shopGoods.getTitle());
        shopOrderDetail.setSpecificationsId(specId);
        shopOrderDetail.setSpecificationsName(specName);
        shopOrderDetail.setPrice(totalPay.longValue());
        shopOrderDetail.setIntegral(shopGoods.getIntegral());
        shopOrderDetail.setImage(shopGoods.getImages().split(",")[0]);
        Integer i = detailMapper.insertSelective(shopOrderDetail);
        if (count != 1) throw new CustomerException(ExceptionEnum.CREATE_ORDER_ERROR);

        ShopOrder shopOrder = shopOrderMapper.selectByPrimaryKey(orderId);
        ShopOrderDTO shopOrderDTO = new ShopOrderDTO();
        BeanUtils.copyProperties(shopOrder, shopOrderDTO);
        ShopOrderDetail detail = new ShopOrderDetail();
        detail.setOrderId(shopOrder.getOrderId());
        List<ShopOrderDetail> select = detailMapper.select(detail);
        shopOrderDTO.setOrderDetails(select);
        return ResponseEntity.ok(shopOrderDTO);
    }

    @Override
    public ResponseEntity modifyAddress(ModifyAddressParam modifyAddressParam) {
        //获取用户登录
        UserInfo user = UserInterceptor.getUserInfo();
        if (user == null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        //获取用户选择的收货地址相关信息
        StringBuilder address = new StringBuilder();
        String zipCode = "";
        String userName = "";
        String userMobile = "";
        if (!String.valueOf(modifyAddressParam.getAddressId()).equals("0")) {
            CustomerAddress customerAddress = addressMapper.selectByPrimaryKey(modifyAddressParam.getAddressId());
            zipCode = customerAddress.getZipCode();
            userName = customerAddress.getUserName();
            userMobile = customerAddress.getUserMobile();
            address.append(customerAddress.getProvince() != null ? customerAddress.getProvince() : "" )
                    .append(customerAddress.getCity() != null ? customerAddress.getCity() : "" )
                    .append(customerAddress.getArea() != null ? customerAddress.getArea() : "" )
                    .append(customerAddress.getStreet() != null ? customerAddress.getStreet() : "" )
                    .append(customerAddress.getDetailed() != null ? customerAddress.getDetailed() : "" );
        }
        ShopOrder shopOrder = shopOrderMapper.selectByPrimaryKey(modifyAddressParam.getOrderId());
        if (shopOrder == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("没有该订单");
        shopOrder.setReceiverAddress(address.toString());
        shopOrder.setUserName(userName);
        shopOrder.setUserMobile(userMobile);
        shopOrder.setZipCode(zipCode);
        Integer count = shopOrderMapper.updateByPrimaryKeySelective(shopOrder);
        if (count != 1)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("修改地址失败");


        // TODO: 2020.2.21 抵用消费券
        String couponCode = modifyAddressParam.getCouponCode();
        if (couponCode != null) {
            Example exampleUserCoupon = new Example(UserCoupon.class);
            Example.Criteria criteriaUserCoupon = exampleUserCoupon.createCriteria();
            criteriaUserCoupon.andEqualTo("couponCode", couponCode);
            criteriaUserCoupon.andEqualTo("couponStatus", 0);
            criteriaUserCoupon.andEqualTo("datalevel", 1);
            criteriaUserCoupon.andGreaterThan("efectiveTime", new Date());
            UserCoupon userCoupon = userCouponMapper.selectOneByExample(exampleUserCoupon);
            if (userCoupon == null) {
                throw new CustomerException(ExceptionEnum.CREATE_ORDER_ERROR);
            }
            BigDecimal multiply = userCoupon.getCouponAmount();

            Example exampleDetail = new Example(ShopOrderDetail.class);
            Example.Criteria criteriaDetail = exampleDetail.createCriteria();
            criteriaDetail.andEqualTo("orderId", shopOrder.getOrderId());
            ShopOrderDetail shopOrderDetail = detailMapper.selectOneByExample(exampleDetail);
            Long goodsId = shopOrderDetail.getGoodsId();

            ShopGoods shopGoods = goodsMapper.selectByPrimaryKey(goodsId);
            BigDecimal discount = shopGoods.getDiscount();

            BigDecimal result = BigDecimal.ZERO;
            if(multiply.compareTo(discount) > -1){
                System.out.println("a大于等于b");
                result = discount;
            }
            if(multiply.compareTo(discount) == -1){
                System.out.println("a小于b");
                result = multiply;
            }
            BigDecimal totalPay = new BigDecimal(String.valueOf(shopOrder.getTotalPay()));
            BigDecimal divide = totalPay.divide(new BigDecimal("100"));
            BigDecimal money = divide.subtract(result);
            String payUrl = WXPayUtil.pay(String.valueOf(money), IdUtil.simpleUUID(), "http://api.banmatongxiao.com/api/order/payNotify/"+ shopOrder.getOrderId(), "http://hao.banmatongxiao.com");

            //更改优惠券状态
            userCoupon.setCouponStatus(1);
            userCouponMapper.updateByPrimaryKey(userCoupon);

            return ResponseEntity.ok(payUrl);
        }

        BigDecimal totalPay = new BigDecimal(String.valueOf(shopOrder.getTotalPay()));
        BigDecimal divide = totalPay.divide(new BigDecimal("100"));
        String payUrl = WXPayUtil.pay(String.valueOf(divide), IdUtil.simpleUUID(), "http://api.banmatongxiao.com/api/order/payNotify/"+ shopOrder.getOrderId(), "http://hao.banmatongxiao.com");

        return ResponseEntity.ok(payUrl);
    }

    @Override
    public ResponseEntity endOrder(String orderId) {
        //获取用户登录
        UserInfo user = UserInterceptor.getUserInfo();
        if (user == null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);

        ShopOrder shopOrder = shopOrderMapper.selectByPrimaryKey(orderId);
        if (shopOrder == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("没有找到订单号：" + orderId);
        shopOrder.setStatus(4);
        Integer count = shopOrderMapper.updateByPrimaryKeySelective(shopOrder);
        if (count != 1)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity orderModifyAddress(ModifyAddressParam modifyAddressParam) {
        ShopOrder shopOrder = shopOrderMapper.selectByPrimaryKey(modifyAddressParam.getOrderId());
        if (shopOrder == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("没有该订单");
        }
        if (shopOrder.getStatus() != 2) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("该订单状态无法修改地址");
        }

        //获取用户选择的收货地址相关信息
        CustomerAddress customerAddress = addressMapper.selectByPrimaryKey(modifyAddressParam.getAddressId());
        String zipCode = customerAddress.getZipCode();
        String userName = customerAddress.getUserName();
        String userMobile = customerAddress.getUserMobile();
        StringBuilder address = new StringBuilder();
        address.append(customerAddress.getProvince() != null ? customerAddress.getProvince() : "" )
                .append(customerAddress.getCity() != null ? customerAddress.getCity() : "" )
                .append(customerAddress.getArea() != null ? customerAddress.getArea() : "" )
                .append(customerAddress.getStreet() != null ? customerAddress.getStreet() : "" )
                .append(customerAddress.getDetailed() != null ? customerAddress.getDetailed() : "" );

        shopOrder.setReceiverAddress(address.toString());
        shopOrder.setUserName(userName);
        shopOrder.setUserMobile(userMobile);
        shopOrder.setZipCode(zipCode);
        Integer count = shopOrderMapper.updateByPrimaryKeySelective(shopOrder);
        if (count != 1) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("修改地址失败");
        }
        return ResponseEntity.ok("修改成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity orderRefund(String id) {

        ShopReturnOrder shopReturnOrder = shopReturnOrderMapper.selectByPrimaryKey(id);
        String orderNo = shopReturnOrder.getOrderNo();
        //获取用户选择的收货地址相关信息
        if (StringUtils.isBlank(orderNo)){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }

        //查询订单
        ShopOrder shopOrder =new ShopOrder();
        shopOrder.setStatus(9);
        shopOrder.setOrderNo(orderNo);
        shopOrder=shopOrderMapper.selectOne(shopOrder);
        if (shopOrder==null){
            throw new CustomerException(ExceptionEnum.ORDER_NOT_ERROR);
        }

        //扣除抽奖机会
        CustomerInfo customerInfo =new CustomerInfo();
        customerInfo.setCustomerCode(shopOrder.getUserCode());
        customerInfo=customerInfoMapper.selectOne(customerInfo);

        BigDecimal totalPay = new BigDecimal(String.valueOf(shopOrder.getTotalPay()));
        Double totalMoney = totalPay.divide(new BigDecimal("100")).doubleValue();
        ShopGoods goods=shopOrderMapper.getGoodsCommission(shopOrder.getOrderId());
        if (customerInfo.getUserMoney()>=goods.getIntegral()){//满足扣减抽奖机会
            customerInfo.setUserMoney(customerInfo.getUserMoney()-goods.getIntegral());
            customerInfoMapper.updateByPrimaryKey(customerInfo);//扣除抽奖机会
        }else{
            totalMoney=totalMoney-goods.getIntegral();
        }
        //扣出佣金
        BigDecimal totalPay1 = new BigDecimal(String.valueOf(shopOrder.getTotalPay()));
        Double totalMoney1 = totalPay1.divide(new BigDecimal("100")).doubleValue();
        Double commission=totalMoney1*goods.getCommission().doubleValue();

        //扣除上级佣金
        Customer customer =new Customer();
        customer.setUserCode(customerInfo.getCustomerCode());
        customer.setFlag(0);
        customer=customerMapper.selectOne(customer);

        CustomerInfo superior =new CustomerInfo();
        superior.setInviteCode(customer.getInviteId());
        superior=customerInfoMapper.selectOne(superior);
        if (superior!=null && superior.getEmployMoney()>commission){//满足扣佣金
            superior.setEmployMoney(superior.getEmployMoney()-commission);
            customerInfoMapper.updateByPrimaryKey(superior);//扣除抽奖机会
        }


        PaymentChannel channel=new PaymentChannel();
        channel.setOrderNo(shopOrder.getOrderId());
        channel=paymentChannelMapper.selectOne(channel);

        Map<String,Object> map=new HashMap<>();
        map.put("payjs_order_id",shopOrder.getPlatformOrderNo());
        map.put("mchid",channel.getMchid());
        map.put("key",channel.getKeyName());
        map= PayHelperByBOB.wxRefund(map);
        if (map.get("return_code").equals(1)){

            shopOrder.setStatus(8);
            Example example=new Example(ShopOrder.class);
            example.createCriteria().andEqualTo("orderId",shopOrder.getOrderId());
            shopOrderMapper.updateByExample(shopOrder,example);

            // 修改退款订单申请记录的状态1，通过申请
            shopReturnOrder.setStatus(1);
            Example example_return=new Example(ShopReturnOrder.class);
            example_return.createCriteria().andEqualTo("id",shopReturnOrder.getId());
            shopReturnOrderMapper.updateByExample(shopReturnOrder,example_return);
            return ResponseEntity.ok("退款成功");//退款成功
        }else{
            return ResponseEntity.ok("退款失败");//退款失败
        }
    }

    @Override
    public ResponseEntity submitoOrderRefund(SubmitoOrderRefundParam submitoOrderRefundParam) {
        //获取用户登录
        UserInfo user = UserInterceptor.getUserInfo();
        if (user == null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        if (StringUtils.isBlank(submitoOrderRefundParam.getOrderId())){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        //查询订单
        ShopOrder shopOrder = shopOrderMapper.selectByPrimaryKey(submitoOrderRefundParam.getOrderId());
        if (shopOrder==null){
            throw new CustomerException(ExceptionEnum.ORDER_NOT_ERROR);
        }
        if (!(shopOrder.getStatus() == 2 || shopOrder.getStatus() == 3)) {
            throw new CustomerException(ExceptionEnum.ORDER_NOT_ERROR);
        }

        shopOrder.setStatus(9);
        shopOrderMapper.updateByPrimaryKey(shopOrder);

        ShopReturnOrder shopReturnOrder = new ShopReturnOrder();
        shopReturnOrder.setId(String.valueOf(idWorker.nextId()));
        shopReturnOrder.setActualPay(shopOrder.getActualPay());
        shopReturnOrder.setDescription(submitoOrderRefundParam.getDescription());
        shopReturnOrder.setImages(submitoOrderRefundParam.getImages());
        shopReturnOrder.setOrderId(shopOrder.getOrderId());
        shopReturnOrder.setOrderNo(shopOrder.getOrderNo());
        shopReturnOrder.setPlatformOrderNo(shopOrder.getPlatformOrderNo());
        shopReturnOrder.setTotalIntegral(shopOrder.getTotalIntegral());
        shopReturnOrder.setUserCode(user.getUserCode());
        shopReturnOrder.setUserName(shopOrder.getUserName());
        shopReturnOrder.setUserMobile(shopOrder.getUserMobile());
        Integer count = shopReturnOrderMapper.insertSelective(shopReturnOrder);
        if (count != 1) {
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
        return ResponseEntity.ok("提交成功");
    }

    @Override
    public ResponseEntity returnOrderList(Integer pageNo, Integer pageSize, Integer status) {
        Example example = new Example(ShopReturnOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if (status != null) criteria.andEqualTo("status", status);
        criteria.andEqualTo("datalevel", 1);
        PageHelper.startPage(pageNo, pageSize);
        PageHelper.orderBy("create_time desc");
        List<ShopReturnOrder> shopReturnOrders = shopReturnOrderMapper.selectByExample(example);
        PageInfo<ShopReturnOrder> pageInfo = new PageInfo<>(shopReturnOrders);
        return ResponseEntity.ok(pageInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity reject(RejectParam rejectParam) {
        ShopReturnOrder shopReturnOrder = shopReturnOrderMapper.selectByPrimaryKey(rejectParam.getId());
        if (shopReturnOrder==null){
            throw new CustomerException(ExceptionEnum.ORDER_NOT_ERROR);
        }
        shopReturnOrder.setStatus(2);
        shopReturnOrderMapper.updateByPrimaryKeySelective(shopReturnOrder);

        ShopOrder shopOrder = shopOrderMapper.selectByPrimaryKey(shopReturnOrder.getOrderId());
        if (shopOrder==null){
            throw new CustomerException(ExceptionEnum.ORDER_NOT_ERROR);
        }
        shopOrder.setStatus(2);
        shopOrderMapper.updateByPrimaryKey(shopOrder);
        return ResponseEntity.ok("成功驳回");
    }

    @Override
    public ResponseEntity adminPasse(String orderNo) {
        //查询订单
        ShopOrder shopOrder =new ShopOrder();
        shopOrder.setStatus(9);
        shopOrder.setOrderNo(orderNo);
        shopOrder=shopOrderMapper.selectOne(shopOrder);
        if (shopOrder==null){
            throw new CustomerException(ExceptionEnum.ORDER_NOT_ERROR);
        }

        //扣除抽奖机会
        CustomerInfo customerInfo =new CustomerInfo();
        customerInfo.setCustomerCode(shopOrder.getUserCode());
        customerInfo=customerInfoMapper.selectOne(customerInfo);

        BigDecimal totalPay = new BigDecimal(String.valueOf(shopOrder.getTotalPay()));
        Double totalMoney = totalPay.divide(new BigDecimal("100")).doubleValue();
        ShopGoods goods=shopOrderMapper.getGoodsCommission(shopOrder.getOrderId());
        if (customerInfo.getUserMoney()>goods.getIntegral()){//满足扣减抽奖机会
            customerInfo.setUserMoney(customerInfo.getUserMoney()-goods.getIntegral());
            customerInfoMapper.updateByPrimaryKey(customerInfo);//扣除抽奖机会
        }else{
            totalMoney=totalMoney-goods.getIntegral();
        }
        //扣出佣金
        BigDecimal totalPay1 = new BigDecimal(String.valueOf(shopOrder.getTotalPay()));
        Double totalMoney1 = totalPay1.divide(new BigDecimal("100")).doubleValue();
        Double commission=totalMoney1*goods.getCommission().doubleValue();

        //扣除上级佣金
        Customer customer =new Customer();
        customer.setUserCode(customerInfo.getCustomerCode());
        customer.setFlag(0);
        customer=customerMapper.selectOne(customer);

        CustomerInfo superior =new CustomerInfo();
        superior.setInviteCode(customer.getInviteId());
        superior=customerInfoMapper.selectOne(superior);
        if (superior!=null && superior.getEmployMoney()>commission){//满足扣佣金
            superior.setEmployMoney(superior.getEmployMoney()-commission);
            customerInfoMapper.updateByPrimaryKey(superior);//扣除抽奖机会
        }

        Map<String,Object> map=new HashMap<>();
        map.put("orderid",shopOrder.getPlatformOrderNo());
        map.put("refund_order_id",idWorker.nextId());
        map.put("money",totalMoney);
        map= WXRefundUtil.wxRefund(map);
        if (map.get("status").equals("success")){
            shopOrder.setStatus(8);
            shopOrderMapper.updateByPrimaryKeySelective(shopOrder);

            return ResponseEntity.ok("退款成功");//退款成功
        }else{
            return ResponseEntity.ok("退款失败");//退款失败
        }
    }

    @Override
    public ResponseEntity orderModifyVirtualAccount(ModifyVirtualAccountParam modifyVirtualAccountParam) {
        ShopOrder shopOrder = shopOrderMapper.selectByPrimaryKey(modifyVirtualAccountParam.getOrderId());
        if (shopOrder == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("没有该订单");
        }

        shopOrder.setVirtualAccount(modifyVirtualAccountParam.getVirtualAccount());
        Integer count = shopOrderMapper.updateByPrimaryKeySelective(shopOrder);
        if (count != 1) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("填写账号失败");
        }
        return ResponseEntity.ok("修改成功");
    }

    @Override
    public String userPaymentNotifyByPayBOB(HttpServletRequest request) throws Exception {
        logger.info("进入回调");
        SortedMap<Object, Object> map = new TreeMap<Object, Object>();
        map.put("return_code", request.getParameter("return_code"));
        map.put("total_fee", request.getParameter("total_fee"));
        map.put("out_trade_no", request.getParameter("out_trade_no"));
        map.put("payjs_order_id", request.getParameter("payjs_order_id"));
        map.put("transaction_id", request.getParameter("transaction_id"));
        map.put("time_end", request.getParameter("time_end"));
        map.put("openid", request.getParameter("openid"));
        map.put("attach", request.getParameter("attach"));
        map.put("mchid", request.getParameter("mchid"));
        map.put("sign", request.getParameter("sign"));

        PaymentChannel channel=new PaymentChannel();
        channel.setOrderNo(request.getParameter("out_trade_no"));
        channel=paymentChannelMapper.selectOne(channel);

        map.put("key", channel.getKeyName());
        // 保证密钥一致性
        if (PayHelperByBOB.checkPaySign(map)) {
            ShopOrder shopOrder = shopOrderMapper.selectByPrimaryKey(request.getParameter("out_trade_no"));
            Long payAmount = Long.valueOf(map.get("total_fee").toString());
            if (shopOrder!=null&&shopOrder.getTotalPay().equals(payAmount)){
                // TODO: 2020/1/17 给购物的商品添加销量
                Example example = new Example(ShopOrderDetail.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("orderId", shopOrder.getOrderId());
                List<ShopOrderDetail> shopOrderDetails = detailMapper.selectByExample(example);

                //商品分销提点
                Double remindSpec=0.0;
                Integer goodsType=0;
                for ( ShopOrderDetail shopOrderDetail : shopOrderDetails ) {
                    Long goodsId = shopOrderDetail.getGoodsId();
                    ShopGoods shopGoods = goodsMapper.selectByPrimaryKey(goodsId);
                    Integer salesVolume = shopGoods.getSalesVolume();
                    Integer total = salesVolume + shopOrderDetail.getNum();
                    shopGoods.setSalesVolume(total);
                    goodsMapper.updateByPrimaryKeySelective(shopGoods);
                    //只有一条记录时可以使用
                    remindSpec=shopGoods.getCommission().doubleValue();
                    if(shopGoods.getGoodsTypeId().equals(1)){//购买特定商品充值20元话费
                        customerService.mobileRecharge(shopOrder.getUserCode(),20);
                        //查询上级给上级赠送抽奖机会
                        Customer customer =new Customer();
                        customer.setUserCode(shopOrder.getUserCode());
                        customer.setFlag(0);
                        customer=customerMapper.selectOne(customer);

                        CustomerInfo superior =new CustomerInfo();
                        superior.setInviteCode(customer.getInviteId());
                        superior=customerInfoMapper.selectOne(superior);
                        if (superior!=null){
                            int rowCount = customerInfoMapper.addAmount(superior.getCustomerCode(), 2.0, 0.00, 1L);
                            if (rowCount > 0) {
                                CustomerRechargeRecord customerRechargeRecord = new CustomerRechargeRecord();
                                customerRechargeRecord.setAmount(2.0);
                                customerRechargeRecord.setCustomerCode(superior.getCustomerCode());
                                customerRechargeRecord.setIntegral(0.00);
                                customerRechargeRecord.setType(1);//'充值消费标识{ 1：充值，2：消费 , 3 :佣金提现 ,4佣金充值}',
                                customerRechargeRecord.setPayment(PayStatusConstant.PAY_FROM_WECHAT);//微信
                                customerRechargeRecord.setCreateTime(new Date());
                                customerRechargeRecord.setGoodsTypeId(goodsType);
                                int rowResult = this.customerRechargeRecordMapper.insertSelective(customerRechargeRecord);
                                if (rowResult== 1) {
                                    logger.info("会员余额充值记录");
                                }
                            }
                        }
                    }
                    goodsType=shopGoods.getGoodsTypeId();
                }

                // TODO: 2020/4/2 虚拟订单的订单支付完成后直接修改订单状态为 已发货未确认状态
                if (shopOrder.getOrderType() == 1) {
                    shopOrder.setStatus(3);
                } else {
                    shopOrder.setStatus(2);
                }
                if(goodsType.equals(1)){
                    shopOrder.setStatus(4);
                }
                shopOrder.setPaymentType(1);
                shopOrder.setActualPay(payAmount.longValue());
                shopOrder.setPaymentTime(new Date());
                shopOrder.setPlatformOrderNo(request.getParameter("payjs_order_id"));
                Integer count = shopOrderMapper.updateByPrimaryKeySelective(shopOrder);
                if (count != 1) {
                    log.error("修改订单状态失败");
                    throw new Exception("修改订单状态失败");
                }

                // TODO: 2020/3/12 给规格商品减库存
                if (shopOrder.getSpecId()>0){
                    ShopSpecification shopSpecification = shopSpecificationMapper.selectByPrimaryKey(shopOrder.getSpecId());
                    Integer stock = shopSpecification.getStock();
                    shopSpecification.setStock(stock - 1);
                    shopSpecificationMapper.updateByPrimaryKey(shopSpecification);
                }

                //  给用户充值福分
                CustomerInfo payCustomer = new CustomerInfo();
                payCustomer.setCustomerCode(shopOrder.getUserCode());
                payCustomer=customerInfoMapper.selectOne(payCustomer);
                UserConf rechargeConf = configService.selectCmsValue(ConfigkeyConstant.MALL_USER_RECHARGE_GIVE);
                double userMoney=shopOrder.getTotalIntegral();
                if (rechargeConf != null && !StringUtils.isEmpty(rechargeConf.getConfValue())) {
                    String[] array1 = rechargeConf.getConfValue().split("/");
                    if (array1.length == 2) {
                        double amountX = Double.parseDouble(array1[0]);
                        double amountY = Double.parseDouble(array1[1]);
                        if (userMoney >= amountX) {// 判断开关 金额是否已达到可以送金额的数量
                            long x = (long) (userMoney / amountX);
                            Double userAmount = ArithUtil.mul(x, amountY);
                            userMoney = userMoney + userAmount;
                        }
                    } else {
                        logger.error("配置格式：X/Y,X为充值金额,Y为赠送金额");
                    }

                }
                int rowCount = customerInfoMapper.addAmount(shopOrder.getUserCode(), userMoney, 0.00, 1L);
                if (rowCount > 0) {
                    CustomerRechargeRecord customerRechargeRecord = new CustomerRechargeRecord();
                    customerRechargeRecord.setAmount(userMoney);
                    customerRechargeRecord.setCustomerCode(payCustomer.getCustomerCode());
                    customerRechargeRecord.setIntegral(0.00);
                    customerRechargeRecord.setType(1);//'充值消费标识{ 1：充值，2：消费 , 3 :佣金提现 ,4佣金充值}',
                    customerRechargeRecord.setPayment(PayStatusConstant.PAY_FROM_WECHAT);//微信
                    customerRechargeRecord.setCreateTime(new Date());
                    customerRechargeRecord.setGoodsTypeId(goodsType);
                    int rowResult = this.customerRechargeRecordMapper.insertSelective(customerRechargeRecord);
                    if (rowResult== 1) {
                        logger.info("会员余额充值记录");
                    }
                }

                Customer customer=new Customer();
                customer.setUserCode(shopOrder.getUserCode());
                customer=customerMapper.selectOne(customer);
                if (!goodsType.equals(1)){
                    log.info("==================================三级分佣开始===========");
                    this.threeLevelDistribution(shopOrder, customer.getInviteId(),remindSpec);
                }
                log.warn(request.getParameter("out_trade_no") + "回调处理成功！");
            }
        }
        return "success";
    }

    @Override
    public Map<String, String> payOrderByBoB(ModifyAddressParam modifyAddressParam) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        //获取用户登录
        UserInfo user = UserInterceptor.getUserInfo();
        if (user == null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        //获取用户选择的收货地址相关信息
        StringBuilder address = new StringBuilder();
        String zipCode = "";
        String userName = "";
        String userMobile = "";
        if (!String.valueOf(modifyAddressParam.getAddressId()).equals("0")) {
            CustomerAddress customerAddress = addressMapper.selectByPrimaryKey(modifyAddressParam.getAddressId());
            zipCode = customerAddress.getZipCode();
            userName = customerAddress.getUserName();
            userMobile = customerAddress.getUserMobile();
            address.append(customerAddress.getProvince() != null ? customerAddress.getProvince() : "" )
                    .append(customerAddress.getCity() != null ? customerAddress.getCity() : "" )
                    .append(customerAddress.getArea() != null ? customerAddress.getArea() : "" )
                    .append(customerAddress.getStreet() != null ? customerAddress.getStreet() : "" )
                    .append(customerAddress.getDetailed() != null ? customerAddress.getDetailed() : "" );
        }
        ShopOrder shopOrder = shopOrderMapper.selectByPrimaryKey(modifyAddressParam.getOrderId());
        if (shopOrder == null)
            throw new CustomerException(ExceptionEnum.ORDER_NOT_ERROR);
        shopOrder.setReceiverAddress(address.toString());
        shopOrder.setUserName(userName);
        shopOrder.setUserMobile(userMobile);
        shopOrder.setZipCode(zipCode);
        Integer count = shopOrderMapper.updateByPrimaryKeySelective(shopOrder);
        if (count != 1)
            throw new CustomerException(ExceptionEnum.USER_ACCOUNT_SETTINGS);


        PaymentChannel channel=new PaymentChannel();

        // TODO: 2020.2.21 抵用消费券
        String couponCode = modifyAddressParam.getCouponCode();
        if (couponCode != null) {
            Example exampleUserCoupon = new Example(UserCoupon.class);
            Example.Criteria criteriaUserCoupon = exampleUserCoupon.createCriteria();
            criteriaUserCoupon.andEqualTo("couponCode", couponCode);
            criteriaUserCoupon.andEqualTo("couponStatus", 0);
            criteriaUserCoupon.andEqualTo("datalevel", 1);
            criteriaUserCoupon.andGreaterThan("efectiveTime", new Date());
            UserCoupon userCoupon = userCouponMapper.selectOneByExample(exampleUserCoupon);
            if (userCoupon == null) {
                throw new CustomerException(ExceptionEnum.CREATE_ORDER_ERROR);
            }
            BigDecimal multiply = userCoupon.getCouponAmount();

            Example exampleDetail = new Example(ShopOrderDetail.class);
            Example.Criteria criteriaDetail = exampleDetail.createCriteria();
            criteriaDetail.andEqualTo("orderId", shopOrder.getOrderId());
            ShopOrderDetail shopOrderDetail = detailMapper.selectOneByExample(exampleDetail);
            Long goodsId = shopOrderDetail.getGoodsId();

            ShopGoods shopGoods = goodsMapper.selectByPrimaryKey(goodsId);
            BigDecimal discount = shopGoods.getDiscount();

            BigDecimal result = BigDecimal.ZERO;
            if(multiply.compareTo(discount) > -1){
                System.out.println("a大于等于b");
                result = discount;
            }
            if(multiply.compareTo(discount) == -1){
                System.out.println("a小于b");
                result = multiply;
            }
            BigDecimal totalPay = new BigDecimal(String.valueOf(shopOrder.getTotalPay()));
            BigDecimal divide = totalPay.divide(new BigDecimal("100"));
            BigDecimal money = divide.subtract(result);

            //更改优惠券状态
            userCoupon.setCouponStatus(1);
            userCouponMapper.updateByPrimaryKey(userCoupon);


            shopOrder.setTotalPay(Long.valueOf(SignUtil.convertAmount(money.toString())));
            count = shopOrderMapper.updateByPrimaryKeySelective(shopOrder);
            if (count != 1)
                throw new CustomerException(ExceptionEnum.USER_ACCOUNT_SETTINGS);

            String notifyURL="http://api.banmatongxiao.com/api/order/userPaymentNotifyByPayBOB";
            Map<String,String> map=PayHelperByBOB.payOrder(money.doubleValue(),shopOrder.getOrderId(),notifyURL,shopOrder.getTotalIntegral());
            channel.setMchid(map.get("mchid"));
            channel.setKeyName(map.get("key"));
            channel.setOrderNo(shopOrder.getOrderId());
            channel.setReturnUrl(map.get("callback_url"));
            channel.setUrlPrefix(map.get("notify_url"));
            channel.setCreateTime(new Date());
            paymentChannelMapper.insertSelective(channel);
            return map;
        }
        BigDecimal totalPay = new BigDecimal(String.valueOf(shopOrder.getTotalPay()));
        BigDecimal divide = totalPay.divide(new BigDecimal("100"));
        String notifyURL="http://api.banmatongxiao.com/api/order/userPaymentNotifyByPayBOB";
        Map<String,String> map=PayHelperByBOB.payOrder(divide.doubleValue(),shopOrder.getOrderId(),notifyURL,shopOrder.getTotalIntegral());
        channel.setMchid(map.get("mchid"));
        channel.setKeyName(map.get("key"));
        channel.setOrderNo(shopOrder.getOrderId());
        channel.setReturnUrl(map.get("callback_url"));
        channel.setUrlPrefix(map.get("notify_url"));
        channel.setCreateTime(new Date());
        paymentChannelMapper.insertSelective(channel);
        Map<String,String> resultMap=new HashMap<>();
        resultMap.put("payUrl",map.get("payUrl"));
        return resultMap;
    }

    @Override
    public String userPayCode(Long userCode, String urlText) throws Exception {
        if (userCode== null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();//io流
        BufferedImage bufferedImage= QRCodeUtil.createImage(urlText, null, false);
        ImageIO.write(bufferedImage, "png", baos);//写入流中
        byte[] bytes = baos.toByteArray();//转换成字节
        BASE64Encoder encoder = new BASE64Encoder();
        //转换成base64串
        String png_base = encoder.encodeBuffer(bytes).trim();
        //删除 \r\n
        String str = png_base.replaceAll("\n", "").replaceAll("\r", "");
        return str;
    }

    @Override
    public Map<String, String> payOrderAgain(String orderId, HttpServletRequest request) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        //获取用户登录
        UserInfo user = UserInterceptor.getUserInfo();
        if (user== null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        ShopOrder order = new ShopOrder();
        order.setStatus(1);
        order.setOrderId(orderId);
        ShopOrder newOrder = shopOrderMapper.selectOne(order);
        if (newOrder == null) {//订单不存在
            throw new CustomerException(ExceptionEnum.ORDER_NOT_ERROR);
        }
        BigDecimal totalPay = new BigDecimal(String.valueOf(newOrder.getTotalPay()));
        BigDecimal divide = totalPay.divide(new BigDecimal("100"));
        String notifyURL="http://api.banmatongxiao.com/api/order/userPaymentNotifyByPayBOB";
        Map<String,String> map=PayHelperByBOB.payOrder(divide.doubleValue(),newOrder.getOrderId(),notifyURL,order.getTotalIntegral());
        return map;
    }

/*    public static void main(String[] args) {
        Map<String,Object> map=new HashMap<>();
        map.put("payjs_order_id",shopOrder.getPlatformOrderNo());
        map.put("mchid","1584324971");
        map.put("key","wv06wj1NDwBquw1P");
        map= PayHelperByBOB.wxRefund(map);
    }*/

}
