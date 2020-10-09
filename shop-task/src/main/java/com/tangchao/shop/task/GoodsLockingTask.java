package com.tangchao.shop.task;

import com.tangchao.common.constant.ConfigkeyConstant;
import com.tangchao.shop.mapper.GoodsLockingMapper;
import com.tangchao.shop.pojo.UserConf;
import com.tangchao.shop.service.ConfService;
import com.tangchao.shop.service.TradeOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/11/18 17:24
 */
@Component
public class GoodsLockingTask {

    @Autowired
    private GoodsLockingMapper lockingMapper;

    @Autowired
    private TradeOrderService orderService;

    @Autowired
    private ConfService confService;

    //每天零点执行
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteThreeDayFrontRecord(){
        System.out.print("========删除三天前商品锁单记录：");
        Integer num = this.lockingMapper.deleteThreeDayFrontRecord();
        System.out.println("删除记录数"+ num +"");
    }


    @Scheduled(fixedDelay = 1000*60*5)
    public void deleteThreeMinuteFrontRecord(){
        System.out.print("========删除五分钟前没有改变状态的商品锁单记录：");
        Integer num = this.lockingMapper.deleteThreeMinuteFrontRecord();
        System.out.println("删除记录数"+ num +"条");
    }

    /**
     * 修改已超时订单的状态
     * 每5分钟执行一次
     */
    @Scheduled(fixedDelay = 1000*60*5)
    public void cancelOrder(){
        UserConf str=this.confService.selectCmsValue(ConfigkeyConstant.MALL_ORDER_OVERTIME);
        Integer time =null;
        try {
            time=Integer.parseInt(str.getConfValue());
            TimeUnit.MINUTES.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //  获取已超时的订单
        this.orderService.updateOverTimeOrderStatus(time.longValue());
    }
}
