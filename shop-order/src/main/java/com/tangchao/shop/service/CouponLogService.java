package com.tangchao.shop.service;

import org.springframework.http.ResponseEntity;

public interface CouponLogService {
    ResponseEntity getCouponOrderList(Integer pageNo, Integer pageSize, Integer logStatus, String userMobile, Integer couponStatus, Long beforeDate, Long rearDate);
}
