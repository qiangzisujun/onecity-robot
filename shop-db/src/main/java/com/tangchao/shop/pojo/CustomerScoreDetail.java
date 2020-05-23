package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "customer_score_detail")
@Data
public class CustomerScoreDetail implements Serializable {

    private static final long serialVersionUID = -2097405933550701934L;

    //  主键,自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  会员编码
    private Long customerCode;

    //  积分
    private Double score;

    //  积分来源{ 1：注册，2：订单，3：签到 ，4：充值 , 5：扣减，6：邀请, 7：晒单, 8：不中全返, 9：充值卡换积分
    private Integer dataSrc;

    //  订单编号
    private String orderCode;

    //  描述
    private String scoreDescribe;

    //  积分标识{ 1：收入，2：支出 }
    private Integer scoreFlag;

    //  创建时间
    private Date createTime;
}
