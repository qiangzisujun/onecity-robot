package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Table(name = "customer_evaluation_show")
@Data
public class CustomerEvaluationShow implements Serializable {

    // 用户Id，主键,自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer isDelete;// 是否删除 { 0:未删除, 1:删除}

    private Long stageId;// 商品期次id

    private Long userCode;// 会员唯一编码

    private Long goodsNo;// 商品唯一编码

    private Integer status;// 状态{ 1: 等待审核，2:未审核通过，3:审核通过}

    private String title;// 评价标题

    private String content;// 评价内容

    private Date createTime;// 创建时间

    private Date applicationDate;// 申请时间

    private Date assessCompletionDate;// 审核时间

    private String assessorName;// 审核人(登录名)

    private Long assessorId;// 审核人Id(主键id)

    private String applyReason;// 审核不通过原因

    private Integer praiseNum;// 点赞数

    private String praiseCodes;// 点赞用户


    @Transient
    private String userName; // 评价人名字
    @Transient
    private String userMobile;// 评价人电话
    @Transient
    private List<String> imgUrl; // 晒单图片
    @Transient
    private String ids; // 扩展属性 多个id 逗号(,)隔开
    @Transient
    private Long orderNo;// 订单编号
    @Transient
    private String goodsName;// 商品名字
}