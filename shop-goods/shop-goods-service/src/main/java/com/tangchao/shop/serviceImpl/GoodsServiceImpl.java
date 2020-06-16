package com.tangchao.shop.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tangchao.common.constant.CommonConstant;
import com.tangchao.common.constant.ConfigkeyConstant;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.common.utils.ExportExcelUtil;
import com.tangchao.common.utils.RandomUtil;
import com.tangchao.common.utils.StringUtil;
import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.dto.adminDTO.GoodsRobotDTO;
import com.tangchao.shop.mapper.*;
import com.tangchao.shop.pojo.*;
import com.tangchao.shop.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GoodsServiceImpl implements GoodsService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsStageMapper goodsStageMapper;


    @Autowired
    private GoodsRcmdMapper goodsRcmdMapper;

    @Autowired
    private GoodsRobotSetMapper goodsRobotSetMapper;

    @Autowired
    private GoodsRobotMapper goodsRobotMapper;

    @Autowired
    private UserConfMapper userConfMapper;

    @Autowired
    private GoodsTypeMapper goodsTypeMapper;


    @Override
    public PageResult<Goods> goodsList(Integer pageNo, Integer pageSize,String  goodsName,Integer typeId) {
        PageHelper.startPage(pageNo,pageSize);


        PageHelper.orderBy("create_time desc");
        Example example = new Example(Goods.class);
        Example.Criteria criteria = example.createCriteria();
        if (!StringUtils.isBlank(goodsName)){
            criteria.andLike("goodsName","%"+goodsName+"%");
        }
        if (typeId!=null){
            criteria.andEqualTo("typeId",typeId);
        }
        criteria.andEqualTo("flag","0");
        List<Goods> goodsList = goodsMapper.selectByExample(example);


        GoodsType type=new GoodsType();
        type.setFlag(0);
        List<GoodsType> typeList=goodsTypeMapper.select(type);


        if (typeList!=null&&typeList.size()>0){
            Map<Long,String> typeName=typeList.stream().collect(Collectors.toMap(t->t.getId(),t->t.getTypeNameZh()));
            goodsList.forEach(s -> s.setTypeName(typeName.get(s.getTypeId())));
        }



        PageInfo<Goods> goodsPageInfo = new PageInfo<>(goodsList);
        return new PageResult<>(goodsPageInfo.getTotal(),goodsList);
    }

    @Override
    public void addGoods( Goods goods) {
        if (goods ==null){
            throw new CustomerException(ExceptionEnum.GOODS_IS_NOT);
        }
        goods.setCreateTime(new Date());// 设置创建时间
        goods.setLastModifyTime(new Date());// 设置最后修改时间
        goods.setGoodsNo(RandomUtil.generateLongByDateTime(3));// 设置商品唯一编码
        goods.setIsRcmd(0);
        goods.setIsNew(0);
        goods.setIsActivity(2);
        goods.setIsSell(0);
        goods.setFlag(0);
        Integer i = goodsMapper.insert(goods);
        if (i.equals(0)){
            throw new CustomerException(ExceptionEnum.GOODS_ADD_ERROR);
        }
    }

    @Override
    public void deleteGoods(Long userId,String goodsNo) {

        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        if (goodsNo==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }

        Goods goods = new Goods();
        goods.setGoodsNo(goodsNo);
        goods.setFlag(0);
        goods=goodsMapper.selectOne(goods);
        if (goods==null){
            throw new CustomerException(ExceptionEnum.GOODS_NOT_FOND);
        }
        goods.setLastModifyId(userId);
        goods.setFlag(-1);
        Integer delete = goodsMapper.updateByPrimaryKeySelective(goods);
        if (delete.equals(0)){
            throw new CustomerException(ExceptionEnum.GOODS_DELECT_ERROR);
        }
    }

    @Override
    public void updateGoods(Goods goods) {
        if (goods ==null){
            throw new CustomerException(ExceptionEnum.GOODS_IS_NOT);
        }
        Goods _goods = goodsMapper.selectByPrimaryKey(goods.getId());
        goods.setFlag(_goods.getFlag());
        goods.setCreateId(_goods.getCreateId());
        goods.setCreateTime(_goods.getCreateTime());
        goods.setLastModifyTime(new Date());// 设置修改时间
        goods.setGoodsNo(_goods.getGoodsNo());
        goods.setIsSell(_goods.getIsSell());
        goods.setSellStartTime(_goods.getSellStartTime());
        goods.setSellEndTime(_goods.getSellEndTime());
        goods.setIsRcmd(_goods.getIsRcmd());
        goods.setIsNew(_goods.getIsNew());
        if (goods.getBuyPrice() == null || goods.getBuyPrice() <= 0) {
            goods.setBuyPrice(1.0);
        }
        Integer i = goodsMapper.updateByPrimaryKey(goods);
        if (i.equals(0)){
            throw new CustomerException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }
    }

    @Override
    public PageResult<Goods> sellgoods(Integer pageNo, Integer pageSize, String goodsName, Integer typeId) {
        PageHelper.startPage(pageNo,pageSize);
        PageHelper.orderBy("r.goods_hot desc,i.id asc");
        List<Goods> goods = goodsMapper.selectSellGoodsList(goodsName, typeId);
        PageInfo<Goods> goodsPageInfo = new PageInfo<>(goods);
        return new PageResult<>(goodsPageInfo.getTotal(),goods);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shelfGoods(Long userCode, Long id, Integer isSell) {
        if (userCode ==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        if (id == null || isSell == null) {
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        Goods goods = new Goods();
        Date now = new Date();
        goods.setId(id);// 设置id
        goods.setIsSell(isSell);// 设置在售状态
        goods.setLastModifyId(userCode);// 设置修改人
        goods.setLastModifyTime(now);// 设置修改时间
        if (isSell == 0) {// 下架
            goods.setIsRcmd(0);// 非推荐商品
            goods.setSellEndTime(now);
        }
        if (isSell == 1) {// 上架
            goods.setSellStartTime(now);
        }
        int count=goodsMapper.updateByPrimaryKeySelective(goods);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }

        if (isSell == 1) {// 上架
            count = this.createGoodsStage(id, null);// 创建商品期数
            if (count!=1){
                throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
            }
        }
        if (isSell == 0) {// 下架

            Example example = new Example(GoodsRcmd.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("goodsId", id);

            GoodsRcmd cmd=new GoodsRcmd();
            cmd.setFlag(-1);
            cmd.setLastModifyId(userCode);
            cmd.setLastModifyTime(new Date());
            cmd.setGoodsId(id);
            count = goodsRcmdMapper.updateByExampleSelective(cmd, example);
            count=goodsRcmdMapper.updateByPrimaryKeySelective(cmd);// 删除推荐表的信息
            /*if (count!=1){
                throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
            }*/
        }
    }

    @Override
    public void exportGoodsInfo(List<String> ids, HttpServletResponse response) throws UnsupportedEncodingException {


        String fileName ="商品信息";
        List<Map<String, Object>> list = goodsMapper.selectByMap();
        String[] headers = { "奖品名称", "类型编号", "奖品库存", "奖品价格", "是否允许晒单", "兑换价格", "每人限购次数","每次购买价格"};
        String[] keys = { "goods_name","type_id", "goods_inv", "goods_price", "is_show_order", "recovery_price","buy_num","buy_price"};
        Integer[] widths = { 80 };
        ExportExcelUtil.exportExcel(fileName, headers, keys, widths, list, response, null);
    }

    @Override
    public void updateGoodsInfoHot(Long userId, Map<String, Object> data) {
        if (userId ==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        String goodsNo=data.get("goodsNo").toString();//商品编号
        String goodsHot=data.get("goodsHot").toString();//热度

        if (StringUtils.isBlank(goodsNo)||StringUtils.isBlank(goodsHot)) {
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        if (Integer.valueOf(goodsHot)> 999 || Integer.valueOf(goodsHot)< 0) {
            goodsHot="999";// 商品热度超过范围则设置默认，默认热度999
        }

        Goods goods = new Goods();
        goods.setGoodsNo(goodsNo);
        goods.setFlag(0);
        goods=goodsMapper.selectOne(goods);
        if (goods==null){
            throw new CustomerException(ExceptionEnum.GOODS_NOT_FOND);
        }
        int count=this.goodsRcmdMapper.updateBygoodsRcmd(goods.getId(),Integer.valueOf(goodsHot));
         count=goodsStageMapper.updateStageHotByGoodsId(goods.getId(), Integer.valueOf(goodsHot));
        if (count!=1) {
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void isRecommend(Long userId, Map<String, Object> data) {
        if (userId ==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        String typeId=data.get("typeId").toString();//1是，2不是
        String goodsId=data.get("goodsId").toString();//商品id

        if (StringUtils.isBlank(typeId)||StringUtils.isBlank(goodsId)){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }


        if (typeId.equals("1")){
            Date now = new Date();
            GoodsRcmd rcmd = new GoodsRcmd();
            rcmd.setFlag(0);
            rcmd.setGoodsId(Long.valueOf(goodsId));
            rcmd=goodsRcmdMapper.selectOne(rcmd);
            if (rcmd==null){
                rcmd = new GoodsRcmd();
                rcmd.setCreateId(userId);
                rcmd.setLastModifyId(userId);
                rcmd.setCreateTime(now);
                rcmd.setLastModifyTime(now);
                rcmd.setFlag(0);
                rcmd.setGoodsId(Long.valueOf(goodsId));
                this.goodsRcmdMapper.insertSelective(rcmd);
            }
            Goods goods=goodsMapper.selectByPrimaryKey(goodsId);
            if (goods!=null){
                goods.setIsRcmd(Integer.valueOf(typeId));
            }
            int count=goodsMapper.updateByPrimaryKey(goods);
        }else if(typeId.equals("0")){
            Example example = new Example(GoodsRcmd.class);
            Example.Criteria criteria = example.createCriteria();
            // 设置查询条件
            criteria.andNotEqualTo("flag", -1);
            criteria.andEqualTo("goodsId", goodsId);
            // 创建需要更新的推荐商品
            GoodsRcmd rcmd = new GoodsRcmd();
            rcmd.setLastModifyId(userId);
            rcmd.setLastModifyTime(new Date());
            rcmd.setFlag(-1);// 设置删除状态
            Goods goods=goodsMapper.selectByPrimaryKey(goodsId);
            if (goods!=null){
                goods.setIsRcmd(Integer.valueOf(typeId));
            }
            int count=goodsMapper.updateByPrimaryKey(goods);
            this.goodsRcmdMapper.updateByExampleSelective(rcmd, example);
            goodsStageMapper.updateStageHotByGoodsId(Long.valueOf(goodsId), null);
        }else {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
    }

    @Override
    public void updateGoodsNew(Long userId, Map<String, Object> data) {
        if (userId ==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        String isNew=data.get("isNew").toString();//1是，0不是
        String goodsId=data.get("goodsId").toString();//商品id

        Goods goods = new Goods();
        goods.setId(Long.valueOf(goodsId));// 设置id
        goods.setLastModifyId(userId);// 设置修改人
        goods.setLastModifyTime(new Date());// 设置修改时间
        goods.setIsNew(Integer.valueOf(isNew));
        int count=this.goodsMapper.updateByPrimaryKeySelective(goods);
        if ( count!=1) {
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchShelfGoods(Long userCode, Map<String, Object> data) {
        List<String> ids= (List<String>) data.get("ids");
        for (String id:ids){
            this.shelfGoods(userCode,Long.valueOf(id),1);
        }
    }

    @Override
    public void importGoodsInfo(MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        String msg = null;
        InputStream stream=null;
        PrintWriter pw = null;
        try {
            stream = file.getInputStream();//获取输入流

            XSSFWorkbook book = new XSSFWorkbook(stream);
            Sheet sheet = book.getSheetAt(0);
            int rows = sheet.getLastRowNum();// 行
            for (int i = 1; i <=rows; i++) {
                Goods goods=new Goods();
                goods.setGoodsName(sheet.getRow(i).getCell(0).toString());
                goods.setTypeId(Long.valueOf(StringUtil.getValue(sheet.getRow(i).getCell(1))));
                goods.setGoodsInv(Integer.valueOf(StringUtil.getValue(sheet.getRow(i).getCell(2))));
                goods.setGoodsPrice(Double.valueOf(StringUtil.getValue(sheet.getRow(i).getCell(3))));
                goods.setIsShowOrder(Integer.valueOf(StringUtil.getValue(sheet.getRow(i).getCell(4))));
                goods.setRecoveryPrice(Double.valueOf(StringUtil.getValue(sheet.getRow(i).getCell(5))));
                goods.setBuyNum(Integer.valueOf(StringUtil.getValue(sheet.getRow(i).getCell(6))));
                goods.setBuyPrice(Double.valueOf(StringUtil.getValue(sheet.getRow(i).getCell(7))));
                this.addGoods(goods);
            }
            msg="导入成功！";
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            stream.close();
            pw = response.getWriter();
            pw.close();
        }
    }

    public int createGoodsStage(Long goodsId, GoodsStage oldStage) {
        GoodsStage stage = new GoodsStage();
        stage.setIsAward(0);
        stage.setFlag(0);
        stage.setGoodsId(goodsId);
        List<GoodsStage> stageList = goodsStageMapper.select(stage);// 如果查询报错则是有多条正在进行开奖的商品
        int count=0;
        if (!stageList.isEmpty()) {
            // 已存在正在开奖的商品
            return 1;
        }
        Goods goods = goodsMapper.selectByPrimaryKey(goodsId);// 查询该商品最新信息
        if (goods == null || goods.getFlag() != 0) {
            Date now = new Date();
            goods.setId(goodsId);// 设置id
            goods.setIsSell(0);// 设置在售状态
            goods.setLastModifyTime(now);// 设置修改时间
            goods.setIsRcmd(0);// 非推荐商品
            goods.setSellEndTime(now);
            count=goodsMapper.updateByPrimaryKeySelective(goods);
            if (count!=1){
                logger.error("商品更新错误1");
            }
            count=goodsRcmdMapper.deleteRcmdByGoodsId(goodsId);
            if (count!=1){
                logger.error("商品更新错误2");
            }
        }
        // 判断商品是否上架
        if (goods.getIsSell() == 0) {
            return 0;
            //throw new CustomerException(ExceptionEnum.SELL_IS_DOWN);
        }
        // 判断库存是否为0
        if (goods.getGoodsInv() <= 0) {
            Date now = new Date();
            goods.setId(goodsId);// 设置id
            goods.setIsSell(0);// 设置在售状态
            goods.setLastModifyTime(now);// 设置修改时间
            goods.setIsRcmd(0);// 非推荐商品
            goods.setSellEndTime(now);
            count=goodsMapper.updateByPrimaryKeySelective(goods);
            if (count!=1){
                logger.error("商品更新错误3");
            }
            count=goodsRcmdMapper.deleteRcmdByGoodsId(goodsId);
            if (count!=1){
                logger.error("商品更新错误4");
            }
            //throw new CustomerException(ExceptionEnum.INV_IS_ZERO);
        }
        // 查询商品最大期数
        Integer index = goodsStageMapper.selectMaxIndexByGoodsId(goodsId);
        if (index == null) {// 无最大期数则是第一期
            index = 1;
        } else {
            index++;
        }
        // 判断是否过期
        if (goods.getSellStage() != null && index > goods.getSellStage()) {
            Date now = new Date();
            goods.setId(goodsId);// 设置id
            goods.setIsSell(0);// 设置在售状态
            goods.setLastModifyTime(now);// 设置修改时间
            goods.setIsRcmd(0);// 非推荐商品
            goods.setSellEndTime(now);
            count=goodsMapper.updateByPrimaryKeySelective(goods);
            if (count!=1){
                logger.error("商品更新错误5");
            }
            count=goodsRcmdMapper.deleteRcmdByGoodsId(goodsId);
            if (count!=1){
                logger.error("商品更新错误6");
            }
        }
        stage = new GoodsStage();// 创建新的商品期数信息
        Example example = new Example(GoodsRcmd.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("flag", 0);
        criteria.andEqualTo("goodsId", goodsId);
        List<GoodsRcmd> rcmdList = this.goodsRcmdMapper.selectByExample(example);// 商品热度
        if (!rcmdList.isEmpty()) {
            stage.setGoodsHot(rcmdList.get(0).getGoodsHot());
        }

        // 创建商品期次模板
        stage = this.createGoodsStageTemplate(goods, stage);
        stage.setStageIndex(index); // 商品期次
        stage.setIsActivity(goods.getIsActivity()); // 活动商品
        if (CommonConstant.YES == goods.getIsActivity()) {// 活动商品
            stage.setBuySize(goods.getActivityBuyNum());
            stage.setBuyNum(goods.getActivityBuyNum());
        } else {
            stage.setBuySize((int) Math.round(goods.getGoodsPrice() / goods.getBuyPrice()));// 购买总数
            if (stage.getBuyNum() <= 0 || stage.getBuyNum() > stage.getBuySize()) {// 每人限购数不在购买总数范围内，则为不限制购买
                stage.setBuyNum(stage.getBuySize());
            }
        }
        stage.setGoodsInv(stage.getBuySize());// 当前购买的商品库存
        stage.setSellStartTime(goods.getSellStartTime());// 设置上架时间
        stage.setRecoveryPrice(goods.getRecoveryPrice());// 设置商品回收价
        if (oldStage != null) {
            stage.setJackPotAll(oldStage.getJackPotAll());
            stage.setJackPotType(oldStage.getJackPotType());
            //检测上期奖池值是否足够，如果未足够则流入当前期
            if (oldStage.getJackPotNow() < oldStage.getJackPotAll()) {
                stage.setJackPotNow(oldStage.getJackPotNow());
            }
        }
        count=goodsStageMapper.insertSelective(stage);
        if (count!=1){
            logger.error("商品更新错误7");
        }
        if (count==1) {// 创建商品期数
            Goods _goods = new Goods();
            _goods.setId(goodsId);
            _goods.setGoodsInv(goods.getGoodsInv() - 1);
            this.goodsMapper.updateByPrimaryKeySelective(_goods);// 更新商品库存

            if (CommonConstant.NO == goods.getIsActivity()) {
                GoodsRobotSet goodsRobotSet = new GoodsRobotSet();
                goodsRobotSet.setGoodsId(goodsId);
                goodsRobotSet.setStatus(1);
                List<GoodsRobotSet> goodsRobotSetList = goodsRobotSetMapper.select(goodsRobotSet);
                if (goodsRobotSetList.size()>0){
                    goodsRobotSet = goodsRobotSetList.get(0);
                    if (goodsRobotSet != null&& goodsRobotSet.getPeriodsNumber() > goodsRobotSet.getBuyingPeriodsNumber()) {// 如果产品有机器人任务设置，重新生成下一期的机器人任务。
                        GoodsRobot robot = new GoodsRobot();
                        robot.setGoodsId(goodsId);
                        List<GoodsRobot> robotList = goodsRobotMapper.select(robot);
                        robot = robotList.get(0);
                        boolean update = true;
                        if (robot == null) {
                            robot = new GoodsRobot();
                            update = false;
                        }
                        robot.setSetId(goodsRobotSet.getId());
                        robot.setGoodsId(stage.getGoodsId());
                        robot.setGoodsPeriodId(stage.getId());
                        Integer max_m = goodsRobotSet.getMaxPurchasesMinute();
                        Integer min_m = goodsRobotSet.getMinPurchasesMinute();
                        if (max_m == null || min_m == null || max_m <= 0 || min_m > max_m) {// 不符合条件的数值
                            robot.setBuyRateMinute(new Random().nextInt(30));// 30分钟的随机购买时间
                        } else {
                            robot.setBuyRateMinute(new Random().nextInt(max_m - min_m + 1) + min_m);
                        }
                        robot.setGoodsLimitCount(stage.getBuyNum());
                        robot.setMaxCount((int) (goodsRobotSet.getPercentage() * stage.getBuySize()));
                        robot.setBoughtCount(0);
                        robot.setMaxPurchasesConut(goodsRobotSet.getMaxPurchasesCount());
                        robot.setMinPurchasesConut(goodsRobotSet.getMinPurchasesCount());
                        robot.setMaxPurchasesMinute(goodsRobotSet.getMaxPurchasesMinute());
                        robot.setMinPurchasesMinute(goodsRobotSet.getMinPurchasesMinute());
                        if (update) {
                            count=goodsRobotMapper.updateByPrimaryKeySelective(robot);// 更新机器人购买任务数据
                            if (count!=1){
                                logger.error("商品更新错误8");
                            }
                        } else {
                            count=goodsRobotMapper.insertSelective(robot);// 创建机器人购买任务数据
                            if (count!=1){
                                logger.error("商品更新错误9");
                            }
                        }
                        goodsRobotSet.setBuyingPeriodsNumber(goodsRobotSet.getBuyingPeriodsNumber() + 1);
                        count=goodsRobotSetMapper.updateByPrimaryKeySelective(goodsRobotSet);
                    } else {//已经跑完，重置购买数量
                        GoodsRobot robot = new GoodsRobot();
                        robot.setGoodsId(goodsId);
                        List<GoodsRobot> robotList = goodsRobotMapper.select(robot);
                        if (robotList.size()>0) {
                            robot = robotList.get(0);
                            robot.setBoughtCount(0);
                            count=goodsRobotMapper.updateByPrimaryKeySelective(robot);// 更新机器人购买任务数据
                            if (count!=1){
                                logger.error("商品更新错误10");
                            }
                        }
                    }
                }else {//已经跑完，重置购买数量
                    GoodsRobot robot = new GoodsRobot();
                    robot.setGoodsId(goodsId);
                    List<GoodsRobot> robotList = goodsRobotMapper.select(robot);
                    if (robotList.size()>0) {
                        robot = robotList.get(0);
                        robot.setBoughtCount(0);
                        count=goodsRobotMapper.updateByPrimaryKeySelective(robot);// 更新机器人购买任务数据
                        if (count!=1){
                            logger.error("商品更新错误10");
                        }
                    }
                }
            }
        }
        return count;
    }

    private GoodsStage createGoodsStageTemplate(Goods goods, GoodsStage stage) {
        // 创建时间,修改时间
        Date now = new Date();
        stage.setCreateTime(now);
        stage.setLastModifyTime(now);
        // 商品Id
        stage.setGoodsId(goods.getId());
        // 商品类型Id
        stage.setTypeId(goods.getTypeId());
        // 商品编号
        stage.setGoodsNo(goods.getGoodsNo());
        // 商品名称
        stage.setGoodsName(goods.getGoodsName());
        // 品牌
        stage.setGoodsBrand(goods.getGoodsBrand());
        // 规格
        stage.setGoodsSpec(goods.getGoodsSpec());
        // 商品价格
        stage.setGoodsPrice(goods.getGoodsPrice());
        // 商品图片
        stage.setGoodsPicture(goods.getGoodsPicture());
        // 商品详情图
        stage.setGoodsInfoPicture(goods.getGoodsInfoPicture());
        // 商品编码
        stage.setGoodsCode(goods.getGoodsCode());
        // 是否允许晒单
        stage.setIsShowOrder(goods.getIsShowOrder());
        // 每人限购次数
        stage.setBuyNum(goods.getBuyNum());
        // 每次购买价格
        stage.setBuyPrice(goods.getBuyPrice());
        // 删除标记
        stage.setFlag(0);
        // 是否已开奖
        stage.setIsAward(0);
        return stage;
    }
}
