package com.tangchao.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Class TongjiDTO
 * @Description TODO
 * @Author Aquan
 * @Date 2020/5/7 18:28
 * @Version 1.0
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TongjiDTO {

    private String name;
    private String date;
    private Object value;

}
