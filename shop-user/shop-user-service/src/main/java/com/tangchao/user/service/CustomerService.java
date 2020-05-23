package com.tangchao.user.service;

import com.github.pagehelper.PageInfo;
import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.dto.CustomerDto;
import com.tangchao.shop.dto.UserDTO;
import com.tangchao.shop.dto.adminDTO.CustomerDTO;
import com.tangchao.shop.pojo.*;
import com.tangchao.shop.vo.CustomerAddressVO;
import com.tangchao.shop.vo.CustomerVO;
import com.tangchao.shop.vo.UserVO;
import com.tangchao.shop.vo.adminVo.CustomerAdminVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;


public interface CustomerService {

    /**
     * 用户名密码登录
     *
     * @param phone
     * @param Password
     * @return
     */
    UserDTO queryUserByUserNameAndPassword(String phone, String Password);

    String sendCode(String phone);

    UserDTO register(UserVO user, HttpServletRequest request);

    Integer checkUserPhone(String phone);

    /**
     * 获取用户地址
     *
     * @param userCode
     * @return
     */
    List<CustomerAddress> getCustomerAddressList(Long userCode);

    void addCustomerAddress(Long userCode, CustomerAddressVO address);

    void deleteCustomerAddressById(Long userCode, Long addressId);

    int updateCustomerScore(Long userCode, double score);

    CustomerEmployTiXianRecord findEmploySumByState(Long userCode);

    String getUserInviteCode(Long userCode);

    PageResult<CustomerVO> findCustomerFriendList(Integer pageNum, Integer pageSize, Long userCode);

    CustomerVO getCustomerInfo(Long userCode);

    void updateAccountNumber(Long userCode,String portrait ,String realname,Integer isCollageRecord,Integer isObtainGoods);

    void updatePass(Long userCode, String pwd, String oldPass);

    PageResult<OrderDistribution> CommissionList(Long userCode, Integer pageNo, Integer pageSize);

    void forgetPass(String pwd, String code, String phone);

    UserDTO phoneLogin(String phone, String code);

 	CustomerVO getCustomerInfoShop(Long userCode);

    void getPaymentCode(String code, String state, HttpServletRequest request,HttpServletResponse response) throws IOException;

    String paymentCodePay(Long userCode,Map<String,Object> data);

    String paymentCodeImg(Long userCode);

    PageInfo<com.tangchao.shop.vo.adminVo.CustomerVO> selectCustomerList(Long userId, CustomerDTO customerDTO);

    Map<String,Object> getUserStatistics(Long userId);

    Map<String,Object> getCustomerScoreDetail(Long userId, Long userCode, Integer pageNo, Integer pageSize);

    List<CustomerAddress> getCustomerAddress(Long userId,Long userCode);

    void resetPassword(Long userId, Long userCode);

    void insertBlackList(Long userId, Long userCode,Integer status);

    void deleteUser(Long userId,List<String> ids);

    void updateCustomer(Long userId, CustomerAdminVO customerVO);

    void bannedCustomer(Long userId, Long userCode,Integer status);

    void importRobot(Long userId, MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException;

    int createCustomer(Customer customer,Long userId,HttpServletRequest request);

    void addCustomer(Long userId, HttpServletRequest request, CustomerAdminVO customerAdminVO);

    Customer selectCustomerInfoByPhone(String phone,Integer type);

    void customerRechargeByPhone(Long userId,Map<String, Object> data,HttpServletRequest request);

    Map<String,Object> customerExpensesRecord(Long userId, CustomerDTO customerDTO);

    void proxyRechargeByPhone(Long userId, Map<String, Object> data);

    Map<String,Object> proxyExpensesRecord(Long userId, CustomerDTO customerDTO);

    Map<String,Object> sumCustomerRechargeRecordTotal(Long userId, CustomerDTO customerDTO);

    Map<String,Object> sumProxyExpensesRecord(Long userId, CustomerDTO customerDTO);

    void insertCustomerGuide(Long userId, Guide data);

    PageInfo getCustomerGuideList(Long userId);

    void deleteCustomerGuide(Long userId,Map<String, Object> data);

    void updateCustomerGuide(Long userId, Guide data);

    void deleteCustomerAddress(Long userCode, Map<String, Object> data);

    String getWXCode(HttpServletRequest request) throws UnsupportedEncodingException;

    List<String> getCollectionCodeList(Long userId);

    void setupAddress(Long userId, Map<String, Object> data);

    void sellTransfer(Long userId, Map<String, Object> data);

    void proxyRecharge(Long userId, Map<String, Object> data);

    int updateCustomerInfo(CustomerInfo customerInfo);

    Map<String,Object> selectProxyRechargeList(CustomerDto customerDto);

    Map<String,Object> selectProxyOrderList(CustomerDto customerDto);

    String shareQRCode(Long userCode,HttpServletRequest request) throws Exception;

    Map<String,Object> userRecharge(Long userCode,Double price, Integer isType);

    String userPaymentNotifyPay(GLpayApi payAPI);

    void testpay();

    Map<String,Object> userPaymentByCoCo(Long userCode, Double price, Integer isType);

    String userPaymentNotifyPay_CoCo(NotifyApi notifyApi);

    Map<String,Object> userPaymentByCPNP(Long userCode,Double price);

    String userPaymentNotifyPay_CPNP(HttpServletRequest request) throws Exception;

    String userPayQRCode(String urlText) throws Exception;

    void paySuccessView(Double money,HttpServletResponse response) throws IOException;

    void mobileRecharge(Long userCode,Integer money) throws UnsupportedEncodingException, NoSuchAlgorithmException;

    String mobileRechargeNotify(HttpServletRequest request) throws UnsupportedEncodingException, NoSuchAlgorithmException;

    void setCustomerRobot(Map<String, Object> data);

    Map<String, Object> getCommissionListByUserCode(Integer page,Integer pageSize,Long userCode);

    Map<String,Object> addCustomerSignIn() throws ParseException;

    Map<String,Object> getCustomerSignIn() throws ParseException;

}
