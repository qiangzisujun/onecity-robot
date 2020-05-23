package com.tangchao.shop.mapper;

import com.tangchao.shop.pojo.OrderDistribution;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface OrderDistributionMapper extends Mapper<OrderDistribution> {
    List<OrderDistribution> CommissionList(Long userCode);

    List<Map<String,Object>> getCommissionListByUserCode(@Param("userCode") Long userCode);

    Map<String,Object> sumCommissionListByUserCode(Long userCode);

    Double sumWithdrawnByUserCode(Long userCode);

}
