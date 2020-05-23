package com.tangchao.common.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/12/9 15:31
 */
public class HttpUtil {

    public static String http(String path, Map<String, Object> params) {


        URL url = null;
        try {
            url = new URL(path);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");// 提交模式
            httpURLConnection.setConnectTimeout(10000);//连接超时 单位毫秒
            httpURLConnection.setReadTimeout(10000);//读取超时 单位毫秒
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // 构建请求参数
            StringBuffer sb = new StringBuffer();
            String str="";
            if (params != null) {
                for (Map.Entry<String, Object> e : params.entrySet()) {
                    sb.append(e.getKey());
                    sb.append("=");
                    sb.append(e.getValue());
                    sb.append("&");
                }
                str=sb.substring(0, sb.length()-1);
            }
            //System.out.println("send_url:" + url);
            //System.out.println("send_data:" + str);
            httpURLConnection.connect();
            OutputStream os=httpURLConnection.getOutputStream();
            os.write(str.getBytes("UTF-8"));
            os.flush();

            StringBuilder sb1 = new StringBuilder();
            int httpRspCode = httpURLConnection.getResponseCode();
            if (httpRspCode == HttpURLConnection.HTTP_OK) {
                // 开始获取数据
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb1.append(line);
                }
                br.close();
                return sb1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String Get(String realUrl) {
        try {

            // 传入参数
            //String realUrl = "https://www.okcoin.cn/api/v1/ticker.do?symbol=btc_cny";
            URL url = new URL(realUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 在连接之前设置属性

            // Content-Type实体头用于向接收方指示实体的介质类型，指定HEAD方法送到接收方的实体介质类型，或GET方法发送的请求介质类型
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            // 设置打开与此URLConnection引用的资源的通信链接时使用的指定超时值（以毫秒为单位）
            conn.setConnectTimeout(10000);
            // 将读取超时设置为指定的超时时间，以毫秒为单位。
            // conn.setReadTimeout(60000);
            conn.setRequestMethod("GET");
            // Post 请求不能使用缓存
            conn.setUseCaches(false);

            // 建立连接
            conn.connect();
            // 获取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String result = "";
            while ((line = reader.readLine()) != null) {
                result += line;
            }
            reader.close();
            conn.disconnect();
            return result;
        } catch (MalformedURLException e) {

            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }

        return null;
    }
}
