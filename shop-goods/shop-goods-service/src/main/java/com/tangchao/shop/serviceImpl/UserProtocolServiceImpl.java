package com.tangchao.shop.serviceImpl;

import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.shop.mapper.UserProtocolMapper;
import com.tangchao.shop.pojo.UserProtocol;
import com.tangchao.shop.service.UserProtocolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2020/4/2 13:52
 */
@Service
public class UserProtocolServiceImpl implements UserProtocolService {

    @Autowired
    private UserProtocolMapper userProtocolMapper;

    @Override
    public List<UserProtocol> getUserProtocol() {
        UserProtocol protocol=new UserProtocol();
        protocol.setFlag(0);
        List<UserProtocol> list=userProtocolMapper.select(protocol);
        return list;
    }

    @Override
    public void insertUserProtocol(UserProtocol protocol) {
        if (protocol==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        protocol.setFlag(0);
        protocol.setCreateTime(new Date());
        protocol.setLastModifyTime(new Date());
        userProtocolMapper.insertSelective(protocol);
    }

    @Override
    public void updateUserProtocol(UserProtocol protocol) {
        if (protocol==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        Example example=new Example(UserProtocol.class);
        example.createCriteria().andEqualTo("id",protocol.getId());
        userProtocolMapper.updateByExampleSelective(protocol,example);
    }

    @Override
    public void deleteUserProtocol(Integer id) {
        userProtocolMapper.deleteByPrimaryKey(id);
    }
}
