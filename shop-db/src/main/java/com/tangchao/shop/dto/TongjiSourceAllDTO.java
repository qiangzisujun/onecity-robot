package com.tangchao.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Class TongjiSourceAllDTO
 * @Description TODO
 * @Author Aquan
 * @Date 2020/5/8 14:54
 * @Version 1.0
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TongjiSourceAllDTO {
    private String sourceName;
    private String elementName;
    private Object value;
}
