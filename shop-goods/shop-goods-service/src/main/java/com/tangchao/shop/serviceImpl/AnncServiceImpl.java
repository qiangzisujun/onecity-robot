package com.tangchao.shop.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.tangchao.shop.advice.DataSourceNames;
import com.tangchao.shop.annotation.DataSource;
import com.tangchao.shop.mapper.AnncMapper;
import com.tangchao.shop.pojo.Annc;
import com.tangchao.shop.service.AnncService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class AnncServiceImpl implements AnncService {


    @Resource
    private AnncMapper anncMapper;

    @Override
    @DataSource(name = DataSourceNames.SECOND)
    public Annc findNewestAnnc() {
        Example example = new Example(Annc.class);
        Example.Criteria criteria = example.createCriteria();
        // 设置查询条件
        criteria.andNotEqualTo("flag", -1);
        criteria.andEqualTo("isShow", 1);
        // 根据修改时间降序,只查询一条
        PageHelper.orderBy(" create_time DESC");
        PageHelper.startPage(1, 1);
        // 查询
        List<Annc> anncList = this.anncMapper.selectByExample(example);
        if (anncList.size() > 0) {
            return anncList.get(0);
        }
        return null;
    }

    @Override
    @DataSource(name = DataSourceNames.SECOND)
    public Annc findUserNewestAnnc() {
        Example example = new Example(Annc.class);
        Example.Criteria criteria = example.createCriteria();
        // 设置查询条件
        criteria.andNotEqualTo("flag", -1);
        criteria.andEqualTo("title", "用户协议");
        // 根据修改时间降序,只查询一条
        PageHelper.orderBy(" create_time DESC");
        PageHelper.startPage(1, 1);
        // 查询
        List<Annc> anncList = this.anncMapper.selectByExample(example);
        if (anncList.size() > 0) {
            return anncList.get(0);
        }
        return null;
    }

    @Override
    @DataSource(name = DataSourceNames.SECOND)
    public List<Annc> selectHistoryAnnc() {
        Example example = new Example(Annc.class);
        Example.Criteria criteria = example.createCriteria();
        // 设置查询条件
        //criteria.andEqualTo("flag", 0);
        criteria.andEqualTo("isShow", 1);
        /*if (createTime != null) {
            criteria.andLessThan("createTime", createTime);
        }*/
        PageHelper.orderBy("create_time desc");
        return this.anncMapper.selectAll();
    }
}
