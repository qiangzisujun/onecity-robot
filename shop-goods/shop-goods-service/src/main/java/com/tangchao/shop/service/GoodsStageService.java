package com.tangchao.shop.service;

import com.github.pagehelper.PageInfo;
import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.pojo.GoodsStage;
import com.tangchao.shop.vo.GoodsLotteryVO;
import com.tangchao.shop.vo.GoodsStageInfoVO;

import java.util.Date;
import java.util.List;
import java.util.Map;


public interface GoodsStageService {

    /**
     * 查询开奖中的商品期次
     *
     * @return
     */
    List<GoodsStage> selectOpeningGoodsList();

    /**
     * 查询开奖的商品期次
     *
     * @param fullTime 查询条件
     * @return List<Map < String, Object>>
     */
    PageResult<GoodsLotteryVO> selectOpenGoodsList(Integer pageNo, Integer pageSize, Date fullTime);

    /**
     * 获取商品详情
     *
     * @param goodsNo
     * @param stageIndex
     * @param stageId
     * @return
     */
    GoodsStageInfoVO getGoodsInfo(String goodsNo, String stageIndex, Long stageId, Long userCode);

    /**
     * 查询开奖商品
     * @param stageId 商品期次Id
     * @return
     */
    Map<String, Object> selectOpenGoods(Long stageId);

    PageInfo getRecommendList(Long userId);
}
