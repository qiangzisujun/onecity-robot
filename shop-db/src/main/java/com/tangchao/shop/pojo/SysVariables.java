package com.tangchao.shop.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @Class SysVariables
 * @Description TODO
 * @Author Aquan
 * @Date 2020/5/6 10:27
 * @Version 1.0
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sys_variables")
public class SysVariables {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sysKey;

    private String sysValue;

    private String description;

    private Date createTime;

    private Date updateTime;

    private Integer datalevel;

}
