package com.tangchao.shop.serviceImpl;

import com.tangchao.common.constant.ConfigkeyConstant;
import com.tangchao.shop.advice.DataSourceNames;
import com.tangchao.shop.annotation.DataSource;
import com.tangchao.shop.mapper.GoodsStageMapper;
import com.tangchao.shop.mapper.UserConfMapper;
import com.tangchao.shop.pojo.GoodsStage;
import com.tangchao.shop.pojo.UserConf;
import com.tangchao.shop.service.OpenWinningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class OpenWinningServiceImpl implements OpenWinningService {

    @Autowired
    private UserConfMapper confMapper;

    @Autowired
    private GoodsStageMapper goodsStageMapper;

    @Override
    @DataSource(name = DataSourceNames.SECOND)
    public Integer findOpenWinningTime() {
        Integer result = 0;

        UserConf conf = new UserConf();
        conf.setConfKey(ConfigkeyConstant.GOODS_OPEN_WINNING_TIME);
        //	读取配置
        UserConf newConf = confMapper.selectOne(conf);
        if (null != newConf) {
            try {
                result = Integer.parseInt(newConf.getConfValue());
            } catch (NumberFormatException e) {
                System.out.println("后台商品开奖时间的配置有误,请配置一个数字，单位秒");
                System.out.println(e.getMessage());
            }
        }
        if (result < 5) {
            result = 5;
        }
        return result;
    }

    @Override
    public List<GoodsStage> findWaitOpenWinning(Integer isActivity) {
        //  获取开机倒计时/秒
        Integer openTime = this.findOpenWinningTime();
        List<GoodsStage> goodsStageList = this.goodsStageMapper.waitOpenWinning(new Date(),openTime,isActivity);
        return goodsStageList;
    }
}
