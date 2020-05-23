package com.tangchao.shop.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.mapper.CustomerEvaluationMapper;
import com.tangchao.shop.mapper.CustomerEvaluationShowMapper;
import com.tangchao.shop.mapper.CustomerInfoMapper;
import com.tangchao.shop.mapper.ManagerMapper;
import com.tangchao.shop.pojo.*;
import com.tangchao.shop.service.GoodsEvaluationShowService;
import com.tangchao.shop.vo.OrderShowVO;
import com.tangchao.user.service.CustomerScoreDetailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service
public class GoodsEvaluationShowServiceImpl implements GoodsEvaluationShowService {

    @Autowired
    private CustomerEvaluationShowMapper customerEvaluationShowMapper;

    @Autowired
    private  ManagerMapper managerMapper;

    @Autowired
    private CustomerScoreDetailService customerScoreDetailService;

    @Autowired
    private CustomerInfoMapper customerInfoMapper;

    @Autowired
    private CustomerEvaluationMapper customerEvaluationMapper;

    @Override
    public PageResult<OrderShowVO> selectOrderShowList(Integer pageNo, Integer pageSize, Long userCode) {
        if (userCode == null) {
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }

        PageHelper.startPage(pageNo, pageSize, true);
        PageHelper.orderBy("createTime desc");
        List<OrderShowVO> list = customerEvaluationShowMapper.selectGoodsShowList(userCode);
        PageInfo<OrderShowVO> pageInfo = new PageInfo<>(list);
        return new PageResult<>(pageInfo.getTotal(), list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void getCustomerEvaluationShow(Long userId, Map<String, Object> data) {

        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        Map<String, Object> returnMap = new HashMap<String, Object>();
        String status=data.get("status").toString();
        String applyReason=data.get("applyReason").toString();
        List<String> ids=(List<String>) data.get("ids");

        Manager manager=managerMapper.selectByPrimaryKey(userId);
        String assessorName = manager.getUserName();//审核人姓名
        Long assessorId = manager.getId();
        Date assessCompletionDate = new Date();//审核时间

        Example example = new Example(CustomerEvaluationShow.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", ids);
        List<CustomerEvaluationShow> list = customerEvaluationShowMapper.selectByExample(example);
        for (CustomerEvaluationShow customerEvaluationShow : list) {
            if(customerEvaluationShow.getStatus()!=1){//待审核
               throw new CustomerException(ExceptionEnum.REVIEW_SAVE_ERROR);
            }
            customerEvaluationShow.setAssessorId(assessorId);
            customerEvaluationShow.setAssessCompletionDate(assessCompletionDate);
            customerEvaluationShow.setAssessorName(assessorName);
            customerEvaluationShow.setAssessCompletionDate(assessCompletionDate);
            customerEvaluationShow.setStatus(Integer.valueOf(status));
            if(status.equals("2")){//审核不通过原因
                customerEvaluationShow.setApplyReason(applyReason);
            }
        }

        for(CustomerEvaluationShow customerEvaluationShow:list){
            int row = customerEvaluationShowMapper.updateByPrimaryKeySelective(customerEvaluationShow);
            if (row==0) {
                throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
            }
            if(customerEvaluationShow.getStatus()==3){//3:审核通过
                Customer customer = new Customer();
                customer.setUserCode(customerEvaluationShow.getUserCode());
                Double aDouble = customerScoreDetailService.evaluationShowScore(customer, null);
                if (aDouble==null){
                    throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
                }


                CustomerInfo info=new CustomerInfo();
                info.setCustomerCode(customerEvaluationShow.getUserCode());
                info=customerInfoMapper.selectOne(info);
                if (info==null){
                    throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
                }

                Example example1 = new Example(CustomerInfo.class);
                Example.Criteria criteria1 = example1.createCriteria();
                criteria1.andEqualTo("customerCode",customerEvaluationShow.getUserCode());
                CustomerInfo customerInfo = new CustomerInfo();
                customerInfo.setUserScore(info.getUserScore()+aDouble);
                customerInfoMapper.updateByExampleSelective(customerInfo, example);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void commentBatchSee(Long userId, Map<String, Object> data) {
        if (userId == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        List<String> ids=(List<String>) data.get("data");

        if (ids.isEmpty()){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }

        CustomerEvaluation eval = new CustomerEvaluation();
        eval.setIsSee(1);
        for (String id:ids){
            eval.setId(Long.valueOf(id));
            int count=customerEvaluationMapper.updateByPrimaryKeySelective(eval);
            if (count!=1){
                throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
            }
        }
    }

    @Override
    public void updateEvalDoBatchDeleteByIds(Long userId, Map<String, Object> data) {
        if (userId == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        List<String> ids=(List<String>) data.get("data");

        if (ids.isEmpty()){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }

        CustomerEvaluation eval = new CustomerEvaluation();
        eval.setIsDelete(1);
        for (String id:ids){
            eval.setId(Long.valueOf(id));
            int count=customerEvaluationMapper.updateByPrimaryKeySelective(eval);
            if (count!=1){
                throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
            }
        }
    }
}
