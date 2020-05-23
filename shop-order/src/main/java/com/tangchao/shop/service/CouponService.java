package com.tangchao.shop.service;

import com.tangchao.shop.dto.ShopCouponDTO;
import com.tangchao.shop.params.GetCouponParam;
import com.tangchao.shop.params.PayCouponParam;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface CouponService {

    /**
     * 新增优惠券
     * @param couponDTO
     */
    void saveCouponInfo(ShopCouponDTO couponDTO);

    void updateCouponInfo(ShopCouponDTO shopCouponDTO);

    void deleteCouponInfo(Long userId, Map<String, Object> id);

    ResponseEntity getCouponList(Integer pageNo, Integer pageSize);

    ResponseEntity getInfo(Integer couponId);

    ResponseEntity payCoupon(PayCouponParam payCouponParam);

    ResponseEntity payNotify(String no, String resultCode, String money, String type, String sign, String timeEnd) throws Exception;

    ResponseEntity getCouponListByAdmin(Long userId,Integer pageNo, Integer pageSize,String name);

    ResponseEntity getUserCouponList(Integer status, Integer pageNo, Integer pageSize);

    ResponseEntity shareCoupon(String shareUrl);

    ResponseEntity getCoupon(GetCouponParam getCouponParam);

    Map<String,String> payOrderByBoB(PayCouponParam payCouponParam) throws UnsupportedEncodingException, NoSuchAlgorithmException;

    String userPaymentNotifyByPayBOB(HttpServletRequest request) throws Exception;
}
