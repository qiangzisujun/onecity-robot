package com.tangchao.user.serviceImpl;

import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageHelper;
import com.tangchao.common.constant.ConfigkeyConstant;
import com.tangchao.common.constant.DataSrcConstant;
import com.tangchao.common.constant.PayStatusConstant;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.shop.interceptor.UserInterceptor;
import com.tangchao.shop.mapper.CustomerInfoMapper;
import com.tangchao.shop.mapper.CustomerScoreDetailMapper;
import com.tangchao.shop.mapper.UserSignRecordMapper;
import com.tangchao.shop.pojo.CustomerScoreDetail;
import com.tangchao.shop.pojo.UserConf;
import com.tangchao.shop.pojo.UserInfo;
import com.tangchao.shop.pojo.UserSignRecord;
import com.tangchao.user.service.CmsConfigService;
import com.tangchao.user.service.CustomerSignService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2020/6/19 14:35
 */
@Slf4j
@Service
public class CustomerSignServiceImpl implements CustomerSignService {

    @Autowired
    private UserSignRecordMapper userSignRecordMapper;

    @Autowired
    private CmsConfigService configService;

    @Autowired
    private CustomerScoreDetailMapper customerScoreDetailMapper;

    @Autowired
    private CustomerInfoMapper customerInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String,Object> insertCustomerSignRecord() {
        //获取用户登录
        UserInfo user = UserInterceptor.getUserInfo();
        if (user== null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        Random random=new Random();
        //获取签到送积分数量
        UserConf conf=configService.selectCmsValue(ConfigkeyConstant.MALL_USER_SIGN_GIVE_SCORE);
        String[] array1 = conf.getConfValue().split("/");

        //每天随机金额
        int integral=0;
        if (array1.length == 2) {
            int startX = Integer.parseInt(array1[0]);
            int endY = Integer.parseInt(array1[1]);
            integral=random.nextInt(endY)%(endY-startX+1)+startX;
        } else {
            log.error("配置格式：X/Y,X为充值金额,Y为赠送金额");
        }


        //第7天随机金额
        UserConf conf7=configService.selectCmsValue(ConfigkeyConstant.MALL_USER_SIGN_GIVE_SCORE_Seven_DAYS);
        String[] array7 = conf7.getConfValue().split("/");

        //每天随机金额
        int sevenDays=0;
        if (array7.length == 2) {
            int start7X = Integer.parseInt(array7[0]);
            int end7Y = Integer.parseInt(array7[1]);
            sevenDays=random.nextInt(end7Y)%(end7Y-start7X+1)+start7X;
        } else {
            log.error("配置格式：X/Y,X为充值金额,Y为赠送金额");
        }


        //连续签到天数
        int num=0;
        PageHelper.startPage(1,1);
        PageHelper.orderBy("id desc");
        UserSignRecord signRecord=new UserSignRecord();
        signRecord.setCustomerCode(user.getUserCode());
        List<UserSignRecord> list=userSignRecordMapper.select(signRecord);

        if (!CollectionUtils.isEmpty(list)){
            num=list.get(0).getContinuousDay();
            //当天不可以签到
            if (DateUtil.isSameDay(new Date(),list.get(0).getSignDate())){
                return null;
            }

            //判断是否是连续
            if (!(DateUtil.betweenDay(new Date(),list.get(0).getSignDate(),false)<=1)){
                num=0;//不连续签到重置为0
            }

        }

        //查询阶段
        UserConf level=configService.selectCmsValue(ConfigkeyConstant.MALL_USER_SIGN_GIVE_SCORE_LEVEL);

        signRecord.setContinuousDay(num+1);
        signRecord.setExplanation("第"+(num+1)+"天签到得积分");
        signRecord.setSignDate(new Date());

        CustomerScoreDetail detail = new CustomerScoreDetail();

        Double score=0.0;
        if (level.getConfValue().equals(num)){
            score=(double) sevenDays; // 积分
        }else{
            score=(double) integral; // 积分
        }
        signRecord.setMoney(score);
        userSignRecordMapper.insert(signRecord);

        //插入积分明细
        detail.setScore(score); // 积分
        detail.setCustomerCode(user.getUserCode());
        detail.setDataSrc(DataSrcConstant.SIGN);
        detail.setScoreFlag(PayStatusConstant.INCOME);
        detail.setCreateTime(new Date()); // 创建时间
        customerScoreDetailMapper.insertSelective(detail);

        //修改积分
        customerInfoMapper.addAmount(user.getUserCode(),0.0,score,1L);
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("data",score);
        resultMap.put("days",num);
        return resultMap;
    }

    @Override
    public Map<String,Object> isCustomerSignIn() {
        UserInfo user = UserInterceptor.getUserInfo();
        if (user== null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        PageHelper.startPage(1,1);
        PageHelper.orderBy("id desc");
        UserSignRecord signRecord=new UserSignRecord();
        signRecord.setCustomerCode(user.getUserCode());
        List<UserSignRecord> list=userSignRecordMapper.select(signRecord);


        Map<String,Object> resultMap=new HashMap<>();
        if (!CollectionUtils.isEmpty(list)){
            //当天不可以签到
            if (DateUtil.isSameDay(new Date(),list.get(0).getSignDate())){
                resultMap.put("data","0");

            }else {
                resultMap.put("data","1");
            }
            if (DateUtil.betweenDay(new Date(),list.get(0).getSignDate(),false)<=1){
                resultMap.put("days",list.get(0).getContinuousDay());
            }else {
                resultMap.put("days","0");
            }
            return resultMap;
        }
        resultMap.put("days",list.get(0).getContinuousDay());
        resultMap.put("data","1");
        return resultMap;
    }
}
