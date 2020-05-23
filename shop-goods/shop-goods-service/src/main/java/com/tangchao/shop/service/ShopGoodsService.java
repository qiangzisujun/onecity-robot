package com.tangchao.shop.service;

import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.dto.GoodsStageDTO;
import com.tangchao.shop.params.ShopGoodsParam;
import com.tangchao.shop.pojo.ShopGoods;
import com.tangchao.shop.vo.GoodsStageVO;
import com.tangchao.shop.vo.GoodsTypeVO;
import com.tangchao.shop.vo.ShopGoodsVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface ShopGoodsService {

    List<ShopGoodsVO> getShopGoodsByPage(Integer page, Integer rows, Long cid1);

    ShopGoodsVO getShopGoodsByGid(Long goodsId);

    /**
     * 商品列表
     *
     * @param stageDTO
     * @return
     */
    PageResult<GoodsStageVO> selectGoodsList(GoodsStageDTO stageDTO,Long userCode);

    /**
     * 商品集合
     *
     * @return
     */
    List<GoodsTypeVO> queryGoodsTypeList();

    ResponseEntity add(Long userId, ShopGoodsParam shopGoodsParam);

    PageResult<ShopGoods> list(Long userId, Integer pageNo, Integer pageSize, String title,String typeId,Integer type);

    ResponseEntity standUpDown(Long userId, Long id, Boolean operate);

    ResponseEntity getBy(Long userId, Long id);

    ResponseEntity deleteBy(Long userId, Long id);

    ResponseEntity updateBy(Long userId, ShopGoodsParam shopGoodsParam);

    PageResult<ShopGoods> getList(Integer pageNo, Integer pageSize, String title);

    ResponseEntity getInfo(Long id);

    PageResult<ShopGoods> getSpecialGoodsList(Integer pageNo, Integer pageSize, Integer type);

 	ResponseEntity isHome(Long userId, Long id, Integer operate);

	int getGoodsLimitNumByGoodsNo(Long userCode, String goodsNo);

    ResponseEntity batchImport(MultipartFile[] files, HttpServletRequest request);

    void batchExport(HttpServletResponse response);

    void batchSetShop(Map<String, Object> data);
}
