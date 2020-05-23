package com.tangchao.shop.utils;

import com.tangchao.common.utils.HttpUtil;
import com.tangchao.common.utils.JsonUtils;
import com.tangchao.common.utils.MD5Util;
import com.tangchao.shop.pojo.NotifyApi;
import com.tangchao.shop.pojo.ResultModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/12/12 20:11
 */
public class PayUtil {

    private static final Logger logger = LoggerFactory.getLogger(PayUtil.class);

    public static String UID = "72320";

    public static String NOTIFY_URL = "http://api.rn193.cn/user/userPaymentNotifyPay_CoCo";

    public static String RETURN_URL = "http://www.rn193.cn/#/pages/yygpage/success";

    public static String BASE_URL = "http://www.cocopay.cc/api/create_order";

    public static String TOKEN = "6Dz7aFTdN5AKIbT9R52X7SNIdE2qsbTQ";

    public static Map<String,Object> createOrder(Double totalFee,Integer type,String tradeNo){
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("Mch_id", UID);
        paramMap.put("Notify_url", NOTIFY_URL);
        paramMap.put("Return_url", RETURN_URL);
        paramMap.put("Total_fee",totalFee);
        paramMap.put("Pay_type",payType(type));
        paramMap.put("Out_trade_no",tradeNo);
        paramMap.put("Sign", getSign(paramMap));
        String result= HttpUtil.http(BASE_URL,paramMap);
        ResultModel model=JsonUtils.parse(result,ResultModel.class);
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("data",model);
        return resultMap;
    }

    public static String getSign(Map<String, Object> remoteMap) {
        String key = "";
        if (null != remoteMap.get("Mch_id")) {
            key += "Mch_id="+remoteMap.get("Mch_id")+"&";
        }
        if (null != remoteMap.get("Out_trade_no")) {
            key += "Out_trade_no="+remoteMap.get("Out_trade_no")+"&";
        }
        if (null != remoteMap.get("Total_fee")) {
            key += "Total_fee="+remoteMap.get("Total_fee")+"&";
        }
        if (null != remoteMap.get("Pay_type")) {
            key +="Pay_type="+remoteMap.get("Pay_type")+"&";
        }
        if (null != remoteMap.get("Notify_url")) {
            key += "Notify_url="+remoteMap.get("Notify_url")+"&";
        }
        key += TOKEN;
        return MD5Util.encryption(key);
    }

    public static boolean checkPaySign(NotifyApi notifyApi) {
        String key = "";
        if (notifyApi.getStatus()!=null){
            logger.info("支付回来的订单号状态：" + notifyApi.getStatus());
            key += "Status="+notifyApi.getStatus();
        }
        if (!StringUtils.isBlank(notifyApi.getMch_id())) {
            logger.info("支付回来的商务号：" + notifyApi.getMch_id());
            key += "&Mch_id="+notifyApi.getMch_id();
        }
        if (!StringUtils.isBlank(notifyApi.getOut_trade_no())) {
            logger.info("支付回来的订单号：" + notifyApi.getOut_trade_no());
            key +="&Out_trade_no="+notifyApi.getOut_trade_no();
        }
        if (notifyApi.getTotal_fee()!=null) {
            logger.info("支付回来的价格：" + notifyApi.getTotal_fee());
            key += "&Total_fee="+notifyApi.getTotal_fee();
        }
        if (!StringUtils.isBlank(notifyApi.getPay_type())) {
            logger.info("支付回来的类型：" + notifyApi.getPay_type());
            key += "&Pay_type="+notifyApi.getPay_type();
        }
        logger.info("支付回来的Key：" + notifyApi.getSign());
        key += "&"+TOKEN;
        logger.info("我们自己拼接的Key：" + MD5Util.encryption(key));
        return notifyApi.getSign().equals(MD5Util.encryption(key));
    }

    public static String payType(Integer isType){
        String payTypeStr="";
        switch (isType){
            case 1:
                payTypeStr ="wechat.h5";//微信H5
                break;
            case 2:
                payTypeStr="wechat.native";//微信扫码
                break;
            case 3:
                payTypeStr="alipay.native";//支付宝扫码
                break;
            case 4:
                payTypeStr="alipay.wap";//支付宝WAP
                break;
             default:
                 payTypeStr="wechat.h5";
        }
        return payTypeStr;
    }
}
