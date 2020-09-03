package com.tangchao.user.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tangchao.common.constant.ConfigkeyConstant;
import com.tangchao.common.constant.DataSrcConstant;
import com.tangchao.common.constant.PayStatusConstant;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.common.utils.ArithUtil;
import com.tangchao.common.utils.DateUtils;
import com.tangchao.shop.config.JwtProperties;
import com.tangchao.shop.mapper.CustomerInfoMapper;
import com.tangchao.shop.mapper.CustomerRechargeRecordMapper;
import com.tangchao.shop.mapper.CustomerScoreDetailMapper;
import com.tangchao.shop.mapper.UserConfMapper;
import com.tangchao.shop.pojo.*;
import com.tangchao.shop.vo.adminVo.CustomerScoreDetailVO;
import com.tangchao.user.service.CmsConfigService;
import com.tangchao.user.service.CustomerScoreDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class CustomerScoreDetailServiceImpl implements CustomerScoreDetailService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerScoreDetailServiceImpl.class);

    @Autowired
    private UserConfMapper userConfMapper;

    @Autowired
    private CustomerScoreDetailMapper customerScoreDetailMapper;

    @Autowired
    private CmsConfigService configService;

    @Autowired
    private CustomerInfoMapper customerInfoMapper;

    @Autowired
    private CustomerRechargeRecordMapper customerRechargeRecordMapper;

    /**
     * 赠送积分
     *
     * @param userCode
     * @param orderScore
     * @param orderNo
     */
    @Override
    public int paySendScore(Long userCode, Double orderScore, String orderNo) {
        CustomerScoreDetail detail = new CustomerScoreDetail();
        detail.setCustomerCode(userCode);
        detail.setDataSrc(DataSrcConstant.PAY);
        detail.setScoreFlag(PayStatusConstant.INCOME);
        detail.setScore(orderScore); // 积分
        detail.setCustomerCode(userCode); // 会员编码
        detail.setOrderCode(orderNo); // 订单编号
        detail.setCreateTime(new Date()); // 创建时间

        UserConf conf = new UserConf();
        conf.setConfKey(ConfigkeyConstant.CN_SWITCH_US);
        conf.setFlag(0);
        List<UserConf> newConf = userConfMapper.select(conf);
        conf = newConf.get(0);
        if (null != conf && "1".equals(conf.getConfValue())) {
            detail.setScoreDescribe("Consume"); // 描述
        } else {
            detail.setScoreDescribe("消费赠送积分"); // 描述
        }
        int count = customerScoreDetailMapper.insertSelective(detail);
        if (count != 1) {
            throw new CustomerException(ExceptionEnum.INTEGRAL_NOT_ERROR);
        }
        return count;
    }

    @Override
    public int payDeductionsScore(Long userCode, Double consumeScore, String orderNo) {
        CustomerScoreDetail detail = new CustomerScoreDetail();
        detail.setCustomerCode(userCode);
        detail.setDataSrc(DataSrcConstant.DEDUCTING);
        detail.setScoreFlag(PayStatusConstant.EXPENDITURE);

        if (consumeScore != null && consumeScore > 0) {
            detail.setScore(Double.valueOf(consumeScore));
        }
        // 抵扣的福分
        detail.setCustomerCode(userCode); // 会员编码
        detail.setOrderCode(orderNo); // 订单编号
        detail.setCreateTime(new Date()); // 创建时间
        detail.setScoreFlag(2);// 支出

        UserConf conf = new UserConf();
        conf.setConfKey(ConfigkeyConstant.CN_SWITCH_US);
        conf.setFlag(0);
        List<UserConf> newConf = userConfMapper.select(conf);
        conf = newConf.get(0);

        if (null != conf && "1".equals(conf.getConfValue())) {
            detail.setScoreDescribe("Consume"); // 描述
        } else {
            detail.setScoreDescribe("消费赠送积分"); // 描述
        }

        int count = customerScoreDetailMapper.insertSelective(detail);
        if (count != 1) {
            throw new CustomerException(ExceptionEnum.INTEGRAL_NOT_ERROR);
        }
        return count;
    }

    @Override
    public Double interDetail(String confKey, CustomerScoreDetail detail) {

        double score = 0;
        UserConf conf=new UserConf();
        conf.setConfKey(ConfigkeyConstant.CN_SWITCH_US);
        conf.setFlag(0);
        List<UserConf> scoreConf = userConfMapper.select(conf);
        conf=scoreConf.get(0);

        UserConf registerScore=new UserConf();
        registerScore.setConfKey(confKey);
        registerScore.setFlag(0);
        registerScore.setConfKey(ConfigkeyConstant.MALL_USER_REGISTER_GIVE_Money);
        List<UserConf> scoreList = userConfMapper.select(registerScore);
        if (!scoreList.isEmpty()){
            registerScore=scoreList.get(0);
        }
        if (null != scoreConf) {
            try {
                if (null !=registerScore){
                    // 获取积分
                    score = Double.parseDouble(registerScore.getConfValue());
                }
                detail.setCustomerCode(detail.getCustomerCode()); // 会员编码
                detail.setScore(score); // 积分

                if (null != scoreConf && "1".equals(conf.getConfValue())) {
                    detail.setScoreDescribe("Score"); // 描述
                }else {
                    detail.setScoreDescribe("积分记录"); // 描述
                }
                //TODO
                //detail.setScoreDescribe(scoreConf.getConfDescribe()); // 积分描述
                detail.setCreateTime(new Date()); // 创建时间
                // 保存流水
                this.customerScoreDetailMapper.insertSelective(detail);
            } catch (NumberFormatException e) {
                // 积分配置值格式不正确
                this.logger.error("配置：\"" + confKey + "\" 的值是个字符串");
                this.logger.error(e.getMessage());
            }
        }
        return score;
    }

    @Override
    public Double interMoneyDetail(String confKey, CustomerScoreDetail detail) {

        double money = 0;
        UserConf registerMoney=new UserConf();
        registerMoney.setConfKey(confKey);
        registerMoney.setFlag(0);
        List<UserConf> scoreList = userConfMapper.select(registerMoney);

        registerMoney=scoreList.get(0);
        try {
            if (null != registerMoney){
                // 获取余额
                money = Double.parseDouble(registerMoney.getConfValue());
            }
        } catch (NumberFormatException e) {
            // 积分配置值格式不正确
            this.logger.error("配置：\"" + confKey + "\" 的值是个字符串");
            this.logger.error(e.getMessage());
        }
        return money;
    }

    @Override
    public List<CustomerScoreDetailVO> getCustomerScoreDetailByUserCode(String customerCode) {
        return customerScoreDetailMapper.getCustomerScoreDetailByUserCode(customerCode);
    }

    @Override
    public Map<String, Object> selectCountByUserCode(Long userCode) {
        return customerScoreDetailMapper.selectCountByUserCode(userCode);
    }

    @Override
    public Double rechargeSendScore(Long customerCode, Double userMoney, Double userScore) {
        try {
            CustomerScoreDetail detail = new CustomerScoreDetail();
            UserConf scoreConf = this.configService.selectCmsValue(ConfigkeyConstant.MALL_USER_RECHARGE_GIVE_SCORE);
            String[] array = scoreConf.getConfValue().split("/");
            if (array.length != 2) {
                throw new Exception("配置格式：X/Y,X为充值金额,Y为赠送福分数量");
            }
            double where = Double.parseDouble(array[0]);
            double score = Double.parseDouble(array[1]);
            if (userScore == 0.0) {
                // 计算可获得积分数量
                long x = (long) (userMoney / where);
                userScore = ArithUtil.mul(score, x);
            }
            detail.setCustomerCode(customerCode);
            detail.setScore(userScore);
            detail.setDataSrc(DataSrcConstant.RECHARGE);
            UserConf confByKeyLanguage = this.configService.selectCmsValue(ConfigkeyConstant.CN_SWITCH_US);
            if (null !=confByKeyLanguage && "1".equals(confByKeyLanguage.getConfValue())){
                detail.setScoreDescribe("Recharge");
            }else {
                detail.setScoreDescribe("充值送福分");
            }
            detail.setScoreFlag(PayStatusConstant.INCOME);
            detail.setCreateTime(new Date());
            int row = customerScoreDetailMapper.insertSelective(detail);
            if (row > 0) {
                return userScore;
            }
        } catch (Exception e) {
            this.logger.error("配置错误：每充值X元可获赠Y数量福分");
            this.logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public Double evaluationShowScore(Customer customer, Double score) {
        try {
            CustomerScoreDetail detail = new CustomerScoreDetail();
            if (score == null) {
                UserConf scoreConf = this.configService.selectCmsValue(ConfigkeyConstant.MALL_USER_SHARE_ORDER_GIVE);
                if (scoreConf != null && !StringUtils.isEmpty(scoreConf.getConfValue())) {
                    String scoreConfConfValue = scoreConf.getConfValue();
                    score = Double.parseDouble(scoreConfConfValue);
                    detail.setScore(score);
                } else {
                    detail.setScore(0.0);
                }
            } else {
                detail.setScore(score);
            }
            detail.setCustomerCode(customer.getUserCode());
            detail.setDataSrc(DataSrcConstant.EVALUATION);
            UserConf confByKeyLanguage = configService.selectCmsValue(ConfigkeyConstant.CN_SWITCH_US);
            if (null !=confByKeyLanguage && "1".equals(confByKeyLanguage.getConfValue())){
                detail.setScoreDescribe("Share");
            }else {
                detail.setScoreDescribe("晒单送福分");
            }

            detail.setScoreFlag(PayStatusConstant.INCOME);
            detail.setCreateTime(new Date());
            int insert = customerScoreDetailMapper.insertSelective(detail);
            if (insert > 0) {
                return score;
            }
        } catch (Exception e) {
            this.logger.error("配置错误：晒单送福分");
            this.logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public PageInfo findCustomerScoreDetailByCustomerCodePage(Long userCode, Integer pageNo, Integer pageSize) {
        if (userCode == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        Date beginDate = DateUtils.getFrontDay(new Date(), 30);//获取前30天记录
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<CustomerScoreDetail> list = null;
        PageHelper.orderBy("create_time desc");
        PageHelper.startPage(pageNo, pageSize);
        Example example = new Example(CustomerScoreDetail.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("customerCode", userCode);
        criteria.andNotEqualTo("score", 0);
        criteria.andGreaterThanOrEqualTo("createTime", sdf.format(beginDate));
        list = customerScoreDetailMapper.selectByExample(example);
        return new PageInfo(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void customerEmployRechargeUserMoney(Long userCode, Map<String, Object> data) {

        if (userCode == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        Double employMoney=Double.valueOf(data.get("employMoney").toString());

        if (employMoney==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }

        int count=0;

        CustomerInfo customerInfo=new CustomerInfo();
        customerInfo.setCustomerCode(userCode);
        customerInfo=customerInfoMapper.selectOne(customerInfo);

        if (null==customerInfo){
            throw new CustomerException(ExceptionEnum.INVALID_USER_NOT_FOND);
        }
        if (customerInfo.getEmployMoney()>=employMoney){
            int row = customerInfoMapper.employMinus(userCode, employMoney, userCode);
            if (row>0){
                CustomerRechargeRecord record = new CustomerRechargeRecord();
                record.setCustomerCode(userCode);
                record.setAmount(employMoney);
                record.setType(4);//'充值消费标识{ 1：充值，2：消费 , 3 :佣金提现 ,4佣金充值}',  ==4
                record.setCreateTime(new Date());
                record.setCreateId(userCode);
                record.setPayment(PayStatusConstant.PAY_FROM_WITHDRAW);//支付方式{ 1：支付宝，2：微信，3：余额，4：后台，5：代理 }
                //佣金充值扣减记录
                count=this.customerRechargeRecordMapper.insertSelective(record);
                if (count!=1){
                    throw new CustomerException(ExceptionEnum.RECHARGE_SAVE_ERROR);
                }
                int i = customerInfoMapper.addAmount(userCode, employMoney, 0.00, userCode);
                CustomerRechargeRecord record1 = new CustomerRechargeRecord();
                record1.setCustomerCode(userCode);
                record1.setAmount(employMoney);
                record1.setType(3);//'充值消费标识{ 1：充值，2：消费 , 3 :佣金提现 ,4佣金充值}',
                record1.setCreateTime(new Date());
                record1.setCreateId(userCode);
                record1.setPayment(PayStatusConstant.PAY_FROM_MONEY);//'支付方式{1：支付宝，2：微信，3：余额 }',
                //会员余额充值记录
                count=this.customerRechargeRecordMapper.insertSelective(record1);
                if (count!=1){
                    throw new CustomerException(ExceptionEnum.RECHARGE_SAVE_ERROR);
                }
            }
        }else{
            throw new CustomerException(ExceptionEnum.USER_BALANCE_INSUFFICIENT);
        }
    }
}
