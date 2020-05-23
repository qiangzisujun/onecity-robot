package com.tangchao.shop.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 收款码实体类
 */
@Table(name = "cms_payment_code")
@Data
public class PaymentCode implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 收款码图片
    private String image;

    // 收款码金额
    private Double price;

    // 收款码类型
    private String type;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    // 最近调用时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastCallTime;

    // 启用标记 { 1：启用，0：未启用 }
    private String isOpen;

    // 状态标记｛0：正常，-1：已删除｝
    private String flag;

}
