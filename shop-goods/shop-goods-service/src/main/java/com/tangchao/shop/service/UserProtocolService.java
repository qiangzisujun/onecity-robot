package com.tangchao.shop.service;

import com.tangchao.shop.pojo.UserProtocol;

import java.util.List;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2020/4/2 13:50
 */
public interface UserProtocolService {


    List<UserProtocol> getUserProtocol();

    void  insertUserProtocol(UserProtocol protocol);

    void  updateUserProtocol(UserProtocol protocol);

    void  deleteUserProtocol(Integer id);
}
