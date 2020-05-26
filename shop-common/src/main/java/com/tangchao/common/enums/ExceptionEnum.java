package com.tangchao.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnum {

    //特定状态码  501:用户已封禁,
    INVALID_USERNAME_PASSWORD(4001, "error_4001"),////无效用户名或密码
    INVALID_USER_NOT_FOND(4002, "error_4002"),//用户不存在
    PASSWORD_NOT_ERROR(4003, "error_4003"),//密码不正确
    INVALID_USER_BANNED(4004, "error_4004"),//您的账户已被冻结,请联系客服工作人员
    INVALID_VERIFY_CODE(4005, "error_4005"),//无效验证码
    USER_PHONE_NOTERROR(4006, "error_4006"),//手机号码错误
    INVALID__PASSWORD_LENGTH(4007, "error_4007"),//密码长度6-16位
    USER_PHONE_NOT_ERROR(4008, "error_4008"),//手机号没注册！
    USER_PHONE_ALREADY_EXIST(4009, "error_4009"),//手机号已注册！
    INVALID_CART_DATA_TYPE(4010, "error_4010"),//参数不对！
    USER_NOT_AUTHORIZED(4011, "error_4011"),//用户未登录!
    USER_ACCOUNT_SETTINGS(4012, "error_4012"),//设置失败
    GOODS_NOT_FOND(4013, "error_4013"),//商品不存在
    CART_NOT_FOUND(4014, "error_4014"),//购物车为空
    GOODS_DETAIL_NOT_FOND(4015, "error_4015"),//购买商品不能为空
    CREATE_ORDER_ERROR(4016, "error_4016"),//创建订单失败
    ADDRESS_SAVE_ERROR(4017, "error_4017"),//新增地址失败
    ORDER_NOT_ERROR(4018, "error_4018"),//订单不存在
    ADDRESS_NOT_FOND(4019, "error_4019"),//收货地址不存在
    UPDATE_CUSTOMER_ERROR(4020, "error_4020"),//积分获取失败
    WITHDRAW_CUSTOMER_ERROR(4021, "error_4021"),//提现失败
    STOCK_INSUFFICIENT_ERROR(4022, "error_4022"),//库存不足
    UPPER_LIMIT_OF_PURCHASE(4023, "error_4023"),//包尾资源不足
    LIMIT_PURCHASE_LIMIT(4024, "error_4024"),//已达本团购买次数上限
    SHOPPING_CART_IS_FULL(4025, "error_4025"),//购物车数量已经存满啦
    COLLECTION_NOT_ERROR(4026, "error_4026"),//商品收藏失败
    COLLECTION_IS_EXIT(4027, "error_4027"),//商品已存在收藏列表
    ORDER_PAYMENT(4028, "error_4028"),//订单已付款
    ORDER_CANCEL(4029, "error_4029"),//订单已取消
    ORDER_TIMEOUT(4030, "error_4030"),//订单已超时
    USER_BALANCE_INSUFFICIENT(4031, "error_4031"),//您的抽奖次数不足
    CUSTOMER_BALANCE_INSUFFICIENT(4032, "error_4032"),//余额满10元时才可以申请提现
    GOODS_IS_LOTTERY(4033, "error_4033"),//商品已开奖
    ORDER_PAYMENT_ERROR(4034, "error_4034"),//订单支付失败
    GOODS_LOTTERY_ERROR(4035, "error_4035"),//商品正在开奖
    SELL_IS_DOWN(4036, "error_4036"),//商品已下架
    INV_IS_ZERO(4037, "error_4037"),//商品库存为0
    STAGE_IS_END(4038, "error_4038"),//商品已到期
    INTEGRAL_NOT_ERROR(4039, "error_4039"),//积分赠送失败
    INVALID_FILE_TYPE(4040, "error_4040"),//无效的文件类型
    UPLOAD_FILE_ERROR(4041, "error_4041"),//文件上传失败
    INVALID_NOPASSWORD(4042, "error_4042"),//密码错误
    INVALID_FAILEDPASSWORD(4043, "error_4043"),//密码错误
    USER_IS_EXIT(4044, "error_4044"),//用户已存在
    USER_NOT_EXIT(4045, "error_4045"),//用户不存在，请先注册!
    SMS_SEND_ERROR(4046, "error_4046"),//短信发送失败
    SMS_CODE_ERROR(4047, "error_4047"),//验证码错误
    SMS_FREQUENTLY(4048, "error_4048"),//发送短信验证码频率过高
    OPEN_LOTTERY_ERROR(4049, "error_4049"),//开奖失败
    REGISTERED_NOT_ERROR(4050, "error_4050"),//注册失败
    GOODS_IS_NOT(4051, "error_4051"),//请输入商品信息
    GOODS_DELECT_ERROR(4052, "error_4052"),//删除商品失败
    GOODS_ADD_ERROR(4053, "error_4053"),//添加商品失败
    GOODS_UPDATE_ERROR(4054, "error_4054"),//修改失败
    NOT_ADD_LAYER(4055, "error_4055"),//不能增加的层级
    GOODS_TYPE_ADD_ERROR(4056, "error_4056"),//添加商品分类失败
    GOODS_TYPE_IS_NOT_ERROR(4057, "error_4057"),//添加商品分类失败
    GOODS_TYPE_IS_NOT(4058, "error_4058"),//请输入商品分类信息
    HAS_SUB_TYPE(4059, "error_4059"),//有子类型，不可修改
    SORT_NOT_ERROR(4060, "error_4060"),//异常错误
    SORT_DELECT_NOT_ERROR(4061, "error_4061"),//分类删除错失败
    PAYMENT_CODE_NOT_EXIST(4062, "error_4062"),//收款码不存在
    CUSTOMER_SERVICE_NOT_EXIST(4063, "error_4063"),//客服充值不存在，请联系管理员！
    OPERATING_FAIL(4064, "error_4064"),//操作失败！
    GOODS_NO_IS_UP(4065, "error_4065"),//产品还未上架
    USER_ACCOUNT_BANNED(4066, "error_4066"),//用户账号已封禁,请联系管理员!
    REVIEW_SAVE_ERROR(4067, "error_4067"),//审核失败！
    STATE_SELECT_NO_1(4068, "error_4068"),//审核通过,才能确认提现
    STATE_SELECT_3(4069, "error_4069"),//您已经确认过提现了
    STATE_SELECT(4070, "error_4070"),//审核失败,请选择状态是待审核的提现申请记录
    CONFIG_NOT_FOND(4071, "error_4071"),//配置信息不存在
    SMSType_NOT_FOND(4072, "error_4072"),//短信类型不存在
    TRANSFER_RECORD_NOT_FOND(4073, "error_4073"),//调用记录不存在
    ROBOT_TASK_EXIST(4074, "error_4074"),//机器人任务已存在
    RECHARGE_SAVE_ERROR(4075, "error_4075"),//充值失败
    SHARE_MAKE_MONEY_ERROR(4076, "error_4076"),//分享赚钱暂不可使用，请联系管理员
    USER_SIGN_OUT(4077, "error_4077"),//退出登录
    UNAUTHORIZED    (4078, "error_4078"),//无权访问
    GOODS_NOT_EXIST(4079, "error_4079"),//优惠券不存在
    ACCOUNT_IS_BOUND(4080, "error_4080"),//账号已绑定了
    COLLECTION_NOT_FOND(4081, "error_4081"),//收款信息不存在
    PHONE_VIRTUAL_ACCOUNT(4082, "error_4082");//收款信息不存在
    ;
    private int code;
    private String msg;
}
