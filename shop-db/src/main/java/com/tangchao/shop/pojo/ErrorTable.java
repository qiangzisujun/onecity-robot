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
 * @Date 2020/1/10 17:41
 */
@Data
@Table(name = "error_table")
public class ErrorTable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long stageId;
    private String goodsName;
    private String orderNo;
    private String goodsNo;
    private Integer isProcess;
    private Date processTime;
    private Date createTime;

}
