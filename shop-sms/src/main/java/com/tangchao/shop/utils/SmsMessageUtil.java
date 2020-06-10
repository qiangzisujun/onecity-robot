package com.tangchao.shop.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * @Class SmsMessageUtil
 * @Description TODO http://www.isms.com.my/sms_api.php 短信发送工具类
 * @Author Aquan
 * @Date 2019/11/26 11:36
 * @Version 1.0
 **/
@Component
public class SmsMessageUtil {

    private final static Logger logger = LoggerFactory.getLogger(SmsMessageUtil.class);

    private final static String success = "2000 = SUCCESS";


    /**
     * @Author Aquan
     * @Description //TODO
     * @Date 2019/11/26 15:31
     * @Param mobile 号码， message 发送的信息， user 服务商的账号， password 服务商账号密码
     * @return
     **/
    public static boolean sendMessage(String mobile, String message, String user, String password) {

        try {
            String returnResult = null;
            // String user = "foursidetech"; /*user isms username_用户isms用户名*/
            // String pass = "fourside9699";    /*user isms password_用户密码*/
            // String dstno = "60102213291"; /*You are going compose a message to this destination number._您将向该目的地号码撰写一条消息。*/
            // String str = "testing using normal!no.3"; /*Your message over here_您在这里留言*/
            String msg = message.replace(" ", "%20");//空格转义
            int type = 1; /*for unicode change to 2, normal will the 1._对于unicode更改为2，正常将1。*/
            String sendid = "isms"; /*Malaysia does not support sender id yet._马来西亚尚不支持发件人ID*/
            String[] urlList = {"https://www.isms.com.my/isms_send.php", "https://ww2.isms.com.my/isms_send.php", "https://www.vocotext.com/isms_send.php"};/* Send data*/

            for (int i=0; i<urlList.length; i++) {

                URL myUrl = new URL(urlList[i] + "?un=" + user + "&pwd=" + password + "&dstno=" + mobile + "&msg=" + msg + "&type=" + type + "&sendid=" + sendid + "&agreedterm=YES");
                URLConnection conn = myUrl.openConnection();
                conn.setDoOutput(true);/* Get the response*/
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {/* Print the response output...*/
                    logger.warn(line);
                    System.out.println(line);
                    returnResult = line;
                }
                // System.out.println("结果" line.equals(success));
                if (returnResult.equals(success)) {
                    logger.warn("发送成功!" + urlList[i]);
                    break;
                } else {
                    logger.warn("发送失败!" + urlList[i]);
                    // 判断是否最后一次也发送失败，就说明短信无法发送。
                    if (i == urlList.length-1) {
                        logger.error("短信服务商服务器异常无法请求发送短信！");
                        return false;
                    }
                }
                rd.close();
                // System.out.println(myUrl);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return true;
    }

}
