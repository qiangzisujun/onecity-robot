package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/11/4 15:51
 */
@Table(name = "third_logistics_company")
@Data
public class LogisticsCompany implements Serializable {
    private Long id;
    private String code; //物流公司编码
    private String logisticsName; //物流公司名称
    private Date createTime;
    private Date updateTime;
    private Integer status; //第三方是否支持 即时查询
    private Integer flag;  //是否启用
}
