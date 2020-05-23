package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2020/3/3 15:57
 */
@Table(name = "shop_specification")
@Data
public class ShopSpecification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//主键自增Id
    private String goodsSpecs;//规格属性json格式
    private Long goodsId;//商品id
    private BigDecimal price;//价格
    private BigDecimal priceForme;//原价
    private Integer stock;//商品库存
    private Integer status;//状态0,删除，1正常

    private String articleNumber;//货号
    private String img;//图片
}
