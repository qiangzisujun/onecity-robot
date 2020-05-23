package com.tangchao.shop.utils;

import cn.hutool.json.JSONObject;
import com.tangchao.common.utils.HttpUtil;
import com.tangchao.common.utils.MD5Util;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2020/3/18 17:44
 */
public class PayMobileRecharge {

    private final static String OPENID="JH715a38897e86518a52c046da7874091b";

    private final static String KEY="eafc410f3799ca4b54eb99e4e3b8bb9e";

    private final static String BASE_URL="http://op.juhe.cn/ofpay/mobile/onlineorder";

    public static Map<String,Object> createMobileRecharge(String orderNo,Integer money,String phone) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        HashMap<String,Object> params=new HashMap<>();
        params.put("key",KEY);
        params.put("phoneno",phone);
        params.put("cardnum",money.toString());
        params.put("orderid",orderNo);

        String signStr=OPENID+KEY+phone+money+orderNo;

        params.put("sign", MD5Util.createMD5(signStr));
        String result= HttpUtil.http(BASE_URL,params);
        JSONObject jsonObject = new JSONObject(result);
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("reason",jsonObject.get("reason"));
        return resultMap;
    }
}
