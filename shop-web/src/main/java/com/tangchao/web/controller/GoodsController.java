package com.tangchao.web.controller;


import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.service.GoodsStageService;
import com.tangchao.shop.service.LotteryService;
import com.tangchao.shop.service.TradeOrderService;
import com.tangchao.shop.vo.GoodsLotteryVO;
import com.tangchao.shop.vo.GoodsStageInfoVO;
import com.tangchao.shop.vo.OrderNoteVO;
import com.tangchao.web.annotation.LoginUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;


@RequestMapping("/api/shop/goods")
@RestController
@Api(value = "元购商品模块", tags = {"元购商品模块"})
public class GoodsController {

    @Autowired
    private GoodsStageService stageService;

    @Autowired
    private LotteryService lotteryService;

    @Autowired
    private TradeOrderService tradeOrderService;

    @ApiOperation("商品详情")
    @GetMapping("/goodsStageInfo")
    public ResponseEntity<GoodsStageInfoVO> goodsInfo(@LoginUser Long userCode,
                                                      @ApiParam(value = "商品编号", name = "goodsNo")
                                                      @RequestParam(value = "goodsNo", required = false) String goodsNo,
                                                      @ApiParam(value = "商品期数", name = "stageIndex")
                                                      @RequestParam(value = "stageIndex", required = false) String stageIndex,
                                                      @ApiParam(value = "商品期数Id", name = "stageId")
                                                      @RequestParam(value = "stageId", required = false) Long stageId) {

        return ResponseEntity.ok(stageService.getGoodsInfo(goodsNo, stageIndex, stageId, userCode));
    }

    @ApiOperation("商品获奖者-顶部")
    @GetMapping("/goodsWinningInfo")
    public ResponseEntity<GoodsLotteryVO> goodsInfo(@ApiParam(value = "商品期数Id", name = "stageId")
                                                    @RequestParam(value = "stageId") Long stageId) {

        return ResponseEntity.ok(lotteryService.selectWinLotteryByStageId(stageId));
    }

    @ApiOperation("商品获奖者-底部")
    @GetMapping("/prev/wininfo")
    public ResponseEntity<GoodsLotteryVO> prevWininfo(@ApiParam(value = "商品编号", name = "goodsNo")
                                                      @RequestParam(value = "goodsNo") Long goodsNo,
                                                      @ApiParam(value = "商品上一期Id", name = "prevIndex")
                                                      @RequestParam(value = "prevIndex") Integer prevIndex) {

        return ResponseEntity.ok(lotteryService.selectPrevWinLottery(goodsNo, prevIndex));
    }


    @ApiOperation("所有参与记录")
    @GetMapping("/selectGoodsLotteryList")
    public ResponseEntity<PageResult<GoodsLotteryVO>> selectGoodsLotteryList(@ApiParam(value = "页数", name = "pageNo") @RequestParam("pageNo") Integer pageNo,
                                                                             @ApiParam(value = "页数大小", name = "pageSize") @RequestParam("pageSize") Integer pageSize,
                                                                             @ApiParam(value = "商品期数Id", name = "stageId") @RequestParam(value = "stageId") Long stageId,
                                                                             @ApiParam(value = "创建时间", name = "createTime") @RequestParam(value = "createTime", required = false) Date createTime) {

        return ResponseEntity.ok(lotteryService.selectGoodsLotteryList(pageNo, pageSize, stageId, createTime));
    }

    @ApiOperation("进行中购买详情")
    @GetMapping("/selectBuyDetail")
    public ResponseEntity<List<OrderNoteVO>> selectBuyDetail(@LoginUser Long userCode,
                                                @ApiParam(value = "商品编号",name = "goodsNo") @RequestParam(value = "goodsNo") String goodsNo,
                                                @ApiParam(value = "商品期数",name = "goodsStage") @RequestParam(value = "goodsStage") Integer goodsStage,
                                                @ApiParam(value = "状态:0进行中,1已揭晓",name = "openWinningStatus") @RequestParam(value = "openWinningStatus") Integer openWinningStatus) {

        return ResponseEntity.ok(lotteryService.selectBuyDetail(userCode,openWinningStatus,goodsNo,goodsStage));
    }

    @ApiOperation("查看计算结果")
    @GetMapping("/calculationResult")
    public ResponseEntity<Map<String,Object>> calculationResult(@LoginUser Long userCode,@RequestParam(value ="stageId") String stageId) {
        return ResponseEntity.ok(lotteryService.calculationResult(userCode,stageId));
    }

    @ApiOperation("中奖列表")
    @GetMapping("/winningOrderByGoodsNo")
    public ResponseEntity<Map<String,Object>> winningOrderByGoodsNo(@ApiParam(value = "页数", name = "pageNo") @RequestParam("pageNo") Integer pageNo,
                                                                    @ApiParam(value = "页数大小", name = "pageSize") @RequestParam("pageSize") Integer pageSize,
                                                                    @RequestParam(value ="goodsNo") String goodsNo) {
        return ResponseEntity.ok(tradeOrderService.winningOrderByGoodsNo(pageNo, pageSize, goodsNo));
    }

}
