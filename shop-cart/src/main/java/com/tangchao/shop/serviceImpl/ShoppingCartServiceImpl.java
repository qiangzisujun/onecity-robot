package com.tangchao.shop.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tangchao.common.constant.ConfigkeyConstant;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.interceptor.UserInterceptor;
import com.tangchao.shop.mapper.GoodsStageMapper;
import com.tangchao.shop.mapper.ShoppingCartMapper;
import com.tangchao.shop.mapper.UserConfMapper;
import com.tangchao.shop.pojo.*;
import com.tangchao.shop.service.ShoppingCartService;
import com.tangchao.shop.vo.CartVO;
import com.tangchao.shop.vo.GoodsStageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private GoodsStageMapper goodsStageMapper;

    @Autowired
    private ShoppingCartMapper cartMapper;

    @Autowired
    private UserConfMapper userConfMapper;

    @Override
    public void addCart(Long stageId,Integer num) {
        //获取用户登录
        UserInfo user = UserInterceptor.getUserInfo();
        if (user== null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        if (stageId == null||num==null) {
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setCustomerCode(user.getUserCode());
        shoppingCart.setStageId(stageId);
        shoppingCart.setPayNum(num);
        //  获取商品信息
        GoodsStage stage = new GoodsStage();
        stage.setId(stageId);
        stage = goodsStageMapper.selectOne(stage);
        if (null == stage || stage.getIsAward() == 1) {
            throw new CustomerException(ExceptionEnum.GOODS_NOT_FOND);
        }
        //  判断库存
        if (stage.getGoodsInv() < shoppingCart.getPayNum()) {
            throw new CustomerException(ExceptionEnum.STOCK_INSUFFICIENT_ERROR);
        }
        shoppingCart.setGoodsNo(stage.getGoodsNo());   //  设置商品编号
        shoppingCart.setIsCheck(1);        //  默认为选中状态
        //  获取用户购物车
        ShoppingCart cartInfo = new ShoppingCart();
        cartInfo.setCustomerCode(user.getUserCode());
        List<ShoppingCart> cartList = cartMapper.select(cartInfo);
        boolean isUpdate = false;
        for (ShoppingCart cart : cartList) {
            if (cart.getStageId().equals(shoppingCart.getStageId())) {
                //  库存校验
                int payNum = cart.getPayNum() + shoppingCart.getPayNum();
                if (payNum > stage.getGoodsInv()) {
                    throw new CustomerException(ExceptionEnum.UPPER_LIMIT_OF_PURCHASE);
                }
                //  判断是否限购
                Long limited = cartMapper.selectPurchasedGoodsNum(shoppingCart.getCustomerCode(), cart.getStageId());
                if (null != limited) {
                    if (payNum > stage.getBuyNum() - limited) {
                        throw new CustomerException(ExceptionEnum.UPPER_LIMIT_OF_PURCHASE);
                    }
                }
                //判断购物车已有的限购次数
                Integer buyNum = goodsStageMapper.selectByPrimaryKey(cart.getStageId()).getBuyNum();
                if (buyNum > 0) {
                    if (payNum > buyNum) {
                        throw new CustomerException(ExceptionEnum.UPPER_LIMIT_OF_PURCHASE);
                    }
                }
                cart.setPayNum(payNum);
                cartMapper.updateByPrimaryKeySelective(cart);
                isUpdate = true;
            }
        }
        if (!isUpdate) {
            //  判断是否限购
            Long limited = cartMapper.selectPurchasedGoodsNum(shoppingCart.getCustomerCode(), shoppingCart.getStageId());
            if (null != limited) {
                if (shoppingCart.getPayNum() > stage.getBuyNum() - limited) {
                    throw new CustomerException(ExceptionEnum.LIMIT_PURCHASE_LIMIT);
                }
            }
            //查询后台配置购物车限制数量
            UserConf confByKey = new UserConf();
            confByKey.setConfKey(ConfigkeyConstant.SHOPPING_CART_IS_FULL);
            confByKey = userConfMapper.selectOne(confByKey);
            Integer shoppingCartNum = (confByKey.getConfValue().equals("")) ? 15 : Integer.parseInt(confByKey.getConfValue());
            if (cartList.size() >= shoppingCartNum) { // 判断用户购物车是否超过限制
                throw new CustomerException(ExceptionEnum.SHOPPING_CART_IS_FULL);
            }
            cartMapper.insertSelective(shoppingCart);
            cartList.add(shoppingCart);
        }
        // 更新缓存
    }

    public void updateCartNum(Long userId,String goodsNo, Integer num) {
        if (userId == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        if (goodsNo == null || num == null || num <= 0) {
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        //获取登录用户的所有购物车
        ShoppingCart cartInfo = new ShoppingCart();
        cartInfo.setGoodsNo(goodsNo);
        cartInfo.setCustomerCode(userId);
        List<ShoppingCart> cartList=cartMapper.select(cartInfo);
        //判断是否存在
        if (cartList.size()==0) {
            //key
            throw new CustomerException(ExceptionEnum.GOODS_NOT_FOND);
        } else {
            cartInfo=cartList.get(0);
            GoodsStage goodsStage =goodsStageMapper.selectByPrimaryKey(cartInfo.getStageId());
            // 判断库存
            if (goodsStage.getGoodsInv() < num){
                // 库存不足
                cartInfo.setPayNum(goodsStage.getGoodsInv());// 库存不足时修改采购量为最大库存
                //throw new CustomerException(ExceptionEnum.STOCK_INSUFFICIENT_ERROR);
            }else{
                cartInfo.setPayNum(num);// 正常修改采购量
            }
            cartInfo.setPayNum(num);
            cartMapper.updateByPrimaryKey(cartInfo);
        }
    }

    @Override
    public void deleteCart(List<String> goodsId) {
        if (goodsId == null) {
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        UserInfo user = UserInterceptor.getUserInfo();
        if (user== null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        //获取登录用户的所有购物车
        for (String it : goodsId) {
            ShoppingCart cartInfo = new ShoppingCart();
            cartInfo.setId(Long.valueOf(it));
            cartInfo.setCustomerCode(user.getUserCode());
            cartInfo = cartMapper.selectOne(cartInfo);
            //判断是否存在
            if (cartInfo == null) {
                //key
                throw new CustomerException(ExceptionEnum.GOODS_NOT_FOND);
            } else {
                cartMapper.deleteByPrimaryKey(cartInfo.getId());
            }
        }
    }

    @Override
    public void clearCart() {
        //获取用户登录
        UserInfo user = UserInterceptor.getUserInfo();
        if (user== null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        ShoppingCart cartInfo = new ShoppingCart();
        cartInfo.setCustomerCode(user.getUserCode());
        List<ShoppingCart> cartList = cartMapper.select(cartInfo);
        if (cartList.size() > 0) {
            cartMapper.delete(cartInfo);
        } else {
            throw new CustomerException(ExceptionEnum.CART_NOT_FOUND);
        }
    }

    public PageResult<CartVO> queryCartList(Integer pageNo, Integer pageSize) {
        //获取用户登录
        UserInfo user = UserInterceptor.getUserInfo();
        if (user== null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        PageHelper.startPage(pageNo, pageSize, true);
        ShoppingCart cartInfo = new ShoppingCart();
        cartInfo.setCustomerCode(user.getUserCode());
        List<ShoppingCart> carts = cartMapper.select(cartInfo);
        List<CartVO> cartVOList = new ArrayList<>();
        for (ShoppingCart cart : carts) {
            //  获取商品信息
            GoodsStage goodsStage = goodsStageMapper.getGoodsStageInfoById(cart.getStageId().toString());

            //  校验库存
            if (goodsStage.getGoodsInv() <= 0){
                // 当前购物车商品最大期数
                cart.setStageId(goodsStage.getMaxStageIndex().longValue());
                int count=cartMapper.updateByPrimaryKey(cart);
                continue;
            }

            CartVO cartVO = new CartVO();
            cartVO.setId(cart.getId());
            cartVO.setBuyPrice(goodsStage.getBuyPrice());
            cartVO.setGoodsName(goodsStage.getGoodsName());
            cartVO.setGoodsNo(goodsStage.getGoodsNo().toString());
            cartVO.setGoodsPicture(goodsStage.getGoodsPicture().split(",")[0]);
            cartVO.setPayNum(cart.getPayNum());
            cartVO.setStageId(cart.getStageId());
            cartVO.setGoodsStageId(goodsStage.getStageIndex());
            Long payNum = cartMapper.selectPurchasedGoodsNum(user.getUserCode(), cart.getStageId());
            payNum = null == payNum ? 0 : payNum;
            //  计算限购数量
            Long limited = goodsStage.getBuyNum() - payNum;
            //  限购数量大于库存时，取库存数量
            if (limited > goodsStage.getGoodsInv()) {
                cartVO.setLimited(goodsStage.getGoodsInv());
            } else {
                cartVO.setLimited(Integer.parseInt(limited.toString()));
            }
            cartVOList.add(cartVO);
        }
        PageInfo<CartVO> pageInfo = new PageInfo<>(cartVOList);
        return new PageResult<>(pageInfo.getTotal(), cartVOList);
    }

    @Override
    public Integer countCartByUserCode(Long userCode) {
        if (userCode== null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        Integer cartNum=cartMapper.countCartByUserCode(userCode);
        return cartNum;
    }

    @Override
    public void addCartByBatch(Long userCode, Map<String, Object> data) {
        if (userCode== null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        Integer typeId=Integer.valueOf(data.get("typeId").toString());
        if (typeId==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }

        //获取期数ID
        List<Long> stageList=goodsStageMapper.getGoodsStageIDByGoodsTypeId(typeId);
        for (Long stageId:stageList){
            this.addCart(stageId,1);
        }
    }

    @Override
    public void addCartByGoodsNo(Long userCode,String goodsNo) {
        if (userCode== null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        if (goodsNo==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }

        Integer stageId=goodsStageMapper.selectMaxIndexByGoodsNo(goodsNo);
        this.addCart(stageId.longValue(),1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void test() {
        int count=0;
        List<Map<String,Object>> mapList=goodsStageMapper.getTest();
        for (Map<String,Object> map:mapList){
            Integer maxStageId=goodsStageMapper.selectMaxIndexByGoodsNo(map.get("goods_no").toString());
            //System.out.println("商品id="+map.get("goods_id")+"的最大期数是："+maxStageId);
            //count+=goodsStageMapper.updateGoodsRobotTest(maxStageId,map.get("goods_id").toString());
        }
        if (count!=mapList.size()){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
    }

    @Override
    public int deleteShopCartByList(List<ShoppingCart> list) {
        if (CollectionUtils.isEmpty(list)){
            return 0;
        }
        List<String> goodsNo=list.stream().map(ShoppingCart::getGoodsNo).collect(Collectors.toList());
        Example example=new Example(ShoppingCart.class);
        example.createCriteria().andEqualTo("customerCode",list.get(0).getCustomerCode());
        example.createCriteria().andIn("id",goodsNo);
        return cartMapper.deleteByExample(example);
    }
}
