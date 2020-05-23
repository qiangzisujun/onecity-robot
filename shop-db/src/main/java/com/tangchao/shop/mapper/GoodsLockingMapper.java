package com.tangchao.shop.mapper;

import com.tangchao.shop.pojo.GoodsLocking;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

public interface GoodsLockingMapper extends Mapper<GoodsLocking> {

    @Select("SELECT IF(SUM(num) IS NULL, 0, SUM(num))  FROM goods_locking WHERE flag = 0 AND stage_id = #{stageId} AND FALSE")
    int findGoodsLockingCount(@Param("stageId") Long stageId);

    /**
     * 解除某个用户，某期次的商品锁单
     *
     * @param stageId
     * @param userCode
     * @return
     */
    @Update("UPDATE goods_locking SET `flag` = '-1',`update_time` = NOW() WHERE stage_id = #{stageId} AND user_code = #{userCode}")
    int updateOneGoodsLocking(@Param("stageId") Long stageId, @Param("userCode") Long userCode);

    @Delete("DELETE FROM goods_locking WHERE DATE(update_time) < DATE(DATE_SUB(NOW(), INTERVAL 3 DAY))")
    Integer deleteThreeDayFrontRecord();

    @Delete("DELETE FROM goods_locking WHERE flag = 0 AND create_time <= (CURRENT_TIMESTAMP () + INTERVAL - 5 MINUTE)")
    Integer deleteThreeMinuteFrontRecord();
}
