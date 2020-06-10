package com.tangchao.shop.service;

import com.tangchao.shop.params.WebhookParam;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2020/5/29 15:27
 */
public interface PayService {

    Map<String,String> createBill(HttpServletRequest request, BigDecimal money,String notify);

    void webhook(WebhookParam webhookParam,HttpServletRequest request) throws UnsupportedEncodingException, NoSuchAlgorithmException;

    void userPaymentNotifyByWebhook(WebhookParam webhookParam, HttpServletRequest request);
}
