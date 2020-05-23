package com.tangchao.shop.service;

import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.pojo.GoodsStage;
import com.tangchao.shop.pojo.Lottery;
import com.tangchao.shop.vo.GoodsLotteryVO;
import com.tangchao.shop.vo.OrderNoteVO;
import com.tangchao.shop.vo.TrendChartVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface LotteryService {

    /**
     * 获奖结果信息
     *
     * @param stageId
     * @return
     */
    GoodsLotteryVO selectWinLotteryByStageId(Long stageId);

    /**
     * 上一期开奖结果
     *
     * @param goodsNo
     * @param prevIndex
     * @return
     */
    GoodsLotteryVO selectPrevWinLottery(Long goodsNo, Integer prevIndex);


    PageResult<GoodsLotteryVO> selectGoodsLotteryList(Integer pageNo, Integer pageSize, Long stageId, Date createTime);


    Map<String,Object> selectTrendChart(String goodsNo);

    List<Lottery> goodsTrendListInfo(String goodsNo);


    Lottery findWinningByResult(Long stageId, String winningResult);

    /**
     * 开奖
     * @param goodsStage 商品期次信息
     */
    void openWinning(GoodsStage goodsStage) throws Exception;

    List<OrderNoteVO> selectBuyDetail(Long userCode,Integer openWinningStatus,String goodsNo, Integer goodsStage);

    Map<String,Object> calculationResult(Long userCode, String stageId);
}
