package com.tangchao.shop.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.dto.CouponLogDTO;
import com.tangchao.shop.mapper.CouponLogMapper;
import com.tangchao.shop.pojo.ShopOrder;
import com.tangchao.shop.service.CouponLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @Class CouponLogServiceImpl
 * @Description TODO
 * @Author Aquan
 * @Date 2020.2.15 18:34
 * @Version 1.0
 **/
@Service
public class CouponLogServiceImpl implements CouponLogService {

    @Autowired
    private CouponLogMapper couponLogMapper;

    @Override
    public ResponseEntity getCouponOrderList(Integer pageNo, Integer pageSize, Integer logStatus, String userMobile, Integer couponStatus, Long beforeDate, Long rearDate) {
        // Example example = new Example(CouponLog.class);
        // Example.Criteria criteria = example.createCriteria();
        // criteria.andEqualTo("datalevel", 1);
        // PageHelper.startPage(pageNo,pageSize);
        // PageHelper.orderBy("create_time");
        // List<CouponLog> couponLogs = couponLogMapper.selectByExample(example);
        PageHelper.startPage(pageNo,pageSize);
        PageHelper.orderBy("a.create_time DESC");
        // Date beforeDateD = null;
        // Date rearDateD = null;
        if (beforeDate != null) {
            beforeDate /= 1000;
        }
        if (rearDate != null) {
            rearDate /= 1000;
        }
        List<CouponLogDTO> couponLogs = couponLogMapper.findAll(logStatus, couponStatus, userMobile, beforeDate, rearDate);
        // TODO: 2020/4/17 给购买多张优惠券的记录，计算出单张的价格。
        for (CouponLogDTO couponLog : couponLogs) {
            if (couponLog.getNumber()>1) {
                Integer number = couponLog.getNumber();
                BigDecimal payAmount = couponLog.getPayAmount();
                BigDecimal divide = payAmount.divide(new BigDecimal(number));
                couponLog.setPayAmount(divide);
            }
        }
        List<CouponLogDTO> countList = couponLogMapper.findAll(logStatus, couponStatus, userMobile, beforeDate, rearDate);
        // BigDecimal totalAmount = couponLogMapper.findAllTotalAmount(logStatus, couponStatus, userMobile, beforeDate, rearDate);
        for (CouponLogDTO couponLog : countList) {
            if (couponLog.getNumber()>1) {
                Integer number = couponLog.getNumber();
                BigDecimal payAmount = couponLog.getPayAmount();
                BigDecimal divide = payAmount.divide(new BigDecimal(number));
                couponLog.setPayAmount(divide);
            }
        }
        BigDecimal actualPay = BigDecimal.ZERO;
        for (CouponLogDTO couponLogDTO : countList) {
            actualPay = actualPay.add(couponLogDTO.getPayAmount());
        }
        BigDecimal totalAmount = actualPay;

        PageInfo<CouponLogDTO> couponLogPage = new PageInfo<>(couponLogs);
        PageResult<CouponLogDTO> couponLogPageResult = new PageResult<>(couponLogPage.getTotal(), totalAmount, couponLogs);

        return ResponseEntity.ok(couponLogPageResult);
    }

}
