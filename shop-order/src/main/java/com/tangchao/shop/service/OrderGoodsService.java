package com.tangchao.shop.service;

import com.github.pagehelper.PageInfo;
import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.dto.adminDTO.CustomerDTO;
import com.tangchao.shop.pojo.CustomerEvaluationShow;
import com.tangchao.shop.pojo.WinningOrder;
import com.tangchao.shop.vo.UserByRecordVO;

import java.util.List;
import java.util.Map;


public interface OrderGoodsService {

    /**
     * 用户购买记录
     *
     * @param pageNo
     * @param pageSize
     * @param userCode
     * @return
     */
    PageResult<UserByRecordVO> selectBuyRecordList(Integer pageNo, Integer pageSize, Long userCode);

    /**
     * 用户获得商品列表
     *
     * @param pageNo
     * @param pageSize
     * @param userCode
     * @return
     */
    PageResult<UserByRecordVO> selectUserObtainGoodsList(Integer pageNo, Integer pageSize, Long userCode);

    PageInfo<CustomerEvaluationShow> getCustomerEvaluationShow(Long userId, CustomerDTO customerDTO);

    Map<String,Object> getUserStatistics(Long userId);

    WinningOrder getLogisticsInfo(Long userCode, String goodsNo, Integer stageId);
}
