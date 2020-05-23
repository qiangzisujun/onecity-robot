package com.tangchao.shop.mapper;

import com.tangchao.shop.dto.CouponLogDTO;
import com.tangchao.shop.pojo.CouponLog;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface CouponLogMapper extends Mapper<CouponLog> {

    List<CouponLogDTO> findAll(@Param("logStatus") Integer logStatus, @Param("couponStatus") Integer couponStatus, @Param("userMobile") String userMobile,@Param("beforeDate") Long beforeDate,@Param("rearDate") Long rearDate);

    BigDecimal findAllTotalAmount(@Param("logStatus") Integer logStatus, @Param("couponStatus") Integer couponStatus, @Param("userMobile") String userMobile,@Param("beforeDate") Long beforeDate,@Param("rearDate") Long rearDate);
}
