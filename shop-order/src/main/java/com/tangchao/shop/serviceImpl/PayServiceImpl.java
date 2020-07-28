package com.tangchao.shop.serviceImpl;

import cn.hutool.json.JSONObject;
import com.tangchao.common.constant.ConfigkeyConstant;
import com.tangchao.common.constant.PayStatusConstant;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.common.utils.ArithUtil;
import com.tangchao.shop.interceptor.UserInterceptor;
import com.tangchao.shop.mapper.*;
import com.tangchao.shop.params.WebhookParam;
import com.tangchao.shop.pojo.*;
import com.tangchao.shop.service.PayService;
import com.tangchao.user.service.CmsConfigService;
import com.tangchao.user.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2020/5/29 15:29
 */
@Slf4j
@Service
public class PayServiceImpl implements PayService {

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private ShopOrderMapper shopOrderMapper;

    @Autowired
    private ShopGoodsMapper goodsMapper;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerInfoMapper customerInfoMapper;

    @Autowired
    private CustomerRechargeRecordMapper customerRechargeRecordMapper;

    @Autowired
    private ShopSpecificationMapper shopSpecificationMapper;

    @Autowired
    private OrderDistributionMapper orderDistributionMapper;

    @Autowired
    private CmsConfigService configService;

    @Autowired
    private ShopOrderDetailMapper detailMapper;

    @Autowired
    private PaymentOrderPlatformMapper paymentOrderPlatformMapper;


    @Override
    public Map<String,String> createBill(HttpServletRequest request, BigDecimal money,String notify) {

        String baseUrl = "http://www.onecityonline.com/";

        //  获取当前用户
        //获取用户登录
        UserInfo user = UserInterceptor.getUserInfo();
        if (user== null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        //用户信息
        Customer record = new Customer();
        record.setUserCode(user.getUserCode());
        Customer userInfo = customerMapper.selectOne(record);

        HttpClient httpclient = HttpClientBuilder.create().build();

        Base64.Encoder encoder = Base64.getEncoder();
        String encoding = encoder.encodeToString(("fc821d48-5f13-4929-97c2-31c57fd33f4f:").getBytes());
        //String encoding = encoder.encodeToString(("cdc4f58c-1a46-433c-ac9f-eb2de906c171:").getBytes());// 沙盒环境

        HttpPost httppost = new HttpPost("https://www.billplz.com/api/v3/bills");
        //HttpPost httppost = new HttpPost("https://www.billplz-sandbox.com/api/v3/bills"); // 沙盒环境
        httppost.setHeader("Authorization", "Basic " + encoding);
        try {
            httppost.setEntity(new UrlEncodedFormEntity(getData(money, userInfo.getUserMobile(), baseUrl,notify)));
        } catch (UnsupportedEncodingException ex) {
            log.error(ex.getMessage());
        }

        System.out.println("executing request " + httppost.getRequestLine());
        HttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
        HttpEntity entity = response.getEntity();
        System.out.println("----------------------------------------");
        System.out.println(response.getStatusLine());
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));

            String line = null;
            while ((line = rd.readLine()) != null) {
                System.out.println(line);
                result.append(line);
            }
        } catch (IOException ex) {
            log.error(ex.getMessage());
        } catch (UnsupportedOperationException ex) {
            log.error(ex.getMessage());
        }
        JSONObject jsonObject = new JSONObject(result);
        String url = jsonObject.getStr("url");
        String refid = jsonObject.getStr("id");

        Map<String,String> resultMap=new HashMap<>();
        resultMap.put("payUrl",url);
        resultMap.put("orderId",refid);
        return resultMap;
    }


    public static List<NameValuePair> getData(BigDecimal money, String mobile, String baseUrl,String notify) {
        BigDecimal amount = money.multiply(new BigDecimal("100"));
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("collection_id", "utogvfxv"));
        //urlParameters.add(new BasicNameValuePair("collection_id", "v3qcsqjm"));//沙盒环境
        urlParameters.add(new BasicNameValuePair("description", "one city"));
        urlParameters.add(new BasicNameValuePair("email", "onecityonline@hotmail.com"));
        urlParameters.add(new BasicNameValuePair("mobile", mobile));
        urlParameters.add(new BasicNameValuePair("name", "Michael API V3"));
        urlParameters.add(new BasicNameValuePair("amount", amount.toString()));
        urlParameters.add(new BasicNameValuePair("callback_url", notify));
        urlParameters.add(new BasicNameValuePair("redirect_url", baseUrl));
        return urlParameters;
    }


    public static Boolean check(WebhookParam webhookParam) {
        String data = webhookParam.toString();
        String key = "S-Ite9LKMFqC2IEk148hqsQg";
        //String key = "S-caHZmB_KjGJRLsgJ4cHjCA";//沙盒环境
        try {
            String secret = HMACSHA256(data, key);
            log.warn("secret：" + secret);
            log.warn("X_signature：" + webhookParam.getX_signature());
            if (secret.equals(webhookParam.getX_signature())) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return true;
        }
    }

    public static String HMACSHA256(String data, String key) throws Exception {

        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] array = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();

    }

    // TODO: 2019/12/13 S-caHZmB_KjGJRLsgJ4cHjCA
    @Autowired
    public void webhook(WebhookParam webhookParam,HttpServletRequest request) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        log.warn("________________________" + LocalDateTime.now().toString() + "________________________");
        Boolean check = check(webhookParam);
        if (check) {
            log.error("数据一致");
            log.error(webhookParam.toString());
            PaymentOrderPlatform platform = new PaymentOrderPlatform();
            platform.setPaymentOrderNo(webhookParam.getId());
            platform = paymentOrderPlatformMapper.selectOne(platform);

            if (platform.getPaymentStatus().equals(1)) {

                paymentOrderPlatformMapper.updatePlatformTradeNo(webhookParam.getId(), platform.getId());

                ShopOrder shopOrder = shopOrderMapper.selectByPrimaryKey(platform.getOrderId());
                Long payAmount = Long.valueOf(webhookParam.getAmount());
                if (shopOrder != null && shopOrder.getTotalPay().equals(payAmount)) {
                    // TODO: 2020/1/17 给购物的商品添加销量
                    Example example = new Example(ShopOrderDetail.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("orderId", shopOrder.getOrderId());
                    List<ShopOrderDetail> shopOrderDetails = detailMapper.selectByExample(example);

                    //商品分销提点
                    Double remindSpec = 0.0;
                    Integer goodsType = 0;
                    for (ShopOrderDetail shopOrderDetail : shopOrderDetails) {
                        Long goodsId = shopOrderDetail.getGoodsId();
                        ShopGoods shopGoods = goodsMapper.selectByPrimaryKey(goodsId);
                        Integer salesVolume = shopGoods.getSalesVolume();
                        Integer total = salesVolume + shopOrderDetail.getNum();
                        shopGoods.setSalesVolume(total);
                        goodsMapper.updateByPrimaryKeySelective(shopGoods);
                        //只有一条记录时可以使用
                        remindSpec = shopGoods.getCommission().doubleValue();
                        if (shopGoods.getGoodsTypeId().equals(1)) {//购买特定商品充值20元话费
                            customerService.mobileRecharge(shopOrder.getUserCode(), 20);
                            //查询上级给上级赠送抽奖机会
                            Customer customer = new Customer();
                            customer.setUserCode(shopOrder.getUserCode());
                            customer.setFlag(0);
                            customer = customerMapper.selectOne(customer);

                            CustomerInfo superior = new CustomerInfo();
                            superior.setInviteCode(customer.getInviteId());
                            superior = customerInfoMapper.selectOne(superior);
                            if (superior != null) {
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
                                    if (rowResult == 1) {
                                        log.info("会员余额充值记录");
                                    }
                                }
                            }
                        }
                        goodsType = shopGoods.getGoodsTypeId();
                    }

                    // TODO: 2020/4/2 虚拟订单的订单支付完成后直接修改订单状态为 已发货未确认状态
                    if (shopOrder.getOrderType() == 1) {
                        shopOrder.setStatus(3);
                    } else {
                        shopOrder.setStatus(2);
                    }
                    if (goodsType.equals(1)) {
                        shopOrder.setStatus(4);
                    }
                    shopOrder.setPaymentType(1);
                    shopOrder.setActualPay(payAmount.longValue());
                    shopOrder.setPaymentTime(new Date());
                    shopOrder.setPlatformOrderNo(request.getParameter("payjs_order_id"));
                    Integer count = shopOrderMapper.updateByPrimaryKeySelective(shopOrder);
                    if (count != 1) {
                        log.error("修改订单状态失败");
                    }

                    // TODO: 2020/3/12 给规格商品减库存
                    if (shopOrder.getSpecId() > 0) {
                        ShopSpecification shopSpecification = shopSpecificationMapper.selectByPrimaryKey(shopOrder.getSpecId());
                        Integer stock = shopSpecification.getStock();
                        shopSpecification.setStock(stock - 1);
                        shopSpecificationMapper.updateByPrimaryKey(shopSpecification);
                    }

                    //  给用户充值福分
                    CustomerInfo payCustomer = new CustomerInfo();
                    payCustomer.setCustomerCode(shopOrder.getUserCode());
                    payCustomer = customerInfoMapper.selectOne(payCustomer);
                    UserConf rechargeConf = configService.selectCmsValue(ConfigkeyConstant.MALL_USER_RECHARGE_GIVE);
                    double userMoney = shopOrder.getTotalIntegral();
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
                            log.error("配置格式：X/Y,X为充值金额,Y为赠送金额");
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
                        if (rowResult == 1) {
                            log.info("会员余额充值记录");
                        }
                    }

                    Customer customer = new Customer();
                    customer.setUserCode(shopOrder.getUserCode());
                    customer = customerMapper.selectOne(customer);
                    if (!goodsType.equals(1)) {
                        log.info("==================================三级分佣开始===========");
                        this.threeLevelDistribution(shopOrder, customer.getInviteId(), remindSpec);
                    }
                    log.warn(webhookParam.getId() + "回调处理成功！");

                } else {
                    log.error("数据已经处理过！无需重复处理！");
                }

            } else {
                log.error("数据给修改过");
            }
            log.warn("________________________________________________________________________");
            // return null;

        }
    }

    @Override
    public void userPaymentNotifyByWebhook(WebhookParam webhookParam, HttpServletRequest request) {
        log.warn("________________________" + LocalDateTime.now().toString() + "________________________");
        Boolean check = check(webhookParam);
        if (check) {
            log.error("数据一致");
            log.error(webhookParam.toString());
            PaymentOrderPlatform platform = new PaymentOrderPlatform();
            platform.setPaymentOrderNo(webhookParam.getId());
            platform = paymentOrderPlatformMapper.selectOne(platform);

            if (platform.getPaymentStatus().equals("1")) {

                paymentOrderPlatformMapper.updatePlatformTradeNo(webhookParam.getId(), platform.getId());
                if (webhookParam.getAmount().equals(webhookParam.getPaid_amount())){
                    log.info("发起支付金额和实际支付一样！");
                    //  给用户充值福分
                    CustomerInfo payCustomer = new CustomerInfo();
                    payCustomer.setCustomerCode(Long.valueOf(platform.getPaymentUserCode()));
                    payCustomer=customerInfoMapper.selectOne(payCustomer);
                    UserConf rechargeConf = configService.selectCmsValue(ConfigkeyConstant.MALL_USER_RECHARGE_GIVE);
                    double userMoney=Double.valueOf(webhookParam.getAmount());
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
                            log.error("配置格式：X/Y,X为充值金额,Y为赠送金额");
                        }

                    }
                    int rowCount = customerInfoMapper.addAmount(Long.valueOf(platform.getPaymentUserCode()), userMoney, 0.00, 1L);
                    if (rowCount > 0) {
                        CustomerRechargeRecord customerRechargeRecord = new CustomerRechargeRecord();
                        customerRechargeRecord.setAmount(userMoney);
                        customerRechargeRecord.setCustomerCode(payCustomer.getCustomerCode());
                        customerRechargeRecord.setIntegral(0.00);
                        customerRechargeRecord.setType(1);//'充值消费标识{ 1：充值，2：消费 , 3 :佣金提现 ,4佣金充值}',
                        customerRechargeRecord.setPayment(PayStatusConstant.PAY_FROM_WECHAT);//微信
                        customerRechargeRecord.setCreateTime(new Date());
                        int rowResult = this.customerRechargeRecordMapper.insertSelective(customerRechargeRecord);
                        if (rowResult== 1) {
                            log.info("会员余额充值记录");
                        }
                    }
                    log.info("修改用户积分!");
                    log.warn(request.getParameter("out_trade_no") + "回调处理成功！");

                } else {
                    log.error("数据已经处理过！无需重复处理！");
                }

            } else {
                log.error("数据给修改过");
            }
            log.warn("________________________________________________________________________");
            // return null;

        }
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

    private void createDistribution(OrderDistribution distribution,String beneficiaryCode,Double remindSpec) {
        //  判断有收益人
        if (null == beneficiaryCode) {
            return;
        }
        if (null == remindSpec || remindSpec == 0) {
            return;
        }

        try {
            //  获取提点比例
            //Double remindSpec = Double.parseDouble(conf.getConfValue());
            distribution.setRemindSpec(remindSpec);
            //  计算提点金额
            Double remindMoney = ArithUtil.mul(remindSpec, distribution.getOrderTotal());
            distribution.setRemindMoney(remindMoney);
            //  获取收益人信息
            CustomerInfo customerInfo = new CustomerInfo();
            customerInfo.setInviteCode(beneficiaryCode);
            List<CustomerInfo> customerList = customerInfoMapper.select(customerInfo);
            if (customerList.isEmpty()) {
                return;
            }
            customerInfo = customerList.get(0);//chang
            distribution.setBeneficiaryCode(customerInfo.getCustomerCode());
            distribution.setId(null);
            //  保存提点记录
            this.orderDistributionMapper.insertSelective(distribution);
            //  更新收益人佣金余额
            double userMoney = ArithUtil.add(customerInfo.getEmployMoney(), remindMoney);
            customerInfo.setEmployMoney(userMoney);
            this.customerInfoMapper.updateByPrimaryKeySelective(customerInfo);

        } catch (NumberFormatException e) {
            //this.logger.error("配置：\"" + confKey + "\" 的值是个字符串");
            this.log.error(e.getMessage());
        }
    }
}
