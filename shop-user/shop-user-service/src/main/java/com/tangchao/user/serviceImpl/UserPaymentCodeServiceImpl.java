package com.tangchao.user.serviceImpl;

import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.shop.interceptor.UserInterceptor;
import com.tangchao.shop.mapper.UserPaymentCodeMapper;
import com.tangchao.shop.params.UpdateUserPaymentCodeParam;
import com.tangchao.shop.params.UserPaymentCodeParam;
import com.tangchao.shop.pojo.UserInfo;
import com.tangchao.shop.pojo.UserPaymentCode;
import com.tangchao.user.service.UserPaymentCodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @Class UserPaymentCodeServiceImpl
 * @Description TODO
 * @Author Aquan
 * @Date 2020/3/27 11:58
 * @Version 1.0
 **/
@Service
public class UserPaymentCodeServiceImpl implements UserPaymentCodeService {

    private final UserPaymentCodeMapper userPaymentCodeMapper;

    public UserPaymentCodeServiceImpl(UserPaymentCodeMapper userPaymentCodeMapper) {
        this.userPaymentCodeMapper = userPaymentCodeMapper;
    }

    @Override
    public ResponseEntity getUserPaymentCode() {
        UserInfo user = UserInterceptor.getUserInfo();
        if (user == null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        Example example = new Example(UserPaymentCode.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", 0);
        criteria.andEqualTo("userCode", user.getUserCode());
        List<UserPaymentCode> userPaymentCodes = userPaymentCodeMapper.selectByExample(example);
        return ResponseEntity.ok(userPaymentCodes);
    }

    @Override
    public ResponseEntity bind(UserPaymentCodeParam userPaymentCodeParam) {
        UserInfo user = UserInterceptor.getUserInfo();
        if (user == null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        Example example = new Example(UserPaymentCode.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userCode", user.getUserCode());
        criteria.andEqualTo("type", userPaymentCodeParam.getType());
        criteria.andEqualTo("status", 0);
        UserPaymentCode userPaymentCode = userPaymentCodeMapper.selectOneByExample(example);
        if (userPaymentCode != null) {
            throw new CustomerException(ExceptionEnum.ACCOUNT_IS_BOUND);
        }
        UserPaymentCode inster = new UserPaymentCode();
        inster.setUserCode(String.valueOf(user.getUserCode()));
        inster.setNumber(userPaymentCodeParam.getNumber());
        inster.setUsername(userPaymentCodeParam.getUsername());
        inster.setPaymentCodeImg(userPaymentCodeParam.getPaymentCodeImg());
        inster.setType(userPaymentCodeParam.getType());
        userPaymentCodeMapper.insertSelective(inster);
        return ResponseEntity.ok("绑定成功");
    }

    @Override
    public ResponseEntity getUserPaymentCodeById(String id) {
        UserPaymentCode userPaymentCode = userPaymentCodeMapper.selectByPrimaryKey(id);
        return ResponseEntity.ok(userPaymentCode);
    }

    @Override
    public ResponseEntity updateBind(UpdateUserPaymentCodeParam updateUserPaymentCodeParam) {
        UserInfo user = UserInterceptor.getUserInfo();
        if (user == null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        UserPaymentCode userPaymentCode = new UserPaymentCode();
        userPaymentCode.setId(updateUserPaymentCodeParam.getId());
        userPaymentCode.setNumber(updateUserPaymentCodeParam.getNumber());
        userPaymentCode.setUsername(updateUserPaymentCodeParam.getUsername());
        userPaymentCode.setPaymentCodeImg(updateUserPaymentCodeParam.getPaymentCodeImg());
        userPaymentCodeMapper.updateByPrimaryKeySelective(userPaymentCode);
        return ResponseEntity.ok("更新成功");
    }


}
