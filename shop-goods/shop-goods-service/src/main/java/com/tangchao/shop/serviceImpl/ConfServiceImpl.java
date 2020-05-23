package com.tangchao.shop.serviceImpl;

import com.tangchao.common.constant.ConfigkeyConstant;
import com.tangchao.common.utils.ArithUtil;
import com.tangchao.shop.mapper.UserConfMapper;
import com.tangchao.shop.pojo.UserConf;
import com.tangchao.shop.service.ConfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConfServiceImpl implements ConfService {

    @Autowired
    private UserConfMapper userConfMapper;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<String> selectHotSearchKey() {
        UserConf conf = new UserConf();
        conf.setConfKey("hot.search.goods.list");
        conf.setFlag(0);
        List<UserConf> newConf = userConfMapper.select(conf);
        List<String> keyList = new ArrayList<>();
        if (newConf.size() > 0) {// 查询默认头像
            String[] arr = newConf.get(0).getConfValue().split(",");
            for (String c : arr) {
                keyList.add(c);
            }
        }
        return keyList;
    }

    @Override
    public double payAvailableScore(double money) {
        UserConf conf = new UserConf();
        conf.setConfKey(ConfigkeyConstant.MALL_USER_PAY_GIVE_SCORE);
        conf.setFlag(0);
        List<UserConf> newConf = userConfMapper.select(conf);
        conf = newConf.get(0);
        try {
            String[] array = conf.getConfValue().split("/");
            if (array.length != 2) {
                throw new Exception("配置格式：X/Y,X为消费最低金额,Y为赠送福分数量");
            }
            double where = Double.parseDouble(array[0]);
            double score = Double.parseDouble(array[1]);
            if (money >= where) {
                // 计算可获得积分数量
                long x = (long) (money / where);
                return ArithUtil.mul(x, score);
            }
        } catch (Exception e) {
            this.logger.error("配置错误：每消费X元可获赠Y数量福分");
            this.logger.error(e.getMessage());
        }
        return 0;
    }

    @Override
    public UserConf selectCmsValue(String type) {
        UserConf conf=new UserConf();
        conf.setConfKey(type);
        conf.setFlag(0);
        conf= userConfMapper.selectOne(conf);
        if (conf!=null){
            return conf;
        }
        return null;
    }
}
