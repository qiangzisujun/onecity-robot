package com.tangchao.shop.mapper;

import com.tangchao.shop.pojo.CustomerScoreDetail;
import com.tangchao.shop.vo.adminVo.CustomerScoreDetailVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface CustomerScoreDetailMapper extends Mapper<CustomerScoreDetail> {

    /**
     * 用户积分明细
     * @param customerCode
     * @return
     */
    List<CustomerScoreDetailVO> getCustomerScoreDetailByUserCode(@Param("customerCode") String customerCode);

    /**
     * 根据用户积分查询用户积分收入和支出
     * @param userCode
     * @return
     */
    Map<String, Object> selectCountByUserCode(@Param("userCode")  Long userCode);
}
