package com.tangchao.web.controller;

import com.github.pagehelper.util.StringUtil;
import com.tangchao.common.constant.SmsTemplateTypeConstant;
import com.tangchao.shop.dto.UserDTO;
import com.tangchao.shop.pojo.*;
import com.tangchao.shop.service.SendSmsMessageService;
import com.tangchao.shop.service.ShopOrderService;
import com.tangchao.shop.utils.FaceBookUtils;
import com.tangchao.shop.utils.PayHelper;
import com.tangchao.shop.vo.CustomerAddressVO;
import com.tangchao.shop.vo.UserVO;
import com.tangchao.user.service.CustomerService;
import com.tangchao.user.service.UserService;
import com.tangchao.web.annotation.LoginUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.*;

@RequestMapping("/user")
@RestController
@Api(value = "商城用户模块", tags = {"商城用户模块"})
public class UserController {

    @Autowired
    public CustomerService customerService;

    @Autowired
    private UserService userService;

    @Autowired
    private SendSmsMessageService smsMessageService;

    @Autowired
    private ShopOrderService shopOrderService;

    @ApiOperation(value = "用户账号密码登陆")
    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        return ResponseEntity.ok(customerService.queryUserByUserNameAndPassword(username, password));
    }


    /**
     * 发送短信
     *
     * @param phone
     * @return
     */
    @ApiOperation(value = "发送短信")
    @GetMapping("/code")
    public ResponseEntity<Void> sendCode(String phone) {
        smsMessageService.sendSMSTemplate(phone, SmsTemplateTypeConstant.REGISTER_CODE,null);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 用户注册
     *
     * @param user
     * @param request
     * @return
     */
    @ApiOperation(value = "用户注册")
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserVO user, HttpServletRequest request) {
        return ResponseEntity.ok(customerService.register(user, request));
    }

    /**
     * 检验手机号是否存在
     *
     * @param phone
     * @return
     */
    @ApiOperation(value = "检验手机号是否存在(1:已存在，0不存在)")
    @GetMapping("/checkUserPhone")
    public ResponseEntity<Integer> checkUserPhone(@RequestParam("phone") String phone) {
        return ResponseEntity.ok(customerService.checkUserPhone(phone));
    }

    @ApiOperation(value = "获取个人信息")
    @PostMapping("/getUserInfo")
    public ResponseEntity<UserDTO> getUserInfo(@LoginUser Long userCode) {
        return ResponseEntity.ok(userService.getShopUserDetail(userCode));
    }

    @ApiOperation(value = "获取用户地址")
    @PostMapping("/getCustomerAddress")
    public ResponseEntity<List<CustomerAddress>> getCustomerAddress(@LoginUser Long userCode) {
        return ResponseEntity.ok(customerService.getCustomerAddressList(userCode));
    }

    @ApiOperation(value = "添加用户地址")
    @PostMapping("/addCustomerAddress")
    public ResponseEntity<Void> addCustomerAddress(@LoginUser Long userCode, @RequestBody CustomerAddressVO address) {
        customerService.addCustomerAddress(userCode, address);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "删除用户地址")
    @PostMapping("/deleteCustomerAddress")
    public ResponseEntity<Void> deleteCustomerAddress(@LoginUser Long userCode,@ApiParam(value = "ids",name = "ids") @RequestBody Map<String,Object> data) {
        customerService.deleteCustomerAddress(userCode, data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @ApiOperation(value = "用户分享赚钱")
    @PostMapping("/share")
    public ResponseEntity<Map<String, Object>> share(@LoginUser Long userCode,
                                                     @ApiParam(name = "inviteId", value = "用户邀请码")
                                                     @RequestParam(value = "inviteId", required = false) String inviteId) {
        Map<String, Object> resultMap = new HashMap<>();
        if (userCode == null) {
            if (StringUtil.isEmpty(inviteId)) {
                resultMap.put("data", "401");
            } else {
                resultMap.put("data", "inviteId");
            }
        } else {
            CustomerEmployTiXianRecord employTiXianRecord = customerService.findEmploySumByState(userCode);
            resultMap.put("data", employTiXianRecord);
        }
        return ResponseEntity.ok(resultMap);
    }

    @ApiOperation(value = "获取用户邀请码")
    @GetMapping("/getUserInviteCode")
    public ResponseEntity<String> getUserInviteCode(@LoginUser Long userCode) {
        return ResponseEntity.ok(customerService.getUserInviteCode(userCode));
    }

    /**
     * 分享二维码生成
     * @param userCode
     * @param request
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "分享二维码生成")
    @RequestMapping(value = "/shareQRCode", method = RequestMethod.GET)
    public ResponseEntity<String> shareQRCode(@LoginUser Long userCode,HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(customerService.shareQRCode(userCode,request));
    }

    @ApiOperation("忘记密码发送短信")
    @GetMapping("/forgetPassSendCode")
    public ResponseEntity<Void> forgetPassSendCode(@ApiParam(value = "手机号码",name ="phone") @RequestParam(value = "phone")String phone) {
        smsMessageService.forgetPassSendCode(phone);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("忘记密码")
    @PostMapping("/forgetPass")
    @ResponseBody
    public ResponseEntity<Void> forgetPass(@ApiParam(value = "手机号码",name ="phone") @RequestParam(value = "phone")String phone,
                                           @ApiParam(value = "用户新密码",name ="pwd") @RequestParam(value = "pwd")String pwd,
                                           @ApiParam(value = "验证码",name ="code") @RequestParam(value = "code")String code) {
        customerService.forgetPass(pwd,code,phone);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("手机验证码登录")
    @PostMapping("/phoneLogin")
    @ResponseBody
    public ResponseEntity<UserDTO> phoneLogin(@ApiParam(value = "手机号码",name ="phone") @RequestParam(value = "phone")String phone,
                                           @ApiParam(value = "验证码",name ="code") @RequestParam(value = "code")String code) {
        return ResponseEntity.ok(customerService.phoneLogin(phone,code));
    }

    @ApiOperation("第三方支付")
    @GetMapping("/userPaymentByOtherPlatform")
    public ResponseEntity<Map<String,Object>> userPaymentByOtherPlatform(@LoginUser Long userCode,@ApiParam(value = "金额",name ="price") @RequestParam(value = "price") Double price,
                                              @ApiParam(value = "支付类型(1：支付宝；2：微信支付)",name ="isType") @RequestParam(value = "isType") Integer isType) {
        return ResponseEntity.ok(customerService.userRecharge(userCode,price,isType));
    }

    @ApiOperation("第三方支付回调接口")
    @RequestMapping("/userPaymentNotifyPay")
    public ResponseEntity<String> notifyPay(HttpServletRequest request, HttpServletResponse response, GLpayApi payAPI) {
        return ResponseEntity.ok(customerService.userPaymentNotifyPay(payAPI));
    }

    @ApiOperation("第三方支付-userPaymentByCoCo")
    @GetMapping("/userPaymentByCoCo")
    public ResponseEntity<Map<String,Object>> userPaymentByCoCo(@LoginUser Long userCode,@ApiParam(value = "金额",name ="price") @RequestParam(value = "price") Double price,
                                                             @ApiParam(value = "支付类型(1：支付宝；2：微信支付)",name ="isType") @RequestParam(value = "isType") Integer isType) {
        return ResponseEntity.ok(customerService.userPaymentByCoCo(userCode,price,isType));
    }

    @ApiOperation("第三方支付回调接口")
    @RequestMapping("/userPaymentNotifyPay_CoCo")
    public ResponseEntity<String> userPaymentNotifyPay_CoCo(HttpServletRequest request, HttpServletResponse response, NotifyApi notifyApi) {
        return ResponseEntity.ok(customerService.userPaymentNotifyPay_CoCo(notifyApi));
    }

    @ApiOperation("第三方支付-userPaymentByCPNP")
    @GetMapping("/userPaymentByCPNP")
    public ResponseEntity<Map<String,Object>> userPaymentByCPNP(@LoginUser Long userCode,@ApiParam(value = "金额",name ="price") @RequestParam(value = "price") Double price) {
        return ResponseEntity.ok(customerService.userPaymentByCPNP(userCode,price));
    }

    @ApiOperation("第三方支付回调接口")
    @RequestMapping(value="/userPaymentNotifyPay_CPNP",method=RequestMethod.POST)
    public ResponseEntity<String> userPaymentNotifyPay_CPNP(HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(customerService.userPaymentNotifyPay_CPNP(request));
    }


    @ApiOperation("支付二维码")
    @RequestMapping("/userPayQRCode")
    public ResponseEntity<String> userPayQRCode(@RequestParam("urlText") String urlText) throws Exception {
        return ResponseEntity.ok(customerService.userPayQRCode(urlText));
    }


    @ApiOperation("支付成功页面")
    @RequestMapping("/paySuccessView")
    public ResponseEntity<Void> paySuccessView(@RequestParam(value = "money",required = false) Double money,HttpServletResponse response) throws IOException {
        customerService.paySuccessView(money,response);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @ApiOperation("话费充值接口")
    @GetMapping("/mobileRecharge")
    public ResponseEntity<Void> mobileRecharge(@LoginUser Long userCode,@ApiParam(value = "充值金额",name = "money") @RequestParam(value = "money") Integer money) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        customerService.mobileRecharge(userCode,money);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("话费充值回调接口")
    @RequestMapping(value = "/mobileRechargeNotify",method = RequestMethod.POST)
    public ResponseEntity<String> mobileRechargeNotify(HttpServletRequest request) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return ResponseEntity.ok(customerService.mobileRechargeNotify(request));
    }


    @RequestMapping("/pay")
    @ResponseBody
    public Map<String, Object> pay(HttpServletRequest request, float price, int istype) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        SortedMap<Object, Object> parameters = new TreeMap<>();
        parameters.put("price", price+"");
        parameters.put("istype", istype+"");
        parameters.put("orderid", PayHelper.getOrderIdByUUId());
        parameters.put("orderuid", "您自己想要传输的ID");
        parameters.put("goodsname", "您自己的商品名称");
        parameters.put("notify_url", "http://www.demo.com/paynotify.php");
        parameters.put("return_url", "http://www.demo.com/payreturn.php");
        parameters.put("token", "yLp44i3ZE4y45eDW4wLt5et43sPWW5wE");



        Map<String, Object> map = new HashMap<>();
        map.put("price", price+"");
        map.put("istype", istype+"");
        map.put("orderid", PayHelper.getOrderIdByUUId());
        map.put("orderuid", "您自己想要传输的ID");
        map.put("goodsname", "您自己的商品名称");
        map.put("notify_url", "http://api.rn193.cn/user/userPaymentNotifyPay");
        map.put("return_url", "http://www.rn193.cn/#/pages/yygpage/success");
        map.put("uid", "7832");


        //resultMap.put("key", PayHelper.getKey(map));

        resultMap.put("key", PayHelper.createKEY("UTF-8",parameters,"7832"));

        resultMap.put("price", price);
        resultMap.put("istype", istype);
        resultMap.put("orderid", PayHelper.getOrderIdByUUId());
        resultMap.put("orderuid", "您自己想要传输的ID");
        resultMap.put("goodsname", "您自己的商品名称");
        resultMap.put("uid", "7832");
        resultMap.put("notify_url", "http://www.demo.com/paynotify.php");
        resultMap.put("return_url", "http://www.demo.com/payreturn.php");
        //resultMap.put("data", PayHelper.payOrderMap(remoteMap));
        //customerService.testpay();
        return resultMap;
    }

    @ApiOperation("用户签到")
    @GetMapping("/addCustomerSignIn")
    public ResponseEntity<Map<String,Object>> addCustomerSignIn() throws ParseException {
        return ResponseEntity.ok(customerService.addCustomerSignIn());
    }

    @ApiOperation("判断用户是否可以签到(返回大于等于3不可以签到)")
    @GetMapping("/getCustomerSignIn")
    public ResponseEntity<Map<String,Object>> getCustomerSignIn() throws ParseException {
        return ResponseEntity.ok(customerService.getCustomerSignIn());
    }

    @ApiOperation("积分充值")
    @GetMapping("/customerRechargeByBillplz")
    public ResponseEntity<Map<String,String>> customerRechargeByBillplz(@LoginUser Long userCode,@ApiParam(value = "充值金额",name = "money") @RequestParam(value = "money") Double money,HttpServletRequest request){
        return  ResponseEntity.ok(shopOrderService.payOrderByBillplz(userCode,request,money));
    }

    /**
     * 脸书登录
     *
     * @param code
     *            仅能使用一次的code，用来获取access_token
     * @throws Exception
     */
    @GetMapping("/facebook")
    public String facebookLogin(String code, HttpServletRequest request,HttpServletResponse response) throws Exception {
        String rootPath = request.getContextPath();
        String serverName =request.getServerName();
        if(serverName==null || serverName=="") {
            serverName = "onecityonline";
        }
        Map<String, String> accessTokenInfo = FaceBookUtils.getAccessTokenInfo(code, rootPath,serverName);
        String accessToken = accessTokenInfo.get("access_token");//取出token
        Map<String, String> fbUserInfoMap = FaceBookUtils.userInfoApiUrl(accessToken);
        String facebookId = null;
        if(fbUserInfoMap !=null ) {//授权成功
            facebookId = fbUserInfoMap.get("id");
            //查询用户是否存在
//            Customer customer = new Customer();
//            Long customerId=customerTokenService.selectCustomerIdByFacebookId(fbUserInfoMap.get("id"));
            if (true) {//用户存在
//                customer = this.customerService.findCustomerById(customerId);// 查询会员
//                if(customer == null) {
//                    customer = createFaceBookCustomer(fbUserInfoMap);// 创建facebook会员
//                    customer = (Customer) this.customerService.createCustomer(customer).getData();
//                    this.customerTokenService.updateCustomerIdByfacebookId(customer.getId(), fbUserInfoMap.get("id"));// 更新令牌信息
//                }
//                customer.setLoginToken(RandomUtil.generateLongByDateTime(3));// 生成登录令牌
//                customerLogin(request, response, customer);// 会员登录
                return "success";
            }else {//用户不存在   需要创建
//                customer = createFaceBookCustomer(fbUserInfoMap);
//                customer = (Customer) this.customerService.createCustomer(customer).getData();
//                CustomerToken token = new CustomerToken();
//                token.setCustomerId(customer.getId());
//                token.setFacebookId(facebookId);
//                this.customerTokenService.createCustomerToken(token);// 创建令牌信息
//                customer.setLoginToken(RandomUtil.generateLongByDateTime(3));// 生成登录令牌
//                customerLogin(request, response, customer);// 会员登录
//                return modelAndView;
            }
        }
        return "success";
    }

}