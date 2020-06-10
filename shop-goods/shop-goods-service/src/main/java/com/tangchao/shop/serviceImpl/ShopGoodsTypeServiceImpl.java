package com.tangchao.shop.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.mapper.ShopGoodsMapper;
import com.tangchao.shop.mapper.ShopGoodsTypeMapper;
import com.tangchao.shop.mapper.ShopSpecificationMapper;
import com.tangchao.shop.pojo.ShopGoods;
import com.tangchao.shop.pojo.ShopGoodsType;
import com.tangchao.shop.pojo.ShopSpecification;
import com.tangchao.shop.service.ShopGoodsTypeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShopGoodsTypeServiceImpl implements ShopGoodsTypeService {

    @Autowired
    private ShopGoodsTypeMapper shopGoodsTypeMapper;

    @Autowired
    private ShopGoodsMapper shopGoodsMapper;

    @Autowired
    private ShopSpecificationMapper shopSpecificationMapper;

    @Override
    public List<ShopGoodsType> goodsTypeList() {
        PageHelper.orderBy("type_sort desc");
        ShopGoodsType type=new ShopGoodsType();
        type.setFlag(0);
        List<ShopGoodsType> typeList=shopGoodsTypeMapper.select(type);
        return typeList;
    }

    @Override
    public void addGoodsType(String typeName, Long typePid, Long userId,String typeNameCN,String typeNameMa) {
        if (typeName == null || typePid==null) {
            throw new CustomerException(ExceptionEnum.GOODS_TYPE_IS_NOT);
        }
        Integer layer = this.getLayerByPid(typePid);// 获取层级
        if (layer == 3) {// 层级最多3层
            throw new CustomerException(ExceptionEnum.NOT_ADD_LAYER);
        }
        ShopGoodsType goodsType = new ShopGoodsType();
        goodsType.setTypeNameZh(typeName);
        goodsType.setTypeNameCn(typeNameCN);
        goodsType.setTypeNameMa(typeNameMa);
        goodsType.setTypePid(typePid);
        goodsType.setTypeLayer(layer + 1);// 设置层级
        Integer maxSort = this.getMaxSortByPid(typePid);// 获取最大的排序值
        goodsType.setTypeSort(maxSort + 1);// 设置最大的排序值
        Date now = new Date();
        goodsType.setCreateTime(now);// 设置创建时间
        goodsType.setLastModifyTime(now);// 设置最后修改时间
        goodsType.setLastModifyId(userId);
        goodsType.setCreateId(userId);
        Integer i = shopGoodsTypeMapper.insertSelective(goodsType);
        if (i.equals(0)){
            throw new CustomerException(ExceptionEnum.GOODS_TYPE_ADD_ERROR);
        }
    }

    @Override
    public void updateGoodsType(ShopGoodsType goodsType) {
        if (goodsType.getTypeNameZh() == null || goodsType.getTypePid()==null) {
            throw new CustomerException(ExceptionEnum.GOODS_TYPE_IS_NOT);
        }
        Long pid = goodsType.getTypePid();
        Long oldPid = this.shopGoodsTypeMapper.selectPidById(goodsType.getId());
        if (pid != oldPid) {
            if (isHasSubType(goodsType.getId())) {// 有子类型，不可修改
                throw new CustomerException(ExceptionEnum.HAS_SUB_TYPE);
            }
            Integer layer = this.getLayerByPid(pid);// 获取层级
            if (layer == 3) {// 层级最多3层
                throw new CustomerException(ExceptionEnum.NOT_ADD_LAYER);
            }
            goodsType.setTypeLayer(layer + 1);// 设置层级
            Integer maxSort = this.getMaxSortByPid(pid);// 获取最大的排序值
            goodsType.setTypeSort(maxSort + 1);// 设置最大的排序值
        }
        goodsType.setLastModifyTime(new Date());// 设置最后修改时间
        Integer i = this.shopGoodsTypeMapper.updateByPrimaryKeySelective(goodsType);
        if (i.equals(0)){
            throw new CustomerException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }
    }

    @Override
    public void updateSort(Long id_1, Long id_2) {
        if (id_1 == null) {
            throw new CustomerException(ExceptionEnum.SORT_NOT_ERROR);
        }
        Date now = new Date();
        ShopGoodsType type_1 = new ShopGoodsType();
        type_1.setId(id_1);
        type_1.setTypeSort(this.getSortById(id_2));
        type_1.setLastModifyTime(now);
        ShopGoodsType type_2 = new ShopGoodsType();
        type_2.setId(id_2);
        type_2.setTypeSort(this.getSortById(id_1));
        type_2.setLastModifyTime(now);
        this.shopGoodsTypeMapper.updateByPrimaryKeySelective(type_1);
        this.shopGoodsTypeMapper.updateByPrimaryKeySelective(type_2);
    }

    @Override
    public void deleteGoodsType(Long id) {
        if (id == null) {
            throw new CustomerException(ExceptionEnum.SORT_NOT_ERROR);
        }
        ShopGoodsType type = new ShopGoodsType();
        type.setId(id);
        type.setFlag(-1);
        type.setLastModifyTime(new Date());
        Integer i = shopGoodsTypeMapper.updateByPrimaryKeySelective(type);
        if (i.equals(0)){
            // 遍历删除子类型
            String ids = id + "";
            List<Long> idList = this.getSubIds(ids);
            while (idList != null && !idList.isEmpty()) {
                ids = "-1";
                for (Long delId : idList) {
                    type.setId(delId);
                    this.shopGoodsTypeMapper.updateByPrimaryKeySelective(type);
                    ids = ids + "," + delId;
                }
                idList = this.getSubIds(ids);
            }
            throw new CustomerException(ExceptionEnum.SORT_NOT_ERROR);
        }
    }

    @Override
    public PageResult<ShopGoods> getUserCouponList(Integer pageNo, Integer pageSize, Integer typeId) {

        Example example = new Example(ShopGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("datalevel", 1);
        criteria.andEqualTo("saleable", 1);
        criteria.andEqualTo("goodsTypeId", typeId);
        PageHelper.startPage(pageNo,pageSize);
        PageHelper.orderBy("sort");
        List<ShopGoods> shopGoods = shopGoodsMapper.selectByExample(example);
        for ( ShopGoods shopGood : shopGoods) {
            String images = shopGood.getImages();
            String[] split = images.split(",");
            shopGood.setImages(split[0]);
        }

        // TODO: 2020/3/16 Aquan：添加shopGoods判断，为空时不执行下面规格组查询
        if (shopGoods.size()>0) {
            List<Long> shopIds = shopGoods.stream().map(s -> s.getId()).collect(Collectors.toList());
            Example example_spec = new Example(ShopSpecification.class);
            Example.Criteria criteria_spec = example_spec.createCriteria();
            criteria_spec.andEqualTo("status", 1);
            if (shopIds.size() > 0) {
                criteria_spec.andIn("goodsId", shopIds);
            }
            List<ShopSpecification> specificationList = shopSpecificationMapper.selectByExample(example_spec);
            if (specificationList.size() > 0) {
                Map<Long, List<ShopSpecification>> specMap = specificationList.stream().collect(Collectors.groupingBy(ShopSpecification::getGoodsId));
                shopGoods.forEach(s -> s.setSpecificationList(specMap.get(s.getId())));
            }
        }

        PageInfo<ShopGoods> goodsPageInfo = new PageInfo<>(shopGoods);
        return new PageResult<>(goodsPageInfo.getTotal(),shopGoods);
    }

    /* * 获取最大排序值
     *
     * @param pid
     *  父级id
     * @return maxSort
     */

    /**
     * 获取最大排序值
     * @param父级id
     * @return maxSort
     */
    private Integer getMaxSortByPid(Long pid) {
        Integer maxSort = this.shopGoodsTypeMapper.selectMaxSortByPid(pid);
        if (maxSort == null) {
            maxSort = 0;
        }
        return maxSort;
    }
    /**
     * 获取父级层级
     *
     * @param pid
     *            父级id
     * @return layer
     */
    private Integer getLayerByPid(Long pid) {
        ShopGoodsType goodsType = shopGoodsTypeMapper.selectByPrimaryKey(pid);
        if (goodsType == null) {
            return 0;
        }
        return goodsType.getTypeLayer();
    }

    private boolean isHasSubType(Long id) {
        List<Long> idList = this.shopGoodsTypeMapper.selectSubIdById(id);
        return (idList != null && idList.size() > 0);
    }

    private Integer getSortById(Long id) {
        return this.shopGoodsTypeMapper.selectSortById(id);
    }

    private List<Long> getSubIds(String ids) {
        return this.shopGoodsTypeMapper.selectSubIdsByIds(ids);
    }
}
