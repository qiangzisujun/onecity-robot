package com.tangchao.user.service;

import com.tangchao.shop.pojo.PaymentCode;
import com.tangchao.shop.pojo.PaymentRecord;

public interface PaymentCodeManageService {

    /**
     * 创建收款码调用记录
     * @param paymentRecord
     * @return
     * @throws Exception
     */
    PaymentCode createPaymentRecord(PaymentRecord paymentRecord);
}
