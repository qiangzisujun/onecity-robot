package com.tangchao.shop.service;

import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.pojo.Goods;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public interface GoodsService {
    PageResult<Goods> goodsList(Integer pageNo, Integer pageSize,String  goodsName,Integer typeId);

    void addGoods(Goods goods);

    void deleteGoods(Long userId,String goodsNo);

    void updateGoods(Goods goods);

    PageResult<Goods> sellgoods(Integer pageNo, Integer pageSize, String goodsName, Integer typeId);

    void shelfGoods(Long userCode, Long id, Integer isSell);

    void exportGoodsInfo(List<String> ids, HttpServletResponse response) throws UnsupportedEncodingException;

    void updateGoodsInfoHot(Long userId, Map<String, Object> data);

    void isRecommend(Long userId, Map<String, Object> data);

    void updateGoodsNew(Long userId, Map<String, Object> data);

    void batchShelfGoods(Long userCode, Map<String, Object> data);

    void importGoodsInfo(MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException;
}
