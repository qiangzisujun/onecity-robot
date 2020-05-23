package com.tangchao.shop.serviceImpl;

import com.alibaba.druid.util.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.common.utils.ArithUtil;
import com.tangchao.common.utils.DateUtils;
import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.mapper.CustomerInfoMapper;
import com.tangchao.shop.mapper.CustomerRechargeRecordMapper;
import com.tangchao.shop.mapper.GoodsFavouriteMapper;
import com.tangchao.shop.mapper.GoodsStageMapper;
import com.tangchao.shop.pojo.*;import com.tangchao.shop.service.GoodsFavouriteService;
import com.tangchao.shop.vo.CapitalDetailsVO;
import com.tangchao.shop.vo.OrderNoteVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class GoodsFavouriteServiceImpl implements GoodsFavouriteService {
    private static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private GoodsFavouriteMapper goodsFavouriteMapper;

    @Autowired
    private CustomerRechargeRecordMapper customerRechargeRecordMapper;

    @Autowired
    private GoodsStageMapper goodsStageMapper;

    @Autowired
    private CustomerInfoMapper customerInfoMapper;

    @Override
    public void saveGoodsFavourite(Long userCode, Long goodsNo) {
        if (userCode == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        if (goodsNo == null) {
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        GoodsFavourite goodsFavourite = new GoodsFavourite();
        goodsFavourite.setGoodsNo(goodsNo.toString());
        goodsFavourite.setCustomerCode(userCode);
        List<GoodsFavourite> goodsFavouriteList = goodsFavouriteMapper.select(goodsFavourite);
        if (!goodsFavouriteList.isEmpty()){
            throw new CustomerException(ExceptionEnum.COLLECTION_IS_EXIT);
        }
        goodsFavourite.setCustomerCode(userCode);
        goodsFavourite.setGoodsNo(goodsNo.toString());
        goodsFavourite.setCreateTime(new Date());
        int count = goodsFavouriteMapper.insertSelective(goodsFavourite);
        if (count != 1) {
            throw new CustomerException(ExceptionEnum.COLLECTION_NOT_ERROR);
        }
    }

    @Override
    public void deleteGoodsFavouriteByGoodsNo(Long userCode, Long goodsNo) {
        if (userCode == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        if (goodsNo == null) {
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        GoodsFavourite goodsFavourite = new GoodsFavourite();
        goodsFavourite.setGoodsNo(goodsNo.toString());
        goodsFavourite.setCustomerCode(userCode);
        List<GoodsFavourite> goodsFavouriteList = goodsFavouriteMapper.select(goodsFavourite);
        if (goodsFavouriteList.isEmpty()) {
            throw new CustomerException(ExceptionEnum.COLLECTION_NOT_ERROR);
        }
        goodsFavourite = goodsFavouriteList.get(0);
        goodsFavourite.setGoodsNo(goodsNo.toString());
        int count = goodsFavouriteMapper.delete(goodsFavourite);
        if (count != 1) {
            throw new CustomerException(ExceptionEnum.COLLECTION_NOT_ERROR);
        }
    }

    @Override
    public PageResult<OrderNoteVO> buyList(Long userCode, Integer pageNo, Integer pageSize, Integer openWinningStatus) {
        if (userCode == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        PageHelper.startPage(pageNo, pageSize, true);
        List<OrderNoteVO> orderNoteVOS = goodsFavouriteMapper.buyLists(userCode, openWinningStatus);
        PageInfo<OrderNoteVO> noteVOPageInfo = new PageInfo<>(orderNoteVOS);
        return new PageResult<>(noteVOPageInfo.getTotal(), orderNoteVOS);

    }

    /**
     * 用户资金明细
     *
     * @param userCode
     * @return
     */
    @Override
    public CapitalDetailsVO consumeRecord(Long userCode) {
        if (userCode == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        //查询消费总额
        Double payAmountSum = goodsFavouriteMapper.findAmountSum(2, userCode);//充值消费标识{ 1：充值，2：消费 }'
        Double payAmountSum2 = goodsFavouriteMapper.findAmountSum(5, userCode);//5 余额提现
        payAmountSum = payAmountSum + payAmountSum2;

        //查询充值总额
        Double rechargeamountSum = goodsFavouriteMapper.findAmountSum(1, userCode);//充值消费标识{ 1：充值，2：消费 }'
        Double rechargeamountSum2 = goodsFavouriteMapper.findAmountSum(4, userCode);//充值消费标识{ 1：充值，2：消费 }'
        rechargeamountSum = rechargeamountSum + rechargeamountSum2;

        //福分明细
        Double scoreGiveSum = goodsFavouriteMapper.findScoreGiveSum(userCode);//增加的福分
        Double scoreReduceSum = goodsFavouriteMapper.findScoreReduceSum(userCode);//减少的福分

        //余额
        CustomerInfo info = new CustomerInfo();
        info.setCustomerCode(userCode);
        CustomerInfo customerInfo = customerInfoMapper.selectOne(info);
        if (customerInfo.getUserMoney()==null){customerInfo.setUserMoney(0.0);}
        if (scoreReduceSum == null) {
            scoreReduceSum = 0.0;
        }
        if (scoreGiveSum == null) {
            scoreGiveSum = 0.0;
        }
        CapitalDetailsVO detailsVO = new CapitalDetailsVO();
        detailsVO.setPayAmountSum(payAmountSum);
        detailsVO.setRechargeamountSum(rechargeamountSum);
        detailsVO.setScore(scoreGiveSum - scoreReduceSum);
        detailsVO.setUserCode(userCode);
        detailsVO.setUserMoney(customerInfo.getUserMoney());
        return detailsVO;
    }

    /**
     * 用户资金明细记录
     *
     * @param userCode
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public PageResult<CustomerRechargeRecord> consumeRecordList(Long userCode, Integer pageNo, Integer pageSize, Integer count) {
        if (userCode == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = DateUtils.getFrontDay(new Date(), 30);//获取前30天记录
        String format = sdf2.format(beginDate);
        List<CustomerRechargeRecord> list = null;
        try {
            PageHelper.orderBy("create_time DESC");
            PageHelper.startPage(pageNo, pageSize);
            Example example = new Example(CustomerRechargeRecord.class);
            Example.Criteria criteria = example.createCriteria();
            if (count.equals(0)) {
                List lists = new ArrayList();
                lists.add(5);
                lists.add(2);
                criteria.andIn("type", lists);
            } else if (count.equals(1)) {
                List lists = new ArrayList();
                lists.add(1);
                lists.add(4);
                lists.add(6);
                criteria.andIn("type", lists);
            } else {
                criteria.andEqualTo("type", count);
            }
            criteria.andEqualTo("customerCode", userCode);
            criteria.andGreaterThanOrEqualTo("createTime", sdf.parse(format));
            list = customerRechargeRecordMapper.selectByExample(example);
            PageInfo<CustomerRechargeRecord> recordPageInfo = new PageInfo<>(list);
            return new PageResult<>(recordPageInfo.getTotal(), list);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public PageResult<GoodsFavourite> goodsFavouriteByGoosNoList(Long userCode, Integer pageNo, Integer pageSize) {
        if (userCode==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        PageHelper.startPage(pageNo, pageSize);
        PageHelper.orderBy(" create_time DESC");
        Example example = new Example(GoodsFavourite.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("customerCode",userCode);
        List<GoodsFavourite> list = goodsFavouriteMapper.selectByExample(example);
        PageInfo<GoodsFavourite> pageInfo = new PageInfo<>(list);
        for (GoodsFavourite goodsFavourite : list) {
            if(goodsFavourite.getGoodsNo()==null){
                continue;
            }
            PageHelper.startPage(1,1);
            PageHelper.orderBy(" create_time DESC");
            List<GoodsStage> stageList = goodsStageMapper.selectGoodsStage(goodsFavourite.getGoodsNo());
            GoodsStage goodsStage = stageList.get(0);
            if (null != goodsStage){
                goodsFavourite.setBuyIndex(goodsStage.getBuyIndex());
                goodsFavourite.setBuySize(goodsStage.getBuySize());
                goodsFavourite.setGoodsPicture(goodsStage.getGoodsPicture().split(",")[0]);
                goodsFavourite.setGoodsInv(goodsStage.getGoodsInv());
                goodsFavourite.setGoodsPrice(goodsStage.getGoodsPrice());
                goodsFavourite.setGoodsName(goodsStage.getGoodsName());
                goodsFavourite.setGoodsNo(goodsStage.getGoodsNo().toString());
            }
        }
        return new PageResult<>(pageInfo.getTotal(),list);

    }

    public GoodsStage selectGoodsStageByNo(String goodsNo, String stageIndex) {
        if (StringUtils.isEmpty(goodsNo)) {
            return null;
        }
        // 设置分页
        PageHelper.startPage(1, 1);
        // 设置排序
        PageHelper.orderBy("stage_index desc");
        Example example = new Example(GoodsStage.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("goodsNo", goodsNo);
        if (!StringUtils.isEmpty(stageIndex)) {
            criteria.andEqualTo("stageIndex", stageIndex);
        }
        List<GoodsStage> stageList = this.goodsStageMapper.selectByExample(example);
        if (stageList.isEmpty()) {
            return null;
        }
        GoodsStage goodsStage = stageList.get(0);
        goodsStage.setMaxStageIndex(this.goodsStageMapper.selectMaxIndexByGoodsId(goodsStage.getGoodsId()));// 设置商品最大期数
        return goodsStage;
    }


}
