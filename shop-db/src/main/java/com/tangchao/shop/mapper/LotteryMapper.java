package com.tangchao.shop.mapper;

import com.tangchao.shop.pojo.Lottery;
import com.tangchao.shop.vo.GoodsLotteryVO;
import com.tangchao.shop.vo.TrendChartVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface LotteryMapper extends Mapper<Lottery> {

    /**
     * 获奖结果信息
     *
     * @param stageId
     * @return
     */
    GoodsLotteryVO selectWinLotteryByStageId(@Param(value = "stageId") Long stageId);

    /**
     * 获取上一期开奖结果
     *
     * @param goodsNo
     * @param prevIndex
     * @return
     */
    GoodsLotteryVO selectPrevWinLottery(@Param(value = "goodsNo") Long goodsNo,
                                        @Param(value = "prevIndex") Integer prevIndex);

    List<GoodsLotteryVO> selectGoodsLotteryList(@Param(value = "stageId") Long stageId,
                                                @Param(value = "createTime") Date createTime);

    /**
     * 根据商品期次 获取全部幸运号码
     *
     * @param stageId 商品期次
     * @return 幸运号码列表
     */
    List<String> selectLotteryCodeByStageId(Long stageId);


    List<TrendChartVO> selectTrendChart(@Param(value = "goodsNo") Long goodsNo);

    List<Lottery> goodsTrendListInfo(@Param(value = "goodsNo") Long goodsNo);

    Double selectSumCountByWinLottery(@Param(value = "stageId") Long stageId,
                                      @Param(value = "id") Long id);

    Integer selectCountByBuyUser(@Param(value = "goodsNo") Long goodsNo,
                                 @Param(value = "stageId") Long stageId);

    // 获取白名单用户 包括机器人
    List<Lottery> selectOneUserByNotBlackList(@Param(value = "goodsNo") Long goodsNo,
                                        @Param(value = "stageId") Long stageId);

    /**
     * 获取全部参与用户
     * @param goodsNo
     * @param stageId
     * @return
     */
    List<Lottery> selectAllLottery(@Param(value = "goodsNo") Long goodsNo,
                                   @Param(value = "stageId") Long stageId);

    /**
     * 获取白名单用户 不包括机器人
     * @return
     */
    List<Lottery> selectOneUserByWhitelist(@Param(value = "goodsNo") Long goodsNo,
                                           @Param(value = "stageId") Long stageId);


    /**
     * 查询某个时间段的前100条数据
     * @param lessThanTime 时间
     * @return List<Lottery>
     */
    List<Lottery> selectLately100(@Param(value = "lessThanTime") Date lessThanTime);


    /**
     * 查询开奖商品
     *
     * @param stageId
     *查询参数，商品期次id
     * @return Map<String, Object>
     */
    Map<String, Object> selectOpenGoods(@Param(value = "stageId") Long stageId);


    List<Lottery> getLotteryListByOrderGoodIds(@Param("orderIdList") List<Long> OrderGoodIds);

    /**
     * 商品走势信息
     *
     * @param goodsNo
     *            商品唯一编码
     * @return Map<String, Object>
     */
    Map<String, Object> selectTrendInfoByGoodsNo(@Param(value = "goodsNo") Long goodsNo);

}
