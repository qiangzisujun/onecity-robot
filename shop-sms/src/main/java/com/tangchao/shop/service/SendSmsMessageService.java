package com.tangchao.shop.service;

import com.github.pagehelper.PageInfo;
import com.tangchao.shop.pojo.SmsInterface;
import com.tangchao.shop.pojo.SmsTemplate;
import com.tangchao.shop.pojo.SmsType;

import java.util.Map;

public interface SendSmsMessageService {

    void forgetPassSendCode(String phone);

    /**
     * 发送短信模板
     * @param mobile 电话号码
     * @param smsType 短信类型  登录，注册，忘记密码
     * @param goodsName 中奖短信有商品名称
     */
    void sendSMSTemplate(String mobile,String smsType,String goodsName);


    PageInfo getCmsInfo(Long userId);

    void updateSmsTypeList(Long userId, SmsType type);

    void deleteSmsTypeById(Long userId, Map<String, Object> data);

    void insertSmsType(Long userId, Map<String, Object> data);

    PageInfo getSmsTemplateList(Long userId);

    void updateSmsTemplate(Long userId, SmsTemplate template);

    void deleteSmsTemplate(Long userId, Map<String, Object> data);

    void insertSmsTemplate(Long userId, SmsTemplate template);

    PageInfo getSmsInterfaceList(Long userId);

    void updateSmsInterfaceList(Long userId, SmsInterface smsInterface);

    void deleteSmsInterfaceList(Long userId, Map<String, Object> data);

    void insertSmsInterfaceList(Long userId, SmsInterface smsInterface);
}
