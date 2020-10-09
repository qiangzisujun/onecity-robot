package com.tangchao.shop.utils;


import cn.hutool.json.JSON;
import com.tangchao.common.utils.HttpUtil;
import com.tangchao.common.utils.JsonUtils;

import java.net.URLEncoder;
import java.util.Map;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2020/9/25 16:34
 */
public class FaceBookUtils {

    //应用编号
    private static String ClientID = "3366272240129528";
    //应用秘钥
    private static String ClientSecret = "28a1ee8959515997815bac46b6e967bd";
    //获取访问口令
    private static String getTokenUrl = "https://graph.facebook.com/oauth/access_token";
    //获取用户信息
    private static String getUserInfoUrl = "https://graph.facebook.com/me";
    //验证口令
    private static String verifyUrl = "https://graph.facebook.com/debug_token";
    //获取临时口令
    private static String codeUrl = "https://www.facebook.com/v2.8/oauth/access_token";
    //表示取得用户信息的权限范围
    private static String scope = "user_about_me,email,read_stream";

    /**
     * 获取网页授权access_token信息
     *
     * @param code
     *            code作为换取access_token的票据
     * @return Map
     */
    public static Map<String, String> getAccessTokenInfo(String code, String rootPath, String serverName) throws Exception {
        String url="https://36n0828199.imdo.co/user/facebook";
        StringBuilder param = new StringBuilder();
        param.append("client_id=" + ClientID);
        param.append("&redirect_uri="+ URLEncoder.encode(url,"utf-8"));
        param.append("&client_secret=" + ClientSecret);
        //param.append("&code=" + code);
        String resultJson = HttpUtil.sendGet(getTokenUrl, param.toString());
        return JsonUtils.parse(resultJson, Map.class);
    }


    /**
     * 使用token 进行授权登录
     * @param accessToken
     * @return
     * @throws Exception
     */
    public static Map<String, String> userInfoApiUrl (String accessToken) throws Exception {
        StringBuilder param = new StringBuilder();
        param.append("access_token=" +accessToken);
        String resultJson = HttpUtil.sendGet(getUserInfoUrl, param.toString());
        return JsonUtils.parse(resultJson,Map.class);
    }
}
