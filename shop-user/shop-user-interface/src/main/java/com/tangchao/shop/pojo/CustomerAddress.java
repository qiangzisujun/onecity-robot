package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "customer_address")
@Data
public class CustomerAddress implements Serializable {

    //  主键,自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  会员的唯一编码
    private Long customerCode;

    //  省
    private String province;

    //  市
    private String city;

    //  区
    private String area;

    //  街道
    private String street;

    //  详细地址
    private String detailed;

    //  邮编
    private String zipCode;

    //  收货人名称
    private String userName;

    //  收货人手机号码
    private String userMobile;

    //  是否默认{ 0：非默认，1：默认 }
    private Integer isDefault;

    //  标记｛-1：删除，0：正常｝
    private Integer flag;

    //  创建人Id
    private Long createId;

    //  创建时间
    private Date createTime;

    //  最后修改人Id
    private Long lastModifyId;

    //  最后修改时间
    private Date lastModifyTime;

    //邮政编码
    private String theZipCode;

    private String provinceCode;

    private String cityCode;

    private String areaCode;
}