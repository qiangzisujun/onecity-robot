package com.tangchao.shop.serviceImpl;

import com.tangchao.common.exception.GoodsExpiredException;
import com.tangchao.common.exception.LackStockException;
import com.tangchao.shop.mapper.GoodsLockingMapper;
import com.tangchao.shop.mapper.GoodsStageMapper;
import com.tangchao.shop.pojo.Customer;
import com.tangchao.shop.pojo.GoodsLocking;
import com.tangchao.shop.pojo.GoodsStage;
import com.tangchao.shop.service.SynchronizedLockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/12/30 11:44
 */
@Service
public class SynchronizedLockServiceImpl implements SynchronizedLockService {

    private static final Logger logger = LoggerFactory.getLogger(SynchronizedLockServiceImpl.class);


    @Autowired
    private GoodsStageMapper goodsStageMapper;

    @Autowired
    private GoodsLockingMapper goodsLockingMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public synchronized int locking(Customer customer, Long stageId, Long orderNo, Long goodsNo, int buyNum, int isRobot) throws Exception {

        // 获取期次商品信息
        GoodsStage goodsStage=goodsStageMapper.selectByPrimaryKey(stageId);
        // 判断是否已开奖
        if (null!=goodsStage&&null != goodsStage.getIsAward() && goodsStage.getIsAward().equals(1)) {
            logger.error("商品已开奖"+goodsStage.getGoodsNo()+"====id:"+goodsStage.getId()+"===bb"+goodsStage.getGoodsName());
            throw new GoodsExpiredException(Long.valueOf(goodsStage.getGoodsNo()), goodsStage.getGoodsName(), goodsStage.getId());
        }
        if (goodsStage!=null){
            Integer goodsLockingNum = goodsLockingMapper.findGoodsLockingCount(stageId);//查询锁定数量
            if (goodsLockingNum == null) {
                goodsLockingNum = 0;
            }

            if (isRobot == 1) {    //机器人
                int lastNum = goodsStage.getGoodsInv();
                if(lastNum > 0 && lastNum < buyNum) buyNum = lastNum;  //当机器人购买数大于剩余可购买数时，buyNum = lastNum
            }

            // 判断库存
            int stock = goodsStage.getGoodsInv() - buyNum;
            if (stock < 0) {
                // 模拟库存不足
                logger.error("商品库存不足"+goodsStage.getGoodsNo()+"bb"+goodsStage.getGoodsName());
                throw new LackStockException(Long.valueOf(goodsStage.getGoodsNo()), goodsStage.getGoodsName(), goodsStage.getId(),
                        buyNum, stock);
            }

            GoodsLocking goodsLocking = new GoodsLocking();
            goodsLocking.setStageId(stageId);
            goodsLocking.setOrderNo(orderNo);
            goodsLocking.setGoodsNo(goodsNo);
            goodsLocking.setUserCode(customer.getUserCode());
            goodsLocking.setNum(buyNum);
            goodsLocking.setCreateTime(new Date());
            goodsLocking.setFlag(0);
            int count=goodsLockingMapper.insertSelective(goodsLocking);// 锁定商品库存
            if (count!=1){
                logger.error("添加库存锁失败");
            }
            return buyNum;
        }
        return buyNum;
    }
}
