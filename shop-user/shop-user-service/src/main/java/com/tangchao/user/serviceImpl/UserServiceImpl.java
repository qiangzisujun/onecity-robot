package com.tangchao.user.serviceImpl;

import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.shop.advice.DataSourceNames;
import com.tangchao.shop.annotation.DataSource;
import com.tangchao.shop.dto.UserDTO;
import com.tangchao.shop.mapper.ShopUserDetailMapper;
import com.tangchao.shop.pojo.ShopUserDetail;
import com.tangchao.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private ShopUserDetailMapper detailMapper;

    @Override
    @DataSource(name = DataSourceNames.FIRST)
    public UserDTO getShopUserDetail(Long userCode) {
        if (userCode == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        ShopUserDetail user = new ShopUserDetail();
        user.setUserCode(userCode);
        user = detailMapper.selectOne(user);
        UserDTO dto = new UserDTO();
        if (user!= null) {
            dto.setAvatarUrl(user.getAvatarUrl());
            dto.setIntegral(user.getIntegral());
            dto.setNickName(user.getNickName());
            dto.setSignature(user.getSignature());
        }
        return dto;
    }
}
