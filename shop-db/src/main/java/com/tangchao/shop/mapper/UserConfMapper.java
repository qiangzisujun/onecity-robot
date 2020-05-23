package com.tangchao.shop.mapper;

import com.tangchao.shop.pojo.UserConf;
import tk.mybatis.mapper.common.Mapper;

public interface UserConfMapper extends Mapper<UserConf> {

    int updateBatchConf(UserConf conf);
}
