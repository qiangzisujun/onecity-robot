package com.tangchao.shop.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel
public class ShopOrderVO {

    @ApiModelProperty(value = "下单时间", name = "createTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @ApiModelProperty(value = "单价", name = "price")
    private Long price;
    @ApiModelProperty(value = "图片", name = "images")
    private String images;
    @ApiModelProperty(value = "商品名称", name = "goodsName")
    private String goodsName;
    @ApiModelProperty(value = "规格名称", name = "specificationsName")
    private String specificationsName;
    @ApiModelProperty(value = "数量", name = "num")
    private Integer num;

    @ApiModelProperty(value = "状态：1、未付款 2、已付款,未发货 3、已发货,未确认 4、交易成功 5、交易关闭 6、已评价,7,用户已取消", name = "status")
    private Integer status;

    @ApiModelProperty(value = "订单id", name = "orderId")
    private Long orderId;
}
