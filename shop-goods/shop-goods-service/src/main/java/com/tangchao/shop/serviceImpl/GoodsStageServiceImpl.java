package com.tangchao.shop.serviceImpl;

import com.alibaba.druid.util.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.mapper.GoodsFavouriteMapper;
import com.tangchao.shop.mapper.GoodsStageMapper;
import com.tangchao.shop.mapper.ShoppingCartMapper;
import com.tangchao.shop.mapper.UserConfMapper;
import com.tangchao.shop.pojo.GoodsFavourite;
import com.tangchao.shop.pojo.GoodsStage;
import com.tangchao.shop.pojo.UserConf;
import com.tangchao.shop.service.GoodsStageService;
import com.tangchao.shop.service.OpenWinningService;
import com.tangchao.shop.vo.GoodsLotteryVO;
import com.tangchao.shop.vo.GoodsStageInfoVO;
import com.tangchao.shop.vo.GoodsStageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoodsStageServiceImpl implements GoodsStageService {

    @Autowired
    private GoodsStageMapper goodsStageMapper;

    @Autowired
    private OpenWinningService openWinningService;

    @Autowired
    private UserConfMapper userConfMapper;

    @Autowired
    private GoodsFavouriteMapper goodsFavouriteMapper;

    @Autowired
    private ShoppingCartMapper cartMapper;


    @Override
    public List<GoodsStage> selectOpeningGoodsList() {
        Integer openTime = this.openWinningService.findOpenWinningTime();
        // 设置分页
        PageHelper.startPage(1, 15);
        // 设置排序
        PageHelper.orderBy("fullTime asc");
        Integer openTimeMillis = openTime * 2 * 1000;
        Date awardTime = new Date(System.currentTimeMillis() - openTimeMillis.longValue());
        List<GoodsStage> resultList = goodsStageMapper.selectOpeningGoodsList(awardTime);
        for (GoodsStage map : resultList) {
            Date fullTime = map.getFullTime();
            map.setCalcTimeMsec((fullTime.getTime() - awardTime.getTime()));
        }
        return resultList;
    }

    @Override
    public PageResult<GoodsLotteryVO> selectOpenGoodsList(Integer pageNo, Integer pageSize, Date fullTime) {
        PageHelper.startPage(pageNo, pageSize, false);
        List<GoodsLotteryVO> resultList = goodsStageMapper.selectOpenGoodsList(fullTime);
        PageInfo<GoodsLotteryVO> pageInfo = new PageInfo<GoodsLotteryVO>(resultList);
        return new PageResult<>(pageInfo.getTotal(), resultList);
    }

    @Override
    public GoodsStageInfoVO getGoodsInfo(String goodsNo, String stageIndex, Long stageId, Long userCode) {
        Map<String, Object> params = new HashMap<>();
        if (stageIndex != null) {
            params.put("stageIndex", stageIndex);
        }
        if (!StringUtils.isEmpty(goodsNo)) {
            params.put("goodsNo", goodsNo);
        }
        if (stageId != null) {
            params.put("stageId", stageId);
        }
        GoodsStageInfoVO goodsStageInfo = goodsStageMapper.getGoodsStageByNo(params);
        if (goodsStageInfo == null) {
            throw new CustomerException(ExceptionEnum.GOODS_NOT_FOND);
        }

        Long calcTimeMsec = 0L;// 设置开奖倒计时毫秒值
        if (goodsStageInfo != null && goodsStageInfo.getFullTime() != null) {
            Integer openTime = this.openWinningService.findOpenWinningTime();
            Long fullTimeSec = goodsStageInfo.getFullTime().getTime();
            Long nowTimeSec = System.currentTimeMillis();
            Integer openTimeMillis = openTime * 2000;
            calcTimeMsec = (fullTimeSec - nowTimeSec) + openTimeMillis.longValue();
            if (calcTimeMsec < 0) {
                goodsStageInfo.setIsAward(1);
                calcTimeMsec = 0L;
            } else {
                goodsStageInfo.setIsAward(2);
            }
            goodsStageInfo.setCalcTimeMsec(calcTimeMsec);
        }
        UserConf conf = new UserConf();
        conf.setConfKey("mall.is.traffic.send");
        conf = userConfMapper.selectOne(conf);
        goodsStageInfo.setIsFlow("1".equals(conf.getConfValue()));

        // 查询会员收藏商品
        if (userCode != null) {
            String goodsNos = goodsFavouriteMapper.selectGoodsNosByUserCode(userCode,goodsStageInfo.getGoodsNo());
            if (!StringUtils.isEmpty(goodsNos)) {
                goodsStageInfo.setIsCollection(1);
            } else {
                goodsStageInfo.setIsCollection(0);
            }
            Long isBuy = cartMapper.selectPurchasedGoodsNum(userCode, goodsStageInfo.getId());
            if (isBuy != null) {
                goodsStageInfo.setHasBuy((goodsStageInfo.getBuyNum() - isBuy));//可购买
            }
        }
        return goodsStageInfo;
    }

    @Override
    public Map<String, Object> selectOpenGoods(Long stageId) {
        if (stageId==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        return goodsStageMapper.selectOpenGoods(stageId);
    }

    @Override
    public PageInfo getRecommendList(Long userId) {
        if (userId ==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        List<GoodsStageVO> goodsStageVOList=goodsStageMapper.getRandomGoodsStage();

        for (GoodsStageVO vo:goodsStageVOList){
            GoodsFavourite favourite=new GoodsFavourite();
            favourite.setCustomerCode(userId);
            favourite.setGoodsNo(vo.getGoodsNo());
            List<GoodsFavourite> favouriteList=goodsFavouriteMapper.select(favourite);
            if (favouriteList.isEmpty()){
                vo.setIsCollection(0);
            }else{
                vo.setIsCollection(1);
            }
        }
        return new PageInfo(goodsStageVOList);
    }
}
