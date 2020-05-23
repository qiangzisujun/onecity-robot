package com.tangchao.shop.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel
public class OrderShowVO {

    @ApiModelProperty(value = "商品期数Id", name = "stageId")
    private Integer stageId;

    @ApiModelProperty(value = "中奖标题", name = "title")
    private String title;

    @ApiModelProperty(value = "评价内容", name = "content")
    private String content;

    @ApiModelProperty(value = "评价时间", name = "createTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "点赞次数", name = "praiseNum")
    private Integer praiseNum;

    @ApiModelProperty(value = "用户名", name = "userName")
    private String userName;

    @ApiModelProperty(value = "评论图片", name = "imgUrls")
    private String imgUrls;

    @ApiModelProperty(value = "评论条数", name = "replyNum")
    private Integer replyNum;
}
