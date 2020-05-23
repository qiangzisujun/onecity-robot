package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2020/3/18 17:32
 */
@Data
@Table(name = "phone_recharge_record")
public class PhoneRechargeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderNo;
    private String phoneNo;
    private Integer money;
    private Long userCode;
    private Integer status;
    private Date createTime;
    private Date rechargeSuccessTime;
    private String platformOrderNo;
}
