package com.tangchao.shop.mapper;

import com.tangchao.shop.pojo.GoodsFavourite;
import com.tangchao.shop.vo.OrderNoteVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface GoodsFavouriteMapper extends Mapper<GoodsFavourite> {

    /**
     * 查询收藏商品唯一编码
     *
     * @param userCode 用户编码
     * @return
     */
    String selectGoodsNosByUserCode(@Param("userCode") Long userCode,@Param("goodsNo") String goodsNo);

    List<OrderNoteVO> buyLists(@Param("userCode") Long userCode, @Param("openWinningStatus") Integer openWinningStatus);

    Double findAmountSum(@Param("type") Integer type, @Param("userCode") Long userCode);

    Double findScoreGiveSum(Long userCode);

    Double findScoreReduceSum(Long userCode);

    int countGoodsFavourite(@Param("customerCode") Long customerCode);
}
