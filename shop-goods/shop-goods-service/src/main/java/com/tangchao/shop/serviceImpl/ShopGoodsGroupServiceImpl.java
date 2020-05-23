package com.tangchao.shop.serviceImpl;

import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.shop.mapper.ShopGroupValueMapper;
import com.tangchao.shop.mapper.ShopSpecGroupMapper;
import com.tangchao.shop.params.ShopSpecGroupParam;
import com.tangchao.shop.pojo.ShopGroupValue;
import com.tangchao.shop.pojo.ShopSpecGroup;
import com.tangchao.shop.service.ShopGoodsGroupService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2020/3/9 10:50
 */
@Service
public class ShopGoodsGroupServiceImpl implements ShopGoodsGroupService {


    @Autowired
    private ShopSpecGroupMapper specGroupMapper;

    @Autowired
    private ShopGroupValueMapper shopGroupValueMapper;

    @Override
    @Transactional
    public void addGoodsGroup(ShopSpecGroup group) {
        group.setCreateTime(new Date());
        Integer count=specGroupMapper.insertSelectiveGroup(group);

        for (ShopGroupValue val:group.getShopGroupValues()){
            val.setGroupId(group.getId());
            val.setCreateTime(new Date());
            count+=shopGroupValueMapper.insertSelective(val);
        }

        if (count==0){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void updateGoodsGroup(ShopSpecGroup group) {
        Example example = new Example(ShopSpecGroup.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", group.getId());
        int count=specGroupMapper.updateByExampleSelective(group, example);

        if (null!=group.getShopGroupValues()&&group.getShopGroupValues().size()>0){
            //添加规格信息
            for (ShopGroupValue val:group.getShopGroupValues()){
                if (null!=val.getId()&&val.getId()>0){

                    Example example_group = new Example(ShopGroupValue.class);
                    Example.Criteria criteria_group = example_group.createCriteria();
                    criteria_group.andEqualTo("id", val.getId());

                    count+=shopGroupValueMapper.updateByExampleSelective(val, example_group);
                }else {
                    val.setGroupId(group.getId());
                    val.setCreateTime(new Date());
                    count+=shopGroupValueMapper.insertSelective(val);
                }
            }
        }


        if (count==0){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void deleteGoodsType(Long id) {
        Example example = new Example(ShopSpecGroup.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", id);
        int count=specGroupMapper.deleteByExample(example);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public List<ShopSpecGroup> selectGoodsGroup(Long userId) {
        ShopSpecGroup group=new ShopSpecGroup();
        group.setStatus(1);
        List<ShopSpecGroup> list=specGroupMapper.select(group);

        List<Long> groupIds=list.stream().map(t->t.getId()).collect(Collectors.toList());

        if (groupIds.size()>0){
            Example example_group = new Example(ShopGroupValue.class);
            Example.Criteria criteria_group = example_group.createCriteria();
            criteria_group.andEqualTo("status",1);
            criteria_group.andIn("groupId", groupIds);
            List<ShopGroupValue>  groupValueList=shopGroupValueMapper.selectByExample(example_group);
            Map<Long,List<ShopGroupValue>> groupMap=groupValueList.stream().collect(Collectors.groupingBy(s->s.getGroupId()));
            list.forEach(s->s.setShopGroupValues(groupMap.get(s.getId())));
        }
        return list;
    }
}
