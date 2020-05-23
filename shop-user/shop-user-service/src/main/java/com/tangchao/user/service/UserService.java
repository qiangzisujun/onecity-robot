package com.tangchao.user.service;

import com.tangchao.shop.dto.UserDTO;

public interface UserService {

    UserDTO getShopUserDetail(Long userCode);
}
