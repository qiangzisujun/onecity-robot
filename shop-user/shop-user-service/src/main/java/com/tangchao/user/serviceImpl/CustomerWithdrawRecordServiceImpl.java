package com.tangchao.user.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tangchao.common.constant.ConfigkeyConstant;
import com.tangchao.common.constant.PayStatusConstant;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.common.utils.ArithUtil;
import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.advice.DataSourceNames;
import com.tangchao.shop.annotation.DataSource;
import com.tangchao.shop.dto.CustomerWithdrawDTO;
import com.tangchao.shop.dto.adminDTO.CustomerDTO;
import com.tangchao.shop.mapper.*;
import com.tangchao.shop.pojo.*;
import com.tangchao.shop.vo.CustomerWithdrawRecordVO;
import com.tangchao.user.service.CustomerWithdrawRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CustomerWithdrawRecordServiceImpl implements CustomerWithdrawRecordService {

    @Autowired
    private CustomerEmployTiXianRecordMapper withdrawMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private CustomerRechargeRecordMapper rechargeRecordMapper;

    @Autowired
    private UserConfMapper userConfMapper;

    @Autowired
    private CustomerInfoMapper customerInfoMapper;

    @Autowired
    private UserConfMapper confMapper;

    @Autowired
    private CustomerEmployWithdrawRecordMapper customerEmployWithdrawRecordMapper;

    @Autowired
    private ManagerMapper managerMapper;

    @Autowired
    private CustomerEvaluationShowMapper customerEvaluationShowMapper;

    @Autowired
    private UserPaymentCodeMapper userPaymentCodeMapper;


    @Override
    @DataSource(name = DataSourceNames.SECOND)
    public PageResult<CustomerWithdrawRecordVO> getCustomerWithdrawRecord(Integer pageNum, Integer pageSize, Long userCode) {
        if (userCode == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        CustomerEmployTiXianRecord withdraw1 = new CustomerEmployTiXianRecord();
        withdraw1.setCustomerCode(userCode.toString());
        List<CustomerEmployTiXianRecord> list1 = withdrawMapper.select(withdraw1);
        PageHelper.startPage(pageNum, pageSize, true);
        CustomerEmployTiXianRecord withdraw = new CustomerEmployTiXianRecord();
        withdraw.setCustomerCode(userCode.toString());
        List<CustomerEmployTiXianRecord> list = withdrawMapper.select(withdraw);
        List<CustomerWithdrawRecordVO> voList = new ArrayList<>();
        for (CustomerEmployTiXianRecord w : list) {
            CustomerWithdrawRecordVO vo = new CustomerWithdrawRecordVO();
            vo.setId(w.getId());
            vo.setApplicationDate(w.getApplicationDate());
            vo.setPayment(w.getPayment());
            vo.setServiceChargeAmount(w.getServiceChargeAmount());
            vo.setState(w.getState());
            vo.setWithdrawPrice(w.getWithdrawPrice());
            voList.add(vo);
        }
        PageInfo<CustomerWithdrawRecordVO> pageInfo = new PageInfo<CustomerWithdrawRecordVO>(voList);
        return new PageResult<>(pageInfo.getTotal(), voList, pageSize, list1.size());
    }

    @Override
    @DataSource(name = DataSourceNames.SECOND)
    @Transactional
    public void addCustomerWithdraw(CustomerWithdrawDTO dto) {

        if (null==dto.getWithdrawPrice()){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }

        //获取用户信息
        Customer record = new Customer();
        record.setUserCode(dto.getCustomerCode());
        List<Customer> userList = customerMapper.select(record);

        if (null!=userList&&userList.isEmpty()) {
            throw new CustomerException(ExceptionEnum.INVALID_USER_NOT_FOND);
        }
        Customer newUser = userList.get(0);
        CustomerInfo customerInfo1=new CustomerInfo();
        customerInfo1.setCustomerCode(newUser.getUserCode());
        customerInfo1=customerInfoMapper.selectOne(customerInfo1);
        if (null!=customerInfo1&&customerInfo1.getEmployMoney()<dto.getWithdrawPrice()){
            throw new CustomerException(ExceptionEnum.USER_BALANCE_INSUFFICIENT);
        }

        if (null!=customerInfo1&&customerInfo1.getEmployMoney()<10){
            throw new CustomerException(ExceptionEnum.CUSTOMER_BALANCE_INSUFFICIENT);
        }

        dto.setUserName(newUser.getUserName());

        //添加提现记录
        CustomerRechargeRecord customerRechargeRecord = new CustomerRechargeRecord();
        customerRechargeRecord.setAmount(dto.getWithdrawPrice());
        customerRechargeRecord.setCreateId(newUser.getId());
        customerRechargeRecord.setCustomerCode(newUser.getUserCode());
        customerRechargeRecord.setType(3);
        customerRechargeRecord.setPayment(PayStatusConstant.PAY_FROM_BACKSTAGE);
        customerRechargeRecord.setCreateTime(new Date());
        int count = rechargeRecordMapper.insertSelective(customerRechargeRecord);
        if (count != 1) {
            throw new CustomerException(ExceptionEnum.WITHDRAW_CUSTOMER_ERROR);
        }

        CustomerEmployTiXianRecord customerEmployTiXianRecord = new CustomerEmployTiXianRecord();
        BeanUtils.copyProperties(dto, customerEmployTiXianRecord);
        //如果是中文的要收取手续费
        UserConf conf = new UserConf();
        conf.setFlag(0);
        conf.setConfKey(ConfigkeyConstant.MALL_USER_TIXIAN_PROCEDURE);
        UserConf confProcedure = userConfMapper.selectOne(conf);

        double serviceChargeAmount = ArithUtil.mul(dto.getWithdrawPrice(), Double.parseDouble(confProcedure.getConfValue()));//收取后台配置的手续费

        //提现金额小于佣金余额---扣佣金手续费
        if (customerInfo1.getEmployMoney()>(dto.getWithdrawPrice()+serviceChargeAmount)){
            //修改余额
            double withdrawPrice = ArithUtil.add(dto.getWithdrawPrice(), serviceChargeAmount);
            count = customerMapper.employMinus(newUser.getUserCode(), withdrawPrice, dto.getId());
            if (count != 1) {
                throw new CustomerException(ExceptionEnum.WITHDRAW_CUSTOMER_ERROR);
            }
            customerEmployTiXianRecord.setWithdrawPrice(dto.getWithdrawPrice());
        }else{
            count = customerMapper.employMinus(newUser.getUserCode(), dto.getWithdrawPrice(), dto.getId());
            double withdrawPrice = ArithUtil.sub(dto.getWithdrawPrice(), serviceChargeAmount);
            customerEmployTiXianRecord.setWithdrawPrice(withdrawPrice);
        }

        //查询收款码
        UserPaymentCode userPaymentCode=userPaymentCodeMapper.selectByPrimaryKey(dto.getPayment());
        if (userPaymentCode==null){
            throw  new CustomerException(ExceptionEnum.COLLECTION_NOT_FOND);
        }

        if (userPaymentCode.getType().equals(1)){
            customerEmployTiXianRecord.setPayment(3);
            customerEmployTiXianRecord.setAlipayName(userPaymentCode.getNumber());
        }else  if (userPaymentCode.getType().equals(2)){
            customerEmployTiXianRecord.setWeixinName(userPaymentCode.getNumber());
            customerEmployTiXianRecord.setPayment(2);
        }
        customerEmployTiXianRecord.setUserName(userPaymentCode.getUsername());
        customerEmployTiXianRecord.setPaymentCodeImg(userPaymentCode.getPaymentCodeImg());
        customerEmployTiXianRecord.setServiceChargeAmount(serviceChargeAmount);
        customerEmployTiXianRecord.setState(0);//'处理状态 0审核中  1审核通过  2审核不通过  3已提现
        customerEmployTiXianRecord.setApplicationDate(new Date());
        customerEmployTiXianRecord.setPhone(newUser.getUserMobile());
        customerEmployTiXianRecord.setCustomerCode(newUser.getUserCode().toString());
        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setCustomerCode(dto.getCustomerCode());
        List<CustomerInfo> customerList = customerInfoMapper.select(customerInfo);
        customerInfo = customerList.get(0);
        if (customerInfo != null) {
            customerEmployTiXianRecord.setRemainder(customerInfo.getEmployMoney());
        }
        withdrawMapper.insertSelective(customerEmployTiXianRecord);
    }

    @Override
    @DataSource(name = DataSourceNames.SECOND)
    public String withdrawHandlingFee() {
        UserConf conf = new UserConf();
        conf.setFlag(0);
        conf.setConfKey(ConfigkeyConstant.MALL_USER_TIXIAN_PROCEDURE);
        UserConf newConf = userConfMapper.selectOne(conf);
        if (newConf != null) {
            return newConf.getConfValue();
        }
        return null;
    }

    @Override
    public PageInfo getCustomerEmployWithdrawRecord(Long userId, CustomerDTO customerDTO) throws ParseException {

        if (userId == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //  设置以创建时间降序排列
        List<CustomerEmployWithdrawRecord> recordList=null;
        PageHelper.orderBy(" application_date DESC ");
        //  设置分页
        PageHelper.startPage(customerDTO.getPageNo(),customerDTO.getPageSize());
        Example example = new Example(CustomerEmployWithdrawRecord.class);
        Example.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(customerDTO.getUserRealName())) {
            criteria.andLike("userName", "%"+customerDTO.getUserRealName()+"%");
        }
        if (!StringUtils.isEmpty(customerDTO.getPhone())) {
            criteria.andEqualTo("phone", customerDTO.getPhone());
        }
        if (!StringUtils.isEmpty(customerDTO.getStatus()) && customerDTO.getStatus()!=-1) {
            criteria.andEqualTo("state", customerDTO.getStatus());
        }
        if (!StringUtils.isEmpty(customerDTO.getTypeId()) && customerDTO.getTypeId()!=-1) {
            criteria.andEqualTo("payment", customerDTO.getTypeId());
        }
        if (!StringUtils.isEmpty(customerDTO.getApplyStartTime())) {
            criteria.andGreaterThanOrEqualTo("applicationDate", sdf.parse(customerDTO.getApplyStartTime()));
        }
        if (!StringUtils.isEmpty(customerDTO.getApplyEndTime())) {
            criteria.andLessThanOrEqualTo("applicationDate",sdf.parse(customerDTO.getApplyEndTime()));
        }
        if (!StringUtils.isEmpty(customerDTO.getRegisterStartTime())) {
            criteria.andGreaterThanOrEqualTo("assessCompletionDate",sdf.parse(customerDTO.getRegisterStartTime()));
        }
        if (!StringUtils.isEmpty(customerDTO.getRegisterEndTime())) {
            criteria.andLessThanOrEqualTo("assessCompletionDate",sdf.parse(customerDTO.getRegisterEndTime()));
        }
        recordList = this.customerEmployWithdrawRecordMapper.selectByExample(example);
        return new PageInfo(recordList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkEmployWithdraw(Long userId, Map<String, Object> data) {

        if (userId == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        try {
            Manager manager =managerMapper.selectByPrimaryKey(userId);

            Integer state=Integer.valueOf(data.get("status").toString());
            String applyReason=data.get("applyReason").toString();
            List<String> ids=(List<String>) data.get("ids");

            Date assessCompletionDate = new Date();//审核时间

            Example example = new Example(CustomerEmployWithdrawRecord.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("id", ids);
            List<CustomerEmployWithdrawRecord> list=customerEmployWithdrawRecordMapper.selectByExample(example);
            for (CustomerEmployWithdrawRecord CustomerEmployWithdrawRecord : list) {
                if(CustomerEmployWithdrawRecord.getState()!=0){//待审核
                    throw new CustomerException(ExceptionEnum.REVIEW_SAVE_ERROR);
                }
                CustomerEmployWithdrawRecord.setAssessorId(manager.getId()+"");
                CustomerEmployWithdrawRecord.setAssessorName(manager.getUserName());
                CustomerEmployWithdrawRecord.setAssessCompletionDate(assessCompletionDate);
                CustomerEmployWithdrawRecord.setState(state);
                if(state==2){//审核不通过原因
                    CustomerEmployWithdrawRecord.setApplyReason(applyReason);
                }
                int count=customerEmployWithdrawRecordMapper.updateByPrimaryKeySelective(CustomerEmployWithdrawRecord);
                if(count!=1){
                    throw new CustomerException(ExceptionEnum.REVIEW_SAVE_ERROR);
                }
                if(state==2){//审核不通过（返还余额）
                    for(CustomerEmployWithdrawRecord CustomerEmployWithdrawRecord1:list){
                        Long customerCode = CustomerEmployWithdrawRecord1.getCustomerCode();
                        double withdrawPrice = CustomerEmployWithdrawRecord1.getWithdrawPrice()+
                                CustomerEmployWithdrawRecord1.getServiceChargeAmount();
                        Long managerId = manager.getId();
                        Integer type = 3;//充值消费标识{ 1：充值，2：消费  3:佣金提现}'


                        Customer customer =new Customer();
                        customer.setUserCode(customerCode);
                        customer=customerMapper.selectOne(customer);
                        if (customer == null) {
                            throw new CustomerException(ExceptionEnum.REVIEW_SAVE_ERROR);
                        }
                        CustomerInfo customerInfo =new CustomerInfo();
                        customerInfo.setCustomerCode(customerCode);
                        customerInfo=customerInfoMapper.selectOne(customerInfo);
                        if (customerInfo == null) {
                            throw new CustomerException(ExceptionEnum.REVIEW_SAVE_ERROR);
                        }
                        int rowCount = customerInfoMapper.employRecharge(customerCode, withdrawPrice, managerId);
                        if (rowCount!=1) {
                            throw new CustomerException(ExceptionEnum.REVIEW_SAVE_ERROR);
                        }
                        CustomerRechargeRecord customerRechargeRecord = new CustomerRechargeRecord();
                        customerRechargeRecord.setAmount(withdrawPrice);
                        customerRechargeRecord.setCreateId(managerId);
                        customerRechargeRecord.setCustomerCode(customerCode);
                        customerRechargeRecord.setType(type);
                        customerRechargeRecord.setPayment(PayStatusConstant.PAY_FROM_BACKSTAGE);
                        customerRechargeRecord.setCreateTime(new Date());
                        int rowResult = this.rechargeRecordMapper.insertSelective(customerRechargeRecord);
                        if (rowCount!=1) {
                            throw new CustomerException(ExceptionEnum.REVIEW_SAVE_ERROR);
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWithdrawAccountsStatus(Long userId, Map<String, Object> data) {
        if (userId == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        List<String> idList= (List<String>) data.get("data");

        if (!idList.isEmpty()) {
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }

        for (String id : idList) {
            CustomerEmployWithdrawRecord CustomerEmployWithdrawRecord = customerEmployWithdrawRecordMapper.selectByPrimaryKey(id);
            int state = CustomerEmployWithdrawRecord.getState();
            if(state!=1){
                throw new CustomerException(ExceptionEnum.STATE_SELECT_NO_1);
            }
            if(state==3){
                throw new CustomerException(ExceptionEnum.STATE_SELECT_3);
            }
            if(state==1){
                Example example = new Example(CustomerEmployWithdrawRecord.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andIn("id", idList);
                CustomerEmployWithdrawRecord record = new CustomerEmployWithdrawRecord();
                record.setState(3);///改为转账状态
                int row = this.customerEmployWithdrawRecordMapper.updateByExampleSelective(record, example);
                if(row==0){
                    throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
                }
            }
        }
    }

    @Override
    public PageInfo<Map<String, Object>> getCustomerCommentList(Long userId, CustomerDTO customerDTO) {
        if (userId == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        // 设置分页
        PageHelper.startPage(customerDTO.getPageNo(), customerDTO.getPageSize());
        List<Map<String, Object>> evalList = customerEvaluationShowMapper.selectList(customerDTO);
        return new PageInfo<>(evalList);
    }

    @Override
    public Map<String,Object> getCustomerWithdrawList(Long userId, CustomerDTO customerDTO) {
        if (userId == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //  设置以创建时间降序排列
        List<CustomerEmployTiXianRecord> recordList=null;
        try {
            PageHelper.orderBy(" application_date DESC ");
            //  设置分页
            PageHelper.startPage(customerDTO.getPageNo(),customerDTO.getPageSize());
            Example example = new Example(CustomerEmployTiXianRecord.class);
            Example.Criteria criteria = example.createCriteria();

            if (!StringUtils.isEmpty(customerDTO.getUserRealName())) {
                criteria.andLike("userName", "%"+customerDTO.getUserRealName()+"%");
            }
            if (!StringUtils.isEmpty(customerDTO.getPhone())) {
                criteria.andEqualTo("phone", customerDTO.getPhone());
            }
            if (!StringUtils.isEmpty(customerDTO.getStatus()) && customerDTO.getStatus()!=-1) {
                criteria.andEqualTo("state", customerDTO.getStatus());
            }
            if (!StringUtils.isEmpty(customerDTO.getTypeId()) && customerDTO.getTypeId()!=-1) {
                criteria.andEqualTo("payment", customerDTO.getTypeId());
            }
            if (!StringUtils.isEmpty(customerDTO.getApplyStartTime())) {
                criteria.andGreaterThanOrEqualTo("applicationDate", sdf.parse(customerDTO.getApplyStartTime()));
            }
            if (!StringUtils.isEmpty(customerDTO.getApplyEndTime())) {
                criteria.andLessThanOrEqualTo("applicationDate",sdf.parse(customerDTO.getApplyEndTime()));
            }
            if (!StringUtils.isEmpty(customerDTO.getRegisterStartTime())) {
                criteria.andGreaterThanOrEqualTo("assessCompletionDate",sdf.parse(customerDTO.getRegisterStartTime()));
            }
            if (!StringUtils.isEmpty(customerDTO.getRegisterEndTime())) {
                criteria.andLessThanOrEqualTo("assessCompletionDate",sdf.parse(customerDTO.getRegisterEndTime()));
            }
            recordList = this.withdrawMapper.selectByExample(example);
        } catch (Exception e) {
            e.printStackTrace();
        }

        double total=rechargeRecordMapper.withdrawPriceTotal(customerDTO);
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("data",new PageInfo<>(recordList));
        resultMap.put("totalMoney",total);
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkEmployCommission(Long userId, Map<String, Object> data) {

        if (userId == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        try {
            Manager manager =managerMapper.selectByPrimaryKey(userId);
            Integer state=Integer.valueOf(data.get("status").toString());
            String applyReason=data.get("applyReason").toString();
            List<String> ids=(List<String>) data.get("ids");
            Date assessCompletionDate = new Date();//审核时间

            Example example = new Example(CustomerEmployTiXianRecord.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("id", ids);
            List<CustomerEmployTiXianRecord> list =withdrawMapper.selectByExample(example);
            for (CustomerEmployTiXianRecord CustomerEmployTiXianRecord : list) {
                if(CustomerEmployTiXianRecord.getState()!=0){//待审核
                    throw new CustomerException(ExceptionEnum.STATE_SELECT);
                }
                CustomerEmployTiXianRecord.setAssessorId(manager.getId().toString());
                CustomerEmployTiXianRecord.setAssessorName(manager.getUserName());
                CustomerEmployTiXianRecord.setAssessCompletionDate(assessCompletionDate);
                CustomerEmployTiXianRecord.setState(state);
                if(state==2){//审核不通过原因
                    CustomerEmployTiXianRecord.setApplyReason(applyReason);
                }
                int count=withdrawMapper.updateByPrimaryKeySelective(CustomerEmployTiXianRecord);
                if(count!=1){
                    throw new CustomerException(ExceptionEnum.REVIEW_SAVE_ERROR);
                }

                if(state==2){//审核不通过（返还余额）
                    for(CustomerEmployTiXianRecord CustomerEmployTiXianRecord1:list){
                        Long customerCode = Long.valueOf(CustomerEmployTiXianRecord1.getCustomerCode());
                        double withdrawPrice = CustomerEmployTiXianRecord1.getWithdrawPrice()+
                                CustomerEmployTiXianRecord1.getServiceChargeAmount();
                        Long managerId = manager.getId();
                        Integer type = 3;//充值消费标识{ 1：充值，2：消费  3:佣金提现}'
                        Customer customer =new Customer();
                        customer.setUserCode(customerCode);
                        customer=customerMapper.selectOne(customer);
                        if (customer == null) {
                            throw new CustomerException(ExceptionEnum.REVIEW_SAVE_ERROR);
                        }
                        CustomerInfo customerInfo =new CustomerInfo();
                        customerInfo.setCustomerCode(customerCode);
                        customerInfo=customerInfoMapper.selectOne(customerInfo);
                        if (customerInfo == null) {
                            throw new CustomerException(ExceptionEnum.REVIEW_SAVE_ERROR);
                        }
                        int rowCount = customerInfoMapper.employRecharge(customerCode, withdrawPrice, managerId);
                        if (rowCount!=1) {
                            throw new CustomerException(ExceptionEnum.REVIEW_SAVE_ERROR);
                        }
                        CustomerRechargeRecord customerRechargeRecord = new CustomerRechargeRecord();
                        customerRechargeRecord.setAmount(withdrawPrice);
                        customerRechargeRecord.setCreateId(managerId);
                        customerRechargeRecord.setCustomerCode(customerCode);
                        customerRechargeRecord.setType(type);
                        customerRechargeRecord.setPayment(PayStatusConstant.PAY_FROM_BACKSTAGE);
                        customerRechargeRecord.setCreateTime(new Date());
                        int rowResult = this.rechargeRecordMapper.insertSelective(customerRechargeRecord);
                        if (rowCount!=1) {
                            throw new CustomerException(ExceptionEnum.REVIEW_SAVE_ERROR);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void checkCommissionWithdraw(Long userId, Map<String, Object> data) {
        if (userId == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        List<String> idList= (List<String>) data.get("data");

        if (!idList.isEmpty()) {
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }

        for (String id : idList) {
            CustomerEmployTiXianRecord customerEmployTiXianRecord = withdrawMapper.selectByPrimaryKey(id);
            int state = customerEmployTiXianRecord.getState();
            if(state!=1){
                throw new CustomerException(ExceptionEnum.STATE_SELECT_NO_1);
            }
            if(state==3){
                throw new CustomerException(ExceptionEnum.STATE_SELECT_3);
            }
            if(state==1){
                Example example = new Example(CustomerEmployTiXianRecord.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andIn("id", idList);
                CustomerEmployTiXianRecord record = new CustomerEmployTiXianRecord();
                record.setState(3);///改为转账状态
                int row = this.withdrawMapper.updateByExampleSelective(record, example);
                if(row==0){
                    throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
                }
            }
        }
    }
}
