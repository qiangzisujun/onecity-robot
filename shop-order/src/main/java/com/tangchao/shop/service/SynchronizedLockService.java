package com.tangchao.shop.service;

import com.tangchao.shop.pojo.Customer;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/12/30 11:43
 */
public interface SynchronizedLockService {

    /**
     * 商品库存同步锁
     * @param customer
     * @param stageId
     * @param orderNo
     * @param goodsNo
     * @param buyNum    购买数量
     * @param isRobot   0=用户，1=机器人
     * @return
     */
    int locking(Customer customer, Long stageId, Long orderNo, Long goodsNo, int buyNum, int isRobot) throws Exception;
}
