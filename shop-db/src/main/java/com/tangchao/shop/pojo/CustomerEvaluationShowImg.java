package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/11/8 11:55
 */
@Table(name = "customer_evaluation_show_img")
@Data
public class CustomerEvaluationShowImg implements Serializable {

    //  用户Id，主键,自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long showId;//晒单表主键

    private String imgUrl;//晒单图片的路径
}
