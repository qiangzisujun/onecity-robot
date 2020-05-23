package com.tangchao.shop.task;

import com.tangchao.common.constant.CommonConstant;
import com.tangchao.shop.dto.TradeOrderDTO;
import com.tangchao.shop.mapper.CustomerMapper;
import com.tangchao.shop.mapper.GoodsLockingMapper;
import com.tangchao.shop.mapper.GoodsStageMapper;
import com.tangchao.shop.pojo.Customer;
import com.tangchao.shop.pojo.GoodsRobot;
import com.tangchao.shop.pojo.GoodsStage;
import com.tangchao.shop.service.GoodsRobotSetService;
import com.tangchao.shop.service.SynchronizedLockService;
import com.tangchao.shop.service.TradeOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2020/4/15 14:14
 */
@Component
public class TradeRobotTask {

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private GoodsRobotSetService goodsRobotService;

    @Autowired
    private TradeOrderService tradeOrderService;

    @Autowired
    private GoodsLockingMapper goodsLockingMapper;

    @Autowired
    private GoodsStageMapper goodsStageMapper;

    @Autowired
    private SynchronizedLockService synchronizedLockService;


    public static List<Customer> robotCustList=null;//机器人会员列表

    public static Map<String,Integer> robotTimeMap=new HashMap<String,Integer>();


    @PostConstruct
    public void starts () {
        Calendar cal = Calendar.getInstance();
        int hour=cal.get(Calendar.HOUR_OF_DAY);

        Customer customer=new Customer();
        customer.setIsRobot(1);
        customer.setFlag(0);
        if (hour>6&&hour<18){
            customer.setBuyPeriod(0);
        }else{
            customer.setBuyPeriod(1);
        }
        List<Customer> customerRobotList = customerMapper.select(customer);
        robotCustList=customerRobotList;
    }

    /**
     * 模拟机器人购买 每分钟执行一次
     */
    //@Scheduled(fixedDelay = 1000*10)
    public void RobotBuyOrder() {
        this.robotMission("0");
    }
    //@Scheduled(fixedDelay = 1000 * 10)
    public void RobotBuyOrder2() {
        this.robotMission("2");
    }
    //@Scheduled(fixedDelay = 1000 * 10)
    public void RobotBuyOrder3() {
        this.robotMission("3");
    }


    //@Scheduled(fixedDelay = 1000*60)
    public void newSelectRobotList() {

        Calendar cal = Calendar.getInstance();
        int hour=cal.get(Calendar.HOUR_OF_DAY);

        Customer customer=new Customer();
        customer.setIsRobot(1);
        customer.setFlag(0);
        if (hour>6&&hour<18){
            customer.setBuyPeriod(0);
        }else{
            customer.setBuyPeriod(1);
        }
        List<Customer> customerRobotList = customerMapper.select(customer);
        robotCustList=customerRobotList;
    }


    public void robotMission(String type){
        // 1、查询机器人任务记录表
        try {
            List<GoodsRobot> allRobotList = goodsRobotService.findGoodsRobotsList(type);
            int randNum=0;
            if(type.equals("0")){
                randNum=new Random().nextInt(2)+2;
            }

            // 需要购买商品的机器人列表
            List<GoodsRobot> goodsRobotList = new ArrayList<GoodsRobot>();
            for (GoodsRobot goodsRobot : allRobotList) {
                Integer buyRateMinute = robotTimeMap.get("robot_time_"+goodsRobot.getId());
                if(robotTimeMap.get("robot_time_"+goodsRobot.getId())==null||robotTimeMap.get("robot_time_"+goodsRobot.getId())<=0){
                    robotTimeMap.put("robot_time_"+goodsRobot.getId(),goodsRobot.getBuyRateMinute());
                    buyRateMinute=goodsRobot.getBuyRateMinute();
                }
                if((buyRateMinute-10) > 0) {
                    robotTimeMap.put("robot_time_"+goodsRobot.getId(),robotTimeMap.get("robot_time_"+goodsRobot.getId())-10);
                }else {
                    goodsRobotList.add(goodsRobot);
                }
            }

            int pros = goodsRobotList.size();

            Random random = new Random();

            if (pros > 0) {

                // 2、查询所有的机器人
                if(robotCustList==null){
                    Calendar cal = Calendar.getInstance();
                    int hour=cal.get(Calendar.HOUR_OF_DAY);
                    Customer customer=new Customer();
                    customer.setIsRobot(1);
                    customer.setFlag(0);
                    if (hour>6&&hour<18){
                        customer.setBuyPeriod(0);
                    }else{
                        customer.setBuyPeriod(1);
                    }
                    List<Customer> customerRobotList = customerMapper.select(customer);
                    robotCustList=customerRobotList;
                }

                // 3、打乱所有的商品
                int x = 0;
                while (x < 2) {
                    Collections.shuffle(goodsRobotList);
                    x++;
                }

                // 4、根据任务记录表的任务配置开始抽取机器人
                for (int i = 0; i < pros; ++i) {
                    GoodsRobot goodsRobot = goodsRobotList.get(i);
                    System.out.println("============================商品期次" + goodsRobot.getGoodsPeriodId());
                    Collections.shuffle(robotCustList);
                    Customer customer = robotCustList.get(random.nextInt(robotCustList.size()));
                    int buyNumber = this.getBuyNumber(goodsRobot);
                    try{
                        buyNumber = synchronizedLockService.locking(customer, goodsRobot.getGoodsPeriodId(), null, null, buyNumber, 1);
                    } catch(Throwable e) {
                        buyNumber = 0;
                        e.printStackTrace();
                    }
                    System.out.println("============================购买数量" + buyNumber);

                    if (buyNumber <= 0 ){
                        continue;
                    }


                    TradeOrderDTO pojo = new TradeOrderDTO();
                    pojo.setStageId(goodsRobot.getGoodsPeriodId()); // 商品期次Id
                    pojo.setPayNum(buyNumber); // 购买数量
                    pojo.setIsUseIntegral(CommonConstant.NO); // 使用积分
                    pojo.setUserName(customer.getUserName());
                    pojo.setCustomerCode(customer.getUserCode());
                    pojo.setUserMobile(customer.getUserMobile());
                    // 5、更新任务记录
                    Integer min_m = goodsRobot.getMinPurchasesMinute();
                    Integer max_m = goodsRobot.getMaxPurchasesMinute();
                    if(max_m == null || min_m == null || max_m <= 0 || min_m > max_m) {//不符合条件的数值
                        goodsRobot.setBuyRateMinute(30);//30分钟的随机购买时间
                        robotTimeMap.put("robot_time_"+goodsRobot.getId(),30);
                    }else {
                        int num=new Random().nextInt(max_m - min_m + 1) + min_m;
                        goodsRobot.setBuyRateMinute(num);
                        robotTimeMap.put("robot_time_"+goodsRobot.getId(),num);
                    }
                    goodsRobot.setBoughtCount(goodsRobot.getBoughtCount() + buyNumber);
                    if(type.equals("0")){
                        goodsRobot.setTimestamp(randNum+"");
                    }
                    // 6、生成购买记录
                    this.tradeOrderService.robotPayGoods(pojo,goodsRobot);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getBuyNumber(GoodsRobot goodsRobot) throws Exception{
        int buyNumber = 0;

        int min_c = goodsRobot.getMinPurchasesConut();//5

        int max_c = goodsRobot.getMaxPurchasesConut();//20

        int goods_c = goodsRobot.getGoodsLimitCount();// 商品本身的限购次数 124

        int goods_rc = goodsRobot.getMaxCount() - goodsRobot.getBoughtCount();// 剩余能购买的数量 124-32 92

        int goods_max = goods_rc > goods_c ? goods_c : goods_rc;//商品最大购买数

        if (min_c > goods_max) {
            buyNumber = goods_max;
        } else if (goods_max < max_c) {
            buyNumber = new Random().nextInt(goods_max - min_c + 1) + min_c;
        } else {
            buyNumber = new Random().nextInt(max_c - min_c + 1) + min_c;
        }

        GoodsStage stage=new GoodsStage();
        stage.setId(goodsRobot.getGoodsPeriodId());
        List<GoodsStage> stageList=goodsStageMapper.select(stage);
        if(!stageList.isEmpty()) {
            stage=stageList.get(0);
            // 查询出锁定的商品数量
            int lockingNum =goodsLockingMapper.findGoodsLockingCount(stage.getId());
            Integer lastNum = stage.getBuySize() - stage.getBuyIndex() - lockingNum;
            if(lastNum < buyNumber) {
                buyNumber = lastNum;
            }
        }
        return buyNumber;
    }
}
