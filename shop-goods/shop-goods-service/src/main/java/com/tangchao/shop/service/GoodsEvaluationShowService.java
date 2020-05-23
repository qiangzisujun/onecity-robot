package com.tangchao.shop.service;

import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.vo.OrderShowVO;

import java.util.Map;

public interface GoodsEvaluationShowService {

    PageResult<OrderShowVO> selectOrderShowList(Integer pageNo, Integer pageSize, Long userCode);

    void getCustomerEvaluationShow(Long userId, Map<String, Object> data);

    void commentBatchSee(Long userId, Map<String, Object> data);

    void updateEvalDoBatchDeleteByIds(Long userId, Map<String, Object> data);
}
