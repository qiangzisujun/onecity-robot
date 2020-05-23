package com.tangchao.shop.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class GoodsStageDTO {

    @ApiModelProperty(value = "排序：default(热品),priceDesc(价值降序),priceAsc(价值升序),new(新品),full(即将揭晓)", name = "orderBy")
    private String orderBy;

    @ApiModelProperty(value = "页数大小", name = "pageSize")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "页数", name = "pageNo")
    private Integer pageNo = 1;

    @ApiModelProperty(value = "搜索关键字", name = "searchKey")
    private String searchKey;

    @ApiModelProperty(value = "类型", name = "typeId")
    private String typeId;

}
