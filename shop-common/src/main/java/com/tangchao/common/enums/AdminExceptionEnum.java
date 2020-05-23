package com.tangchao.common.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum  AdminExceptionEnum {


    GOODS_SAVE_ERROR(500,"开奖失败!"),
    REGISTERED_NOT_ERROR(500,"注册失败！")
    ;
    private int code;
    private String msg;
}
