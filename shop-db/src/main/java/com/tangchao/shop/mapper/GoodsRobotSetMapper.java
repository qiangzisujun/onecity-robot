package com.tangchao.shop.mapper;

import com.tangchao.shop.pojo.GoodsRobot;
import com.tangchao.shop.pojo.GoodsRobotSet;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface GoodsRobotSetMapper extends Mapper<GoodsRobotSet> {

    /**
     * 机器人列表
     * @param goodsName
     * @param goodsNo
     * @param typeId
     * @return
     */
    List<Map> selectRobotAndSetList(@Param("goodsName") String goodsName,@Param("goodsNo") String goodsNo,@Param("typeId") Integer typeId);

    /**
     * 修改最大购买数
     * @param setId 配置id
     */
    void updateRobotMaxCountBySetId(@Param(value = "setId") Long setId);

    List<Map<String,Object>> getGoodsSetList();

    List<GoodsRobot> selectList(String type);
}
