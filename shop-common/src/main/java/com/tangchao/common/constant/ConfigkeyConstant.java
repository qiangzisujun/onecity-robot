package com.tangchao.common.constant;

/**
 * title: ConfigkeyConstant
 * package: com.chichao.eyyg.pojo.constant
 * description:
 * author: 王飞腾
 * date: 2018/7/9
 */
public abstract class ConfigkeyConstant {

    /**
     * 文件服务器地址
     */
    public static final String IMAGE_SERVER_HOST = "image.server.host";

    /**
     * 客服QQ
     */
    public static final String CUSTOMER_SERVICE_QQ = "customer.service.qq";

    /**
     * 客服电话
     */
    public static final String CUSTOMER_SERVICE_MOBILE = "customer.service.mobile";

    /**
     * 微信客服
     */
    public static final String CUSTOMER_SERVER_WX = "customer.service.wx";

    /**
     * 后台用户默认登陆密码
     */
    public static final String BOSS_USER_DEFAULT_LOGIN_PWD = "boss.user.default.login.pwd";

    /**
     * 商城会员默认登陆密码
     */
    public static final String MALL_USER_DEFAULT_LOGIN_PWD = "mall.user.default.login.pwd";

    /**
     * 后台用户默认头像
     */
    public static final String BOSS_USER_DEFAULT_PORTRAIT = "boss.user.default.portrait";

    /**
     * 商城会员默认头像
     */
    public static final String MALL_USER_DEFAULT_PORTRAIT = "mall.user.default.portrait";

    //  用户积分
    //  -------------------------------------------------------------------------------------------

    /**
     * 会员注册送福分数量
     */
    public static final String MALL_USER_REGISTER_GIVE_SCORE = "mall.user.register.give.score";

    /**
     * 会员注册送余额数量
     */
    public static final String MALL_USER_REGISTER_GIVE_Money = "mall.user.register.give.money";

    /**
     * 会员首次充值送福分数量
     */
    public static final String MALL_USER_FIRST_RECHARGE_GIVE_SCORE = "mall.user.first.recharge.give.score";

    /**
     * 需要充值多少元后才可以签到
     */
    public static final String MALL_USER_SIGN_GIVE_SCORE_NEED = "mall.user.sign.give.score.need";

    /**
     * 会员签到送福分数量
     */
    public static final String MALL_USER_SIGN_GIVE_SCORE = "mall.user.sign.give.score";

    /**
     * 邀请会员送福分数量
     */
    public static final String MALL_USER_INVITE_GIVE_SCORE = "mall.user.invite.give.score";

    /**
     * 消费一元送福分数量
     */
    public static final String MALL_USER_PAY_GIVE_SCORE = "mall.user.pay.give.score";

    /**
     * 会员每次最低充值多少元
     */
    public static final String MALL_USER_RECHARGE_GIVE_NEED = "mall.user.recharge.give.need";

    /**
     * 会员佣金提现手续费
     */
    public static final String MALL_USER_TIXIAN_PROCEDURE = "mall.user.tixian.procedure";

    //  订单相关
    //  -------------------------------------------------------------------------------------------

    /**
     * 订单超时时间
     */
    public static final String MALL_ORDER_OVERTIME = "mall.order.overtime";

    /**
     * 会员使用福分抵扣订单需满足
     */
    public static final String MALL_USER_SCORE_NEED = "mall.user.score.need";

    /**
     * 秒单地址
     */
    public static final String MALL_ORDER_SECOND_ADDRESS = "mall.order.second.address";

    /**
     * 分销提点 1：一级分销，2：二级分销，3：三级分销
     */
    public static final String MALL_ORDER_DISTRIBUTION_REMIND = "mall.order.distribution.remind.";

    /**
     * 每X福分可抵扣Y元
     */
    public static final String MALL_ORDER_SCORE_DEDUCTION = "mall.order.score.deduction";

    //  商品相关
    //  -------------------------------------------------------------------------------------------

    /**
     * 开奖时间（单位：分钟）
     */
    public static final String GOODS_OPEN_WINNING_TIME = "goods.open.winning.time";


    //  App下载地址
    //  -------------------------------------------------------------------------------------------

    /**
     * 安卓 客户端下载地址
     */
    public static final String MOBILE_APP_ANDROID_DOWNLOAD_URL = "mobile.app.android.download.url";

    /**
     * IOS 客户端下载地址
     */
    public static final String MOBILE_APP_IOS_DOWNLOAD_URL = "mobile.app.ios.download.url";

    /**
     * 会员晒单审核通过默认送多少积分
     */
    public static final String MALL_USER_SHARE_ORDER_GIVE = "mall.user.share.order.give";
    /**
     * 是否开启会员中心签到功能
     */
    public static final String MALL_IS_MEMBER_SIGN = "mall.is.member.sign";
    /**
     * 每次充值多少赠送多少积分
     */
    public static final String MALL_USER_RECHARGE_GIVE_SCORE = "mall.user.recharge.give.score";
    /**
     * 每充值X元赠送Y元
     */
    public static final String MALL_USER_RECHARGE_GIVE = "mall.user.recharge.give";
    /**
     * 商城地址二维码
     */
    public static final String MALL_ADDRESS_CODE = "mall.address.code";
    /**
     * 客服工作时间
     */
    public static final String MALL_SERVICE_WORKING_TIME = "mall.service.working.time";

    /**
     * 是否开启流量赠送功能
     */
    public static final String MALL_IS_TRAFFIC_SEND = "mall.is.traffic.send";

    /**
     * 中英文开关
     */
    public static final String CN_SWITCH_US = "cn.switch.us";

    /**
     * 购物车限制添加数量
     */
    public static final String SHOPPING_CART_IS_FULL = "shopping.cart.is.full";

    /**
     * 购物车限制添加数量
     */
    public static final String RECEIVABLES_FUNCTION = "receivables.function";

    /**
     * 商城+元购开关
     */
    public static final String MALL_Switch = "mall.switch";

    /**
     * 分享赚钱背景图
     */
    public static final String MALL_SHARE_BACKGROUNDIMG = "mall.share.backgroundImg";

    /**
     * 图片坐标
     */
    public static final String MALL_IMAGE_COORDINATE = "mall.image.coordinate";

    /**
     *微信支付重定向地址
     */
    public static final String MALL_WXPAY_REDIRECT_URL = "mall.wxpay.redirect.url";

    /**
     *支付成功重定向地址
     */
    public static final String MALL_PAY_SUCCESS_URL = "mall.pay.success.url";

}
