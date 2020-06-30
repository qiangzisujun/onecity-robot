package com.tangchao.user.service;

import java.util.Map;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2020/6/19 14:35
 */
public interface CustomerSignService {

    Map<String,Object> insertCustomerSignRecord();

    Map<String,Object> isCustomerSignIn();
}
