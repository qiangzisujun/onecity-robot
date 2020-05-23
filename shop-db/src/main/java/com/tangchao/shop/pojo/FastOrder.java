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
 * @Description 秒款订单
 * @Date 2019/11/2 16:22
 */
@Table(name = "fast_order")
@Data
public class FastOrder implements Serializable {

    // 主键,自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 删除标记 { 0：正常,1：已删除 }
    private Integer flag;

    // 状态标识{0：未校验，1：已校验}
    private Integer statusFlag;

    // 创建时间
    private Date createTime;

    // 创建时间
    private Date checkTime;

    // 中奖订单id
    private Long winOrderId;

    // 商品期次id
    private Long stageId;

    // 充值会员唯一编码
    private Long puserCode;

    // 中奖会员唯一编码
    private Long userCode;

    // 校验码
    private String checkCode;
}
