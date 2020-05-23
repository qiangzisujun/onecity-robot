package com.tangchao.shop.vo;

import com.tangchao.shop.pojo.Lottery;
import io.swagger.annotations.ApiModel;
import lombok.Data;


@Data
@ApiModel
public class TrendChartVO  {

    //商品期数
    private Integer stageIndex;

    //当前购买数
    private Integer  buyIndex;

    //商品名称
    private String  goodsName;

    //购买总数
    private Integer buySize;

    //商品图片url
    private String  goodsPicture;

    //用户名
    private String userName;

    //商品期次
    private String goodStage;

    //剩余
    private Integer buyOver;


}
