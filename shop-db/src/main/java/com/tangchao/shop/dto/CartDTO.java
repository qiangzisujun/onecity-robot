package com.tangchao.shop.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class CartDTO implements Serializable {

    private String goodsNo;
    private Integer number;
   /* private Integer id;
    private String img;
    private Integer limited;
    private String name;
    private Double price;
    private Boolean selected;*/
}
