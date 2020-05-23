package com.tangchao.shop.mapper;

import com.tangchao.shop.dto.CustomerDto;
import com.tangchao.shop.dto.adminDTO.CustomerDTO;
import com.tangchao.shop.pojo.Customer;
import com.tangchao.shop.vo.adminVo.CustomerVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface CustomerMapper extends Mapper<Customer> {

    @Update("UPDATE customer_info set employ_money = employ_money - #{employMoney} ,last_modify_id = #{lastModifyId} where customer_code = #{customerCode}")
    int employMinus(@Param("customerCode") Long customerCode, @Param("employMoney") Double employMoney, @Param("lastModifyId") Long lastModifyId);

    int updateAccountNumber(@Param("userCode") Long userCode,@Param("portrait") String portrait , @Param("realname") String realname,@Param("isCollageRecord") Integer isCollageRecord,@Param("isObtainGoods") Integer isObtainGoods);

    int updatePass(@Param("userCode")Long userCode,@Param("pwd1") String pwd1);

    List<Customer> selectBlacklist();

    List<CustomerVO> findCustomerInfo(@Param("inviteCode") String inviteCode);

    /**
     * 查询网站会员总数
     * @return
     */
    Integer findCustomerSum();

    /**
     * 查询普通会员总数
     * @return
     */
    Integer findMemberCustomerSum();

    /**
     * 查询机器人总数
     *
     * @return
     */
    Integer findRobotSum();

    /**
     * 查询充值会员总数
     * @return
     */
    Integer findSupplierSum();

    /**
     * 查询会员列表
     * @param customerDTO
     * @return
     */
    List<CustomerVO> selectCustomerList(CustomerDTO customerDTO);

    List<Customer> getCustomerListByCode(@Param("userCodeList") List<String> userCodeList);

    /**
     * 查询代理充值记录
     * @param customerDto
     * @return List<Map<String, Object>>
     */
    List<Map<String, Object>> selectProxyRechargeList(CustomerDto customerDto);


    Double countProxyRechargeList(CustomerDto customerDto);

    int getInviteNum(@Param("inviteCode") String inviteCode);

    int updateByIds(@Param("ids") List<String> ids, @Param("type") Integer type);
}
