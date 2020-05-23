package com.tangchao.user.service;

import com.github.pagehelper.PageInfo;
import com.tangchao.shop.pojo.Customer;
import com.tangchao.shop.pojo.CustomerScoreDetail;
import com.tangchao.shop.vo.adminVo.CustomerScoreDetailVO;

import java.util.List;
import java.util.Map;

public interface CustomerScoreDetailService {

    int paySendScore(Long userCode, Double orderScore, String orderNo);

    int payDeductionsScore(Long userCode, Double consumeScore, String orderNo);

    /**
     * 保存积分明细
     * @param confKey
     * @param detail
     * @return
     */
    Double interDetail(String confKey, CustomerScoreDetail detail);

    Double interMoneyDetail(String confKey, CustomerScoreDetail detail);

    /**
     * 用户积分明细
     * @param customerCode
     * @return
     */
    List<CustomerScoreDetailVO> getCustomerScoreDetailByUserCode(String customerCode);

    Map<String, Object> selectCountByUserCode(Long userCode);

    /**
     * 充值送积分
     */
    Double rechargeSendScore(Long customerCode, Double userMoney, Double userScore);

    /**
     * 晒单审核通过送积分
     *
     * @param customer
     * @return
     */
    Double evaluationShowScore(Customer customer, Double score);

    PageInfo findCustomerScoreDetailByCustomerCodePage(Long userCode, Integer pageNo, Integer pageSize);

    void customerEmployRechargeUserMoney(Long userCode, Map<String, Object> data);
}
