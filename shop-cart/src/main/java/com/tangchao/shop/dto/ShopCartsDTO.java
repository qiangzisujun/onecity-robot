package com.tangchao.shop.dto;

import com.tangchao.shop.pojo.ShopCart;
import lombok.Data;

import java.util.List;

/**
 * @Class ShopCartsDTO
 * @Description TODO
 * @Author Aquan
 * @Date 2020/1/9 19:17
 * @Version 1.0
 **/
@Data
public class ShopCartsDTO {

    private Long total;
    private Long totalIntegral;
    private List<ShopCart> shopCartList;

}
