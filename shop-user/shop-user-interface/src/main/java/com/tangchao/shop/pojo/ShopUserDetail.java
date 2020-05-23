package com.tangchao.shop.pojo;


import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "shop_user_detail")
@Data
public class ShopUserDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userCode;
    private String nickName;
    private String avatarUrl;
    private Long integral;
    private String signature;
}
