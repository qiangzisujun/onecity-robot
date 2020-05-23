package com.tangchao.shop.serviceImpl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tangchao.common.constant.ConfigkeyConstant;
import com.tangchao.common.constant.PayStatusConstant;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.common.utils.ArithUtil;
import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.dto.CouponLogDTO;
import com.tangchao.shop.dto.ShopCouponDTO;
import com.tangchao.shop.interceptor.UserInterceptor;
import com.tangchao.shop.mapper.*;
import com.tangchao.shop.params.GetCouponParam;
import com.tangchao.shop.params.PayCouponParam;
import com.tangchao.shop.pojo.*;
import com.tangchao.shop.service.CouponService;
import com.tangchao.shop.utils.MPSignUtil;
import com.tangchao.shop.utils.PayHelperByBOB;
import com.tangchao.shop.utils.WXPayUtil;
import com.tangchao.user.service.CmsConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Slf4j
@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private CouponLogMapper couponLogMapper;

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Autowired
    private CustomerScoreDetailMapper customerScoreDetailMapper;

    @Autowired
    private CustomerInfoMapper customerInfoMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private CmsConfigService configService;

    @Autowired
    private CustomerRechargeRecordMapper customerRechargeRecordMapper;

    @Autowired
    private OrderDistributionMapper orderDistributionMapper;

    @Autowired
    private UserConfMapper userConfMapper;

    @Autowired
    private PaymentChannelMapper paymentChannelMapper;

    @Override
    public void saveCouponInfo(ShopCouponDTO couponDTO) {
        if (couponDTO.getUserId()==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        Coupon coupon=new Coupon();
        coupon.setCouponName(couponDTO.getCouponName());
        coupon.setImg(couponDTO.getImg());
        coupon.setDetailImg(couponDTO.getDetailImg());
        coupon.setEfectiveTime(couponDTO.getEfectiveTime());
        coupon.setCouponAmount(couponDTO.getCouponAmount());
        coupon.setNum(couponDTO.getNum());
        coupon.setPurchaseAmount(couponDTO.getPurchaseAmount());
        coupon.setLuckdrawNum(couponDTO.getLuckdrawNum());
        coupon.setSort(couponDTO.getSort());
        coupon.setDescription(couponDTO.getDescription());
        coupon.setIsShare(0);
        coupon.setStatus(1);
        coupon.setCreateTime(new Date());
        int count=couponMapper.insertSelective(coupon);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void updateCouponInfo(ShopCouponDTO shopCouponDTO) {

        if (shopCouponDTO.getUserId()==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        Coupon coupon1=couponMapper.selectByPrimaryKey(shopCouponDTO.getId());
        if (coupon1==null){
            throw new CustomerException(ExceptionEnum.GOODS_NOT_EXIST);
        }
        Coupon coupon=new Coupon();
        coupon.setId(coupon1.getId());
        coupon.setCouponName(shopCouponDTO.getCouponName());
        coupon.setImg(shopCouponDTO.getImg());
        coupon.setDetailImg(shopCouponDTO.getDetailImg());
        coupon.setEfectiveTime(shopCouponDTO.getEfectiveTime());
        coupon.setCouponAmount(shopCouponDTO.getCouponAmount());
        coupon.setNum(shopCouponDTO.getNum());
        coupon.setPurchaseAmount(shopCouponDTO.getPurchaseAmount());
        coupon.setLuckdrawNum(shopCouponDTO.getLuckdrawNum());
        coupon.setSort(shopCouponDTO.getSort());
        coupon.setDescription(shopCouponDTO.getDescription());
        coupon.setIsShare(0);
        coupon.setStatus(1);
        coupon.setCreateTime(new Date());
        int count=couponMapper.updateByPrimaryKeySelective(coupon);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void deleteCouponInfo(Long userId, Map<String, Object> id) {

        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        if (id==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        String data=id.get("id").toString();
        Coupon coupon1=couponMapper.selectByPrimaryKey(data);
        if (coupon1==null){
            throw new CustomerException(ExceptionEnum.GOODS_NOT_EXIST);
        }
        Coupon coupon=new Coupon();
        coupon.setId(coupon1.getId());
        coupon.setStatus(0);
        int count=couponMapper.updateByPrimaryKeySelective(coupon);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }


    @Override
    public ResponseEntity getCouponList(Integer pageNo, Integer pageSize) {
        Example example = new Example(Coupon.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", 1);
        PageHelper.startPage(pageNo,pageSize);
        PageHelper.orderBy("sort desc");
        List<Coupon> coupons = couponMapper.selectByExample(example);
        PageInfo<Coupon> couponPage = new PageInfo<>(coupons);
        PageResult<Coupon> couponPageResult = new PageResult<>(couponPage.getTotal(), coupons);
        return ResponseEntity.ok(couponPageResult);
    }

    @Override
    public ResponseEntity getInfo(Integer couponId) {
        Coupon coupon = couponMapper.selectByPrimaryKey(couponId);
        return ResponseEntity.ok(coupon);
    }

    @Override
    public ResponseEntity payCoupon(PayCouponParam payCouponParam) {
        //获取用户登录
        UserInfo user = UserInterceptor.getUserInfo();
        if (user == null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        Coupon coupon = couponMapper.selectByPrimaryKey(payCouponParam.getCouponId());
        BigDecimal purchaseAmount = coupon.getPurchaseAmount().multiply(new BigDecimal(String.valueOf(payCouponParam.getNumber())));//优惠券购买金额
        //插入购买记录
        String no = IdUtil.simpleUUID();//流水号
        CouponLog couponLog = new CouponLog();
        couponLog.setUserCode(user.getUserCode());
        couponLog.setCouponId(payCouponParam.getCouponId());
        couponLog.setPayAmount(purchaseAmount);
        couponLog.setNo(no);
        couponLog.setLogStatus(0);
        couponLog.setCreateTime(new Date());
        couponLog.setDatalevel(1);
        couponLog.setNumber(payCouponParam.getNumber());
        couponLogMapper.insert(couponLog);
        // http://api.banmatongxiao.com
        String payUrl = WXPayUtil.pay(String.valueOf(purchaseAmount), IdUtil.simpleUUID(), "http://api.banmatongxiao.com/api/coupon/payNotify/"+ no, "http://hao.banmatongxiao.com");
        return ResponseEntity.ok(payUrl);
    }

    @Override
    @Transactional
    public ResponseEntity payNotify(String no, String resultCode, String money, String type, String sign, String timeEnd) throws Exception {
        Boolean aBoolean = WXPayUtil.checkSign(sign, money, no);
        if (!aBoolean && !resultCode.equals("SUCCESS")) {
            log.error( no + "订单号回调处理失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("回调失败");
        }
        log.warn("回调处理，订单号：" + no);
        Example example = new Example(CouponLog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("no", no);
        CouponLog couponLog = couponLogMapper.selectOneByExample(example);
        if (couponLog == null && couponLog.getLogStatus() != 1) {
            log.error("没有该订单号或者该订单号已经支付");
            throw new Exception("没有该订单号或者该订单号已经支付");
        }
        // TODO: 2020.2.15 修改log的支付状态
        couponLog.setLogStatus(1);
        couponLogMapper.updateByPrimaryKey(couponLog);
        // TODO: 2020.2.15 生成优惠券码到对应的账号下
        Integer number = couponLog.getNumber();//购买数量
        Coupon coupon = couponMapper.selectByPrimaryKey(couponLog.getCouponId());
        for (int i=0; i<number; i++) {
            String couponCode = IdUtil.simpleUUID();
            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setCouponId(couponLog.getCouponId());
            userCoupon.setCouponName(coupon.getCouponName());
            userCoupon.setCouponCode(couponCode);
            userCoupon.setImg(coupon.getImg());
            userCoupon.setEfectiveTime(DateUtil.offsetDay(new Date(), coupon.getEfectiveTime()));
            userCoupon.setCouponAmount(coupon.getCouponAmount());
            userCoupon.setPurchaseAmount(coupon.getPurchaseAmount());
            userCoupon.setDescription(coupon.getDescription());
            userCoupon.setIsShare(coupon.getIsShare());
            userCoupon.setUserCode(String.valueOf(couponLog.getUserCode()));
            userCoupon.setCreateTime(new Date());
            userCoupon.setDatalevel(1);
            userCoupon.setCouponStatus(0);
            userCoupon.setCouponLogNo(couponLog.getNo());
            userCouponMapper.insert(userCoupon);
            log.info("回调成功生成优惠券码："  + couponCode);
            // TODO: 2020.2.17 减去优惠券库库存数
            Integer num = coupon.getNum();
            if (num != -1) {
                coupon.setNum(num - 1);
                couponMapper.updateByPrimaryKey(coupon);
            }
        }


        // TODO: 2020.2.18 充值积分
        //  给用户充值福分
       /* //插入钻石明细
        CustomerScoreDetail customerScoreDetail = new CustomerScoreDetail();
        customerScoreDetail.setCustomerCode(couponLog.getUserCode());
        customerScoreDetail.setScore(coupon.getLuckdrawNum().doubleValue());
        customerScoreDetail.setDataSrc(2);
        customerScoreDetail.setOrderCode(couponLog.getNo());
        customerScoreDetail.setScoreDescribe("钻石商城购买优惠券");
        customerScoreDetail.setScoreFlag(1);
        Integer i2 = customerScoreDetailMapper.insertSelective(customerScoreDetail);
        if (i2 != 1) {
            log.error("插入钻石明细失败");
            throw new Exception("插入钻石明细失败");
        }
        //插入钻石到用户的账号里
        Example example1 = new Example(CustomerInfo.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andEqualTo("customerCode", couponLog.getUserCode());
        CustomerInfo customerInfo = customerInfoMapper.selectOneByExample(example1);
        Double userMoney = customerInfo.getUserMoney();
        customerInfo.setUserMoney(userMoney + coupon.getLuckdrawNum());
        Integer count1 = customerInfoMapper.updateByPrimaryKeySelective(customerInfo);
        if (count1 != 1) {
            log.error("更新用户钻石数失败");
            throw new Exception("更新用户钻石数失败");
        }*/
        //  给用户充值福分
        CustomerInfo payCustomer = new CustomerInfo();
        payCustomer.setCustomerCode(couponLog.getUserCode());
        payCustomer=customerInfoMapper.selectOne(payCustomer);
        UserConf rechargeConf = configService.selectCmsValue(ConfigkeyConstant.MALL_USER_RECHARGE_GIVE);
        // TODO: 2020/3/18 有多张所以得乘与数量
        double userMoney = coupon.getLuckdrawNum() * number;
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
        int rowCount = customerInfoMapper.addAmount(couponLog.getUserCode(), userMoney, 0.00, 1L);
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
        Customer customer=new Customer();
        customer.setUserCode(couponLog.getUserCode());
        customer=customerMapper.selectOne(customer);
        log.info("==================================三级分佣开始===========");
        this.threeLevelDistribution(couponLog, customer);
        log.warn("回调处理成功！");
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity getCouponListByAdmin(Long userId, Integer pageNo, Integer pageSize,String name) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        PageHelper.startPage(pageNo,pageSize);
        PageHelper.orderBy(" sort desc");
        Example example = new Example(Coupon.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", 1);
        if (!StringUtils.isBlank(name)){
            criteria.andEqualTo("couponName", name);
        }
        List<Coupon> coupon = couponMapper.selectByExample(example);
        PageInfo<Coupon> pageInfo=new PageInfo<>(coupon);
        PageResult<Coupon> couponPageResult = new PageResult<>(pageInfo.getTotal(), coupon);
        return ResponseEntity.ok(couponPageResult);
    }

    @Override
    public ResponseEntity getUserCouponList(Integer status, Integer pageNo, Integer pageSize) {
        //获取用户登录
        UserInfo user = UserInterceptor.getUserInfo();
        if (user == null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);

        // TODO: 2020.2.17 status状态：1、未使用 2、已使用 3、已过期
        Example example = new Example(UserCoupon.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("datalevel", 1);
        // criteria.andEqualTo("userCode", "163455019085099146");
        criteria.andEqualTo("userCode", user.getUserCode());
        if (status == 1) {
            criteria.andEqualTo("couponStatus", 0);
            criteria.andGreaterThan("efectiveTime", new Date());
        }
        if (status == 2) {
            criteria.andEqualTo("couponStatus", 1);
            criteria.andGreaterThan("efectiveTime", new Date());
        }
        if (status == 3) {
            criteria.andLessThan("efectiveTime", new Date());
        }
        PageHelper.startPage(pageNo,pageSize);
        PageHelper.orderBy("create_time");
        List<UserCoupon> userCoupons = userCouponMapper.selectByExample(example);
        PageInfo<UserCoupon> userCouponPage = new PageInfo<>(userCoupons);
        PageResult<UserCoupon> userCouponPageResult = new PageResult<>(userCouponPage.getTotal(), userCoupons);
        return ResponseEntity.ok(userCouponPageResult);
    }

    @Override
    public ResponseEntity shareCoupon(String shareUrl) {
        //获取用户登录
        UserInfo user = UserInterceptor.getUserInfo();
        if (user == null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);

        final String appId = "wx4af5214e5db4563b";
        final String appSecret = "d44406d75f0638673055067335a7ec92";
        final String accessTokenURL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + appSecret;
        String accessTokenJSON = HttpRequest.get(accessTokenURL)
                .timeout(20000)//超时，毫秒
                .execute().body();
        JSONObject jsonAccessToken = JSONUtil.parseObj(accessTokenJSON);
        String accessToken = (String) jsonAccessToken.get("access_token");
        // http://api.weixin.qq.com/cgi-bin/ticket/getticket?type=jsapi&access_token=ACCESS_TOKEN
        final String ticketURL = "http://api.weixin.qq.com/cgi-bin/ticket/getticket?type=jsapi&access_token=" + accessToken;
        String ticketJSON = HttpRequest.get(ticketURL)
                .timeout(20000)//超时，毫秒
                .execute().body();
        JSONObject jsonTicket = JSONUtil.parseObj(ticketJSON);
        String ticket = (String) jsonTicket.get("ticket");
        Map<String, String> sign = MPSignUtil.sign(ticket, shareUrl);
        return ResponseEntity.ok(sign);
    }

    @Override
    public ResponseEntity getCoupon(GetCouponParam getCouponParam) {
        //获取用户登录
        UserInfo user = UserInterceptor.getUserInfo();
        if (user == null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);

        Example example = new Example(UserCoupon.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("datalevel", 1);
        criteria.andEqualTo("id", getCouponParam.getCouponId());
        criteria.andEqualTo("userCode", getCouponParam.getGiveUserCode());
        UserCoupon userCoupon = userCouponMapper.selectOneByExample(example);
        if (userCoupon == null) throw new CustomerException(ExceptionEnum.GOODS_NOT_EXIST);

        userCoupon.setUserCode(String.valueOf(user.getUserCode()));
        userCouponMapper.updateByPrimaryKey(userCoupon);

        return ResponseEntity.ok("领取成功");
    }

    @Override
    public Map<String, String> payOrderByBoB(PayCouponParam payCouponParam) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        //获取用户登录
        UserInfo user = UserInterceptor.getUserInfo();
        if (user == null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        Coupon coupon = couponMapper.selectByPrimaryKey(payCouponParam.getCouponId());
        BigDecimal purchaseAmount = coupon.getPurchaseAmount().multiply(new BigDecimal(String.valueOf(payCouponParam.getNumber())));//优惠券购买金额
        //插入购买记录
        String no = IdUtil.simpleUUID();//流水号
        CouponLog couponLog = new CouponLog();
        couponLog.setUserCode(user.getUserCode());
        couponLog.setCouponId(payCouponParam.getCouponId());
        couponLog.setPayAmount(purchaseAmount);
        couponLog.setNo(no);
        couponLog.setLogStatus(0);
        couponLog.setCreateTime(new Date());
        couponLog.setDatalevel(1);
        couponLog.setNumber(payCouponParam.getNumber());
        couponLogMapper.insert(couponLog);

        Integer totalIntegral=payCouponParam.getNumber()*coupon.getLuckdrawNum();
        String notifyURL="http://api.banmatongxiao.com/api/coupon/userPaymentNotifyByPayBOB";
        Map<String,String> map=PayHelperByBOB.payOrder(purchaseAmount.doubleValue(),no,notifyURL,Long.parseLong(totalIntegral.toString()));
        PaymentChannel channel=new PaymentChannel();
        channel.setMchid(map.get("mchid"));
        channel.setKeyName(map.get("key"));
        channel.setOrderNo(no);
        channel.setReturnUrl(map.get("callback_url"));
        channel.setUrlPrefix(map.get("notify_url"));
        channel.setCreateTime(new Date());
        paymentChannelMapper.insertSelective(channel);
        return map;
    }

    @Override
    public String userPaymentNotifyByPayBOB(HttpServletRequest request) throws Exception {
        log.info("进入回调");
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
            Example example = new Example(CouponLog.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("no", request.getParameter("out_trade_no"));
            criteria.andEqualTo("logStatus",0);
            CouponLog couponLog = couponLogMapper.selectOneByExample(example);
            BigDecimal totalPay = new BigDecimal(map.get("total_fee").toString());
            BigDecimal money = totalPay.divide(new BigDecimal("100"));
            if (couponLog!=null&&couponLog.getPayAmount().doubleValue()==money.doubleValue()){
                //执行相应的逻辑
                log.info("金额相等");
                // TODO: 2020.2.15 修改log的支付状态
                couponLog.setLogStatus(1);
                couponLogMapper.updateByPrimaryKey(couponLog);
                // TODO: 2020.2.15 生成优惠券码到对应的账号下
                Integer number = couponLog.getNumber();//购买数量
                Coupon coupon = couponMapper.selectByPrimaryKey(couponLog.getCouponId());
                for (int i=0; i<number; i++) {
                    String couponCode = IdUtil.simpleUUID();
                    UserCoupon userCoupon = new UserCoupon();
                    userCoupon.setCouponId(couponLog.getCouponId());
                    userCoupon.setCouponName(coupon.getCouponName());
                    userCoupon.setCouponCode(couponCode);
                    userCoupon.setImg(coupon.getImg());
                    userCoupon.setEfectiveTime(DateUtil.offsetDay(new Date(), coupon.getEfectiveTime()));
                    userCoupon.setCouponAmount(coupon.getCouponAmount());
                    userCoupon.setPurchaseAmount(coupon.getPurchaseAmount());
                    userCoupon.setDescription(coupon.getDescription());
                    userCoupon.setIsShare(coupon.getIsShare());
                    userCoupon.setUserCode(String.valueOf(couponLog.getUserCode()));
                    userCoupon.setCreateTime(new Date());
                    userCoupon.setDatalevel(1);
                    userCoupon.setCouponStatus(0);
                    userCoupon.setCouponLogNo(couponLog.getNo());
                    userCouponMapper.insert(userCoupon);
                    log.info("回调成功生成优惠券码："  + couponCode);
                    // TODO: 2020.2.17 减去优惠券库库存数
                    Integer num = coupon.getNum();
                    if (num != -1) {
                        coupon.setNum(num - 1);
                        couponMapper.updateByPrimaryKey(coupon);
                    }
                }

                //  给用户充值福分
                CustomerInfo payCustomer = new CustomerInfo();
                payCustomer.setCustomerCode(couponLog.getUserCode());
                payCustomer=customerInfoMapper.selectOne(payCustomer);
                UserConf rechargeConf = configService.selectCmsValue(ConfigkeyConstant.MALL_USER_RECHARGE_GIVE);
                // TODO: 2020/3/18 有多张所以得乘与数量
                double userMoney = coupon.getLuckdrawNum() * number;
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
                int rowCount = customerInfoMapper.addAmount(couponLog.getUserCode(), userMoney, 0.00, 1L);
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
                Customer customer=new Customer();
                customer.setUserCode(couponLog.getUserCode());
                customer=customerMapper.selectOne(customer);
                log.info("==================================三级分佣开始===========");
                this.threeLevelDistribution(couponLog, customer);
                log.warn("回调处理成功！");
                return "success";
            }
        }
        return "success";
    }

    private void threeLevelDistribution(CouponLog order, Customer customer){

        OrderDistribution distribution = new OrderDistribution();
        distribution.setOrderNo(order.getNo());            //  订单编号
        //  订单总额（扣除最小支付金额）
        distribution.setOrderTotal(order.getPayAmount().doubleValue());
        distribution.setRemindLayer(1);                         //  提点层级
        distribution.setPurchaserCode(Long.valueOf(order.getUserCode())); //  订单消费者编号
        distribution.setCreateTime(new Date());                 //  创建时间
        //  创建分销提点
        this.createDistribution(distribution,customer.getInviteId());
    }

    private void createDistribution(OrderDistribution distribution,String beneficiaryCode){
        //  判断有收益人
        if (null == beneficiaryCode){
            return;
        }
        //  查询相应层级的提点比例
        int layer = distribution.getRemindLayer();
        String confKey = ConfigkeyConstant.MALL_ORDER_DISTRIBUTION_REMIND + layer;
        UserConf conf=new UserConf();
        conf.setConfKey(confKey);
        conf.setFlag(0);
        conf = userConfMapper.selectOne(conf);
        if (null == conf|| StringUtils.isEmpty(conf.getConfValue())){
            return;
        }
        try{
            //  获取提点比例
            Double remindSpec = Double.parseDouble(conf.getConfValue());
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
            layer++;
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
            this.createDistribution(distribution,customer.getInviteId() == null ? null : customer.getInviteId());
        }catch (NumberFormatException e){
            log.error("配置：\"" + confKey + "\" 的值是个字符串");
            log.error(e.getMessage());
        }
    }
}
