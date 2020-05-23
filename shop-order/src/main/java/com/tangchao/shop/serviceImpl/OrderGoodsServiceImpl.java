package com.tangchao.shop.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tangchao.common.constant.OrderConstant;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.dto.adminDTO.CustomerDTO;
import com.tangchao.shop.mapper.*;
import com.tangchao.shop.pojo.*;
import com.tangchao.shop.service.OrderGoodsService;
import com.tangchao.shop.vo.UserByRecordVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderGoodsServiceImpl implements OrderGoodsService {


    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private OrderGoodsMapper orderGoodsMapper;

    @Autowired
    private CustomerEvaluationShowMapper customerEvaluationShowMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private GoodsFavouriteMapper goodsFavouriteMapper;

    @Autowired
    private WinningOrderMapper winningOrderMapper;

    @Autowired
    private TradeOrderMapper tradeOrderMapper;

    @Autowired
    private CustomerInfoMapper customerInfoMapper;

    @Override
    public PageResult<UserByRecordVO> selectBuyRecordList(Integer pageNo, Integer pageSize, Long userCode) {
        if (userCode == null) {
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        //  设置分页
        PageHelper.startPage(pageNo, pageSize, true);
        PageHelper.orderBy(" id DESC ");
        //  查询数据库
        List<UserByRecordVO> orderGoodsList = orderGoodsMapper.selectBuyRecordList(userCode);
        PageInfo<UserByRecordVO> pageInfo = new PageInfo<>(orderGoodsList);
        return new PageResult<>(pageInfo.getTotal(), orderGoodsList);
    }

    @Override
    public PageResult<UserByRecordVO> selectUserObtainGoodsList(Integer pageNo, Integer pageSize, Long userCode) {
        if (userCode == null) {
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        //  设置分页
        PageHelper.startPage(pageNo, pageSize, true);
        //  设置排序
        PageHelper.orderBy("open_prize_time DESC,id DESC ");
        //  查询订单列表
        //  查询数据库
        List<UserByRecordVO> orderGoodsList = orderGoodsMapper.selectUserObtainGoodsList(userCode);
        PageInfo<UserByRecordVO> pageInfo = new PageInfo<>(orderGoodsList);
        return new PageResult<>(pageInfo.getTotal(), orderGoodsList);
    }

    @Override
    public PageInfo<CustomerEvaluationShow> getCustomerEvaluationShow(Long userId, CustomerDTO customerDTO) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        PageHelper.startPage(customerDTO.getPageNo(),customerDTO.getPageSize(),false);

        if (!StringUtils.isBlank(customerDTO.getOrderNo())){
            OrderGoods goods=new OrderGoods();
            goods.setOrderNo(customerDTO.getOrderNo());
            List<OrderGoods> goodsList=orderGoodsMapper.select(goods);
            if (!goodsList.isEmpty()){
                customerDTO.setStageId(goodsList.get(0).getStageId().toString());
            }
        }
        List<CustomerEvaluationShow> list=customerEvaluationShowMapper.getCustomerEvaluationShowList(customerDTO);

        List<Long> showId=list.stream().map(CustomerEvaluationShow::getId).collect(Collectors.toList());

        List<Map<String,Object>> imgList=null;
        if (!showId.isEmpty()){
            imgList=customerEvaluationShowMapper.findByEvaluationShowImgByShowId(showId);
        }
        if (imgList!=null){
            Map<String,List<String>> imgMap=new HashMap<>();
            for (Map<String,Object> param:imgList){
                if (!imgMap.containsKey(param.get("showId").toString())){
                    imgMap.put(param.get("showId").toString(),new ArrayList<>());
                }
                imgMap.get(param.get("showId").toString()).add(param.get("imgUrl").toString());
            }
            list.forEach(s->s.setImgUrl(imgMap.get(s.getId().toString())));
        }
        int totalRecord=customerEvaluationShowMapper.countEvaluationShowImgByShow(customerDTO);
        PageInfo pageInfo=new PageInfo();
        pageInfo.setList(list);
        pageInfo.setTotal(totalRecord);
        return pageInfo;
    }

    @Override
    public Map<String, Object> getUserStatistics(Long userId) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setCustomerCode(userId);
        customerInfo=customerInfoMapper.selectOne(customerInfo);
        if (customerInfo==null){
            throw new CustomerException(ExceptionEnum.INVALID_USER_NOT_FOND);
        }
        Map<String,Object> resultMap=new HashMap<>();
        //我的收藏
        int favouriteNum=goodsFavouriteMapper.countGoodsFavourite(userId);
        resultMap.put("favouriteNum",favouriteNum);//我的收藏


        //邀请数量
        int friendSum = customerMapper.getInviteNum(customerInfo.getInviteCode());
        resultMap.put("inviteNum",friendSum);//邀请数量

        //获奖商品
        int winningNum=winningOrderMapper.countWinningOrderByUserCode(userId);
        resultMap.put("winningNum",winningNum);//获奖商品

        //欢购商品
        int orderNum=tradeOrderMapper.countTradeOrderByUserCode(userId);
        resultMap.put("orderNum",orderNum);//欢购商品
        return resultMap;
    }

    @Override
    public WinningOrder getLogisticsInfo(Long userCode, String goodsNo, Integer stageId) {
        if (userCode == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        WinningOrder winningOrder=new WinningOrder();
        winningOrder.setCustomerCode(userCode.toString());
        winningOrder.setGoodsNo(goodsNo);
        winningOrder.setStageId(stageId.longValue());
        winningOrder=winningOrderMapper.selectOne(winningOrder);
        return winningOrder;
    }
}
