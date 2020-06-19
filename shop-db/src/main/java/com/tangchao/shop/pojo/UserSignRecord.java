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
 * @Date 2020/6/19 14:28
 */
@Data
@Table(name = "user_sign_record")
public class UserSignRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerCode;

    private Integer continuousDay;

    private Date signDate;

    private Double money;

    private String explanation;

}
