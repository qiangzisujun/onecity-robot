package com.tangchao.shop.service;

import com.github.pagehelper.PageInfo;
import com.tangchao.shop.pojo.PayCustomer;
import com.tangchao.shop.pojo.PaymentCode;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/11/11 13:52
 */
public interface PayManagementService {

    PageInfo payCustomerService(Long userId, Integer pageNo, Integer pageSize);

    void updatePayCustomerService(Long userId, PayCustomer payCustomer);

    void deletePayCustomerService(Long userId, Map<String, Object> data);

    void insertPayCustomerService(Long userId, PayCustomer payCustomer);

    PageInfo getPayTransferRecordList(Long userId, String mobile, String weChatNickName, String startDate, String endDate, Integer typeId, Integer pageNo, Integer pageSize);

    void updatePaymentRecordStatus(Long userId, Map<String, Object> data, HttpServletRequest request);

    PageInfo getPaymentCodeList(Long userId, Integer pageNo, Integer pageSize);

    void updatePaymentCode(Long userId, PaymentCode paymentCode);

    void deletePaymentCode(Long userId, Map<String, Object> data);

    void insertPaymentCode(Long userId, PaymentCode paymentCode);
}
