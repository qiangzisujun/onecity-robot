package com.tangchao.shop.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.*;


/**
 * ��������
 * ��������̳д��࣬��дcreateSign�������ɡ�
 *
 * @author miklchen
 */
public class RequestHandler {

    /**
     * ���url��ַ
     */
    private String gateUrl;

    /**
     * ��Կ
     */
    private String key;

    /**
     * ����Ĳ���
     */
    private SortedMap parameters;

    /**
     * debug��Ϣ
     */
    private String debugInfo;

    protected HttpServletRequest request;

    protected HttpServletResponse response;
    //private static String parentKey="51528F0E3EF0FAE966992A66D451CB15";

    /**
     * ���캯��
     *
     * @param request
     * @param response
     */
    public RequestHandler(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;

        this.gateUrl = "https://gw.tenpay.com/gateway/pay.htm";
        this.key = "";
        this.parameters = new TreeMap();
        this.debugInfo = "";
    }

    /**
     * ��ʼ������
     */
    public void init() {
        //nothing to do
    }

    /**
     * ��ȡ��ڵ�ַ,�������ֵ
     */
    public String getGateUrl() {
        return gateUrl;
    }

    /**
     * ������ڵ�ַ,�������ֵ
     */
    public void setGateUrl(String gateUrl) {
        this.gateUrl = gateUrl;
    }

    /**
     * ��ȡ��Կ
     */
    public String getKey() {
        return key;
    }

    /**
     * ������Կ
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * ��ȡ����ֵ
     *
     * @param parameter �������
     * @return String
     */
    public String getParameter(String parameter) {
        String s = (String) this.parameters.get(parameter);
        return (null == s) ? "" : s;
    }

    /**
     * ���ò���ֵ
     *
     * @param parameter      �������
     * @param parameterValue ����ֵ
     */
    public void setParameter(String parameter, String parameterValue) {
        String v = "";
        if (null != parameterValue) {
            v = parameterValue.trim();
        }
        this.parameters.put(parameter, v);
    }

    /**
     * �������еĲ���
     *
     * @return SortedMap
     */
    public SortedMap getAllParameters() {
        return this.parameters;
    }

    /**
     * ��ȡdebug��Ϣ
     */
    public String getDebugInfo() {
        return debugInfo;
    }

    /**
     * ��ȡ����������URL
     *
     * @return String
     * @throws UnsupportedEncodingException
     */
    public String getRequestURL(String parentKey) throws UnsupportedEncodingException {
        this.createSign(parentKey);

        StringBuffer sb = new StringBuffer();

        sb.append("<xml>");

        String enc = TenpayUtil.getCharacterEncoding(this.request, this.response);

        Set es = this.parameters.entrySet();

        Iterator it = es.iterator();

        while (it.hasNext()) {

            Map.Entry entry = (Map.Entry) it.next();

            String k = (String) entry.getKey();

            String v = (String) entry.getValue();

            if ("attach".equalsIgnoreCase(k) || "body".equalsIgnoreCase(k) || "sign".equalsIgnoreCase(k)) {

                sb.append("<" + k + ">" + "<![CDATA[" + v + "]]></" + k + ">");

            } else {

                sb.append("<" + k + ">" + v + "</" + k + ">");

            }

        }

        sb.append("</xml>");

        return sb.toString();
    }

    //public void doSend() throws UnsupportedEncodingException, IOException {
//		this.response.sendRedirect(this.getRequestURL());
    //}

    /**
     * ����md5ժҪ,������:���������a-z����,���ֵ�Ĳ���μ�ǩ��
     */
    protected void createSign(String parentKey) {
        StringBuffer sb = new StringBuffer();

        Set es = this.parameters.entrySet();

        Iterator it = es.iterator();

        while (it.hasNext()) {

            Map.Entry entry = (Map.Entry) it.next();

            String k = (String) entry.getKey();

            String v = (String) entry.getValue();

            if (null != v && !"".equals(v)

                    && !"sign".equals(k) && !"key".equals(k)) {

                sb.append(k + "=" + v + "&");

            }

        }

        sb.append("key=" + parentKey); //自己的API密钥      

        String enc = TenpayUtil.getCharacterEncoding(this.request, this.response);

        String sign = MD5Util.MD5Encode(sb.toString(), enc).toUpperCase();
        System.out.println("签名串：" + sb.toString());
        System.out.println("createSign:" + sign);
        this.setParameter("sign", sign);
    }

    public static String createSign(String charSet, SortedMap<Object, Object> parameters, String parentKey) {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
//            try {
//				v=URLEncoder.encode(v, "utf-8");
//			}catch(Exception e){
//				v = (String)entry.getValue();
//			}
            if (null != v && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + parentKey);//xxxxxx换成你的API密钥
        System.out.println("sb.toString():" + sb.toString());
        String sign = MD5Util.MD5Encode(sb.toString(), charSet).toUpperCase();
        return sign;
    }

    /**
     * ����debug��Ϣ
     */
    protected void setDebugInfo(String debugInfo) {
        this.debugInfo = debugInfo;
    }

    protected HttpServletRequest getHttpServletRequest() {
        return this.request;
    }

    protected HttpServletResponse getHttpServletResponse() {
        return this.response;
    }

}
