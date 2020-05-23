package com.tangchao.shop.pojo;

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
 * @Date 2019/11/8 17:10
 */
@Table(name = "customer_evaluation")
@Data
public class CustomerEvaluation implements Serializable {

    // 用户Id，主键,自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 晒单表id
    private Integer isDelete;

    // 晒单表id
    private Integer isSee;

    // 晒单表id
    private Long showId;

    // 晒单表id
    private Long stageId;

    // 会员唯一标识 评价人ID
    private Long customerCode;

    // 评价内容
    private String content;

    // 创建时间
    private Date createTime;

    // 最后修改时间
    private Date lastModifyTime;
}
