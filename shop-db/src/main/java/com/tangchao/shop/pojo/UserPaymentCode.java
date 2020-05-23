package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Class UserPaymentCode
 * @Description TODO
 * @Author Aquan
 * @Date 2020/3/27 11:50
 * @Version 1.0
 **/
@Data
@Table(name = "user_payment_code")
public class UserPaymentCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userCode;

    private String number;

    private String username;

    private String paymentCodeImg;

    private Integer type;

    private Integer status;

}
