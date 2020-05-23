package com.tangchao.shop.pojo;

import lombok.Data;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/12/12 22:49
 */
@Data
public class ResultModel {

    private Integer code;
    private String msg;
    private ResultData data;
}