package com.tangchao.shop.task;

import com.tangchao.common.constant.CommonConstant;
import com.tangchao.common.constant.PayStatusConstant;
import com.tangchao.common.constant.SmsTemplateTypeConstant;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.common.utils.ArithUtil;
import com.tangchao.shop.mapper.*;
import com.tangchao.shop.pojo.*;
import com.tangchao.shop.service.GoodsRobotSetService;
import com.tangchao.shop.service.LotteryService;
import com.tangchao.shop.service.OpenWinningService;
import com.tangchao.shop.service.SendSmsMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/11/20 15:09
 */
@Component
public class OpenWinningTask {


    private static final Logger logger = LoggerFactory.getLogger(OpenWinningTask.class);

    @Autowired
    private LotteryMapper lotteryMapper;

    @Autowired
    private LotteryService lotteryService;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private CustomerInfoMapper customerInfoMapper;

    @Autowired
    private CustomerRechargeRecordMapper customerRechargeRecordMapper;

    @Autowired
    private GoodsStageMapper goodsStageMapper;

    @Autowired
    private OpenWinningService openWinningService;

    @Autowired
    private GoodsRobotSetService goodsRobotSetService;

    @Autowired
    private SendSmsMessageService smsMessageService;




    /** 根据彩票列表随机出奖 **/
    public String randomResultByLotteryList(List<Lottery> lotteryList) {
        int randomNum=new Random().nextInt(lotteryList.size()); // 幸运彩票
        Lottery luckyLottery=lotteryList.get(randomNum);
        String [] nums = luckyLottery.getLotteryCode().split(",");
        randomNum = new Random().nextInt(nums.length);	// 的幸运号码
        return nums[randomNum];
    }

    /**  指定中奖  **/
    private String fixedOpen(GoodsStage stage) {
        // 获取用户或机器人购买的彩票列表
        Lottery lottery=new Lottery();
        lottery.setGoodsNo(stage.getGoodsNo());
        lottery.setStageId(stage.getId());
        lottery.setCustomerCode(Long.valueOf(stage.getCustCode()));
        List<Lottery> lotteryList = lotteryMapper.select(lottery);
        return this.randomResultByLotteryList(lotteryList);
    }

    /** 正常开奖 */
    private String comomOpen(GoodsStage stage, List<Lottery> top100List) {
        Long timeSum = 0L;  // 前100条记录的下单时间和

        SimpleDateFormat format = new SimpleDateFormat("HHmmssSSS");
        for (Lottery lot : top100List) {
            String time = format.format(lot.getCreateTime());
            long num = Long.parseLong(time);
            timeSum += num;
        }
        // 购买数量
        long count = stage.getBuySize();

        // 幸运号码(开奖结果)
        Long result = timeSum % count + CommonConstant.OPEN_WINNING_SALT;
        // 中奖的彩票
        Lottery lot = lotteryService.findWinningByResult(stage.getId(), result.toString());

        // 获取黑名单用户列表
        List<Customer> heiList = customerMapper.selectBlacklist();
        boolean big = false;

        // 判断幸运号码是否属于黑名单玩家
        if (lot!=null){
            for(int i=0; i<heiList.size(); i++) {
                if(lot.getCustomerCode().toString().equals(heiList.get(i).getUserCode().toString())) {
                    big = true;
                    break;
                }
            }
        }
        // 如果是黑名单用户中奖
        if(big) {
            // 如果是全包
            if(lotteryMapper.selectCountByBuyUser(Long.valueOf(stage.getGoodsNo()),stage.getId())==1) {
                return result.toString();
            }

            // 获取购买该商品的所有白名单用户彩票列表 包括机器人
            List<Lottery> baiLotteryList = lotteryMapper.selectOneUserByNotBlackList(Long.valueOf(stage.getGoodsNo()),stage.getId());
            return this.randomResultByLotteryList(baiLotteryList);
        }
        String resultStr = result.toString();
        return resultStr;
    }

    /** 保底开奖 **/
    private String conservativeOpen(GoodsStage stage, List<Lottery> top100List) {
        // 获取当前购买记录中其中一个机器人的彩票列表
        Lottery lottery=new Lottery();
        lottery.setGoodsNo(stage.getGoodsNo());
        lottery.setStageId(stage.getId());
        lottery.setIsRobot(1);
        List<Lottery> lotteryList = lotteryMapper.select(lottery);

        // 如果没有机器人参与转正常开奖
        if(lotteryList == null || lotteryList.size() <1) {
            return this.comomOpen(stage, top100List);
        }

        return this.randomResultByLotteryList(lotteryList);
    }

    /** 促销开奖 **/
    private String promotionOpen(GoodsStage stage, List<Lottery> top100List) {

        logger.info("进入促销开奖！");
        // 未设定奖池满值 转 正常开奖
        if(stage.getJackPotAll() <= 0) {
            logger.info("未设定奖池满值转正常开奖！");
            return this.comomOpen(stage, top100List);
        }

        // 奖池未满 转保底开奖 让机器人中奖
        if(stage.getJackPotNow() < stage.getJackPotAll()) {
            logger.info("奖池未满 转保底开奖 让机器人中奖！");
            return this.conservativeOpen(stage, top100List);
        }

        // 奖池已满 用户必中 黑名单用户除外（除非全包）
        // 判断该期商品是否由一人全包 全包则不作黑名单逻辑判断  true : 全包    false : 非全包
        boolean big = lotteryMapper.selectCountByBuyUser(Long.valueOf(stage.getGoodsNo()),stage.getId())==1?true:false;
        if(big) {
            // 获取所有玩家的彩票列表
            Lottery lottery=new Lottery();
            lottery.setGoodsNo(stage.getGoodsNo());
            lottery.setStageId(stage.getId());
            lottery.setIsRobot(0);
            List<Lottery> lotteryList = lotteryMapper.select(lottery);
            return this.randomResultByLotteryList(lotteryList);
        }else {
            // 获取白名单玩家的彩票列表
            List<Lottery> lotteryList = lotteryMapper.selectOneUserByWhitelist(Long.valueOf(stage.getGoodsNo()),stage.getId());

            // 如果无白名单玩家 转保底开奖 让机器人中奖
            if(lotteryList == null || lotteryList.size() < 1) {
                return this.conservativeOpen(stage, top100List);
            }
            return this.randomResultByLotteryList(lotteryList);
        }
    }


    @Scheduled(fixedDelay = 1000)
    public void openWinning() throws Exception {

        // 修复库存
        int count=goodsStageMapper.repairGoodsInv();

        Integer openTime = this.openWinningService.findOpenWinningTime();
        try {
            TimeUnit.SECONDS.sleep(openTime - 3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 获取待开奖的商品列表
        List<GoodsStage> goodsStageList = this.openWinningService.findWaitOpenWinning(null);
        if(!goodsStageList.isEmpty()){
            List<GoodsStage> activityList = new ArrayList<GoodsStage>();
            for (GoodsStage goodsStage : goodsStageList) {
                // 设置开奖时间
                goodsStage.setOpenTime(new Date());
                // 开奖前 100条记录 用于计算 彩票幸运号
                List<Lottery> lotteryList = lotteryMapper.selectLately100(goodsStage.getOpenTime());
                // 开奖结果
                String result = null;

                /** 查询商品开奖类型设置 **/
                GoodsRobotSet robotSet = this.goodsRobotSetService.findByGoodsNo(Long.valueOf(goodsStage.getGoodsNo()));

                // 标识是否已执行过开奖动作
                boolean execRecordFlag = false;
                //robotSet.setProtocolLotteryNo("0");

                // 指定开奖 必中
                if(!execRecordFlag && goodsStage.getCustCode() != null&&!"".equals(goodsStage.getCustCode())){
                    result = this.fixedOpen(goodsStage);
                    execRecordFlag = true;
                }

                // 正常开奖 玩家和机器人均有几率中奖		protocolLotteryNo ：null or 0
                if(!execRecordFlag && robotSet != null && (robotSet.getProtocolLotteryNo() == null || "0".equals(robotSet.getProtocolLotteryNo()))) {
                    result = this.comomOpen(goodsStage, lotteryList);
                    execRecordFlag = true;
                }

                // 保底开奖 机器人随机中奖			protocolLotteryNo ：1
                if(!execRecordFlag && robotSet != null && "1".equals(robotSet.getProtocolLotteryNo())) {
                    result = this.conservativeOpen(goodsStage, lotteryList);
                    execRecordFlag = true;
                }

                // 促销开奖 用户随机中奖				protocolLotteryNo ：2
                if(!execRecordFlag && robotSet != null && "2".equals(robotSet.getProtocolLotteryNo())) {
                    result = this.promotionOpen(goodsStage, lotteryList);
                    execRecordFlag = true;
                }

                if(result == null) {
                    result = this.comomOpen(goodsStage, lotteryList);
                }

                // 设置 结果
                goodsStage.setAwardResults(result);
                goodsStage.setIsAward(1);
                // 保存开奖信息
                count=goodsStageMapper.updateByPrimaryKeySelective(goodsStage);
                if (count!=1){
                    throw new CustomerException(ExceptionEnum.OPEN_LOTTERY_ERROR);
                }
                // 开奖
                this.lotteryService.openWinning(goodsStage);

                if (CommonConstant.YES == goodsStage.getIsActivity()) {
                    activityList.add(goodsStage);
                }

                // 开奖后发送中奖短信
                Map<String, Object> resultMap = lotteryMapper.selectOpenGoods(goodsStage.getId());

                if (!resultMap.isEmpty()) {
                    String goodsName = (String) resultMap.get("goodsName");
                    String userMobile = (String) resultMap.get("userMobile");
                    Integer isRobot = (Integer) resultMap.get("isRobot");
                    if(isRobot!=null&&isRobot.equals(0)){//
                        smsMessageService.sendSMSTemplate(userMobile, SmsTemplateTypeConstant.WINNING_PRIZE,goodsName);
                    }

                }
            }
        }
    }


    /**
     * 返余额
     *
     * @param stage
     *            GoodsStage
     */
    private void cashBack(GoodsStage stage) {
        // 商品金额
        double money = stage.getBuyPrice();
        Lottery lotteryInfo=new Lottery();
        lotteryInfo.setIsRobot(0);//  非机器人购买
        lotteryInfo.setStageId(stage.getId());//  商品期次
        List<Lottery> lotteryList = lotteryMapper.select(lotteryInfo);
        for (Lottery lottery : lotteryList) {
            Integer num = lottery.getLotteryCode().split(",").length;
            if (CommonConstant.NO == lottery.getIsWinning()) {
                num--;
            }
            // 福分
            Double backMoney=ArithUtil.mul(ArithUtil.mul(money, num), 1);
            // 获取会员信息
            CustomerInfo userInfo = new CustomerInfo();
            userInfo.setCustomerCode(lottery.getCustomerCode());
            List<CustomerInfo> customerList = customerInfoMapper.select(userInfo);
            userInfo = customerList.get(0);
            userInfo.setUserMoney(ArithUtil.add(userInfo.getUserMoney(), backMoney));
            // 保存会员信息
            if (customerInfoMapper.updateByPrimaryKeySelective(userInfo) > 0) {
                CustomerRechargeRecord record = new CustomerRechargeRecord();
                record.setCustomerCode(userInfo.getCustomerCode());
                record.setAmount(backMoney);
                record.setType(1);//'充值消费标识{ 1：充值，2：消费 , 3 :佣金提现 ,4佣金充值}',  ==4
                record.setCreateTime(new Date());
                record.setCreateId(userInfo.getCustomerCode());
                record.setPayment(PayStatusConstant.PAY_FROM_BACK);//支付方式{ 1：支付宝，2：微信，3：余额，4：后台，5：代理 }
                //佣金充值扣减记录
                customerRechargeRecordMapper.insertSelective(record);
            }
        }
    }
}