package com.tangchao.shop.mapper;

import com.tangchao.shop.dto.adminDTO.CustomerDTO;
import com.tangchao.shop.pojo.CustomerEvaluationShow;
import com.tangchao.shop.vo.OrderShowVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface CustomerEvaluationShowMapper extends Mapper<CustomerEvaluationShow> {

    List<OrderShowVO> selectGoodsShowList(@Param("userCode") Long userCode);

    List<CustomerEvaluationShow> getCustomerEvaluationShowList(CustomerDTO customerDTO);

    List<Map<String,Object>> findByEvaluationShowImgByShowId(@Param("list") List<Long> showId);

    int countEvaluationShowImgByShow(CustomerDTO customerDTO);

    List<Map<String, Object>> selectList(CustomerDTO customerDTO);

}
