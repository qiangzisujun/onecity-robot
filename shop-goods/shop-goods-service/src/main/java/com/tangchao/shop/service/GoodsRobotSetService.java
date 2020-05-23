package com.tangchao.shop.service;

import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.dto.adminDTO.GoodsRobotDTO;
import com.tangchao.shop.pojo.GoodsRobot;
import com.tangchao.shop.pojo.GoodsRobotSet;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public interface GoodsRobotSetService {

    /**
     * 根据商品编号查询设置
     * @param goodsNo 商品编号
     * @return GoodsRobotSet
     */
    GoodsRobotSet findByGoodsNo(Long goodsNo);

    PageResult<Map> selectGoodsRootSetList(String goodsName,String goodsNo,Integer typeId,Integer pageNo,Integer pageSize);

    void updateGoodsRootSet(Long userCode, GoodsRobotDTO goodsRobotDTO);

    void deleteGoodsRootSet(Long userCode, Long id);

    void insertGoodsRootSet(Long userCode, GoodsRobotDTO goodsRobotDTO);

    void importRobotSet(Long userId, MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException;

    void exportRobotSet(Long userCode, HttpServletResponse response) throws UnsupportedEncodingException;


    /**
     * 查询所有商品机器人购买任务
     * @return
     */
    List<GoodsRobot> findGoodsRobotsList(String type);
}
