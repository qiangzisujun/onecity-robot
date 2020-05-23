package com.tangchao.shop.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tangchao.common.constant.CommonConstant;
import com.tangchao.common.constant.ConfigkeyConstant;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.mapper.GoodsStageMapper;
import com.tangchao.shop.mapper.LotteryMapper;
import com.tangchao.shop.mapper.UserConfMapper;
import com.tangchao.shop.pojo.GoodsStage;
import com.tangchao.shop.pojo.Lottery;
import com.tangchao.shop.pojo.UserConf;
import com.tangchao.shop.service.LotteryService;
import com.tangchao.shop.service.WinningOrderService;
import com.tangchao.shop.vo.GoodsLotteryVO;
import com.tangchao.shop.vo.GoodsStageInfoVO;
import com.tangchao.shop.vo.OrderNoteVO;
import com.tangchao.shop.vo.TrendChartVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class LotteryServiceImpl implements LotteryService {

    @Autowired
    private LotteryMapper lotteryMapper;

    @Autowired
    private GoodsStageMapper goodsStageMapper;

    @Autowired
    private UserConfMapper confMapper;

    @Autowired
    private WinningOrderService winningOrderService;

    @Override
    public GoodsLotteryVO selectWinLotteryByStageId(Long stageId) {
        if (stageId == null) {
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        return lotteryMapper.selectWinLotteryByStageId(stageId);
    }

    @Override
    public GoodsLotteryVO selectPrevWinLottery(Long goodsNo, Integer prevIndex) {
        if (goodsNo == null || prevIndex == null || prevIndex <= 0) {
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        GoodsLotteryVO goods = lotteryMapper.selectPrevWinLottery(goodsNo, prevIndex);
        if (goods==null){
            return null;
        }
        Map<String, Object> params = new HashMap<>();
        if (prevIndex != null) {
            params.put("stageIndex", prevIndex);
        }
        if (goodsNo != null) {
            params.put("goodsNo", goodsNo);
        }
        GoodsStageInfoVO goodsStageInfo = goodsStageMapper.getGoodsStageByNo(params);
        Long calcTimeMsec = 0L;// 设置开奖倒计时毫秒值
        if (goodsStageInfo != null && goodsStageInfo.getFullTime() != null) {
            //	读取配置
            UserConf conf = new UserConf();
            conf.setConfKey(ConfigkeyConstant.GOODS_OPEN_WINNING_TIME);
            UserConf newConf = confMapper.selectOne(conf);
            Long fullTimeSec = goodsStageInfo.getFullTime().getTime();
            Long nowTimeSec = System.currentTimeMillis();
            Integer openTimeMillis = Integer.parseInt(newConf.getConfValue()) * 2000;
            calcTimeMsec = (fullTimeSec - nowTimeSec) + openTimeMillis.longValue();
            if (calcTimeMsec < 0) {
                goods.setIsShow(1);
            } else {
                goods.setIsShow(0);
            }
        }
        return goods;
    }

    @Override
    public PageResult<GoodsLotteryVO> selectGoodsLotteryList(Integer pageNo, Integer pageSize, Long stageId, Date createTime) {
        if (stageId == null) {
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        // 设置查询分页
        PageHelper.startPage(pageNo, pageSize);
        PageHelper.orderBy(" createTime desc");// 设置排序
        List<GoodsLotteryVO> goodsLotteryVOList = null;
        if (createTime == null) {
            goodsLotteryVOList = lotteryMapper.selectGoodsLotteryList(stageId, null);
        } else {
            goodsLotteryVOList = lotteryMapper.selectGoodsLotteryList(stageId, createTime);
        }

        PageInfo<GoodsLotteryVO> pageInfo = new PageInfo<>(goodsLotteryVOList);
        return new PageResult<>(pageInfo.getTotal(), goodsLotteryVOList);

    }

    @Override
    public Map<String,Object> selectTrendChart(String goodsNo) {
        if (goodsNo==null){
            throw new CustomerException(ExceptionEnum.GOODS_NOT_FOND);
        }
        return lotteryMapper.selectTrendInfoByGoodsNo(Long.valueOf(goodsNo));
    }

    @Override
    public List<Lottery> goodsTrendListInfo(String goodsNo) {
        List<Lottery> lotteryList= this.lotteryMapper.goodsTrendListInfo(Long.valueOf(goodsNo));
        if(lotteryList==null||lotteryList.size()==0){
            return null;
        }
        Double count=0.0;
        for(Lottery list:lotteryList){
            //查询比中奖更早参与的信息
            count=this.lotteryMapper.selectSumCountByWinLottery(Long.parseLong(list.getStageId()+""),Long.parseLong(list.getId()+""));
            if(count==null){
                list.setIsWinning(0);
            }else{
                list.setIsWinning(count.intValue());
                int str = list.getLotteryCode().split(",").length - 1;
                list.setBuying(str+1);
            }
        }
        return lotteryList;
    }

    @Override
    public Lottery findWinningByResult(Long stageId, String winningResult) {
        //  获取到中奖记录
        Example example = new Example(Lottery.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("stageId",stageId);
        criteria.andLike("lotteryCode","%" + winningResult + "%");
        PageHelper.startPage(1,1);
        List<Lottery> list = this.lotteryMapper.selectByExample(example);
        if (list.size() > 0){
            return list.get(0);
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void openWinning(GoodsStage goodsStage) throws Exception {
        String winningResult = goodsStage.getAwardResults();
        Long stageId = goodsStage.getId();
        //  获取中奖记录
        Lottery lottery = this.findWinning(stageId,winningResult);
        if (null == lottery){
            log.error("开奖失败：未查询到中奖的记录！");
            return;
        }
        lottery.setId(lottery.getId());
        lottery.setIsWinning(2);                                    //  标记为中奖
        lottery.setOpenWinningTime(goodsStage.getOpenTime());       //  开奖时间
        lottery.setResultCode(winningResult);                       //  中奖号码
        lottery.setResultUserCode(lottery.getCustomerCode());       //  中奖用户
        //  保存中奖信息
        if (this.lotteryMapper.updateByPrimaryKeySelective(lottery) > 0){
            //  生成中奖订单
            this.winningOrderService.createOrder(lottery, goodsStage);

            //  其他标记为未中奖
            Example example = new Example(Lottery.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("stageId",stageId);
            criteria.andNotEqualTo("id",lottery.getId());
            Lottery record = new Lottery();
            record.setIsWinning(1);
            record.setOpenWinningTime(lottery.getOpenWinningTime());
            record.setResultCode(winningResult);                       //  中奖号码
            record.setResultUserCode(lottery.getCustomerCode());       //  中奖用户
            this.lotteryMapper.updateByExampleSelective(record,example);
        }
    }

    @Override
    public List<OrderNoteVO> selectBuyDetail(Long userCode,Integer openWinningStatus,String goodsNo, Integer goodsStage) {
        if (userCode==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        if (openWinningStatus==null|| StringUtils.isBlank(goodsNo)||goodsStage==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        List<OrderNoteVO> vo=this.goodsStageMapper.selectBuyDetail(openWinningStatus,userCode,goodsStage,goodsNo);
        return vo;
    }

    @Override
    public Map<String, Object> calculationResult(Long userCode, String stageId) {

        //  获取商品
        GoodsStage goodsStage =goodsStageMapper.selectByPrimaryKey(stageId);
        //  数据校验
        if (null == goodsStage || !goodsStage.getIsAward().equals(CommonConstant.YES)){
            throw new CustomerException(ExceptionEnum.SELL_IS_DOWN);
        }
        //  获取该期次开奖前100条参与记录列表
        List<Lottery> lotteryList = this.lotteryMapper.selectLately100(goodsStage.getOpenTime());
        //  求和
        long count = 0;
        SimpleDateFormat format = new SimpleDateFormat("HHmmssSSS");
        for (Lottery lottery : lotteryList){
            String time = format.format(lottery.getCreateTime());
            count += Long.parseLong(time);
        }


        Long awardResult=Long.parseLong(goodsStage.getAwardResults())-CommonConstant.OPEN_WINNING_SALT;
        Long sum1=count%goodsStage.getBuyIndex();
        if(sum1>=awardResult){
            count=count-(sum1-awardResult);
        }else{
            count=count+(awardResult-sum1);
        }

        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("goods",goodsStage);
        resultMap.put("lotteryList",lotteryList);
        resultMap.put("calcCount",count);
        resultMap.put("openWinningSalt",CommonConstant.OPEN_WINNING_SALT);
        return resultMap;
    }

    /**
     * 查询中奖记录
     * @param stageId 商品期次Id
     * @param winningResult 中奖号码
     */
    private Lottery findWinning(Long stageId,String winningResult){
        //  获取到中奖记录
        Example example = new Example(Lottery.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("stageId",stageId);
        criteria.andLike("lotteryCode","%" + winningResult + "%");
        PageHelper.startPage(1,1);
        List<Lottery> list = this.lotteryMapper.selectByExample(example);
        if (list.size() > 0){
            return list.get(0);
        }
        return null;
    }
}
