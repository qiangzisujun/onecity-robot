package com.tangchao.shop.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.common.utils.ExportExcelUtil;
import com.tangchao.common.utils.StringUtil;
import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.dto.adminDTO.GoodsRobotDTO;
import com.tangchao.shop.mapper.GoodsMapper;
import com.tangchao.shop.mapper.GoodsRobotMapper;
import com.tangchao.shop.mapper.GoodsRobotSetMapper;
import com.tangchao.shop.mapper.GoodsStageMapper;
import com.tangchao.shop.pojo.Goods;
import com.tangchao.shop.pojo.GoodsRobot;
import com.tangchao.shop.pojo.GoodsRobotSet;
import com.tangchao.shop.pojo.GoodsStage;
import com.tangchao.shop.service.GoodsRobotSetService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class GoodsRobotSetServiceImpl implements GoodsRobotSetService {

    private static final Logger logger = LoggerFactory.getLogger(GoodsRobotSetServiceImpl.class);

    @Resource
    private GoodsRobotSetMapper goodsRobotSetMapper;

    @Autowired
    private GoodsStageMapper goodsStageMapper;

    @Resource
    private GoodsRobotMapper goodsRobotMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public GoodsRobotSet findByGoodsNo(Long goodsNo) {
        Example example = new Example(GoodsRobotSet.class);
        Example.Criteria criteria = example.createCriteria();
        // 商品编号
        criteria.andEqualTo("goodsNo", goodsNo);
        // 启用状态
        criteria.andEqualTo("status", 1);
        // 约定者不为空
        criteria.andIsNotNull("protocolLotteryNo");
        List<GoodsRobotSet> goodsRobotSetList = this.goodsRobotSetMapper.selectByExample(example);
        if (goodsRobotSetList.size() > 0) {
            return goodsRobotSetList.get(0);
        }
        return null;
    }

    @Override
    public PageResult<Map> selectGoodsRootSetList(String goodsName, String goodsNo, Integer typeId, Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        PageHelper.orderBy(" rs.create_time desc");
        List<Map> list=goodsRobotSetMapper.selectRobotAndSetList(goodsName,goodsNo,typeId);
        PageInfo<Map> pageInfo=new PageInfo<>(list);
        return new PageResult<>(pageInfo.getTotal(),list);
    }

    @Override
    public void updateGoodsRootSet(Long userCode, GoodsRobotDTO goodsRobotDTO) {
        if (userCode==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        GoodsStage goodsStage=new GoodsStage();
        goodsStage.setGoodsNo(goodsRobotDTO.getGoodsNo());
        goodsStage.setIsAward(0);
        PageHelper.startPage(1, 1);
        goodsStage=goodsStageMapper.selectOne(goodsStage);
        if(goodsStage==null){
            throw new CustomerException(ExceptionEnum.GOODS_NOT_FOND);
        }

        goodsStage.setJackPotAll(goodsRobotDTO.getJackPot()* goodsStage.getBuySize());
        try {
            goodsStage.setJackPotType(Integer.parseInt(goodsRobotDTO.getProtocolLotteryNo()));
        } catch (NumberFormatException e) {
            goodsStage.setJackPotType(0);
        }
        logger.info(goodsRobotDTO.getJackPot()+"");
        GoodsRobotSet goodsRobotSet=new GoodsRobotSet();
        goodsRobotSet.setId(Long.valueOf(goodsRobotDTO.getId()));
        goodsRobotSet.setGoodsNo(goodsRobotDTO.getGoodsNo());
        goodsRobotSet.setMaxPurchasesCount(goodsRobotDTO.getMaxPurchasesCount());
        goodsRobotSet.setMinPurchasesCount(goodsRobotDTO.getMinPurchasesCount());
        goodsRobotSet.setMaxPurchasesMinute(goodsRobotDTO.getMaxPurchasesMinute());
        goodsRobotSet.setMinPurchasesMinute(goodsRobotDTO.getMinPurchasesMinute());
        goodsRobotSet.setJackPot(goodsRobotDTO.getJackPot());
        goodsRobotSet.setProtocolLotteryNo(goodsRobotDTO.getProtocolLotteryNo());
        goodsRobotSet.setStatus(goodsRobotDTO.getStatus());
        goodsRobotSet.setLastModifyId(userCode);
        goodsRobotSet.setPercentage(1.0);
        goodsRobotSet.setPeriodsNumber(10000L);
        goodsRobotSet.setLastModifyTime(new Date());
        int count=goodsStageMapper.updateByPrimaryKeySelective(goodsStage);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
        logger.info(goodsRobotSet.getJackPot()+"");
        count=this.goodsRobotSetMapper.updateByPrimaryKeySelective(goodsRobotSet);
        if (count>0){
            if (goodsRobotSet.getStatus() == 1) {// 如果启用，查询下是否有创建任务，如果没有创建则创建任务，以防创建设置时未创建任务。
                if (!this.couGoodsRobot(goodsRobotSet)) {
                    throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
                }
            } else {
                goodsRobotSetMapper.updateRobotMaxCountBySetId(goodsRobotSet.getId());
                GoodsRobotSet robotSet=new GoodsRobotSet();
                robotSet.setId(Long.valueOf(goodsRobotDTO.getId()));
                robotSet.setStatus(goodsRobotDTO.getStatus());
                this.goodsRobotSetMapper.updateByPrimaryKeySelective(goodsRobotSet);
            }
        }
    }

    @Override
    public void deleteGoodsRootSet(Long userCode, Long id) {
        if (userCode==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        if (id==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        GoodsRobotSet goodsRobotSet=goodsRobotSetMapper.selectByPrimaryKey(id);
        if (goodsRobotSet==null){
            throw new CustomerException(ExceptionEnum.INVALID_USER_NOT_FOND);
        }
        int count=goodsRobotSetMapper.deleteByPrimaryKey(id);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertGoodsRootSet(Long userCode, GoodsRobotDTO goodsRobotDTO) {
        if (userCode==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        GoodsRobotSet goodsRobotSet1=new GoodsRobotSet();
        goodsRobotSet1.setGoodsNo(goodsRobotDTO.getGoodsNo());
        goodsRobotSet1=goodsRobotSetMapper.selectOne(goodsRobotSet1);
        if (goodsRobotSet1!=null){
            throw new CustomerException(ExceptionEnum.ROBOT_TASK_EXIST);
        }
        Goods goods=new Goods();
        goods.setGoodsNo(goodsRobotDTO.getGoodsNo());
        goods=goodsMapper.selectOne(goods);
        if (null == goods) {
            throw new CustomerException(ExceptionEnum.GOODS_NOT_FOND);
        }
        GoodsStage goodsStage=new GoodsStage();
        goodsStage.setGoodsNo(goodsRobotDTO.getGoodsNo());
        goodsStage.setIsAward(0);
        PageHelper.startPage(1, 1);
        goodsStage=goodsStageMapper.selectOne(goodsStage);
        if (null == goodsStage) {
            throw new CustomerException(ExceptionEnum.GOODS_NO_IS_UP);
        }else{
            goodsStage.setJackPotAll(goodsRobotDTO.getJackPot()* goodsStage.getBuySize());
            try {
                goodsStage.setJackPotType(Integer.parseInt(goodsRobotDTO.getProtocolLotteryNo()));
            } catch (NumberFormatException e) {
                goodsStage.setJackPotType(0);
            }
            this.goodsStageMapper.updateByPrimaryKeySelective(goodsStage);
        }
        GoodsRobotSet goodsRobotSet=new GoodsRobotSet();
        goodsRobotSet.setGoodsNo(goodsRobotDTO.getGoodsNo());
        goodsRobotSet.setMaxPurchasesCount(goodsRobotDTO.getMaxPurchasesCount());
        goodsRobotSet.setMinPurchasesCount(goodsRobotDTO.getMinPurchasesCount());
        goodsRobotSet.setMaxPurchasesMinute(goodsRobotDTO.getMaxPurchasesMinute());
        goodsRobotSet.setMinPurchasesMinute(goodsRobotDTO.getMinPurchasesMinute());
        goodsRobotSet.setJackPot(goodsRobotDTO.getJackPot());
        goodsRobotSet.setProtocolLotteryNo(goodsRobotDTO.getProtocolLotteryNo());
        goodsRobotSet.setStatus(goodsRobotDTO.getStatus());

        goodsRobotSet.setPeriodsNumber(10000L);
        goodsRobotSet.setPercentage(1.0);
        goodsRobotSet.setCreateId(userCode);
        goodsRobotSet.setLastModifyId(userCode);
        goodsRobotSet.setGoodsName(goods.getGoodsName());
        goodsRobotSet.setGoodsId(goods.getId());
        goodsRobotSet.setBuyingPeriodsNumber(1L);
        Date now = new Date();
        // 设置创建人,创建时间
        goodsRobotSet.setCreateTime(now);
        // 设置修改人,修改时间
        goodsRobotSet.setLastModifyTime(now);

        if (this.goodsRobotSetMapper.insertSelective(goodsRobotSet) > 0) {
            if (goodsRobotSet.getStatus() == 1) {// 如果不启用，则先不创建任务；启用，同步创建任务
                if (!this.couGoodsRobot(goodsRobotSet)) {
                    throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
                }
            } else {
                goodsRobotSetMapper.updateRobotMaxCountBySetId(goodsRobotSet.getId());
            }
        }


    }

    @Override
    public void importRobotSet(Long userId, MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException {
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
                GoodsRobotDTO goodsRobotDTO=new GoodsRobotDTO();
                goodsRobotDTO.setGoodsNo(sheet.getRow(i).getCell(0).toString());
                goodsRobotDTO.setMinPurchasesCount(Integer.valueOf(StringUtil.getValue(sheet.getRow(i).getCell(1))));
                goodsRobotDTO.setMaxPurchasesCount(Integer.valueOf(StringUtil.getValue(sheet.getRow(i).getCell(2))));
                goodsRobotDTO.setMinPurchasesMinute(Integer.valueOf(StringUtil.getValue(sheet.getRow(i).getCell(3))));
                goodsRobotDTO.setMaxPurchasesMinute(Integer.valueOf(StringUtil.getValue(sheet.getRow(i).getCell(4))));
                goodsRobotDTO.setJackPot(Double.valueOf(sheet.getRow(i).getCell(5).toString()));
                goodsRobotDTO.setProtocolLotteryNo(StringUtil.getValue(sheet.getRow(i).getCell(6)));
                goodsRobotDTO.setStatus(1);
                this.insertGoodsRootSet(1L,goodsRobotDTO);
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

    @Override
    public void exportRobotSet(Long userCode, HttpServletResponse response) throws UnsupportedEncodingException {

        List<Map<String,Object>> list=goodsRobotSetMapper.getGoodsSetList();

        String[] headers = { "奖品编号", "每期购买最小值", "每期购买最大值", "购买时间频率最小值(秒)", "购买时间频率最大值(秒)", "物品奖池比例", "开奖类型（0为正常开奖，1为保底开奖，2为促销开奖）"};
        String[] keys = { "goods_no", "min_purchases_count","max_purchases_count", "min_purchases_minute", "max_purchases_minute", "percentage", "protocol_lottery_no"};
        Integer[] widths = { 100 };
        ExportExcelUtil.exportExcel("商品机器人设置", headers, keys, widths, list, response, null);
    }

    @Override
    public List<GoodsRobot> findGoodsRobotsList(String type) {
        return this.goodsRobotSetMapper.selectList(type);
    }


    /**
     * 创建或更新任务
     * @param goodsRobotSet
     * @return
     */
    private boolean couGoodsRobot(GoodsRobotSet goodsRobotSet) {
        GoodsStage goodsStage = new GoodsStage();
        Example exampleGoodsStage = new Example(GoodsStage.class);
        Example.Criteria criteriaGoodsStage = exampleGoodsStage.createCriteria();
        criteriaGoodsStage.andEqualTo("goodsNo", goodsRobotSet.getGoodsNo());
        criteriaGoodsStage.andEqualTo("isAward", 0);
        PageHelper.startPage(1, 1);
        List<GoodsStage> listGoodsStage = this.goodsStageMapper.selectByExample(exampleGoodsStage);
        if (listGoodsStage.size() > 0){
            goodsStage=listGoodsStage.get(0);
        }else{
            return false;
        }

        Example example = new Example(GoodsRobot.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("goodsId", goodsStage.getGoodsId());
        PageHelper.startPage(1, 1);
        List<GoodsRobot> list = this.goodsRobotMapper.selectByExample(example);
        GoodsRobot goodsRobot = null;
        boolean update = true;//是否需要更新
        boolean addStage = true;//是否需要加1期
        if (list.isEmpty()) {
            goodsRobot = new GoodsRobot();
            update = false;
            goodsRobot.setBoughtCount(0);
            goodsRobot.setJackPotNow(0.0);
        }else {
            goodsRobot = list.get(0);
        }
        goodsRobot.setSetId(goodsRobotSet.getId());
        goodsRobot.setGoodsId(goodsStage.getGoodsId());
        if(goodsRobot.getGoodsPeriodId() == goodsStage.getId()) {
            addStage = false;
        }else {
            goodsRobot.setGoodsPeriodId(goodsStage.getId());
        }
        goodsRobot.setGoodsLimitCount(goodsStage.getBuyNum());

        goodsRobot.setMaxPurchasesConut(goodsRobotSet.getMaxPurchasesCount());
        goodsRobot.setMinPurchasesConut(goodsRobotSet.getMinPurchasesCount());
        Integer max_m = goodsRobotSet.getMaxPurchasesMinute();
        Integer min_m = goodsRobotSet.getMinPurchasesMinute();
        if (max_m == null || min_m == null || max_m <= 0 || min_m > max_m) {// 不符合条件的数值
            goodsRobot.setBuyRateMinute(new Random().nextInt(30));// 30分钟的随机购买时间
        } else {
            goodsRobot.setBuyRateMinute(new Random().nextInt(max_m - min_m + 1) + min_m);
        }
        goodsRobot.setMaxPurchasesMinute(max_m);
        goodsRobot.setMinPurchasesMinute(min_m);
        goodsRobot.setMaxCount((int) (goodsRobotSet.getPercentage() * goodsStage.getBuySize()));
        //
        goodsRobot.setJackPotAll(goodsRobotSet.getJackPot()* goodsStage.getBuySize());
        try {
            goodsRobot.setJackPotType(Integer.parseInt(goodsRobotSet.getProtocolLotteryNo()));
        } catch (NumberFormatException e) {
            goodsRobot.setJackPotType(0);
        }
        //
        if(update) {
            this.goodsRobotMapper.updateByPrimaryKeySelective(goodsRobot);//更新机器人购买任务数据
        }else {
            this.goodsRobotMapper.insertSelective(goodsRobot);
        }
        if(addStage) {
            goodsRobotSet.setBuyingPeriodsNumber(goodsRobotSet.getBuyingPeriodsNumber());
            this.goodsRobotSetMapper.updateByPrimaryKeySelective(goodsRobotSet);
        }
        return true;
    }
}
