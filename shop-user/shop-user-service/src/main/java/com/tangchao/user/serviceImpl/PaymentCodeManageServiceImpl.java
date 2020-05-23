package com.tangchao.user.serviceImpl;

import com.tangchao.shop.mapper.PaymentCodeMapper;
import com.tangchao.shop.mapper.PaymentRecordMapper;
import com.tangchao.shop.pojo.PaymentCode;
import com.tangchao.shop.pojo.PaymentRecord;
import com.tangchao.user.service.PaymentCodeManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


@Service
public class PaymentCodeManageServiceImpl implements PaymentCodeManageService {

    @Autowired
    private PaymentCodeMapper paymentCodeMapper;

    @Autowired
    private PaymentRecordMapper recordMapper;

    @Override
    @Transactional
    public PaymentCode createPaymentRecord(PaymentRecord paymentRecord) {
        // 调用收款码
        PaymentCode paymentCode = paymentCodeMapper.callPaymentCode(paymentRecord.getPaymentCodeType(), paymentRecord.getPaymentCodePrice());

        if(paymentCode == null) {
            return null;
        }
        // 更新最近调用时间
        paymentCode.setLastCallTime(new Date());
        int count=paymentCodeMapper.updatePaymentCodeByPrimaryKeySelective(paymentCode);

        paymentRecord.setPaymentCodeImage(paymentCode.getImage());
        paymentRecord.setCreateTime(new Date());
        paymentRecord.setFlag("0");
        paymentRecord.setRechargeStatu("0");
        // 保存调取记录
        count=recordMapper.insertSelective(paymentRecord);

        return paymentCode;
    }
}
