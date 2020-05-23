package com.tangchao.shop.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


@Table(name = "customer")
@Data
public class Customer implements Serializable {

    //  用户Id，主键,自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  用户名
    private String userName;

    //  唯一标识
    private Long userCode;

    //  用户密码
    private String loginPwd;

    //  用户手机号
    private String userMobile;

    //  用户邮箱
    private String userEmail;

    //  用户昵称
    private String userRealName;

    //  用户头像
    private String userPortrait;

    //  是否为机器人{ 0：不是，1：是 }
    private Integer isRobot;

    //  是否为充值会员{ 0：不是，1：是 }
    private Integer isSupplier;

    //  邀请人编号
    private String inviteId;

    //  状态标记｛0：正常，-1：已删除｝
    private Integer flag;

    //  创建人Id
    private Long createId;

    //  创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    //  最后修改人Id
    private Long lastModifyId;

    //  最后修改时间
    private Date lastModifyTime;

    //是否公开拼团记录{0：公开，1：隐藏}
    private Integer isCollageRecord;

    //是否公开获得的商品{0：公开，1：隐藏}
    private Integer isObtainGoods;

    //是否公开晒单{0：公开，1：隐藏}
    private Integer isShowOrder;

    // 拉黑状态
    private String blackStatu;

    // 账号状态（0：正常，-1：已冻结）
    private String accountStatu;


    private Integer buyPeriod;

    @Transient
    private Double registerScore;

    @Transient
    private Double registerMoney;


    /* -------------------------------------------------------------------------------------------------------------- */

    @Transient
    private String registerIp;
    @Transient
    private Double userMoney;
    @Transient
    private Double employMoney;
    @Transient
    private Double userScore;

    @Transient
    private Integer isShopping;//是否购买 0：否 1：是
    @Transient
    private String isSign;//是否开启会员签到功能 0 不开启 1 开启
    @Transient
    private String customerService;//客服微信图片地址
    @Transient
    private String mallAddress;//商城地址图片地址
    @Transient
    private String customerServiceMobile;//客服电话
    @Transient
    private String serviceWorkingTime;//客服工作时间
    @Transient
    private String inviteCode;//邀请码
    @Transient
    private Double userFlow; //  会员流量

    @Transient
    private String mobileHomeDialogBanners;//客服电话

    @Transient
    private Double winningTotal;    // 中奖金额

    @Transient
    private String wxNavWebsiteImg;//客服电话

    @Transient
    private Double payAmountSum;//消费总额

}
