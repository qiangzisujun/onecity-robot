package com.tangchao.shop.serviceImpl;

import com.github.pagehelper.PageInfo;
import com.tangchao.common.constant.SmsTemplateTypeConstant;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.common.utils.NumberUtils;
import com.tangchao.common.utils.StringUtil;
import com.tangchao.shop.mapper.CustomerMapper;
import com.tangchao.shop.mapper.SmsInterfaceMapper;
import com.tangchao.shop.mapper.SmsTemplateMapper;
import com.tangchao.shop.mapper.SmsTypeMapper;
import com.tangchao.shop.pojo.*;
import com.tangchao.shop.service.SendSmsMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class SendSmsMessageServiceImpl implements SendSmsMessageService {

    private static final String KEY_PREFIX = "user:verify:phone";

    private  static final long SMS_MIN_INTERVAL_IN_MILLIS=80000;

    private static URL url;

    static {
        try {
            url = new URL("http://sms.izjun.cn/v2sms.aspx");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private SmsInterfaceMapper smsInterfaceMapper;

    @Autowired
    private SmsTemplateMapper smsTemplateMapper;

    @Autowired
    private SmsTypeMapper smsTypeMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void sendSMSTemplate(String mobile,String smsType,String goodsName) {
        String key=KEY_PREFIX+mobile;
        //读取时间
        String lastTime = redisTemplate.opsForValue().get(key);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(lastTime)){
            Long last=Long.valueOf(lastTime);
            if (System.currentTimeMillis()-last<SMS_MIN_INTERVAL_IN_MILLIS){
                log.info("[短信服务] 发送短信验证码频率过高，手机号：{}",mobile);
                throw new CustomerException(ExceptionEnum.SMS_FREQUENTLY);
            }
        }
        try {
            //获取发送接口
            //  创建查询对象
            Example example = new Example(SmsInterface.class);
            Example.Criteria criteria = example.createCriteria();
            //  设置查询条件
            criteria.andEqualTo("flag",1);
            List<SmsInterface> interfaceList = smsInterfaceMapper.selectByExample(example);
            SmsInterface smsInterface=interfaceList.get(0);
            //判断是否有存在的接口
            if(smsInterface==null) {
                //  未配置短信接口
                log.error(" 未配置短信接口");
                throw new CustomerException(ExceptionEnum.SMS_SEND_ERROR);
            }
                //  获取短信登陆模板
            SmsTemplate template=new SmsTemplate();
            template.setFlag(1);
            template.setSmsTypeCode(smsType);
            List<SmsTemplate> templateList=smsTemplateMapper.select(template);
            template =templateList.get(0);
            if (null == template || StringUtils.isEmpty(template.getSmsContent())){
                log.error("  未配置短信模板");
                throw new CustomerException(ExceptionEnum.SMS_SEND_ERROR);
            }

            String text="";
            if (SmsTemplateTypeConstant.WINNING_PRIZE.equals(smsType)){         //  中奖信息
                //获取中奖信息短信模板
                //填充商品名称
                text = MessageFormat.format(template.getSmsContent(),goodsName);
                //  填充商品名称
                template.setSmsContent(text);
                template.setMobile(mobile);
                int count=this.sendSmsMessage(smsInterface,template);
                if (count!=1){
                    throw new CustomerException(ExceptionEnum.SMS_SEND_ERROR);
                }
            }else if (SmsTemplateTypeConstant.REGISTER_CODE.equals(smsType)){ //登录注册手机验证码
                //生成验证码
                String code = NumberUtils.generateCode(6);
                text = MessageFormat.format(template.getSmsContent(),code);
                template.setSmsContent(text);
                template.setMobile(mobile);
                int count=this.sendSmsMessage(smsInterface,template);
                if (count!=1){
                    throw new CustomerException(ExceptionEnum.SMS_SEND_ERROR);
                }
                //生成key
                Map<String, String> msg = new HashMap<>();
                msg.put("phone", mobile);
                msg.put("code", code);
                //保存验证码 5分钟有效
                redisTemplate.opsForValue().set(key, code, 2, TimeUnit.MINUTES);
            }else if(SmsTemplateTypeConstant.FORGET_PASSWORD_CODE.equals(smsType)){//忘记密码
                int count = this.sendSmsMessage(smsInterface, template);
                if (count != 1) {
                    throw new CustomerException(ExceptionEnum.SMS_SEND_ERROR);
                }
                //生成验证码
                String code = NumberUtils.generateCode(6);
                text = MessageFormat.format(template.getSmsContent(),code);
                //生成key
                Map<String, String> msg = new HashMap<>();
                msg.put("phone", mobile);
                msg.put("code", code);
                //保存验证码 5分钟有效
                redisTemplate.opsForValue().set(key, code, 2, TimeUnit.MINUTES);
            }else if (SmsTemplateTypeConstant.REMITTANCE.equals(smsType)){          //汇款通知
                text = MessageFormat.format(template.getSmsContent(),goodsName);
                //  填充商品名称
                template.setSmsContent(text);
                template.setMobile(mobile);
                int count=this.sendSmsMessage(smsInterface,template);
                if (count!=1){
                    throw new CustomerException(ExceptionEnum.SMS_SEND_ERROR);
                }
            }else{
                throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public PageInfo getCmsInfo(Long userId) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        SmsType type=new SmsType();
        type.setFlag(0);
        List<SmsType> typeList=smsTypeMapper.select(type);
        return new PageInfo(typeList);
    }

    @Override
    public void updateSmsTypeList(Long userId, SmsType type) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        if (StringUtils.isBlank(type.getId().toString())){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        SmsType smsType=smsTypeMapper.selectByPrimaryKey(type.getId());
        if (smsType==null){
            throw new CustomerException(ExceptionEnum.SMSType_NOT_FOND);
        }

        int count=smsTypeMapper.updateByPrimaryKeySelective(type);
        if(count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void deleteSmsTypeById(Long userId, Map<String, Object> data) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        String id=data.get("id").toString();
        if (StringUtils.isBlank(id)){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        SmsType smsType=smsTypeMapper.selectByPrimaryKey(id);
        if (smsType==null){
            throw new CustomerException(ExceptionEnum.SMSType_NOT_FOND);
        }
        smsType.setFlag(-1);
        smsType.setLastModifyId(userId);
        smsType.setLastModifyTime(new Date());
        int count=smsTypeMapper.updateByPrimaryKeySelective(smsType);
        if(count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void insertSmsType(Long userId, Map<String, Object> data) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        String typeName=data.get("smsTypeName").toString();//类型名称
        String typeIdentification=data.get("smsTypeCode").toString();//类型标识
        SmsType type=new SmsType();
        type.setLastModifyTime(new Date());
        type.setLastModifyId(userId);
        type.setFlag(0);
        type.setCreateId(userId);
        type.setSmsTypeCode(typeIdentification);
        type.setSmsTypeName(typeName);
        int count=smsTypeMapper.insertSelective(type);
        if(count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public PageInfo getSmsTemplateList(Long userId) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        SmsTemplate template=new SmsTemplate();
        template.setFlag(1);
        List<SmsTemplate> typeList=smsTemplateMapper.select(template);
        return new PageInfo(typeList);
    }

    @Override
    public void updateSmsTemplate(Long userId, SmsTemplate template) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        if (StringUtils.isBlank(template.getId().toString())){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        SmsTemplate smsType=smsTemplateMapper.selectByPrimaryKey(template.getId());
        if (smsType==null){
            throw new CustomerException(ExceptionEnum.SMSType_NOT_FOND);
        }

        int count=smsTemplateMapper.updateByPrimaryKeySelective(template);
        if(count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void deleteSmsTemplate(Long userId, Map<String, Object> data) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        String id=data.get("id").toString();
        if (StringUtils.isBlank(id)){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        SmsTemplate smsType=smsTemplateMapper.selectByPrimaryKey(id);
        if (smsType==null){
            throw new CustomerException(ExceptionEnum.SMSType_NOT_FOND);
        }
        smsType.setFlag(-1);
        smsType.setLastModifyId(userId);
        smsType.setLastModifyTime(new Date());
        int count=smsTemplateMapper.updateByPrimaryKeySelective(smsType);
        if(count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void insertSmsTemplate(Long userId, SmsTemplate template) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        template.setFlag(1);
        template.setLastModifyId(userId);
        template.setLastModifyTime(new Date());
        template.setCreateTime(new Date());
        template.setCreateId(userId);
        int count=smsTemplateMapper.insertSelective(template);
        if(count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public PageInfo getSmsInterfaceList(Long userId) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        Example example = new Example(SmsInterface.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andNotEqualTo("flag", -1);
        List<SmsInterface> typeList=smsInterfaceMapper.selectByExample(example);
        return new PageInfo(typeList);
    }

    @Override
    public void updateSmsInterfaceList(Long userId, SmsInterface smsInterface) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        if (StringUtils.isBlank(smsInterface.getId().toString())){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        SmsInterface smsType=smsInterfaceMapper.selectByPrimaryKey(smsInterface.getId());
        if (smsType==null){
            throw new CustomerException(ExceptionEnum.SMSType_NOT_FOND);
        }

        int count=smsInterfaceMapper.updateByPrimaryKeySelective(smsInterface);
        if(count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void deleteSmsInterfaceList(Long userId, Map<String, Object> data) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        String id=data.get("id").toString();
        if (StringUtils.isBlank(id)){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        SmsInterface smsType=smsInterfaceMapper.selectByPrimaryKey(id);
        if (smsType==null){
            throw new CustomerException(ExceptionEnum.SMSType_NOT_FOND);
        }
        smsType.setFlag(-1);
        smsType.setLastModifyId(userId);
        smsType.setLastModifyTime(new Date());
        int count=smsInterfaceMapper.updateByPrimaryKeySelective(smsType);
        if(count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void insertSmsInterfaceList(Long userId, SmsInterface smsInterface) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        smsInterface.setFlag(1);
        smsInterface.setLastModifyId(userId);
        smsInterface.setLastModifyTime(new Date());
        smsInterface.setCreateId(userId);
        smsInterface.setCreateTime(new Date());
        int count=smsInterfaceMapper.insertSelective(smsInterface);
        if(count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    private int sendSmsMessage(SmsInterface smsInterface,SmsTemplate template) throws IOException, NoSuchAlgorithmException, DocumentException {
        //  根据供应商发送短信
        String supplierName = smsInterface.getSupplierName();
        if("etracker".equals(supplierName)) {
            return this.mlxySend(smsInterface,template);
        }else if("淘惠".equals(supplierName)){
            return this.taoHui(smsInterface,template);
        }
        //  接口正在开发中
        return  0;
    }

    private int taoHui(SmsInterface smsInterface,SmsTemplate template) throws NoSuchAlgorithmException, IOException, DocumentException {
        String msg = "【" +
                template.getSmsSignName() +
                "】" +
                template.getSmsContent();
        Date date = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddhhmmss");
        String dateStr = sf.format(date);
        //String str  = smsInterface.getAccessKeyId() + smsInterface.getAccessKeySecret() + dateStr;
        String str  = smsInterface.getAccessKeySecret()+ smsInterface.getAccessKeySecret() + dateStr;   // 潮汇
        //String str  = "theghy" + smsInterface.getAccessKeySecret() + dateStr;  // 新团惠
        String sign = md5(str).toLowerCase();


        String send = "action=send&userid="+Integer.parseInt(smsInterface.getAccessKeyId())+"&timestamp="+dateStr+"&sign="+sign+"&mobile="+template.getMobile()+"&content=" +msg+"&sendTime=&extno=";
        HttpURLConnection hp = (HttpURLConnection) url.openConnection();
        byte[] b = send.getBytes("utf-8");
        hp.setRequestMethod("POST");
        hp.setDoOutput(true);
        hp.setDoInput(true);
        OutputStream out = hp.getOutputStream();
        out.write(b);
        out.close();
        BufferedReader in = new BufferedReader(new InputStreamReader(hp.getInputStream(),"utf-8"));
        String inputLine;
        //System.out.println("提交短信：");
        StringBuffer sb = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            sb.append(inputLine);
        }
        in.close();
        hp.disconnect();
        Map map = map = StringUtil.xml2map(sb.toString(),false);

        if (null != map){
            //解析响应结果
            if ("ok".equals(map.get("message"))) {
                return 1;
            }
            return 0;
        }
       return 0;
    }

    private static String md5(String encryptStr) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        // md5
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(encryptStr.getBytes("UTF-8"));
        byte[] digest = md.digest();
        StringBuffer md5 = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
            md5.append(Character.forDigit((digest[i] & 0xF0) >> 4, 16));
            md5.append(Character.forDigit((digest[i] & 0xF), 16));
        }
        encryptStr = md5.toString();
        return encryptStr;
    }

    private int mlxySend(SmsInterface smsInterface,SmsTemplate template){
        return 1;
    }

    @Override
    public void forgetPassSendCode(String phone) {
        Customer customer = new Customer();
        customer.setFlag(0);
        customer.setUserMobile(phone);
        Customer newUser = customerMapper.selectOne(customer);
        if (newUser==null){throw new CustomerException(ExceptionEnum.USER_PHONE_NOT_ERROR);}
        String key = KEY_PREFIX + phone;
        Example example = new Example(SmsInterface.class);
        Example.Criteria criteria = example.createCriteria();
        //  设置查询条件
        criteria.andEqualTo("flag", 1);
        List<SmsInterface> interfaceList = smsInterfaceMapper.selectByExample(example);
        SmsInterface smsInterface = interfaceList.get(0);
        if (null == smsInterface) {
            //  未配置短信接口
            log.error(" 未配置短信接口");
            throw new CustomerException(ExceptionEnum.SMS_SEND_ERROR);
        }
        SmsTemplate template = new SmsTemplate();
        template.setFlag(1);
        template.setSmsTypeCode(SmsTemplateTypeConstant.FORGET_PASSWORD_CODE);
        List<SmsTemplate> templateList = smsTemplateMapper.select(template);
        template = templateList.get(0);
        if (null == template || StringUtils.isEmpty(template.getSmsContent())) {
            log.error("  未配置短信模板");
            throw new CustomerException(ExceptionEnum.SMS_SEND_ERROR);
        }
        //生成验证码
        String code = NumberUtils.generateCode(6);
        String text = MessageFormat.format(template.getSmsContent(), code);
        template.setSmsContent(text);
        template.setMobile(phone);
        try {
            int count = this.sendSmsMessage(smsInterface, template);
            if (count != 1) {
                throw new CustomerException(ExceptionEnum.SMS_SEND_ERROR);
            }
            //生成key
            Map<String, String> msg = new HashMap<>();
            msg.put("phone", phone);
            msg.put("code", code);
            //保存验证码 5分钟有效
            redisTemplate.opsForValue().set(key, code, 2, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
