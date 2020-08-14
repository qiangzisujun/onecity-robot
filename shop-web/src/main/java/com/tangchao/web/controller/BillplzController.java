package com.tangchao.web.controller;


import cn.hutool.json.JSONObject;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.shop.interceptor.UserInterceptor;
import com.tangchao.shop.mapper.CustomerMapper;
import com.tangchao.shop.params.WebhookParam;
import com.tangchao.shop.pojo.Customer;
import com.tangchao.shop.pojo.UserInfo;
import com.tangchao.shop.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @Class BillplzController
 * @Description TODO Billplz马来西亚支付接口
 * @Author Aquan
 * @Date 2020/5/29 13:45
 * @Version 1.0
 **/
@Slf4j
@RequestMapping(value = "/api/pay/billplz")
@RestController
public class BillplzController {

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private PayService payService;

    @RequestMapping(value = "/createBill",method = RequestMethod.GET)
    public ResponseEntity createBill(HttpServletRequest request, @RequestParam(value = "money") BigDecimal money) {

        String contextPath = request.getServerName();
        String baseUrl = "http://" + contextPath.trim();

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
        //String encoding = encoder.encodeToString(("fc821d48-5f13-4929-97c2-31c57fd33f4f:").getBytes());
        String encoding = encoder.encodeToString(("cdc4f58c-1a46-433c-ac9f-eb2de906c171:").getBytes());// 沙盒环境

        //HttpPost httppost = new HttpPost("https://www.billplz.com/api/v3/bills");
        HttpPost httppost = new HttpPost("https://www.billplz-sandbox.com/api/v3/bills"); // 沙盒环境
        httppost.setHeader("Authorization", "Basic " + encoding);
        try {
            httppost.setEntity(new UrlEncodedFormEntity(getData(money, userInfo.getUserMobile(), baseUrl)));
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

        return ResponseEntity.ok(url);

    }

    // TODO: 2019/12/13 S-caHZmB_KjGJRLsgJ4cHjCA
    @RequestMapping(value = "/webhook",method = RequestMethod.POST)
    public void webhook(WebhookParam webhookParam,HttpServletRequest request) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        payService.webhook(webhookParam,request);
    }


    // TODO: 2019/12/13 S-caHZmB_KjGJRLsgJ4cHjCA
    @RequestMapping(value = "/userPaymentNotifyByWebhook",method = RequestMethod.POST)
    public void userPaymentNotifyByWebhook(WebhookParam webhookParam,HttpServletRequest request) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        payService.userPaymentNotifyByWebhook(webhookParam,request);
    }


    public static List<NameValuePair> getData(BigDecimal money, String mobile, String baseUrl) {
        BigDecimal amount = money.multiply(new BigDecimal("100"));
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("collection_id", "utogvfxv"));
        //urlParameters.add(new BasicNameValuePair("collection_id", "v3qcsqjm"));//沙盒环境
        urlParameters.add(new BasicNameValuePair("description", "Test callback"));
        urlParameters.add(new BasicNameValuePair("email", "853029827@qq.com"));
        urlParameters.add(new BasicNameValuePair("mobile", mobile));
        urlParameters.add(new BasicNameValuePair("name", "Michael API V3"));
        urlParameters.add(new BasicNameValuePair("amount", amount.toString()));
        urlParameters.add(new BasicNameValuePair("callback_url", baseUrl + "/pay/billplz/webhook"));
        urlParameters.add(new BasicNameValuePair("redirect_url", baseUrl + "/user/home"));
        return urlParameters;
    }


    public static Boolean check(WebhookParam webhookParam) {
        String data = webhookParam.toString();
        String key = "S-Ite9LKMFqC2IEk148hqsQg";
        // String key = "S-caHZmB_KjGJRLsgJ4cHjCA";//沙盒环境
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

}
