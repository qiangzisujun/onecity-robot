package com.tangchao.shop.mapper;

import com.tangchao.shop.pojo.PayCustomer;
import com.tangchao.shop.pojo.PaymentRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/10/29 16:22
 */
public interface PayCustomerMapper extends Mapper<PayCustomer> {

    PayCustomer getPayCustomer();

    List<PaymentRecord> selectPaymentRecordList(@Param("mobile") String mobile, @Param("weChatNickName") String weChatNickName, @Param("typeId") Integer typeId, @Param("startDate") String startDate, @Param("endDate") String endDate);
}
