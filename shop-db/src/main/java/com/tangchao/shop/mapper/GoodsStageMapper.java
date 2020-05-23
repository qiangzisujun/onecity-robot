package com.tangchao.shop.mapper;

import com.tangchao.shop.pojo.GoodsStage;
import com.tangchao.shop.vo.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface GoodsStageMapper extends Mapper<GoodsStage> {


    List<GoodsStage> selectOpeningGoodsList(@Param(value = "awardTime") Date awardTime);

    /**
     * 查询开奖的商品期数
     *
     * @param fullTime 查询参数，满团时间
     * @return List<Map < String, Object>>
     */
    List<GoodsLotteryVO> selectOpenGoodsList(@Param(value = "fullTime") Date fullTime);

    /**
     * 查询商品类型集合
     *
     * @return
     */
    List<GoodsTypeVO> selectGoodsTypeList();

    GoodsStageInfoVO getGoodsStageByNo(Map<String, Object> map);

    Integer selectMaxIndexByGoodsId(@Param(value = "goodsId") Long goodsId);

    @Update("update goods_stage set goods_inv=1,buy_index = buy_size-1 where is_award = 0 and goods_inv <0")
    int repairGoodsInv();


    GoodsStage getGoodsStageInfo(@Param("stageId") Integer stageId);

    /**
     * 查询等待开奖的商品列表
     * @param nowTime 开奖时间
     * @return List<GoodsStage>
     */
    List<GoodsStage> waitOpenWinning(
            @Param("nowTime")Date nowTime,
            @Param(value = "second") Integer second,
            @Param(value = "isActivity") Integer isActivity);

    @Select("select goods_id goodsId,buy_index buyIndex,buy_size buySize,goods_picture goodsPicture,goods_inv goodsInv,goods_price goodsPrice,goods_name goodsName,goods_no goodsNo " +
            "from  (select  max(stage_index) stageIndex  from goods_stage where goods_no=#{goodsNo} ) AS si,goods_stage gs where stage_index=si.stageIndex And gs.flag=0 AND gs.goods_no=#{goodsNo}")
    List<GoodsStage> selectGoodsStage(@Param("goodsNo") String goodsNo);


    /**
     * 查询开奖商品
     *
     * @param stageId
     *  查询参数，商品期次id
     * @return Map<String, Object>
     */
    Map<String, Object> selectOpenGoods(@Param(value = "stageId") Long stageId);

    /**
     * 开奖总数
     * @param fullTime
     * @return
     */
    int countOpenGoods(@Param(value = "fullTime") Date fullTime);

    int updateGoodsRobotSet(GoodsStage stage);

    int updateGoodsStageByCustCode(GoodsStage stage);


    int updateStageHotByGoodsId(@Param(value = "goodsId") Long goodsId, @Param(value = "goodsHot") Integer goodsHot);

    List<OrderNoteVO> selectBuyDetail(@Param("openWinningStatus") Integer openWinningStatus,
                                @Param("userCode") Long userCode,
                                @Param("goodsStage") Integer goodsStage,
                                @Param("goodsNo") String goodsNo);

    /**
     * 个人推荐
     * @return
     */
    List<GoodsStageVO> getRandomGoodsStage();

    /**
     * 按分类查询未开奖的商品
     * @param typeId
     * @return
     */
    List<Long> getGoodsStageIDByGoodsTypeId(@Param("typeId") Integer typeId);

    Integer selectMaxIndexByGoodsNo(@Param("goodsNo") String goodsNo);

    GoodsStage getGoodsStageInfoById(@Param("stageId") String stageId);

    List<Map<String,Object>> getTest();

    int updateGoodsRobotTest(@Param("maxStageId") Integer maxStageId,@Param("goodsId") String goodsId);
}
