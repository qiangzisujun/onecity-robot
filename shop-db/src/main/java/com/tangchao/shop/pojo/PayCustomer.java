package com.tangchao.shop.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/10/29 16:21
 */
@Table(name = "cms_pay_customer")
@Data
public class PayCustomer implements Serializable {

    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;//id
    private String custImg;//图片
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;//创建日期
    private Integer status;//状态0正常 1禁用
}
