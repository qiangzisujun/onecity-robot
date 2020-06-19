package com.tangchao.shop.mapper;


import com.tangchao.shop.pojo.CustomerInfo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.BaseMapper;
import tk.mybatis.mapper.common.Mapper;

public interface CustomerInfoMapper extends Mapper<CustomerInfo> {

    @Update("update customer_info set user_score=user_score+#{score}  where customer_code=#{customerCode}")
    int updateCustomerScore(@Param("customerCode") Long customerCode, @Param("score") double score);

    @Update("UPDATE customer_info SET user_money = user_money - #{money}, user_score = #{score} WHERE customer_code = #{customerCode} and user_money>=#{money}")
    int customerConsume(@Param("money") Double money, @Param("score") Double score, @Param("customerCode") Long customerCode);


    @Update("UPDATE customer_info set user_money = user_money + #{userMoney},user_score = user_score + #{userScore},last_modify_id = #{lastModifyId}  WHERE customer_code = #{customerCode}")
    int addAmount(@Param("customerCode")Long customerCode,@Param("userMoney")Double userMoney,
                  @Param("userScore") Double userScore,@Param("lastModifyId")Long lastModifyId);


    /**
     * 后台 用户扣减余额和积分
     * @param customerCode
     * @param userMoney
     * @param userScore
     * @param lastModifyId
     * @return
     */
    @Update("UPDATE customer_info set user_money = user_money - #{userMoney},user_score = user_score - #{userScore},last_modify_id = #{lastModifyId} WHERE customer_code = #{customerCode}")
    int customerMinus(@Param("customerCode")Long customerCode,@Param("userMoney")Double userMoney,
                      @Param("userScore")Double userScore,@Param("lastModifyId")Long lastModifyId);

    /**
     * 会员佣金充值
     * @param customerCode
     * @param employMoney
     * @param lastModifyId
     * @return
     */
    @Update("UPDATE customer_info set employ_money = employ_money + #{employMoney},last_modify_id = #{lastModifyId} WHERE customer_code = #{customerCode}")
    int employRecharge(@Param("customerCode") Long customerCode,@Param("employMoney") Double employMoney,@Param("lastModifyId") Long lastModifyId);


    /**
     * 会员佣金扣减
     * @param customerCode
     * @param employMoney
     * @param lastModifyId
     * @return
     */
    @Update("UPDATE customer_info set employ_money = employ_money - #{employMoney},last_modify_id = #{lastModifyId}  WHERE customer_code = #{customerCode}")
    int employMinus(@Param("customerCode")Long customerCode,@Param("employMoney")Double employMoney,@Param("lastModifyId")Long lastModifyId);

    @Delete("delete from customer_info where customer_code=#{customerCode}")
    int deleteCustomerInfo(@Param("customerCode") String customerCode);
}
