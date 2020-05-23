package com.tangchao.shop.service;

import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.pojo.Customer;
import com.tangchao.shop.pojo.CustomerRechargeRecord;
import com.tangchao.shop.pojo.GoodsFavourite;
import com.tangchao.shop.pojo.WinningOrder;
import com.tangchao.shop.vo.CapitalDetailsVO;
import com.tangchao.shop.vo.OrderNoteVO;

import java.util.List;


public interface GoodsFavouriteService {

    /**
     * 添加收藏商品
     *
     * @param goodsNo
     */
    void saveGoodsFavourite(Long userCode, Long goodsNo);

    /**
     * 商品详情取消收藏
     *
     * @param userCode
     * @param goodsNo
     */
    void deleteGoodsFavouriteByGoodsNo(Long userCode, Long goodsNo);

    PageResult<OrderNoteVO> buyList(Long userCode, Integer pageNo, Integer pageSize, Integer openWinningStatus);

    CapitalDetailsVO consumeRecord(Long userCode);

    PageResult<CustomerRechargeRecord> consumeRecordList(Long userCode, Integer pageNo, Integer pageSize, Integer count);

    PageResult<GoodsFavourite> goodsFavouriteByGoosNoList(Long userCode, Integer pageNo, Integer pageSize);

}
