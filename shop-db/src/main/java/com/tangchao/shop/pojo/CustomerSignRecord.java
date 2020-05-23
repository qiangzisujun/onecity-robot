package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2020/5/6 15:12
 */
@Data
@Table(name = "customer_sign_record")
public class CustomerSignRecord {


    //主键id 自动递增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //会员唯一标识
    private Long customerCode;

    //续连签到天数
    private Integer continuousDay;

    //签到日期
    private Date signDate;

    private BigDecimal money;

    private String explanation;

}
