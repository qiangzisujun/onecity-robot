package com.tangchao.shop.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.shop.mapper.GoodsTypeMapper;
import com.tangchao.shop.pojo.GoodsType;
import com.tangchao.shop.service.GoodsTypeService;
import com.tangchao.shop.util.TreeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class GoodsTypeServiceImpl implements GoodsTypeService {

    @Autowired
    private GoodsTypeMapper goodsTypeMapper;

    @Override
    public List<GoodsType> goodsTypeList() {
        PageHelper.orderBy("type_sort desc");
        GoodsType type=new GoodsType();
        type.setFlag(0);
        List<GoodsType> typeList=goodsTypeMapper.select(type);
        return typeList;
    }

    /**
     * 获取父级层级
     *
     * @param pid
     *            父级id
     * @return layer
     */
    private Integer getLayerByPid(Long pid) {
        GoodsType goodsType = goodsTypeMapper.selectByPrimaryKey(pid);
        if (goodsType == null) {
            return 0;
        }
        return goodsType.getTypeLayer();
    }

    /**
     * 获取最大排序值
     *
     * @param pid
     *            父级id
     * @return maxSort
     */
    private Integer getMaxSortByPid(Long pid) {
        Integer maxSort = this.goodsTypeMapper.selectMaxSortByPid(pid);
        if (maxSort == null) {
            maxSort = 0;
        }
        return maxSort;
    }

    @Override
    public void addGoodsType(String  typeName,Long  typePid,Long userId) {
        if (typeName == null || typePid==null) {
            throw new CustomerException(ExceptionEnum.GOODS_TYPE_IS_NOT);
        }
        Integer layer = this.getLayerByPid(typePid);// 获取层级
        if (layer == 3) {// 层级最多3层
            throw new CustomerException(ExceptionEnum.NOT_ADD_LAYER);
        }
        GoodsType goodsType = new GoodsType();
        goodsType.setTypeName(typeName);
        goodsType.setTypePid(typePid);
        goodsType.setTypeLayer(layer + 1);// 设置层级
        Integer maxSort = this.getMaxSortByPid(typePid);// 获取最大的排序值
        goodsType.setTypeSort(maxSort + 1);// 设置最大的排序值
        Date now = new Date();
        goodsType.setCreateTime(now);// 设置创建时间
        goodsType.setLastModifyTime(now);// 设置最后修改时间
        goodsType.setLastModifyId(userId);
        goodsType.setCreateId(userId);
        Integer i = goodsTypeMapper.insertSelective(goodsType);
        if (i.equals(0)){
            throw new CustomerException(ExceptionEnum.GOODS_TYPE_ADD_ERROR);
        }
    }

    private boolean isHasSubType(Long id) {
        List<Long> idList = this.goodsTypeMapper.selectSubIdById(id);
        return (idList != null && idList.size() > 0);
    }



    @Override
    public void updateGoodsType(GoodsType goodsType) {
        if (goodsType.getTypeName() == null || goodsType.getTypePid()==null) {
            throw new CustomerException(ExceptionEnum.GOODS_TYPE_IS_NOT);
        }
        Long pid = goodsType.getTypePid();
        Long oldPid = this.goodsTypeMapper.selectPidById(goodsType.getId());
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
        Integer i = this.goodsTypeMapper.updateByPrimaryKeySelective(goodsType);
        if (i.equals(0)){
            throw new CustomerException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }

    }

    private Integer getSortById(Long id) {
        return this.goodsTypeMapper.selectSortById(id);
    }

    @Override
    public void updateSort(Long id_1, Long id_2) {
        if (id_1 == null) {
            throw new CustomerException(ExceptionEnum.SORT_NOT_ERROR);
        }
        Date now = new Date();
        GoodsType type_1 = new GoodsType();
        type_1.setId(id_1);
        type_1.setTypeSort(this.getSortById(id_2));
        type_1.setLastModifyTime(now);
        GoodsType type_2 = new GoodsType();
        type_2.setId(id_2);
        type_2.setTypeSort(this.getSortById(id_1));
        type_2.setLastModifyTime(now);
        this.goodsTypeMapper.updateByPrimaryKeySelective(type_1);
        this.goodsTypeMapper.updateByPrimaryKeySelective(type_2);
    }

    private List<Long> getSubIds(String ids) {
        return this.goodsTypeMapper.selectSubIdsByIds(ids);
    }

    @Override
    public void delectGoodsType(Long id) {
        if (id == null) {
            throw new CustomerException(ExceptionEnum.SORT_NOT_ERROR);
        }
        GoodsType type = new GoodsType();
        type.setId(id);
        type.setFlag(-1);
        type.setLastModifyTime(new Date());
        Integer i = goodsTypeMapper.updateByPrimaryKeySelective(type);
        if (i.equals(0)){
            // 遍历删除子类型
            String ids = id + "";
            List<Long> idList = this.getSubIds(ids);
            while (idList != null && !idList.isEmpty()) {
                ids = "-1";
                for (Long delId : idList) {
                    type.setId(delId);
                    this.goodsTypeMapper.updateByPrimaryKeySelective(type);
                    ids = ids + "," + delId;
                }
                idList = this.getSubIds(ids);
            }
            throw new CustomerException(ExceptionEnum.SORT_NOT_ERROR);
        }
    }
}
