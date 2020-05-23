package com.tangchao.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Class TongjiVisitToppageDTO
 * @Description TODO
 * @Author Aquan
 * @Date 2020/5/8 15:33
 * @Version 1.0
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TongjiVisitToppageDTO {
    private String url;
    private String metrics;
    private Object value;
}
