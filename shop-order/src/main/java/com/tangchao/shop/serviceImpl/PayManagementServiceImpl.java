package com.tangchao.shop.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.shop.mapper.CustomerMapper;
import com.tangchao.shop.mapper.PayCustomerMapper;
import com.tangchao.shop.mapper.PaymentCodeMapper;
import com.tangchao.shop.mapper.PaymentRecordMapper;
import com.tangchao.shop.pojo.*;
import com.tangchao.shop.service.PayManagementService;
import com.tangchao.user.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/11/11 13:54
 */
@Service
public class PayManagementServiceImpl implements PayManagementService {

    @Autowired
    private PayCustomerMapper payCustomerMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private PaymentRecordMapper recordMapper;

    @Autowired
    private PaymentCodeMapper paymentCodeMapper;

    @Autowired
    private CustomerService customerService;

    @Override
    public PageInfo payCustomerService(Long userId, Integer pageNo, Integer pageSize) {

        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        PageHelper.startPage(pageNo,pageSize);
        PayCustomer customer=new PayCustomer();
        customer.setStatus(0);
        List<PayCustomer> list=payCustomerMapper.select(customer);
        list=list.stream().sorted(Comparator.comparing(PayCustomer::getCreateDate).reversed()).collect(Collectors.toList());
        return new PageInfo(list);
    }

    @Override
    public void updatePayCustomerService(Long userId, PayCustomer payCustomer) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        PayCustomer pay=payCustomerMapper.selectByPrimaryKey(payCustomer.getId());
        if (pay==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        int count=payCustomerMapper.updateByPrimaryKeySelective(payCustomer);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void deletePayCustomerService(Long userId, Map<String, Object> data) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        String id=data.get("id").toString();

        PayCustomer pay=payCustomerMapper.selectByPrimaryKey(id);
        if (pay==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        pay.setStatus(1);
        int count=payCustomerMapper.updateByPrimaryKeySelective(pay);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void insertPayCustomerService(Long userId, PayCustomer payCustomer) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        payCustomer.setCreateDate(new Date());
        payCustomer.setStatus(0);
        int count=payCustomerMapper.insertSelective(payCustomer);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public PageInfo getPayTransferRecordList(Long userId, String mobile, String weChatNickName, String startDate, String endDate, Integer typeId, Integer pageNo, Integer pageSize) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        PageHelper.startPage(pageNo,pageSize);
        List<PaymentRecord> list=payCustomerMapper.selectPaymentRecordList(mobile,weChatNickName,typeId,startDate,endDate);
        list=list.stream().sorted(Comparator.comparing(PaymentRecord::getCreateTime).reversed()).collect(Collectors.toList());
        return new PageInfo(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePaymentRecordStatus(Long userId, Map<String, Object> data, HttpServletRequest request) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        String userCode=data.get("userCode").toString();
        double money=Double.valueOf(data.get("money").toString());
        Long id=Long.valueOf(data.get("id").toString());

        Customer customer=new Customer();
        customer.setUserCode(Long.valueOf(userCode));
        customer.setIsSupplier(0);
        customer.setBlackStatu(0+"");
        customer.setAccountStatu(0+"");
        customer.setFlag(0);
        customer=customerMapper.selectOne(customer);

        if (customer==null){
            throw new CustomerException(ExceptionEnum.INVALID_USER_NOT_FOND);
        }

        PaymentRecord record=new PaymentRecord();
        record.setFlag(0+"");
        record.setRechargeStatu(0+"");
        record.setId(id);
        record=recordMapper.selectOne(record);
        if (record==null){
            throw new CustomerException(ExceptionEnum.TRANSFER_RECORD_NOT_FOND);
        }
        record.setRechargeStatu(1+"");
        int count=recordMapper.updateByPrimaryKeySelective(record);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }

        //充值
        Map<String, Object> params=new HashMap<>();
        params.put("money",money);
        params.put("mobile",customer.getUserMobile());
        params.put("integral",0);
        params.put("type",1);
        customerService.customerRechargeByPhone(userId,params,request);
    }

    @Override
    public PageInfo getPaymentCodeList(Long userId, Integer pageNo, Integer pageSize) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        PageHelper.startPage(pageNo,pageSize);
        PaymentCode paymentCode=new PaymentCode();
        paymentCode.setFlag(0+"");
        List<PaymentCode> list =paymentCodeMapper.select(paymentCode);
        list=list.stream().sorted(Comparator.comparing(PaymentCode::getCreateTime).reversed()).collect(Collectors.toList());
        return new PageInfo(list);
    }

    @Override
    public void updatePaymentCode(Long userId, PaymentCode paymentCode) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        PaymentCode paymentCode1=paymentCodeMapper.selectByPrimaryKey(paymentCode.getId());
        if (paymentCode1==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        int count=paymentCodeMapper.updateByPrimaryKeySelective(paymentCode);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void deletePaymentCode(Long userId, Map<String, Object> data) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        String id=data.get("id").toString();
        PaymentCode paymentCode1=paymentCodeMapper.selectByPrimaryKey(id);
        if (paymentCode1==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        paymentCode1.setFlag(-1+"");
        int count=paymentCodeMapper.updateByPrimaryKeySelective(paymentCode1);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void insertPaymentCode(Long userId, PaymentCode paymentCode) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        if (paymentCode==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        paymentCode.setType(1+"");
        paymentCode.setCreateTime(new Date());
        paymentCode.setIsOpen(1+"");
        paymentCode.setFlag(0+"");
        int count=paymentCodeMapper.insertSelective(paymentCode);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }
}
