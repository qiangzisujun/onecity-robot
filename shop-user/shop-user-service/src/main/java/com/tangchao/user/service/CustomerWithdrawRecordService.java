package com.tangchao.user.service;

import com.github.pagehelper.PageInfo;
import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.dto.CustomerWithdrawDTO;
import com.tangchao.shop.dto.adminDTO.CustomerDTO;
import com.tangchao.shop.vo.CustomerWithdrawRecordVO;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface CustomerWithdrawRecordService {

    /**
     * 提现记录
     *
     * @param pageNum
     * @param pageSize
     * @param userCode
     * @return
     */
    PageResult<CustomerWithdrawRecordVO> getCustomerWithdrawRecord(Integer pageNum, Integer pageSize, Long userCode);

    void addCustomerWithdraw(CustomerWithdrawDTO dto);

    /**
     * 查询佣金提现手续费
     *
     * @return
     */
    String withdrawHandlingFee();

    PageInfo getCustomerEmployWithdrawRecord(Long userId, CustomerDTO customerDTO) throws ParseException;

    void checkEmployWithdraw(Long userId, Map<String, Object> data);

    void updateWithdrawAccountsStatus(Long userId, Map<String, Object> data);

    PageInfo<Map<String, Object>> getCustomerCommentList(Long userId, CustomerDTO customerDTO);

    Map<String,Object> getCustomerWithdrawList(Long userId, CustomerDTO customerDTO);

    void checkEmployCommission(Long userId, Map<String, Object> data);

    void checkCommissionWithdraw(Long userId, Map<String, Object> data);
}
