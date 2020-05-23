package com.tangchao.user.serviceImpl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tangchao.common.constant.ConfigkeyConstant;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.shop.mapper.SeoConfigMapper;
import com.tangchao.shop.mapper.UserConfMapper;
import com.tangchao.shop.mapper.WxPayConfMapper;
import com.tangchao.shop.pojo.SeoConfig;
import com.tangchao.shop.pojo.UserConf;
import com.tangchao.shop.pojo.WxPayConf;
import com.tangchao.user.service.CmsConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class CmsConfigServiceImpl implements CmsConfigService {

    private static final String KEY_PREFIX="mall.switch";

    @Autowired
    private UserConfMapper userConfMapper;

    @Autowired
    private WxPayConfMapper wxPayConfMapper;

    @Autowired
    private SeoConfigMapper seoConfigMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Map<String,Object> consultation() {

        Map<String,Object> map=new HashMap<>();

        UserConf conf=this.selectCmsValue("customer.service.wx");
        if (conf!=null){
            map.put("img",conf.getConfValue()) ;
        }
        UserConf conf1=this.selectCmsValue("customer.service.mobile");
        if (conf1!=null){
            map.put("phone",conf1.getConfValue()) ;
        }
        UserConf conf2=this.selectCmsValue("mall.service.working.time");
        if (conf2!=null){
            map.put("date",conf2.getConfValue()) ;
        }
        UserConf conf3=this.selectCmsValue("customer.service.qq");
        if (conf3!=null){
            map.put("qqNumber",conf3.getConfValue()) ;
        }
        UserConf conf4=this.selectCmsValue("customer.recharge.qq");
        if (conf4!=null){
            map.put("rechargeQQ",conf4.getConfValue()) ;
        }
        return map;
    }

    @Override
    public UserConf selectCmsValue(String type) {
        UserConf conf=new UserConf();
        conf.setConfKey(type);
        conf.setFlag(0);
        conf= userConfMapper.selectOne(conf);
        if (conf!=null){
            return conf;
        }
        return null;
    }

    @Override
    public Map<String, Object> getCmsInfo(Long userId) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        UserConf conf=new UserConf();
        conf.setFlag(0);
        List<UserConf> confList=userConfMapper.select(conf);
        Map<String,Object> resultMap=new HashMap<>();
        for (UserConf c:confList){
            if (StringUtils.isBlank(c.getConfValue())){
                resultMap.put(c.getConfKey(),"");
            }else{
                resultMap.put(c.getConfKey(),c.getConfValue());
            }
        }
        return resultMap;
    }

    @Override
    public PageInfo getCmsSetUpInfo(Long userId,Integer pageNo,Integer pageSize) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        PageHelper.startPage(pageNo,pageSize);
        UserConf conf=new UserConf();
        conf.setFlag(0);
        List<UserConf> confList=userConfMapper.select(conf);
        return new PageInfo(confList);
    }

    @Override
    public void updateCmsSetUpInfo(Long userId, UserConf conf) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        if (StringUtils.isBlank(conf.getId().toString())||StringUtils.isBlank(conf.getConfKey())||StringUtils.isBlank(conf.getConfValue())){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        conf.setLastModifyId(userId);
        conf.setLastModifyTime(new Date());
        int count=userConfMapper.updateByPrimaryKeySelective(conf);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void deleteCmsSetUpInfo(Long userId, Map<String,Object> data) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        String id=data.get("id").toString();
        if (StringUtils.isBlank(id)){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        UserConf conf=new UserConf();
        conf.setFlag(0);
        conf.setId(Long.valueOf(id));
        conf=userConfMapper.selectOne(conf);
        if (conf == null){
            throw new CustomerException(ExceptionEnum.CONFIG_NOT_FOND);
        }
        conf.setFlag(-1);
        conf.setLastModifyId(userId);
        conf.setLastModifyTime(new Date());
        int count=userConfMapper.updateByPrimaryKeySelective(conf);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public WxPayConf wxPayInfo(Long userId) {
        WxPayConf conf=new WxPayConf();
        conf.setFlag(0);
        conf.setIsOpen(1);
        conf=wxPayConfMapper.selectOne(conf);
        return conf;
    }

    @Override
    public void updateWxPayInfo(Long userId, WxPayConf conf) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        if (StringUtils.isBlank(conf.getId().toString())){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        int count=wxPayConfMapper.updateByPrimaryKeySelective(conf);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public PageInfo getSeoConfigList(Long userId) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        SeoConfig seoConfig=new SeoConfig();
        seoConfig.setFlag(0);
        List<SeoConfig> seoConfigList=seoConfigMapper.select(seoConfig);
        return new PageInfo(seoConfigList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCmsInfo(Long userId, List<Map<String, Object>> maps) {
        int count=0;
        for (Map<String, Object> entry : maps) {
            for (Map.Entry<String, Object> map : entry.entrySet()) {
                UserConf conf = new UserConf();
                String key =map.getKey();
                String value =map.getValue().toString();
                conf.setConfKey(key);
                conf.setConfValue(value);
                conf.setLastModifyId(userId);
                conf.setLastModifyTime(new Date());
                count=userConfMapper.updateBatchConf(conf);
                /*if (count!=1){
                    throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
                }*/
            }

        }
    }

    @Override
    public void insertCmsSetUpInfo(Long userId, UserConf conf) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        conf.setFlag(0);
        conf.setLastModifyTime(new Date());
        conf.setCreateId(userId);
        conf.setCreateTime(new Date());
        conf.setLastModifyId(userId);
        int count=userConfMapper.insertSelective(conf);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public String getMallSwitch() {
        String key=KEY_PREFIX;
        String config = redisTemplate.opsForValue().get(key);
        if (StringUtils.isBlank(config)){
            UserConf conf=new UserConf();
            conf.setFlag(0);
            conf.setConfKey(ConfigkeyConstant.MALL_Switch);
            conf=userConfMapper.selectOne(conf);
            if (conf==null){//默认为1，保证有数据
                redisTemplate.opsForValue().set(key, "1", 30, TimeUnit.DAYS);
                return "1";
            }
            redisTemplate.opsForValue().set(key, conf.getConfValue(), 30, TimeUnit.DAYS);
            return  conf.getConfValue();
        }else {
            return config;
        }
    }
}
