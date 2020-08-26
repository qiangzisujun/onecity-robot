package com.tangchao.user.serviceImpl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tangchao.common.constant.ConfigkeyConstant;
import com.tangchao.common.constant.DataSrcConstant;
import com.tangchao.common.constant.OrderConstant;
import com.tangchao.common.constant.PayStatusConstant;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.common.utils.*;
import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.advice.DataSourceNames;
import com.tangchao.shop.annotation.DataSource;
import com.tangchao.shop.config.JwtProperties;
import com.tangchao.shop.dto.CustomerDto;
import com.tangchao.shop.dto.adminDTO.CustomerDTO;
import com.tangchao.shop.interceptor.UserInterceptor;
import com.tangchao.shop.mapper.*;
import com.tangchao.shop.pojo.*;
import com.tangchao.shop.utils.*;
import com.tangchao.shop.dto.UserDTO;
import com.tangchao.shop.vo.CustomerAddressVO;
import com.tangchao.shop.vo.CustomerVO;
import com.tangchao.shop.vo.UserVO;
import com.tangchao.shop.vo.adminVo.CustomerAdminVO;
import com.tangchao.shop.vo.adminVo.CustomerScoreDetailVO;
import com.tangchao.user.service.*;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;
import tk.mybatis.mapper.entity.Example;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {


    private static final String KEY_PREFIX = "user:verify:phone";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private JwtProperties pro;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private ShopUserDetailMapper userDetailMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserConfMapper userConfMapper;

    @Autowired
    private CustomerAddressMapper addressMapper;

    @Autowired
    private CustomerInfoMapper customerInfoMapper;

    @Autowired
    private CustomerEmployTiXianRecordMapper tiXianRecordMapper;

    @Autowired
    private CustomerRechargeRecordMapper customerRechargeRecordMapper;

    @Autowired
    private OrderDistributionMapper orderDistributionMapper;

    @Autowired
    private CustomerScoreDetailService customerScoreDetailService;

    @Autowired
    private PaymentCodeManageService paymentCodeManageService;

    @Autowired
    private PayCustomerMapper payCustomerMapper;

    @Autowired
    private WinningOrderMapper winningOrderMapper;

    @Autowired
    private CmsConfigService configService;


    @Autowired
    private PaymentCodeMapper paymentCodeMapper;

    @Autowired
    private GuideMapper guideMapper;

    @Autowired
    private FastOrderMapper fastOrderMapper;

    @Autowired
    private CustomerScoreDetailMapper customerScoreDetailMapper;

    @Autowired
    private TradeOrderMapper tradeOrderMapper;

    @Autowired
    private PaymentOrderPlatformMapper paymentOrderPlatformMapper;

    @Autowired
    private ShopOrderMapper shopOrderMapper;

    @Autowired
    private PhoneRechargeRecordMapper phoneRechargeRecordMapper;

    @Autowired
    private CustomerSignRecordMapper signRecordMapper;

    @Autowired
    private UserSignRecordMapper userSignRecordMapper;

    @Override
    @DataSource(name = DataSourceNames.SECOND)
    public UserDTO queryUserByUserNameAndPassword(String phone, String password) {
        if (StringUtils.isBlank(phone) || StringUtils.isBlank(password)) {
            throw new CustomerException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        Customer record = new Customer();
        record.setUserMobile(phone);
        record.setFlag(0);
        //record.setIsRobot(0);
        List<Customer> userList = customerMapper.select(record);
        if (userList.isEmpty()) {//用户不存在
            throw new CustomerException(ExceptionEnum.INVALID_USER_NOT_FOND);
        }
        Customer newUser = userList.get(0);
        //校验密码
        if (!StringUtils.equals(newUser.getLoginPwd(), MD5Util.pwdEncoding(password))) {
            throw new CustomerException(ExceptionEnum.PASSWORD_NOT_ERROR);
        }

        //账号封禁处理
        if ("-1".equals(newUser.getAccountStatu())) {
            throw new CustomerException(ExceptionEnum.INVALID_USER_BANNED);
        }


        //用户名密码正确 生成对应的token
        String token = JwtUtils.generateToken(new UserInfo(newUser.getUserCode(), newUser.getUserName()), pro.getPrivateKey());
        //返回用户信息
        UserDTO user = new UserDTO();
        user.setToken(token);
        int count=tradeOrderMapper.updateUserOrderStatusByOverTime(newUser.getUserCode(), OrderConstant.UNPAID, OrderConstant.TIME_OUT);
        if (newUser.getIsSupplier()==1&&newUser.getIsRobot()==0){
            user.setUserIdentity("1");//充值会员
        }else {
            user.setUserIdentity("0");
        }
        return user;
    }


    @Override
    public String sendCode(String phone) {
        //生成key
        String key = KEY_PREFIX + phone;

        //生成验证码
        String code = NumberUtils.generateCode(6);
        Map<String, String> msg = new HashMap<>();
        msg.put("phone", phone);
        msg.put("code", code);
        //发送验证码 暂时没有


        //保存验证码 5分钟有效
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);

        return code;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO register(UserVO user, HttpServletRequest request) {
        if (StringUtils.isBlank(user.getPhone()) || StringUtils.isBlank(user.getPassword())) {
            throw new CustomerException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }

        //密码长度6-16位
        if (user.getPassword().length() > 16 || user.getPassword().length() < 6) {
            throw new CustomerException(ExceptionEnum.INVALID__PASSWORD_LENGTH);
        }

        //从redis取出验证码
        String cacheCode = redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        //校验验证码
        if (!StringUtils.equals(user.getCode(), cacheCode)) {
            throw new CustomerException(ExceptionEnum.INVALID_VERIFY_CODE);
        }

        //判断手机号是否已经正常
        Customer record = new Customer();
        record.setUserMobile(user.getPhone());
        List<Customer> userList = customerMapper.select(record);
        if (!userList.isEmpty()) {//用户不存在
            throw new CustomerException(ExceptionEnum.USER_PHONE_ALREADY_EXIST);
        }

        //写入数据库
        String ip = IPAddressUtil.getClientIpAddress(request);
        Customer customer = new Customer();
        String mobile = user.getPhone();
        customer.setLoginPwd(user.getPassword()); // 登陆密码
        customer.setUserName(new StringBuilder(mobile).replace(3, 7, "****").toString()); // 设置会员名（手机号，用户可修改）
        customer.setUserMobile(mobile); // 设置会员手机
        customer.setIsRobot(0); // 不是机器人
        customer.setIsSupplier(0); // 不是充值会员
        customer.setRegisterIp(ip); // 注册Ip
        customer.setBlackStatu("0");
        customer.setInviteId(user.getInviteId()); // 邀请人编号
        customer.setAccountStatu(0+"");
        if (null!=user.getInviteId()){
            logger.info("分享人邀请码="+user.getInviteId());
            customer.setInviteId(user.getInviteId());
        }
        UserConf conf = new UserConf();
        conf.setConfKey("mall.user.default.portrait");
        UserConf newConf = userConfMapper.selectOne(conf);
        if (newConf != null) {// 查询默认头像
            customer.setUserPortrait(newConf.getConfValue());
        }
        // 生成唯一编码
        String userCode = RandomUtil.generateLongByDateTime(3);
        customer.setUserCode(Long.parseLong(userCode));
        // 密码加密
        customer.setLoginPwd(MD5Util.pwdEncoding(user.getPassword()));
        // 设置创建时间,修改时间
        Date now = new Date();
        customer.setCreateTime(now);
        customer.setLastModifyTime(now);
        // 设置为正常状态
        customer.setFlag(0);
        int count=customerMapper.insert(customer);
        redisTemplate.delete(KEY_PREFIX + user.getPhone());//删除手机验证码
        if(count!=1){
            throw new CustomerException(ExceptionEnum.REGISTERED_NOT_ERROR);
        }

        //添加用户详情
        ShopUserDetail newUser = new ShopUserDetail();
        newUser.setUserCode(customer.getUserCode());
        newUser.setAvatarUrl(newConf.getConfValue());
        newUser.setIntegral(0L);
        newUser.setNickName(customer.getUserName());
        count=userDetailMapper.insertSelective(newUser);
        if(count!=1){
            throw new CustomerException(ExceptionEnum.REGISTERED_NOT_ERROR);
        }

        CustomerScoreDetail detail = new CustomerScoreDetail();
        detail.setCustomerCode(customer.getUserCode());
        detail.setDataSrc(DataSrcConstant.REGISTER);
        detail.setScoreFlag(PayStatusConstant.INCOME);


        //用户信息
        CustomerInfo info = new CustomerInfo();
        info.setCustomerCode(customer.getUserCode());           //  用户唯一标识
        info.setRegisterIp(customer.getRegisterIp());           //  用户注册ip
        info.setCreateId(customer.getCreateId());               //  创建人Id
        info.setCreateTime(customer.getCreateTime());           //  创建时间
        info.setLastModifyId(customer.getLastModifyId());       //  修改人Id
        info.setLastModifyTime(customer.getLastModifyTime());   //  修改时间
        info.setUserMoney(0.0);                                 //  初始化余额
        info.setUserFlow(0.0);                                  //  会员流量
        //  生成邀请码
        String inviteCode = RandomUtil.generateUniqueCode(6,customer.getId());
        info.setInviteCode(inviteCode);
        //  注册送福分
        double score = customerScoreDetailService.interDetail(ConfigkeyConstant.MALL_USER_REGISTER_GIVE_SCORE, detail);
        double money = customerScoreDetailService.interMoneyDetail(ConfigkeyConstant.MALL_USER_REGISTER_GIVE_Money, detail);
        customer.setUserScore(score);
        customer.setRegisterScore(score);
        customer.setRegisterMoney(money);
        count=customerInfoMapper.insertSelective(info);
        if(count!=1){
            throw new CustomerException(ExceptionEnum.REGISTERED_NOT_ERROR);
        }

        //用户名密码正确 生成对应的token

        String token = JwtUtils.generateToken(new UserInfo(newUser.getUserCode(), customer.getUserName()), pro.getPrivateKey());
        //返回用户信息
        UserDTO registerUser = new UserDTO();
        registerUser.setToken(token);
        registerUser.setUserIdentity("0");
        return registerUser;
    }

    @Override
    public Integer checkUserPhone(String phone) {

        //手机虚拟段号
        List<String> segmentNumber1=new ArrayList<>(Arrays.asList("167","162","165"));
        List<String> segmentNumber2=new ArrayList<>(Arrays.asList("1703","1705","1706","1704","1707","1708","1709","1700","1701","1702"));

        if (segmentNumber2.contains(phone.substring(0,4))||segmentNumber1.contains(phone.substring(0,3))){
            throw new CustomerException(ExceptionEnum.PHONE_VIRTUAL_ACCOUNT);
        }

        Customer record = new Customer();
        record.setFlag(0);
        record.setUserMobile(phone);
        List<Customer> newUser = customerMapper.select(record);
        if (newUser.size() > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public List<CustomerAddress> getCustomerAddressList(Long userCode) {
        if (userCode == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        CustomerAddress address = new CustomerAddress();
        address.setCustomerCode(userCode);
        address.setFlag(0);
        List<CustomerAddress> list = addressMapper.select(address);
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCustomerAddress(Long userCode, CustomerAddressVO addressVO) {
        if (userCode == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        Customer record = new Customer();
        record.setUserCode(userCode);
        record = customerMapper.selectOne(record);
        CustomerAddress address = new CustomerAddress();
        address.setCustomerCode(userCode);            //  会员编码
        address.setFlag(0);                                         //  状态：正常
        address.setCreateId(record.getId());                      //  创建人Id
        address.setLastModifyId(record.getId());                  //  修改人Id
        Date now = new Date();
        address.setCreateTime(now);                                 //  创建时间
        address.setLastModifyTime(now);
        address.setProvince(addressVO.getProvince());
        address.setCity(addressVO.getCity());
        address.setArea(addressVO.getArea());
        //address.setStreet(addressVO.getStreet());
        address.setDetailed(addressVO.getDetailed());
        address.setZipCode(addressVO.getZipCode());
        address.setUserName(addressVO.getUserName());
        address.setUserMobile(addressVO.getUserMobile().trim());
        address.setIsDefault(addressVO.getIsDefault());
        address.setProvinceCode(addressVO.getProvinceCode());
        address.setCityCode(addressVO.getCityCode());
        address.setAreaCode(addressVO.getAreaCode());

        if (addressVO.getId() > 0) {//修改地址
            if (null != address.getIsDefault() && address.getIsDefault() == 1) {
                //  取消其他的默认地址
                int num = addressMapper.cancelDefaultAddress(record.getUserCode());
            }

            CustomerAddress params = new CustomerAddress();
            params.setId(addressVO.getId().longValue());
            params.setCustomerCode(userCode);
            CustomerAddress addressInfo = addressMapper.selectOne(params);
            if (addressInfo != null) {
                address.setId(addressInfo.getId());
                int count=addressMapper.updateByPrimaryKeySelective(address);
                if (count != 1) {
                    throw new CustomerException(ExceptionEnum.ADDRESS_SAVE_ERROR);
                }
            }
        } else {
            //  判断是否默认
            if (null != address.getIsDefault() && address.getIsDefault() == 1) {
                //  取消其他的默认地址
                int num = addressMapper.cancelDefaultAddress(record.getUserCode());
                /*if (num >0) {
                    throw new CustomerException(ExceptionEnum.ADDRESS_SAVE_ERROR);
                }*/
            } else {
                address.setIsDefault(0);                                //  非默认
            }
            int num = addressMapper.insertSelective(address);
            if (num != 1) {
                throw new CustomerException(ExceptionEnum.ADDRESS_SAVE_ERROR);
            }
        }
    }

    @Override
    public void deleteCustomerAddressById(Long userCode, Long addressId) {
        if (StringUtils.isBlank(userCode.toString())) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        if (StringUtils.isBlank(addressId.toString())) {
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        CustomerAddress params = new CustomerAddress();
        params.setId(addressId);
        params.setCustomerCode(userCode);
        CustomerAddress addressInfo = addressMapper.selectOne(params);
        if (addressInfo != null) {
            addressInfo.setFlag(-1);
            addressMapper.updateByPrimaryKeySelective(addressInfo);
        } else {
            throw new CustomerException(ExceptionEnum.ADDRESS_NOT_FOND);
        }
    }

    @Override
    @Transactional
    @DataSource(name = DataSourceNames.SECOND)
    public int updateCustomerScore(Long userCode, double score) {
        int count = customerInfoMapper.updateCustomerScore(userCode, score);
        if (count != 1) {
            throw new CustomerException(ExceptionEnum.UPDATE_CUSTOMER_ERROR);
        }
        return 1;
    }

    @Override
    @DataSource(name = DataSourceNames.SECOND)
    public CustomerEmployTiXianRecord findEmploySumByState(Long userCode) {
        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setCustomerCode(userCode);
        List<CustomerInfo> customerList = customerInfoMapper.select(customerInfo);
        customerInfo = customerList.get(0);

        Double arEmployMoneySum = tiXianRecordMapper.findEmploySumByState(userCode, 3);//3为已提现
        Double checkEmployMoneySum = tiXianRecordMapper.findEmploySumByState(userCode, 0);//0为审核中
        if (arEmployMoneySum == null) {
            arEmployMoneySum = 0.00;
        }
        if (checkEmployMoneySum == null) {
            checkEmployMoneySum = 0.00;
        }
        if (customerInfo.getEmployMoney() == null) {
            customerInfo.setEmployMoney(0.00);
        }
        CustomerEmployTiXianRecord customerEmployTiXianRecord = new CustomerEmployTiXianRecord();
        if (customerInfo != null) {
            Customer record = new Customer();
            record.setInviteId(customerInfo.getInviteCode());
            Integer friendSum = customerMapper.selectCount(record);
            customerEmployTiXianRecord.setFriendSum(friendSum);
        }
        //double employMoneySum = ArithUtil.add(customerInfo.getEmployMoney(), checkEmployMoneySum);
        double d = customerInfo.getEmployMoney();
        BigDecimal b = new BigDecimal(String.valueOf(d));
        d = b.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        customerEmployTiXianRecord.setEmployMoneySum(d);
        customerEmployTiXianRecord.setArEmployMoneySum(arEmployMoneySum);
        customerEmployTiXianRecord.setCheckEmployMoneySum(checkEmployMoneySum);
        customerEmployTiXianRecord.setRemainder(customerInfo.getEmployMoney());
        return customerEmployTiXianRecord;
    }

    @Override
    @DataSource(name = DataSourceNames.SECOND)
    public String getUserInviteCode(Long userCode) {
        if (userCode == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setCustomerCode(userCode);
        List<CustomerInfo> customerList = customerInfoMapper.select(customerInfo);
        customerInfo = customerList.get(0);
        return customerInfo.getInviteCode();
    }

    @Override
    @DataSource(name = DataSourceNames.SECOND)
    public PageResult<CustomerVO> findCustomerFriendList(Integer pageNum, Integer pageSize, Long userCode) {
        if (userCode == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setCustomerCode(userCode);
        List<CustomerInfo> customers = customerInfoMapper.select(customerInfo);
        customerInfo = customers.get(0);

        Example example1 = new Example(Customer.class);
        Example.Criteria criteria1 = example1.createCriteria();
        //criteria.andEqualTo("inviteId", pojo.getInviteCode());
        criteria1.andEqualTo("inviteId", customerInfo.getInviteCode());
        criteria1.andNotEqualTo("flag", -1);
        List<Customer> list1 = customerMapper.selectByExample(example1);
        // 设置分页
        PageHelper.startPage(pageNum, pageSize, true);
        Example example = new Example(Customer.class);
        Example.Criteria criteria = example.createCriteria();
        //criteria.andEqualTo("inviteId", pojo.getInviteCode());
        criteria.andEqualTo("inviteId", customerInfo.getInviteCode());
        criteria.andNotEqualTo("flag", -1);
        List<Customer> list = customerMapper.selectByExample(example);
        List<CustomerVO> customerList = new ArrayList<>();
        for (Customer customer : list) {
            // type:充值消费标识{1：充值，2：消费,3:佣金提现}
            CustomerVO customerVO = new CustomerVO();
            // Double findAmountSum = customerRechargeRecordMapper.findAmountSum(customer.getUserCode(), 2);
            // TODO: 2020/1/18: 修改为是否够没过钻石商品
            Example exampleShopOrder = new Example(ShopOrder.class);
            Example.Criteria criteriaShopOrder = exampleShopOrder.createCriteria();
            criteriaShopOrder.andEqualTo("userCode", customer.getUserCode());
            criteriaShopOrder.andEqualTo("status", 2);
            Integer findAmountSum = shopOrderMapper.selectCountByExample(exampleShopOrder);
            if (findAmountSum != null && findAmountSum > 0) {
                customerVO.setIsShopping("是");// 是否购买 0：否 1：是
            } else {
                customerVO.setIsShopping("否");//是否购买 0：否 1：是
            }
            String result = customer.getUserMobile();
            String uUserMobile = result.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
            customerVO.setUserMobile(uUserMobile);
            customerVO.setId(customer.getId());
            customerVO.setUserCode(customer.getUserCode());
            customerVO.setCreateTime(customer.getCreateTime());
            customerList.add(customerVO);
        }
        PageInfo<CustomerVO> pageInfo = new PageInfo<CustomerVO>(customerList);
        return new PageResult<>(pageInfo.getTotal(), customerList, pageSize, list1.size());
    }


    @Override
    public CustomerVO getCustomerInfo(Long userCode) {
        if (userCode==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }

        Customer record = new Customer();
        record.setUserCode(userCode);
        record = customerMapper.selectOne(record);
        if (record == null) {
            throw new CustomerException(ExceptionEnum.INVALID_USER_NOT_FOND);
        }
        CustomerVO customerVO = new CustomerVO();
        customerVO.setUserCode(record.getUserCode());
        customerVO.setUserName(record.getUserName());
        customerVO.setUserPortrait(record.getUserPortrait());
        customerVO.setIsCollageRecord(record.getIsCollageRecord());
        customerVO.setIsObtainGoods(record.getIsObtainGoods());
        customerVO.setIsShowOrder(record.getIsShowOrder());
        return customerVO;
    }

    @Override
    public void updateAccountNumber(Long userCode,String portrait ,String realname,Integer isCollageRecord,Integer isObtainGoods) {
        if (userCode==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        int i = customerMapper.updateAccountNumber(userCode, portrait, realname, isCollageRecord, isObtainGoods);
        if (i != 1){
            throw new CustomerException(ExceptionEnum.USER_ACCOUNT_SETTINGS);
        }
    }

    @Override
    public void updatePass(Long userCode, String pwd, String oldPass) {
        Customer customer = new Customer();
        customer.setUserCode(userCode);
        Customer select = customerMapper.selectOne(customer);
        if(!StringUtils.equals(select.getLoginPwd(), MD5Util.pwdEncoding(oldPass))) {
            throw new CustomerException(ExceptionEnum.INVALID_NOPASSWORD);
        }
        //密码长度6-16位
        if (pwd.length() > 16 || pwd.length() < 6) {
            throw new CustomerException(ExceptionEnum.INVALID__PASSWORD_LENGTH);
        }
        String pwd1 = MD5Util.pwdEncoding(pwd);
        int count = customerMapper.updatePass(userCode, pwd1);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.INVALID_FAILEDPASSWORD);
        }
    }

    @Override
    public void forgetPass(String pwd, String code,String phone) {
        Customer customer = new Customer();
        customer.setFlag(0);
        customer.setUserMobile(phone);
        Customer select = customerMapper.selectOne(customer);

        if (select==null){
            throw new CustomerException(ExceptionEnum.SMS_CODE_ERROR);
        }

        String key=KEY_PREFIX+phone;
        String s = redisTemplate.opsForValue().get(key);
        if (s==null){throw new CustomerException(ExceptionEnum.USER_PHONE_NOTERROR);}
        if (!s.equals(code)){
            throw new CustomerException(ExceptionEnum.SMS_CODE_ERROR);
        }
        //密码长度6-16位
        if (pwd.length() > 16 || pwd.length() < 6) {
            throw new CustomerException(ExceptionEnum.INVALID__PASSWORD_LENGTH);
        }
        String pwd1 = MD5Util.pwdEncoding(pwd);
        int count = customerMapper.updatePass(select.getUserCode(), pwd1);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.INVALID_FAILEDPASSWORD);
        }
    }

    @Override
    public PageResult<OrderDistribution> CommissionList(Long userCode, Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<OrderDistribution> commissionList = orderDistributionMapper.CommissionList(userCode);
        PageInfo<OrderDistribution> pageInfo = new PageInfo<>(commissionList);
        return new PageResult<>(pageInfo.getTotal(),commissionList);
    }
    public UserDTO phoneLogin(String phone, String code) {
        Customer customer = new Customer();
        customer.setUserMobile(phone);
        customer.setFlag(0);
        customer.setIsRobot(0);
        Customer newUser = customerMapper.selectOne(customer);
        if (newUser==null){throw new CustomerException(ExceptionEnum.USER_PHONE_NOT_ERROR);}
        String key=KEY_PREFIX+phone;
        String s = redisTemplate.opsForValue().get(key);
        if (s==null){throw new CustomerException(ExceptionEnum.USER_PHONE_NOTERROR);}
        if (!s.equals(code)){
            throw new CustomerException(ExceptionEnum.SMS_CODE_ERROR);
        }
        //账号封禁处理
        if ("-1".equals(newUser.getAccountStatu())) {
            throw new CustomerException(ExceptionEnum.INVALID_USER_BANNED);
        }
        String token = JwtUtils.generateToken(new UserInfo(newUser.getUserCode(), newUser.getUserName()), pro.getPrivateKey());
        //返回用户信息
        UserDTO user = new UserDTO();
        user.setToken(token);
        int count=tradeOrderMapper.updateUserOrderStatusByOverTime(newUser.getUserCode(), OrderConstant.UNPAID, OrderConstant.TIME_OUT);
        if (newUser.getIsSupplier()==1&&newUser.getIsRobot()==0){
            user.setUserIdentity("1");//充值会员
        }else {
            user.setUserIdentity("0");
        }
        return user;

    }


    @Override
    public CustomerVO getCustomerInfoShop(Long userCode) {
        if (userCode==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        Customer record = new Customer();
        record.setUserCode(userCode);
        record = customerMapper.selectOne(record);
        if (record == null) {
            throw new CustomerException(ExceptionEnum.INVALID_USER_NOT_FOND);
        }
        CustomerVO customerVO = new CustomerVO();
        customerVO.setUserCode(record.getUserCode());
        customerVO.setUserName(record.getUserName());
        customerVO.setUserPortrait(record.getUserPortrait());
        customerVO.setIsCollageRecord(record.getIsCollageRecord());
        customerVO.setIsObtainGoods(record.getIsObtainGoods());
        customerVO.setIsShowOrder(record.getIsShowOrder());
        customerVO.setUserMobile(record.getUserMobile());
        customerVO.setIsSupplier(record.getIsSupplier());

        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setCustomerCode(record.getUserCode());
        List<CustomerInfo> customers = customerInfoMapper.select(customerInfo);
        customerInfo=customers.get(0);
        customerVO.setBalance(customerInfo.getUserMoney().longValue());
        if (null!=customerInfo.getUserScore()&&null!=customerInfo.getRegisterScore()){
            customerVO.setIntegral(customerInfo.getUserScore()+customerInfo.getRegisterScore());
        }

        //获取签到天数
        PageHelper.startPage(1,1);
        PageHelper.orderBy("id desc");
        UserSignRecord signRecord=new UserSignRecord();
        signRecord.setCustomerCode(customerInfo.getCustomerCode());
        List<UserSignRecord> list=userSignRecordMapper.select(signRecord);
        if (CollectionUtils.isEmpty(list)){
            customerVO.setSignInDays(0);
        }else {
            customerVO.setSignInDays(list.get(0).getContinuousDay());
        }

        return customerVO;
    }

    @Override
    public void getPaymentCode(String code, String state, HttpServletRequest request,HttpServletResponse response) throws IOException {
        WxPayConf wxPayConf=configService.wxPayInfo(null);
        if (wxPayConf==null){
            throw new CustomerException(ExceptionEnum.CONFIG_NOT_FOND);
        }

        Map<String,Object> resultMap=new HashMap<>();
        logger.info("用户登录凭证code为----------------------------------------------->" + code);

        // 通过code换取网页授权access_token
        String requestCodeUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+wxPayConf.getAppId()+"&secret="+wxPayConf.getAppSecret()+"&code="+code+"&grant_type=authorization_code";
        String result = CommonUtil.Get(requestCodeUrl);

        logger.info("通过CODE请求微信获取网页授权的返回结果：" + result);

        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(result);

        Object access_token = jsonObject.get("access_token");
        Object openid = jsonObject.get("openid");
        Object errcode = jsonObject.get("errcode");
        Object errmsg = jsonObject.get("errmsg");

        logger.info("access_token--------------------------------：" + access_token);
        logger.info("openid--------------------------------：" + openid);
        logger.info("errcode--------------------------------：" + errcode);
        logger.info("errmsg--------------------------------：" + errmsg);


        // 拉取用户信息
        String requestUserInfo = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid=" + openid + "&lang=zh_CN";
        String resultInfo = CommonUtil.Get(requestUserInfo);

        com.alibaba.fastjson.JSONObject userInfo = JSON.parseObject(resultInfo);
        System.out.println("通过access_token请求微信拉去用户信息的返回结果：" + userInfo);

        Object u_openid = userInfo.get("openid");
        Object nickname = userInfo.get("nickname");
        Object headimgurl = userInfo.get("headimgurl");
        Object u_errcode = userInfo.get("errcode");
        Object u_errmsg = userInfo.get("errmsg");

        logger.info("u_openid--------------------------------：" + u_openid);
        logger.info("nickname--------------------------------：" + nickname);
        logger.info("headimgurl--------------------------------：" + headimgurl);
        logger.info("u_errcode--------------------------------：" + u_errcode);
        logger.info("u_errmsg--------------------------------：" + u_errmsg);

        //java.net.URL  url = new  java.net.URL(request.getHeader("Referer"));
        //String domain="http://"+url.getHost();
        //logger.info(domain);
        UserConf conf=configService.selectCmsValue(ConfigkeyConstant.MALL_WXPAY_REDIRECT_URL);
        if (null==conf.getConfValue()){
            throw new CustomerException(ExceptionEnum.SHARE_MAKE_MONEY_ERROR);
        }
        String redirectUrl=conf.getConfValue()+"?nickname="+nickname+"&headimgurl="+headimgurl;
        logger.info(redirectUrl);
        response.sendRedirect(redirectUrl);
    }

    @Override
    public String paymentCodePay(Long userCode,Map<String,Object> data) {
        if (userCode==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        String weChatHeadPortrait=data.get("weChatHeadPortrait").toString();
        String weChatRealName=data.get("weChatRealName").toString();
        Double paymentCodePrice=Double.valueOf(data.get("paymentCodePrice").toString());
        String paymentCodeType=data.get("paymentCodeType").toString();
        if (StringUtils.isEmpty(weChatHeadPortrait)||StringUtils.isEmpty(weChatRealName)
            ||paymentCodePrice==null||paymentCodeType==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }

        this.logger.info("进入调取收款码接口**********************************");
        this.logger.info("调取收款码的用户昵称**********************************"+weChatRealName);
        this.logger.info("调取收款码的用户微信昵称**********************************"+weChatHeadPortrait);

        // 获取在线用户
        Customer customer = new Customer();
        customer.setUserCode(userCode);
        List<Customer> recordList = customerMapper.select(customer);
        if (recordList.isEmpty()) {
            throw new CustomerException(ExceptionEnum.INVALID_USER_NOT_FOND);
        }
        customer=recordList.get(0);
        // 初始化用户调用收款码记录基本信息
        PaymentRecord paymentRecord=new PaymentRecord();
        paymentRecord.setCustomerCode(customer.getUserCode().toString());
        paymentRecord.setUserMobile(customer.getUserMobile());
        paymentRecord.setHeadPortrait(customer.getUserPortrait());
        paymentRecord.setRealname(customer.getUserRealName());
        paymentRecord.setWechatHeadPortrait(weChatHeadPortrait);
        paymentRecord.setWechatRealname(weChatRealName);
        paymentRecord.setPaymentCodePrice(paymentCodePrice);
        paymentRecord.setPaymentCodeType(paymentCodeType);
        PaymentCode paymentCode = paymentCodeManageService.createPaymentRecord(paymentRecord);
        this.logger.info("完成调用逻辑**********************************返回对应的收款码url："+paymentCode.getImage());
        if(paymentCode != null && paymentCode.getImage() != null && !"".equals(paymentCode.getImage())) {
            this.logger.info("完成调用逻辑**********************************未找到对应的收款码");
            return paymentCode.getImage();
        }else{
            throw new CustomerException(ExceptionEnum.PAYMENT_CODE_NOT_EXIST);
        }
    }

    @Override
    public String paymentCodeImg(Long userCode) {
        if (userCode==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        PayCustomer payCustomer=payCustomerMapper.getPayCustomer();
        if (payCustomer!=null){
            return payCustomer.getCustImg();
        }else{
            throw new CustomerException(ExceptionEnum.CUSTOMER_SERVICE_NOT_EXIST);
        }
    }

    @Override
    public PageInfo<com.tangchao.shop.vo.adminVo.CustomerVO> selectCustomerList(Long userId, CustomerDTO customerDTO) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        List<com.tangchao.shop.vo.adminVo.CustomerVO> customerList = null;
        // 设置以创建时间降序排列
        PageHelper.orderBy(" create_time DESC ");
        // 设置分页
        PageHelper.startPage(customerDTO.getPageNo(), customerDTO.getPageSize());
        customerList = this.customerMapper.selectCustomerList(customerDTO);
        /*if (!StringUtils.isEmpty(customerDTO.getInviteCode())){
            customerList = this.customerMapper.findCustomerInfo(customerDTO.getInviteCode());
        } else {
            customerList = this.customerMapper.selectCustomerList(customerDTO);
        }*/
        return new PageInfo<>(customerList);
    }

    @Override
    public Map<String, Object> getUserStatistics(Long userId) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("customerSum",customerMapper.findCustomerSum());//网站总数
        resultMap.put("generalCustomer",customerMapper.findMemberCustomerSum());//普通会员
        resultMap.put("robotSum",customerMapper.findRobotSum());//机器人数量
        resultMap.put("supplierSum",customerMapper.findSupplierSum());//充值会员
        return resultMap;
    }

    @Override
    public Map<String,Object> getCustomerScoreDetail(Long userId, Long userCode, Integer pageNo, Integer pageSize) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        if (userCode==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        Map<String,Object> resultMap=new HashMap<>();
        PageHelper.orderBy(" create_time DESC ");
        // 设置分页
        PageHelper.startPage(pageNo,pageSize);
        List<CustomerScoreDetailVO> list=customerScoreDetailService.getCustomerScoreDetailByUserCode(userCode.toString());
        PageInfo<CustomerScoreDetailVO> pageInfo=new PageInfo<>(list);
        resultMap=customerScoreDetailService.selectCountByUserCode(userCode);
        if (resultMap==null){
            resultMap=new HashMap<>();
        }
        resultMap.put("data",pageInfo);
        return resultMap;
    }

    @Override
    public List<CustomerAddress> getCustomerAddress(Long userId,Long userCode) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        CustomerAddress address=new CustomerAddress();
        address.setFlag(0);
        address.setCustomerCode(userCode);
        return addressMapper.select(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long userId, Long userCode) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        if (userCode==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        Customer user=new Customer();
        user.setUserCode(userCode);
        user=customerMapper.selectOne(user);
        if (user==null){
            throw new CustomerException(ExceptionEnum.INVALID_USER_NOT_FOND);
        }

        UserConf defaultPwd=new UserConf();
        defaultPwd.setConfKey(ConfigkeyConstant.MALL_USER_DEFAULT_LOGIN_PWD);
        defaultPwd.setFlag(0);
        defaultPwd = userConfMapper.selectOne(defaultPwd);
        if (null != defaultPwd) {
            user.setLoginPwd(defaultPwd.getConfValue());
        } else {
            user.setLoginPwd("123456");//后台没设置
        }
        // 密码加密
        String pwd = MD5Util.pwdEncoding(user.getLoginPwd());
        user.setLoginPwd(pwd);
        int count=customerMapper.updateByPrimaryKeySelective(user);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.INVALID_USER_NOT_FOND);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertBlackList(Long userId, Long userCode,Integer status) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        if (userCode==null||status==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        Customer user=new Customer();
        user.setUserCode(userCode);
        user.setFlag(0);
        user=customerMapper.selectOne(user);
        if (user==null){
            throw new CustomerException(ExceptionEnum.INVALID_USER_NOT_FOND);
        }
        user.setBlackStatu(status.toString());
        int count=customerMapper.updateByPrimaryKeySelective(user);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.INVALID_USER_NOT_FOND);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId,List<String> ids) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        if (ids.isEmpty()){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }

        int count=0;
        for (String str:ids){
            Customer customer=customerMapper.selectByPrimaryKey(str);
            if (customer==null){
                throw new CustomerException(ExceptionEnum.INVALID_USER_NOT_FOND);
            }
            count+=this.customerMapper.deleteByPrimaryKey(str);//客户要求物理删除数据
            count+=this.customerInfoMapper.deleteCustomerInfo(customer.getUserCode().toString());
        }
        if (count> 0) {
            // 更新秒款地址
            String address = findAddressBySupplier();
            //  查询数据库
            Example exampleConf = new Example(UserConf.class);
            Example.Criteria criteriaConf = exampleConf.createCriteria();
            //  根据Key,查询未删除配置
            criteriaConf.andEqualTo("confKey",ConfigkeyConstant.MALL_ORDER_SECOND_ADDRESS);
            criteriaConf.andNotEqualTo("flag",-1);
            //  只查询一条
            PageHelper.startPage(1,1);
            List<UserConf> confList = userConfMapper.selectByExample(exampleConf);
            UserConf conf=new UserConf();
            if (confList.size() > 0){
                 conf =confList.get(0);
            }
            conf.setConfValue(address);
            //  设置修改人和修改时间
            conf.setLastModifyId(userId);
            conf.setLastModifyTime(new Date());
            userConfMapper.updateByPrimaryKeySelective(conf);
            // 更新秒款地址end
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCustomer(Long userId, CustomerAdminVO customerVO) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        Customer customer=new Customer();
        customer.setId(Long.valueOf(customerVO.getId()));
        customer.setFlag(0);
        customer=customerMapper.selectOne(customer);
        if (customer==null){
            throw new CustomerException(ExceptionEnum.INVALID_USER_NOT_FOND);
        }
        customer.setCreateId(userId);
        customer.setLastModifyId(userId);
        customer.setInviteId(customerVO.getInviteId());
        customer.setUserMobile(customerVO.getUserMobile());
        customer.setUserPortrait(customerVO.getUserPortrait());
        customer.setUserName(customerVO.getUserName());
        customer.setUserRealName(customerVO.getUserRealName());
        customer.setUserEmail(customerVO.getUserEmail());
        customer.setIsSupplier(customerVO.getIsSupplier());
        int count=customerMapper.updateByPrimaryKeySelective(customer);
        if (count==1){
            // 更新秒款地址
            if (customer.getIsSupplier() == 1 && customer.getIsRobot() == 0) {
                // 更新秒款地址
                String address = findAddressBySupplier();

                //  查询数据库
                Example exampleConf = new Example(UserConf.class);
                Example.Criteria criteriaConf = exampleConf.createCriteria();
                //  根据Key,查询未删除配置
                criteriaConf.andEqualTo("confKey",ConfigkeyConstant.MALL_ORDER_SECOND_ADDRESS);
                criteriaConf.andNotEqualTo("flag",-1);
                //  只查询一条
                PageHelper.startPage(1,1);
                List<UserConf> confList = userConfMapper.selectByExample(exampleConf);
                UserConf conf=new UserConf();
                if (confList.size() > 0){
                    conf =confList.get(0);
                }
                conf.setConfValue(address);
                //  设置修改人和修改时间
                conf.setLastModifyId(userId);
                conf.setLastModifyTime(new Date());
                userConfMapper.updateByPrimaryKeySelective(conf);
                // 更新秒款地址end
            }
        }
    }

    @Override
    public void bannedCustomer(Long userId, Long userCode,Integer status) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        if (userCode==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        Customer user=new Customer();
        user.setUserCode(userCode);
        user=customerMapper.selectOne(user);
        if (user==null){
            throw new CustomerException(ExceptionEnum.INVALID_USER_NOT_FOND);
        }
        user.setAccountStatu(status.toString());
        int count=customerMapper.updateByPrimaryKeySelective(user);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.INVALID_USER_NOT_FOND);
        }
    }

    @Override
    public void importRobot(Long userId, MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        response.setContentType("text/html;charset=UTF-8");
        String msg = null;
        InputStream stream=null;
        PrintWriter pw = null;
        try {
            stream = file.getInputStream();//获取输入流
            List<Map> list=new ArrayList<Map>();
            // 获取Ip
            String ip = IPAddressUtil.getClientIpAddress(request);
            Customer customer=new Customer();
            customer.setRegisterIp(ip);
            // 设置创建Id,修改人Id
            customer.setCreateId(userId);
            customer.setLastModifyId(userId);
        //	customer.setUserRealname("");
            customer.setUserEmail("user@163.com");
            customer.setIsRobot(1);
            customer.setIsSupplier(0);
            UserConf conf = new UserConf();
            conf.setConfKey("mall.user.default.portrait");
            UserConf newConf = userConfMapper.selectOne(conf);
            if (newConf != null) {// 查询默认头像
                customer.setUserPortrait(newConf.getConfValue());
            }
            UserConf conf1 = new UserConf();
            conf.setConfKey(ConfigkeyConstant.MALL_USER_DEFAULT_LOGIN_PWD);
            UserConf newConf1 = userConfMapper.selectOne(conf);
            // 生成唯一编码
            String userCode = RandomUtil.generateLongByDateTime(3);
            customer.setUserCode(Long.parseLong(userCode));
            // 密码加密
            customer.setLoginPwd(MD5Util.pwdEncoding(newConf1.getConfValue()));
            // 设置创建时间,修改时间
            Date now = new Date();
            customer.setCreateTime(now);
            customer.setLastModifyTime(now);
            // 设置为正常状态
            customer.setFlag(0);
            customer.setIsCollageRecord(1);
            customer.setIsShowOrder(0);
            customer.setIsObtainGoods(1);
            int count=customerMapper.insert(customer);

            String name=null;
            String mobile=null;

            XSSFWorkbook book = new XSSFWorkbook(stream);
            Sheet sheet = book.getSheetAt(0);
            int rows = sheet.getLastRowNum();// 行
            for (int i = 1; i <=rows; i++) {
                name = sheet.getRow(i).getCell(0)+"";// 数据类型
                mobile = sheet.getRow(i).getCell(1)+"";// 数据类型
                customer.setUserName(name);
                customer.setUserRealName(name);
                customer.setIsRobot(1);
                customer.setIsSupplier(0);
                customer.setUserMobile(mobile);
                customer.setId(null);
                this.createCustomer(customer,userId,request);
            }
            msg="导入成功！";
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            stream.close();
            pw = response.getWriter();
            pw.write(msg);
            pw.close();
        }
    }

    @Override
    public int  createCustomer(Customer customer,Long userId,HttpServletRequest request) {
        // 获取Ip
        String ip = IPAddressUtil.getClientIpAddress(request);
        customer.setRegisterIp(ip);
        // 设置创建Id,修改人Id
        customer.setCreateId(userId);
        customer.setLastModifyId(userId);
        customer.setUserEmail("user@163.com");
        customer.setAccountStatu(0+"");
        customer.setBlackStatu(0+"");
        UserConf conf = new UserConf();
        conf.setConfKey("mall.user.default.portrait");
        UserConf newConf = userConfMapper.selectOne(conf);
        if (newConf != null) {// 查询默认头像
            customer.setUserPortrait(newConf.getConfValue());
        }
        UserConf conf1 = new UserConf();
        conf.setConfKey(ConfigkeyConstant.MALL_USER_DEFAULT_LOGIN_PWD);
        UserConf newConf1 = userConfMapper.selectOne(conf);
        // 生成唯一编码
        String userCode = RandomUtil.generateLongByDateTime(3);
        customer.setUserCode(Long.parseLong(userCode));
        // 密码加密
        customer.setLoginPwd(MD5Util.pwdEncoding(newConf1.getConfValue()));
        // 设置创建时间,修改时间
        Date now = new Date();
        customer.setCreateTime(now);
        customer.setLastModifyTime(now);
        // 设置为正常状态
        customer.setFlag(0);
        int count=customerMapper.insert(customer);
        if (count!=1){
            return 0;
        }
        CustomerScoreDetail detail = new CustomerScoreDetail();
        detail.setCustomerCode(customer.getUserCode());
        detail.setDataSrc(DataSrcConstant.REGISTER);
        detail.setScoreFlag(PayStatusConstant.INCOME);

        //用户信息
        CustomerInfo info = new CustomerInfo();
        info.setCustomerCode(customer.getUserCode());           //  用户唯一标识
        info.setRegisterIp(customer.getRegisterIp());           //  用户注册ip
        info.setCreateId(customer.getCreateId());               //  创建人Id
        info.setCreateTime(customer.getCreateTime());           //  创建时间
        info.setLastModifyId(customer.getLastModifyId());       //  修改人Id
        info.setLastModifyTime(customer.getLastModifyTime());   //  修改时间
        info.setUserMoney(0.0);                                 //  初始化余额
        info.setUserFlow(0.0);                                  //  会员流量
        //  生成邀请码
        String inviteCode = RandomUtil.generateUniqueCode(6,customer.getId());
        info.setInviteCode(inviteCode);
        //  注册送福分
        double score = customerScoreDetailService.interDetail(ConfigkeyConstant.MALL_USER_REGISTER_GIVE_SCORE, detail);
        double money = customerScoreDetailService.interMoneyDetail(ConfigkeyConstant.MALL_USER_REGISTER_GIVE_Money, detail);
        customer.setUserScore(score);
        customer.setRegisterScore(score);
        customer.setRegisterMoney(money);
        count=customerInfoMapper.insertSelective(info);
        if (count!=1){
            return 0;
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCustomer(Long userId, HttpServletRequest request, CustomerAdminVO customerAdminVO) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        Customer user=new Customer();
        user.setUserMobile(customerAdminVO.getUserMobile());
        List<Customer> customers=customerMapper.select(user);
        if (!customers.isEmpty()){
            throw new CustomerException(ExceptionEnum.USER_IS_EXIT);
        }
        Customer customer=new Customer();
        customer.setUserMobile(customerAdminVO.getUserMobile());
        customer.setUserRealName(customerAdminVO.getUserRealName());
        customer.setUserName(customerAdminVO.getUserName());
        customer.setUserPortrait(customerAdminVO.getUserPortrait());
        customer.setInviteId(customerAdminVO.getInviteId());
        customer.setIsRobot(customerAdminVO.getIsRobot());
        customer.setIsSupplier(customerAdminVO.getIsSupplier());
        int count=this.createCustomer(customer,userId,request);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
        // 更新秒款地址
        if (customer.getIsSupplier() == 1 && customer.getIsRobot() == 0) {
            String address = findAddressBySupplier();
            UserConf conf = this.configService.selectCmsValue(ConfigkeyConstant.MALL_ORDER_SECOND_ADDRESS);
            conf.setConfValue(address);
            conf.setId(userId);
            count=userConfMapper.updateBatchConf(conf);
            if (count!=1){
                throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
            }
        }
    }

    @Override
    public Customer selectCustomerInfoByPhone(String phone,Integer type) {

        if (StringUtils.isBlank(phone)||type==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }

        Example example = new Example(Customer.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userMobile", phone);

        if (type==1){
            criteria.andEqualTo("isSupplier", 0);
        }else if (type==1){
            criteria.andEqualTo("isSupplier", 0);
        }else{
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        criteria.andNotEqualTo("flag", -1);
        Customer customer = this.findOnlyOneByExample(example);
        if (customer==null){
            customer=new Customer();
            customer.setUserName(phone);
            customer.setIsSupplier(0);
            customer.setFlag(0);
            customer=customerMapper.selectOne(customer);
            if (customer==null){
                throw new CustomerException(ExceptionEnum.INVALID_USER_NOT_FOND);
            }
        }
        CustomerInfo customerInfo =new CustomerInfo();
        customerInfo.setCustomerCode(customer.getUserCode());
        customerInfo=customerInfoMapper.selectOne(customerInfo);
        if (customerInfo == null) {
            throw new CustomerException(ExceptionEnum.INVALID_USER_NOT_FOND);
        }
        customer.setUserMoney(customerInfo.getUserMoney());
        customer.setUserScore(customerInfo.getUserScore());
        return customer;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void customerRechargeByPhone(Long userId,Map<String, Object> data,HttpServletRequest request) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        String mobile=data.get("mobile").toString();//电话号码
        double userMoney=Double.valueOf(data.get("money").toString());//充值金额
        Double userScore=Double.valueOf(data.get("integral").toString());//积分
        Integer type=Integer.valueOf(data.get("type").toString());//
        Double handselMoney=Double.valueOf(data.get("handselMoney").toString());//

        if (userScore==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }

        try {
            Customer customer = selectCustomerInfoByPhone(mobile,1);
            if (customer == null) {
                throw new CustomerException(ExceptionEnum.INVALID_USER_NOT_FOND);
            }


            if (type==1){//充值
                logger.info("进入充值:登录账号Id"+userId+"登录ip"+IPAddressUtil.getClientIpAddress(request)+"充值手机号:"+mobile+"充值金额:userMoney","充值积分:"+type);
                UserConf rechargeConf =configService.selectCmsValue(ConfigkeyConstant.MALL_USER_RECHARGE_GIVE);
                if (rechargeConf != null && !StringUtils.isEmpty(rechargeConf.getConfValue())) {
                    String[] array1 = rechargeConf.getConfValue().split("/");
                    if (array1.length == 2) {
                        double amountX = Double.parseDouble(array1[0]);
                        double amountY = Double.parseDouble(array1[1]);
                        if (userMoney >= amountX) {// 判断开关 金额是否已达到可以送金额的数量
                            long x = (long) (userMoney / amountX);
                            Double userAmount = ArithUtil.mul(x, amountY);
                            userMoney = userMoney + userAmount;
                        }

                    }else{
                        logger.error("配置格式：X/Y,X为充值金额,Y为赠送金额");
                    }

                }
                UserConf scoreConf = configService.selectCmsValue(ConfigkeyConstant.MALL_USER_RECHARGE_GIVE_SCORE);
                if (scoreConf != null && !org.springframework.util.StringUtils.isEmpty(scoreConf.getConfValue())) {
                    String[] array = scoreConf.getConfValue().split("/");
                    if (array.length == 2) {
                        double where = Double.parseDouble(array[0]);// 最低充值金额数量
                        if (userMoney >= where) {// 判断送积分开关是否打开
                            userScore = customerScoreDetailService.rechargeSendScore(customer.getUserCode(), userMoney, userScore);
                        }
                    }else{
                        logger.error("配置格式：X/Y,X为充值金额,Y为赠送福分数量");
                    }
                }
                CustomerInfo customerInfo =new CustomerInfo();
                customerInfo.setCustomerCode(customer.getUserCode());
                customerInfo=customerInfoMapper.selectOne(customerInfo);
                if (customerInfo != null && customerInfo.getRegisterScore() > 0) {
                    Double registerScore = customerInfo.getRegisterScore();// 把注册送的800福分加到可用的福分里面
                    userScore = userScore + registerScore;
                    customerInfo.setRegisterScore(0.0);
                    customerInfoMapper.updateByPrimaryKeySelective(customerInfo);// 清空这个注册送的福分
                }

                //后台赠送抽奖次数
                if(handselMoney>0){

                    CustomerRechargeRecord customerRechargeRecord = new CustomerRechargeRecord();
                    customerRechargeRecord.setAmount(handselMoney);
                    customerRechargeRecord.setCreateId(userId);
                    customerRechargeRecord.setCustomerCode(customer.getUserCode());
                    customerRechargeRecord.setIntegral(userScore);
                    customerRechargeRecord.setType(1);
                    customerRechargeRecord.setPayment(PayStatusConstant.PAY_ADMIN_HANDSELMONEY);//后台赠送
                    customerRechargeRecord.setCreateTime(new Date());
                    int rowResult = this.customerRechargeRecordMapper.insertSelective(customerRechargeRecord);
                }

                int rowCount = customerInfoMapper.addAmount(customer.getUserCode(), userMoney, userScore, userId);
                if (rowCount > 0) {
                    CustomerRechargeRecord customerRechargeRecord = new CustomerRechargeRecord();
                    customerRechargeRecord.setAmount(userMoney);
                    customerRechargeRecord.setCreateId(userId);
                    customerRechargeRecord.setCustomerCode(customer.getUserCode());
                    customerRechargeRecord.setIntegral(userScore);
                    customerRechargeRecord.setType(1);
                    customerRechargeRecord.setPayment(PayStatusConstant.PAY_FROM_BACKSTAGE);
                    customerRechargeRecord.setCreateTime(new Date());
                    int rowResult = this.customerRechargeRecordMapper.insertSelective(customerRechargeRecord);
                    if (rowResult!=1) {
                        throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
                    }
                }
                this.threeLevelDistribution(userMoney+handselMoney, customer.getInviteId(),customer.getUserCode());
            }else if (type==2){//扣减
                logger.info("进入扣减:登录账号Id"+userId+"登录ip"+IPAddressUtil.getClientIpAddress(request)+"充值手机号:"+mobile+"充值金额:userMoney","充值积分:"+type);
                int count=this.customerMinus(customer.getUserCode(),userMoney,userScore,userId,2,null);
            }else{
                throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
            }

        } catch (Exception e) {
            this.logger.error(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> customerExpensesRecord(Long userId, CustomerDTO customerDTO) {

        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        PageHelper.startPage(customerDTO.getPageNo(),customerDTO.getPageSize(),false);
        List<CustomerRechargeRecord> list=customerRechargeRecordMapper.findCustomerRechargeRecord(customerDTO);


        List<Long> customerCodeList=list.stream().map(CustomerRechargeRecord::getCustomerCode).collect(Collectors.toList());

        // 查询消费金额
        List<Map<String,Object>>  payAmountSum=null;
        if (!customerCodeList.isEmpty()){
            payAmountSum=customerRechargeRecordMapper.sumCustomerRechargeRecordTotal(customerCodeList);
        }


        if (payAmountSum!=null){
            Map<String,Double>  amountSumMap=new HashMap<>();
            for (Map<String, Object> map : payAmountSum){
                amountSumMap.put(map.get("customerCode").toString(),Double.valueOf(map.get("amount").toString()));
            }
            list.forEach(s -> s.setPayAmountSum(amountSumMap.get(s.getCustomerCode().toString())));
        }


        // 查询中奖金额
        List<Map<String,Object>>  winningTotal=null;
        if (!customerCodeList.isEmpty()){
            winningTotal=winningOrderMapper.sumWinningPriceTotalByUserCode(customerCodeList);
        }

        if (winningTotal!=null){
            Map<String,Double>  winningOrderMap=new HashMap<>();
            for (Map<String, Object> map : winningTotal){
                winningOrderMap.put(map.get("customerCode").toString(),Double.valueOf(map.get("winningTotal").toString()));
            }
            list.forEach(s -> s.setWinningTotal(winningOrderMap.get(s.getCustomerCode().toString())));
        }

        for (CustomerRechargeRecord record:list){
            if (record.getWinningTotal()==null){
                record.setWinningTotal(0.00);
            }

            if (record.getPayAmountSum()==null){
                record.setPayAmountSum(1.0);
            }
            if(record.getWinningTotal()/record.getPayAmountSum() < 0.3) {
                record.setWhiteStatu("1");
            }else {
                record.setWhiteStatu("0");
            }
        }
        int totalNum=customerRechargeRecordMapper.countCustomerRechargeRecord(customerDTO);
        PageInfo pageInfo=new PageInfo();
        pageInfo.setList(list);
        pageInfo.setTotal(totalNum);
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("data",pageInfo);
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void proxyRechargeByPhone(Long userId, Map<String, Object> data) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        String customerCode=data.get("customerCode").toString();//用户标识
        double userMoney=Double.valueOf(data.get("money").toString());//充值金额
        try {
            Customer customer = new Customer();
            customer.setFlag(0);
            customer.setIsSupplier(1);
            customer.setUserCode(Long.valueOf(customerCode));
            customer=customerMapper.selectOne(customer);
            if (customer == null) {
                throw new CustomerException(ExceptionEnum.INVALID_USER_NOT_FOND);
            }
            int rowCount = customerInfoMapper.addAmount(Long.valueOf(customerCode), userMoney,null, userId);
            if (rowCount > 0) {
                CustomerRechargeRecord customerRechargeRecord = new CustomerRechargeRecord();
                customerRechargeRecord.setCustomerCode(Long.valueOf(customerCode));
                customerRechargeRecord.setAmount(userMoney);
                customerRechargeRecord.setRechargeDescribe("平台充值给代理额度");
                customerRechargeRecord.setType(7);
                customerRechargeRecord.setCreateTime(new Date());
                customerRechargeRecord.setCreateId(userId);
                customerRechargeRecord.setPayment(PayStatusConstant.PAY_FROM_PROXY);
                int rowResult = this.customerRechargeRecordMapper.insertSelective(customerRechargeRecord);
                if (rowResult > 0) {
                    throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
                }
            }
        } catch (Exception e) {
            this.logger.error(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> proxyExpensesRecord(Long userId, CustomerDTO customerDTO) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        PageHelper.startPage(customerDTO.getPageNo(),customerDTO.getPageSize(),false);
        List<CustomerRechargeRecord> list=customerRechargeRecordMapper.findAgentRechargeRecord(customerDTO);

        int totalRecord=customerRechargeRecordMapper.sumAgentRechargeRecord(customerDTO);
        PageInfo pageInfo=new PageInfo();
        pageInfo.setList(list);
        pageInfo.setTotal(totalRecord);
        Map<String, Object> resultMap=new HashMap<>();
        resultMap.put("data",pageInfo);
        return resultMap;
    }

    @Override
    public Map<String, Object> sumCustomerRechargeRecordTotal(Long userId, CustomerDTO customerDTO) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        Double rechargeTotal=0.00;
        Double consumptionTotal=0.00;
        Double commissionTotal=0.00;
        Double commissionRechargeTotal=0.00;
        if (customerDTO.getType()==null){
            customerDTO.setTypeId(1);
            rechargeTotal=customerRechargeRecordMapper.sumCustomerRechargeRecord(customerDTO);

            customerDTO.setTypeId(2);
            consumptionTotal=customerRechargeRecordMapper.sumCustomerRechargeRecord(customerDTO);

            customerDTO.setTypeId(3);
            commissionTotal=customerRechargeRecordMapper.sumCustomerRechargeRecord(customerDTO);

            customerDTO.setTypeId(4);
            commissionRechargeTotal=customerRechargeRecordMapper.sumCustomerRechargeRecord(customerDTO);
        }else{
            rechargeTotal=customerRechargeRecordMapper.sumCustomerRechargeRecord(customerDTO);
            consumptionTotal=customerRechargeRecordMapper.sumCustomerRechargeRecord(customerDTO);
            commissionTotal=customerRechargeRecordMapper.sumCustomerRechargeRecord(customerDTO);
            commissionRechargeTotal=customerRechargeRecordMapper.sumCustomerRechargeRecord(customerDTO);
        }
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("rechargeTotal",rechargeTotal);//充值总金额
        resultMap.put("consumptionTotal",consumptionTotal);//消费总额
        resultMap.put("commissionTotal",commissionTotal);//佣金充值总额
        resultMap.put("commissionRechargeTotal",commissionRechargeTotal);//佣金提现总额
        return resultMap;
    }

    @Override
    public Map<String, Object> sumProxyExpensesRecord(Long userId, CustomerDTO customerDTO) {

        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        Double proxyRechargeTotal=0.00;
        Double platformRechargeTotal=0.00;
        Double deductionTotal=0.00;
        if (customerDTO.getType()==null){
            customerDTO.setTypeId(1);
            proxyRechargeTotal=customerRechargeRecordMapper.sumProxyExpensesRecord(customerDTO);

            customerDTO.setTypeId(7);
            platformRechargeTotal=customerRechargeRecordMapper.sumProxyExpensesRecord(customerDTO);

            customerDTO.setTypeId(8);
            deductionTotal=customerRechargeRecordMapper.sumProxyExpensesRecord(customerDTO);
        }else{
            proxyRechargeTotal=customerRechargeRecordMapper.sumProxyExpensesRecord(customerDTO);
            platformRechargeTotal=customerRechargeRecordMapper.sumProxyExpensesRecord(customerDTO);
            deductionTotal=customerRechargeRecordMapper.sumProxyExpensesRecord(customerDTO);
        }

        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("proxyRechargeTotal",proxyRechargeTotal);//代理充值总额
        resultMap.put("platformRechargeTotal",platformRechargeTotal);//平台充值代理总额
        resultMap.put("deductionTotal",deductionTotal);//平台扣减代理总额
        return resultMap;
    }

    @Override
    public void insertCustomerGuide(Long userId, Guide data) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        data.setLastModifyId(userId);
        data.setCreateTime(new Date());
        data.setCreateId(userId);
        data.setLastModifyTime(new Date());
        int count=guideMapper.insertSelective(data);
        if(count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public PageInfo getCustomerGuideList(Long userId) {
       /* if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }*/
        Guide guide=new Guide();
        guide.setFlag(0);
        List<Guide>  list=guideMapper.select(guide);
        return new PageInfo(list);
    }

    @Override
    public void deleteCustomerGuide(Long userId,Map<String, Object> data) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        String id=data.get("id").toString();

        Guide guide=guideMapper.selectByPrimaryKey(id);
        if (guide==null){
            throw new CustomerException(ExceptionEnum.CONFIG_NOT_FOND);
        }
        guide.setFlag(-1);
        guide.setLastModifyId(userId);
        guide.setLastModifyTime(new Date());
        int count=guideMapper.updateByPrimaryKeySelective(guide);
        if(count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void updateCustomerGuide(Long userId, Guide data) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        Guide guide=guideMapper.selectByPrimaryKey(data.getId());
        if (guide==null){
            throw new CustomerException(ExceptionEnum.CONFIG_NOT_FOND);
        }
        data.setLastModifyId(userId);
        data.setLastModifyTime(new Date());
        int count=guideMapper.updateByPrimaryKeySelective(data);
        if(count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCustomerAddress(Long userCode, Map<String, Object> data) {
        if (userCode == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        List<String> ids= (List<String>) data.get("ids");
        if (ids.isEmpty()){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        for (String str:ids){
            CustomerAddress address = addressMapper.selectByPrimaryKey(str);
            if (address==null){
                throw new CustomerException(ExceptionEnum.ADDRESS_NOT_FOND);
            }
            address.setFlag(-1);
            int count=addressMapper.updateByPrimaryKeySelective(address);
            if (count!=1){
                throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
            }
        }
    }

    @Override
    public String getWXCode(HttpServletRequest request) throws UnsupportedEncodingException{
        WxPayConf wxPayConf=configService.wxPayInfo(null);
        if (wxPayConf==null){
            throw new CustomerException(ExceptionEnum.CONFIG_NOT_FOND);
        }
        String domain=request.getServerName();
        return "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + wxPayConf.getAppId() + "&redirect_uri="
                + URLEncoder.encode("http://"+domain+"/api/user/home/getPaymentCode", "UTF-8") + "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
    }

    @Override
    public List<String> getCollectionCodeList(Long userId) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        return paymentCodeMapper.getPaymentCodeList();
    }

    @Override
    public void setupAddress(Long userId, Map<String, Object> data) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        String orderNo=data.get("orderNo").toString();//订单编号
        String addressId=data.get("addressId").toString();//收货地址
        //查订单
        WinningOrder  winningOrder=winningOrderMapper.getWinningOrderByOrderNo(orderNo);
        if (winningOrder==null){
            throw new CustomerException(ExceptionEnum.ORDER_NOT_ERROR);
        }

        //查收货地址
        CustomerAddress userAddress=addressMapper.selectByPrimaryKey(addressId);
        if (userAddress==null){
            throw new CustomerException(ExceptionEnum.ADDRESS_NOT_FOND);
        }

        //  TODO  秒款流程需要修改
        //  获取秒单地址
        UserConf conf=configService.selectCmsValue(ConfigkeyConstant.MALL_ORDER_SECOND_ADDRESS);
        String secondOrderAddress ="";
        if (conf!=null){
            secondOrderAddress=conf.getConfValue();
        }
        String userMobile = userAddress.getUserMobile();
        if (!StringUtils.isEmpty(secondOrderAddress)){
            String[] addressArr = secondOrderAddress.split(",");

            //  判断是否为秒款订单 --->> 秒款订单（未秒款）
            for (String address : addressArr) {
                if (userMobile.contains(address)) {
                    winningOrder.setOrderStatus(OrderConstant.SECOND_MONEY);
                    //充值会员
                    Customer pCustomer =new Customer();
                    pCustomer.setUserMobile(userMobile);
                    pCustomer.setFlag(0);
                    pCustomer=customerMapper.selectOne(pCustomer);
                    FastOrder fastOrder = new FastOrder();
                    fastOrder.setCreateTime(new Date());
                    fastOrder.setWinOrderId(winningOrder.getId());
                    fastOrder.setStageId(winningOrder.getStageId());
                    fastOrder.setPuserCode(pCustomer.getUserCode());
                    fastOrder.setUserCode(userId);
                    fastOrder.setCheckCode(RandomUtil.getStringRandom(new Random().nextInt(7) + 8));//随机8~14位字符
                    fastOrderMapper.insertSelective(fastOrder);
                    winningOrder.setOrderStatus(5);//秒款订单
                    break;
                }else{
                    winningOrder.setOrderStatus(8);//普通订单
                }
            }

        }else{
            winningOrder.setOrderStatus(8);//普通订单
        }
        //  设置中奖订单的收货地址
        winningOrder.setUserName(userAddress.getUserName());
        winningOrder.setUserMobile(userMobile);
        String str=userAddress.getProvince()+userAddress.getCity()+userAddress.getArea();
        if (null!=userAddress.getStreet()){
            str+=userAddress.getStreet();
        }
        if (null!=userAddress.getDetailed()){
            str+=userAddress.getDetailed();
        }
        winningOrder.setZipCode(userAddress.getZipCode());
        winningOrder.setUserAddress(str);
        winningOrder.setSubmitTime(new Date());
        //  保存订单信息
        int count=this.winningOrderMapper.updateByPrimaryKey(winningOrder);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void sellTransfer(Long userId, Map<String, Object> data) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        String orderNo=data.get("orderNo").toString();//订单编号
        String userName=data.get("userName").toString();//姓名
        String userMobile=data.get("userMobile").toString();//电话
        String userAddress=data.get("userAddress").toString();//收货地址
        String zipCode=data.get("zipCode").toString();//邮政编码

        if (StringUtils.isBlank(orderNo)||StringUtils.isBlank(orderNo)||
                StringUtils.isBlank(orderNo)||StringUtils.isBlank(orderNo)||
                StringUtils.isBlank(orderNo)){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }

        //查订单
        WinningOrder winningOrder=new WinningOrder();
        winningOrder.setOrderNo(orderNo);
        winningOrder=winningOrderMapper.selectOne(winningOrder);
        if (winningOrder==null){
            throw new CustomerException(ExceptionEnum.ORDER_NOT_ERROR);
        }


        //  TODO  秒款流程需要修改
        //  获取秒单地址
        UserConf conf=configService.selectCmsValue(ConfigkeyConstant.MALL_ORDER_SECOND_ADDRESS);
        String secondOrderAddress ="";
        if (conf!=null){
            secondOrderAddress=conf.getConfValue();
        }
        if (!StringUtils.isEmpty(secondOrderAddress)){
            String[] addressArr = secondOrderAddress.split(",");

            //  判断是否为秒款订单 --->> 秒款订单（未秒款）
            for (String address : addressArr) {
                if (userMobile.contains(address)) {
                    winningOrder.setOrderStatus(OrderConstant.SECOND_MONEY);
                    //充值会员
                    Customer pCustomer =new Customer();
                    pCustomer.setUserMobile(userMobile);
                    pCustomer.setFlag(0);
                    pCustomer=customerMapper.selectOne(pCustomer);
                    FastOrder fastOrder = new FastOrder();
                    fastOrder.setCreateTime(new Date());
                    fastOrder.setWinOrderId(winningOrder.getId());
                    fastOrder.setStageId(winningOrder.getStageId());
                    fastOrder.setPuserCode(pCustomer.getUserCode());
                    fastOrder.setUserCode(userId);
                    fastOrder.setCheckCode(RandomUtil.getStringRandom(new Random().nextInt(7) + 8));//随机8~14位字符
                    fastOrderMapper.insertSelective(fastOrder);
                    break;
                }
            }

        }
        //  设置中奖订单的汇款信息
        winningOrder.setOrderStatus(7);//普通订单
        winningOrder.setUserName(userName);
        winningOrder.setUserMobile(userMobile);
        winningOrder.setUserAddress(userAddress);
        winningOrder.setZipCode(zipCode);
        winningOrder.setSubmitTime(new Date());
        //  保存订单信息
        int count=this.winningOrderMapper.updateByPrimaryKey(winningOrder);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void proxyRecharge(Long userId, Map<String, Object> data) {


        int count=0;
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        Double price=Double.valueOf(data.get("amount").toString());//用户电话
        String userMobile=data.get("userMobile").toString();

        if (price==null||price<0||StringUtils.isBlank(userMobile)){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }

        Customer customer =new Customer();
        customer.setUserMobile(userMobile);
        customer.setFlag(0);
        customer=customerMapper.selectOne(customer);

        if (customer==null){
            throw new CustomerException(ExceptionEnum.INVALID_USER_NOT_FOND);
        }
        // 送积分
        Double score = 0.0;
        // 送money
        Double amount = 0.0;

        // 每消费X元可获赠Y数量福分（？/？）
        String fuFen = this.configService.selectCmsValue(ConfigkeyConstant.MALL_USER_RECHARGE_GIVE_SCORE).getConfValue().trim();
        if (!org.springframework.util.StringUtils.isEmpty(fuFen)) {
            String[] fuFenArr = fuFen.split("/");
            Double fuFenX = Double.valueOf(fuFenArr[0]);
            Double fuFenY = Double.valueOf(fuFenArr[1]);
            score = Math.floor(price / fuFenX) * fuFenY;
        }
        // 每充值X元赠送Y元（？/？）
        String yuan = this.configService.selectCmsValue(ConfigkeyConstant.MALL_USER_RECHARGE_GIVE).getConfValue().trim();
        if (!org.springframework.util.StringUtils.isEmpty(yuan)) {
            String[] yuanArr = yuan.split("/");
            Double yuanX = Double.valueOf(yuanArr[0]);
            Double yuanY = Double.valueOf(yuanArr[1]);
            amount = Math.floor(price / yuanX) * yuanY;
        }

        Date now = new Date();

        CustomerRechargeRecord record = new CustomerRechargeRecord();
        record.setPuserCode(userId);
        record.setCustomerCode(customer.getUserCode());
        record.setAmount(price);
        record.setIntegral(score);
        record.setRechargeDescribe("代理充值");
        record.setType(1);// 充值
        record.setCreateTime(now);
        record.setPayment(PayStatusConstant.PAY_FROM_PROXY);
        count=this.customerRechargeRecordMapper.insertSelective(record);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.RECHARGE_SAVE_ERROR);
        }

        if (amount > 0) {
            CustomerRechargeRecord rechargeRecord2 = new CustomerRechargeRecord();
            rechargeRecord2.setCustomerCode(customer.getUserCode());
            rechargeRecord2.setAmount(amount);
            rechargeRecord2.setIntegral(0.0);
            rechargeRecord2.setRechargeDescribe("充值赠送");
            rechargeRecord2.setType(1);// 充值
            rechargeRecord2.setCreateTime(now);
            rechargeRecord2.setPayment(PayStatusConstant.PAY_FROM_PROXY);
            count=this.customerRechargeRecordMapper.insertSelective(rechargeRecord2);
            if (count!=1){
                throw new CustomerException(ExceptionEnum.RECHARGE_SAVE_ERROR);
            }
        }

        if (score > 0) {
            CustomerScoreDetail scoreDetail = new CustomerScoreDetail();
            scoreDetail.setCustomerCode(customer.getUserCode());
            scoreDetail.setScore(score);
            scoreDetail.setDataSrc(4);// 充值来源
            UserConf confByKey = configService.selectCmsValue(ConfigkeyConstant.CN_SWITCH_US);
            if (null !=confByKey && "1".equals(confByKey.getConfValue())){
                scoreDetail.setScoreDescribe("By top up agent");
            }else {
                scoreDetail.setScoreDescribe("代理充值赠送积分");
            }
            scoreDetail.setScoreFlag(1);// 收入
            scoreDetail.setCreateTime(now);
            count=customerScoreDetailMapper.insertSelective(scoreDetail);
            if (count!=1){
                throw new CustomerException(ExceptionEnum.RECHARGE_SAVE_ERROR);
            }
        }

        CustomerInfo customerInfo =new CustomerInfo();
        customerInfo.setCustomerCode(customer.getUserCode());
        customerInfo=customerInfoMapper.selectOne(customerInfo);
        customerInfo.setUserMoney(price + amount);
        customerInfo.setUserScore(customerInfo.getRegisterScore() + score);
        customerInfo.setRegisterScore(0.0);
        count=customerInfoMapper.addAmount(customerInfo.getCustomerCode(),customerInfo.getUserMoney(),customerInfo.getUserScore(),1L);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.RECHARGE_SAVE_ERROR);
        }


        CustomerInfo proxyCustomerInfo =new CustomerInfo();
        proxyCustomerInfo.setCustomerCode(userId);
        proxyCustomerInfo=customerInfoMapper.selectOne(proxyCustomerInfo);
        if (proxyCustomerInfo.getUserMoney()<=price){
            throw new CustomerException(ExceptionEnum.USER_BALANCE_INSUFFICIENT);
        }
        proxyCustomerInfo.setUserMoney(price);
        count=customerInfoMapper.customerMinus(proxyCustomerInfo.getCustomerCode(),proxyCustomerInfo.getUserMoney(),0.00,1L);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.RECHARGE_SAVE_ERROR);
        }
    }

    @Override
    public int updateCustomerInfo(CustomerInfo customerInfo) {
        if (customerInfo==null||null==customerInfo.getCustomerCode()){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        Example example=new Example(CustomerInfo.class);
        example.createCriteria().andEqualTo("customerCode",customerInfo.getCustomerCode());
        int count=customerInfoMapper.updateByExampleSelective(customerInfo,example);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        return count;
    }

    @Override
    public Map<String,Object> selectProxyRechargeList(CustomerDto customerDto) {
        if (customerDto.getUserCode()== null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        Map<String,Object> resultMap=new HashMap<>();
        PageHelper.startPage(customerDto.getPageNo(),customerDto.getPageSize());
        List<Map<String, Object>> list=customerMapper.selectProxyRechargeList(customerDto);
        double totalMoney=customerMapper.countProxyRechargeList(customerDto);
        resultMap.put("data",list);
        resultMap.put("totalMoney",totalMoney);
        return resultMap;
    }

    @Override
    public Map<String, Object> selectProxyOrderList(CustomerDto customerDto) {
        if (customerDto.getUserCode()== null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        Map<String,Object> resultMap=new HashMap<>();
        PageHelper.startPage(customerDto.getPageNo(),customerDto.getPageSize());
        List<Map<String, Object>> list=winningOrderMapper.selectProxyOrderList(customerDto);
        double totalMoney=winningOrderMapper.selectProxyOrderCount(customerDto);
        resultMap.put("data",list);
        resultMap.put("totalMoney",totalMoney);
        return resultMap;
    }

    @Override
    public String shareQRCode(Long userCode,HttpServletRequest request) throws Exception {

        if (userCode== null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setCustomerCode(userCode);
        List<CustomerInfo> customerList = customerInfoMapper.select(customerInfo);
        if (customerList.size()==0){
            throw new CustomerException(ExceptionEnum.INVALID_USER_NOT_FOND);
        }
        customerInfo = customerList.get(0);


        UserConf image=configService.selectCmsValue(ConfigkeyConstant.MALL_SHARE_BACKGROUNDIMG);
        if (null==image.getConfValue()){
            throw new CustomerException(ExceptionEnum.SHARE_MAKE_MONEY_ERROR);
        }

        UserConf coordinate=configService.selectCmsValue(ConfigkeyConstant.MALL_IMAGE_COORDINATE);
        if (null==coordinate.getConfValue()){
            throw new CustomerException(ExceptionEnum.SHARE_MAKE_MONEY_ERROR);
        }
        String[] array1 = coordinate.getConfValue().split("/");
        Integer abscissa=null;
        Integer ordinate=null;
        try{
            abscissa=Integer.valueOf(array1[0]);
            ordinate=Integer.valueOf(array1[1]);
        }catch (Exception e){
            throw new CustomerException(ExceptionEnum.SHARE_MAKE_MONEY_ERROR);
        }

        java.net.URL  url = new  java.net.URL(request.getHeader("Referer"));
        String content="http://"+url.getHost()+"?id="+customerInfo.getInviteCode();
        System.out.println(content);
        BufferedImage bufferedImage= QRCodeUtil.createImage(content, null, false);
        return WaterMarkUtil.markImageByIcon(bufferedImage,image.getConfValue(),abscissa,ordinate);
    }

    @Override
    public synchronized Map<String,Object> userRecharge(Long userCode,Double price, Integer isType) {

        if (userCode== null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        //写入数据库
        PaymentOrderPlatform pay=new PaymentOrderPlatform();
        pay.setFlag(1);
        pay.setPaymentMoney(price);
        pay.setPaymentStatus(1);//未支付
        pay.setPaymentTime(new Date());
        pay.setPaymentUserCode(userCode.toString());
        pay.setPaymentType(isType);
        pay.setPlatformType("9mao");
        pay.setPaymentOrderNo(Long.toString(idWorker.nextId()));
        int count=paymentOrderPlatformMapper.insertSelective(pay);
        if (count==1){
            logger.info("调用成功!");
        }
        Map<String,Object> paramMap=new HashMap<>();
        paramMap.put("price", price);
        paramMap.put("istype", isType);
        paramMap.put("orderid", pay.getPaymentOrderNo());
        paramMap.put("orderuid", userCode);
        return PayHelper.payOrder(paramMap);
    }

    @Override
    public String userPaymentNotifyPay(GLpayApi payAPI) {
        logger.info("进入回调");
        // 保证密钥一致性
        if (PayHelper.checkPayKey(payAPI)) {
            logger.info("回调信息正确！");
            PaymentOrderPlatform pay=new PaymentOrderPlatform();
            pay.setFlag(1);
            pay.setPaymentStatus(1);//未支付
            pay.setPaymentOrderNo(payAPI.getOrderid());
            pay.setPaymentUserCode(payAPI.getOrderuid());
            pay=paymentOrderPlatformMapper.selectOne(pay);
            if (pay!=null){
                logger.info("修改订单状态!");
                //修改订单状态
                int count=paymentOrderPlatformMapper.updatePlatformTradeNo(payAPI.getPlatform_trade_no(),pay.getId());
                if (count==1){
                    logger.info("订单修改成功");
                }

                //  给用户充值福分
                CustomerInfo payCustomer = new CustomerInfo();
                payCustomer.setCustomerCode(Long.valueOf(pay.getPaymentUserCode()));
                payCustomer=customerInfoMapper.selectOne(payCustomer);
                UserConf rechargeConf = configService.selectCmsValue(ConfigkeyConstant.MALL_USER_RECHARGE_GIVE);
                double userMoney=Double.valueOf(payAPI.getRealprice());
                if (rechargeConf != null && !StringUtils.isEmpty(rechargeConf.getConfValue())) {
                    String[] array1 = rechargeConf.getConfValue().split("/");
                    if (array1.length == 2) {
                        double amountX = Double.parseDouble(array1[0]);
                        double amountY = Double.parseDouble(array1[1]);
                        if (userMoney >= amountX) {// 判断开关 金额是否已达到可以送金额的数量
                            long x = (long) (userMoney / amountX);
                            Double userAmount = ArithUtil.mul(x, amountY);
                            userMoney = userMoney + userAmount;
                        }
                    } else {
                        logger.error("配置格式：X/Y,X为充值金额,Y为赠送金额");
                    }

                }
                int rowCount = customerInfoMapper.addAmount(Long.valueOf(pay.getPaymentUserCode()), userMoney, 0.00, 1L);
                if (rowCount > 0) {
                    CustomerRechargeRecord customerRechargeRecord = new CustomerRechargeRecord();
                    customerRechargeRecord.setAmount(userMoney);
                    customerRechargeRecord.setCustomerCode(payCustomer.getCustomerCode());
                    customerRechargeRecord.setIntegral(0.00);
                    customerRechargeRecord.setType(1);//'充值消费标识{ 1：充值，2：消费 , 3 :佣金提现 ,4佣金充值}',
                    customerRechargeRecord.setPayment(PayStatusConstant.PAY_FROM_BACK_9MAO);//支付猫
                    customerRechargeRecord.setCreateTime(new Date());
                    int rowResult = this.customerRechargeRecordMapper.insertSelective(customerRechargeRecord);
                    if (rowResult== 1) {
                        logger.info("会员余额充值记录");
                    }
                }
                logger.info("修改用户积分!");
                return "OK";
            }
        } else {
            // TODO 该怎么做就怎么做
        }
        return "OK";
    }

    @Override
    public void testpay() {
        PaymentOrderPlatform pay=new PaymentOrderPlatform();
        pay.setFlag(1);
        pay.setPaymentStatus(1);//未支付
        pay.setPaymentOrderNo("1204696736833081344");
        pay.setPaymentUserCode("98634450580112686");
        pay=paymentOrderPlatformMapper.selectOne(pay);
        if (pay!=null){
            pay.setPaymentStatus(2);
            pay.setPlatformTradeNo("20191211515451518193");
            int count=paymentOrderPlatformMapper.updatePlatformTradeNo(pay.getPlatformTradeNo(),pay.getId());
            if (count==1){
                logger.info("订单修改成功");
            }
            System.out.println(pay.getFlag());
        }
    }

    @Override
    public Map<String, Object> userPaymentByCoCo(Long userCode, Double price, Integer isType) {
        if (userCode== null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        Map<String,Object> resultMap=new HashMap<>();
        //写入数据库
        PaymentOrderPlatform pay=new PaymentOrderPlatform();
        pay.setFlag(1);
        pay.setPaymentMoney(price);
        pay.setPaymentStatus(1);//未支付
        pay.setPaymentTime(new Date());
        pay.setPaymentUserCode(userCode.toString());
        pay.setPaymentType(isType);
        pay.setPaymentOrderNo(Long.toString(idWorker.nextId()));
        int count=paymentOrderPlatformMapper.insertSelective(pay);
        resultMap=PayUtil.createOrder(price,isType,pay.getPaymentOrderNo());
        return resultMap;
    }

    @Override
    public String userPaymentNotifyPay_CoCo(NotifyApi notifyApi) {
        // 保证密钥一致性
        if (PayUtil.checkPaySign(notifyApi)){
            PaymentOrderPlatform pay=new PaymentOrderPlatform();
            pay.setFlag(1);
            pay.setPaymentStatus(1);//未支付
            pay.setPaymentOrderNo(notifyApi.getOut_trade_no());
            pay=paymentOrderPlatformMapper.selectOne(pay);
            if (pay!=null) {
                //修改订单状态
                int count=paymentOrderPlatformMapper.updatePlatformTradeNo(null,pay.getId());
                //  给用户充值福分
                CustomerInfo payCustomer = new CustomerInfo();
                payCustomer.setCustomerCode(Long.valueOf(pay.getPaymentUserCode()));
                payCustomer=customerInfoMapper.selectOne(payCustomer);
                UserConf rechargeConf = configService.selectCmsValue(ConfigkeyConstant.MALL_USER_RECHARGE_GIVE);
                double userMoney=Double.valueOf(notifyApi.getTotal_fee());
                if (rechargeConf != null && !StringUtils.isEmpty(rechargeConf.getConfValue())) {
                    String[] array1 = rechargeConf.getConfValue().split("/");
                    if (array1.length == 2) {
                        double amountX = Double.parseDouble(array1[0]);
                        double amountY = Double.parseDouble(array1[1]);
                        if (userMoney >= amountX) {// 判断开关 金额是否已达到可以送金额的数量
                            long x = (long) (userMoney / amountX);
                            Double userAmount = ArithUtil.mul(x, amountY);
                            userMoney = userMoney + userAmount;
                        }
                    } else {
                        logger.error("配置格式：X/Y,X为充值金额,Y为赠送金额");
                    }

                }
                int rowCount = customerInfoMapper.addAmount(Long.valueOf(pay.getPaymentUserCode()), userMoney, 0.00, 1L);
                if (rowCount > 0) {
                    CustomerRechargeRecord customerRechargeRecord = new CustomerRechargeRecord();
                    customerRechargeRecord.setAmount(userMoney);
                    customerRechargeRecord.setCustomerCode(payCustomer.getCustomerCode());
                    customerRechargeRecord.setIntegral(0.00);
                    customerRechargeRecord.setType(1);//'充值消费标识{ 1：充值，2：消费 , 3 :佣金提现 ,4佣金充值}',
                    customerRechargeRecord.setPayment(PayStatusConstant.PAY_FROM_BACKSTAGE);
                    customerRechargeRecord.setCreateTime(new Date());
                    int rowResult = this.customerRechargeRecordMapper.insertSelective(customerRechargeRecord);

                }
                return "success";
            }
        }
        return "success";
    }

    @Override
    public synchronized Map<String,Object> userPaymentByCPNP(Long userCode,Double price) {
        if (userCode== null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        Map<String,Object> resultMap=new HashMap<>();
        //写入数据库
        PaymentOrderPlatform pay=new PaymentOrderPlatform();
        pay.setFlag(1);
        pay.setPaymentMoney(price);
        pay.setPaymentStatus(1);//未支付
        pay.setPaymentTime(new Date());
        pay.setPaymentUserCode(userCode.toString());
        pay.setPaymentType(1);//unionpay
        pay.setPaymentOrderNo(Long.toString(idWorker.nextId()));
        pay.setPlatformType("CPNP");
        int count=paymentOrderPlatformMapper.insertSelective(pay);
        if (count==1){
            logger.info("调用成功!");
        }
        return PayByCPNPUtil.createOrder(price,pay.getPaymentOrderNo());
    }

    @Override
    public String userPaymentNotifyPay_CPNP(HttpServletRequest request) throws Exception {
        logger.info("进入回调");

        DataInputStream in=new DataInputStream(request.getInputStream());
        byte[] dataOrigin=new byte[request.getContentLength()];
        in.readFully(dataOrigin);
        in.close();
        String notifyStr=new String(dataOrigin);
        JSONObject jsonObject = JSONObject.fromObject(notifyStr);

        SortedMap<Object, Object> map = new TreeMap<Object, Object>();
        map.put("resultCode", jsonObject.getString("resultCode"));
        map.put("message", jsonObject.getString("message"));
        map.put("orderNo", jsonObject.getString("orderNo"));
        map.put("sysOrderNo",jsonObject.getString("sysOrderNo"));
        map.put("threeOrderNo",jsonObject.getString("threeOrderNo"));
        map.put("price", jsonObject.getString("price"));
        map.put("payPrice",jsonObject.getString("payPrice"));
        map.put("sign",jsonObject.getString("sign"));
        // 保证密钥一致性
        if (PayByCPNPUtil.checkPaySign(map)){
            logger.info("回调信息正确！");
            if (map.get("payPrice").equals(map.get("price"))){
                logger.info("发起支付金额和实际支付一样！");
                PaymentOrderPlatform pay=new PaymentOrderPlatform();
                pay.setFlag(1);
                pay.setPaymentStatus(1);//未支付
                pay.setPaymentOrderNo(map.get("orderNo").toString());
                pay=paymentOrderPlatformMapper.selectOne(pay);
                if (pay!=null) {
                    logger.info("修改订单状态!");
                    //修改订单状态
                    int count=paymentOrderPlatformMapper.updatePlatformTradeNo(map.get("sysOrderNo").toString(),pay.getId());
                    if (count==1){
                        logger.info("订单修改成功");
                    }

                    //  给用户充值福分
                    CustomerInfo payCustomer = new CustomerInfo();
                    payCustomer.setCustomerCode(Long.valueOf(pay.getPaymentUserCode()));
                    payCustomer=customerInfoMapper.selectOne(payCustomer);
                    UserConf rechargeConf = configService.selectCmsValue(ConfigkeyConstant.MALL_USER_RECHARGE_GIVE);
                    double userMoney=Double.valueOf(map.get("payPrice").toString());
                    if (rechargeConf != null && !StringUtils.isEmpty(rechargeConf.getConfValue())) {
                        String[] array1 = rechargeConf.getConfValue().split("/");
                        if (array1.length == 2) {
                            double amountX = Double.parseDouble(array1[0]);
                            double amountY = Double.parseDouble(array1[1]);
                            if (userMoney >= amountX) {// 判断开关 金额是否已达到可以送金额的数量
                                long x = (long) (userMoney / amountX);
                                Double userAmount = ArithUtil.mul(x, amountY);
                                userMoney = userMoney + userAmount;
                            }
                        } else {
                            logger.error("配置格式：X/Y,X为充值金额,Y为赠送金额");
                        }

                    }
                    int rowCount = customerInfoMapper.addAmount(Long.valueOf(pay.getPaymentUserCode()), userMoney, 0.00, 1L);
                    if (rowCount > 0) {
                        CustomerRechargeRecord customerRechargeRecord = new CustomerRechargeRecord();
                        customerRechargeRecord.setAmount(userMoney);
                        customerRechargeRecord.setCustomerCode(payCustomer.getCustomerCode());
                        customerRechargeRecord.setIntegral(0.00);
                        customerRechargeRecord.setType(1);//'充值消费标识{ 1：充值，2：消费 , 3 :佣金提现 ,4佣金充值}',
                        customerRechargeRecord.setPayment(PayStatusConstant.PAY_FROM_WECHAT);//微信
                        customerRechargeRecord.setCreateTime(new Date());
                        int rowResult = this.customerRechargeRecordMapper.insertSelective(customerRechargeRecord);
                        if (rowResult== 1) {
                            logger.info("会员余额充值记录");
                        }
                    }
                    logger.info("修改用户积分!");
                    return "SUCCESS";
                }
            }
        }
        return "SUCCESS";
    }

    @Override
    public String userPayQRCode(String urlText) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();//io流
        BufferedImage bufferedImage= QRCodeUtil.createImage(urlText, null, false);
        ImageIO.write(bufferedImage, "png", baos);//写入流中
        byte[] bytes = baos.toByteArray();//转换成字节
        BASE64Encoder encoder = new BASE64Encoder();
        //转换成base64串
        String png_base = encoder.encodeBuffer(bytes).trim();
        //删除 \r\n
        String str = png_base.replaceAll("\n", "").replaceAll("\r", "");
        return str;
    }

    @Override
    public void paySuccessView(Double money,HttpServletResponse response) throws IOException {

       /* String redirectUrl="";
        UserConf conf=configService.selectCmsValue(ConfigkeyConstant.MALL_PAY_SUCCESS_URL);
        if (null==conf.getConfValue()){
            redirectUrl="http://www.rn193.cn/#/pages/yygtabber/my";
        }
        redirectUrl=conf.getConfValue()+"?money="+money;*/

        String url="http://www.rn193.cn/#/pages/yygpage/success";
        response.sendRedirect(url);
    }

    @Override
    public void mobileRecharge(Long userCode,Integer money) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        if (userCode== null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        Customer customer =new Customer();
        customer.setUserCode(userCode);
        customer.setFlag(0);
        customer=customerMapper.selectOne(customer);
        Map<String,Object> resultMap=new HashMap<>();
        //写入数据库
        PhoneRechargeRecord pay=new PhoneRechargeRecord();
        pay.setCreateTime(new Date());
        pay.setMoney(money);
        pay.setPhoneNo(customer.getUserMobile());
        pay.setStatus(0);
        pay.setOrderNo(Long.toString(idWorker.nextId()));
        pay.setUserCode(userCode);
        int count=phoneRechargeRecordMapper.insertSelective(pay);
        if (count==1){
            logger.info("调用成功!");
        }
        resultMap=PayMobileRecharge.createMobileRecharge(pay.getOrderNo(),money,customer.getUserMobile());
    }

    @Override
    public String mobileRechargeNotify(HttpServletRequest request) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        HashMap<String,Object> params=new HashMap<>();
        params.put("sporder_id",request.getParameter("sporder_id"));
        params.put("orderid",request.getParameter("orderid"));
        Integer status=Integer.valueOf(request.getParameter("sta"));
        String sign=request.getParameter("sign");

        StringBuffer queryString=new StringBuffer();
        params.keySet().stream()
                .filter(key-> params.get(key)!=null)
                .forEach(key->queryString.append(String.format("%s+",params.get(key))));
        String createSign=MD5Util.createMD5(""+queryString.toString());
        if (createSign.equals(sign)){
            PhoneRechargeRecord pay=new PhoneRechargeRecord();
            pay.setOrderNo(params.get("orderid").toString());
            pay=phoneRechargeRecordMapper.selectOne(pay);
            if (pay!=null){
                pay.setStatus(status);
                pay.setPlatformOrderNo(params.get("sporder_id").toString());
                phoneRechargeRecordMapper.updateByPrimaryKey(pay);
            }
        }
        return "success";
    }

    @Override
    public void setCustomerRobot(Map<String, Object> data) {
        Integer type=Integer.valueOf(data.get("type").toString());
        List<String> ids= (List<String>) data.get("ids");
        int count=customerMapper.updateByIds(ids,type);
    }

    @Override
    public Map<String, Object> getCommissionListByUserCode(Integer page, Integer pageSize, Long userCode) {
        PageHelper.startPage(page,pageSize);
        List<Map<String, Object>> list=orderDistributionMapper.getCommissionListByUserCode(userCode);
        PageInfo<Map<String, Object>> pageInfo=new PageInfo<>(list);

        Map<String, Object> totalMoney=orderDistributionMapper.sumCommissionListByUserCode(userCode);

        Double balance=orderDistributionMapper.sumWithdrawnByUserCode(userCode);

        Map<String, Object> resultMap=new HashMap<>();
        resultMap.put("data",new PageResult<>(pageInfo.getTotal(),list));
        resultMap.putAll(totalMoney);
        resultMap.put("withdrawn",balance);
        return resultMap;
    }

    @Override
    public Map<String,Object> addCustomerSignIn() throws ParseException {
        UserInfo user = UserInterceptor.getUserInfo();
        if (user==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }


        PageHelper.orderBy("id desc");
        Map<String,Object> resultMap=new HashMap<>();
        CustomerSignRecord signRecord=new CustomerSignRecord();
        signRecord.setCustomerCode(user.getUserCode());
        List<CustomerSignRecord> oldSignRecord=signRecordMapper.select(signRecord);
        if (!CollectionUtils.isEmpty(oldSignRecord)){
            signRecord=oldSignRecord.get(0);
        }


        OrderDistribution distribution = new OrderDistribution();
        distribution.setRemindLayer(1);                         //  提点层级
        distribution.setCreateTime(new Date());                 //  创建时间
        distribution.setBeneficiaryCode(user.getUserCode());

        Random random=new Random();
        Double num =random.nextDouble()*2+1;
        BigDecimal b = new BigDecimal(num);
        num = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        //第一次签到

        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setCustomerCode(user.getUserCode());
        List<CustomerInfo> customerList = customerInfoMapper.select(customerInfo);
        if (customerList.isEmpty()){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        customerInfo = customerList.get(0);

        CustomerSignRecord addSignRecord=new CustomerSignRecord();
        if (CollectionUtils.isEmpty(oldSignRecord)){

            Double randomNum =random.nextDouble()*1.1+4;
            BigDecimal b1 = new BigDecimal(randomNum);
            randomNum = b1.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();


            addSignRecord.setCustomerCode(user.getUserCode());
            addSignRecord.setContinuousDay(1);
            addSignRecord.setSignDate(new Date());
            addSignRecord.setMoney(new BigDecimal(randomNum.toString()));
            addSignRecord.setExplanation("第一次签到获得5元佣金");
            signRecordMapper.insertSelective(addSignRecord);
            distribution.setRemindMoney(randomNum);

            this.orderDistributionMapper.insertSelective(distribution);
            //  更新收益人佣金余额
            double userMoney = ArithUtil.add(customerInfo.getEmployMoney(),randomNum);
            customerInfo.setEmployMoney(userMoney);
            this.customerInfoMapper.updateByPrimaryKeySelective(customerInfo);

            resultMap.put("num",1);
            resultMap.put("data",randomNum);
            return resultMap;
        }else if (signRecord.getContinuousDay().equals(1)){//第2次签到
            addSignRecord.setCustomerCode(user.getUserCode());
            addSignRecord.setSignDate(new Date());
            addSignRecord.setContinuousDay(2);
            addSignRecord.setMoney(new BigDecimal(num+""));
            addSignRecord.setExplanation("第二次签到获得"+num+"元佣金");
            signRecordMapper.insertSelective(addSignRecord);
            distribution.setRemindMoney(num);

            this.orderDistributionMapper.insertSelective(distribution);
            //  更新收益人佣金余额
            double userMoney = ArithUtil.add(customerInfo.getEmployMoney(),num);
            customerInfo.setEmployMoney(userMoney);
            this.customerInfoMapper.updateByPrimaryKeySelective(customerInfo);
            resultMap.put("num",2);
            resultMap.put("data",num);
            return resultMap;
        }else if (signRecord.getContinuousDay().equals(2)){//第3次签到
            int number=random.nextInt(2)+1;
            addSignRecord.setCustomerCode(user.getUserCode());
            addSignRecord.setSignDate(new Date());
            addSignRecord.setContinuousDay(3);
            addSignRecord.setExplanation("第三次签到获得抽奖机会"+number+"次");
            signRecordMapper.insertSelective(addSignRecord);

            double userMoney = ArithUtil.add(customerInfo.getUserMoney(),number);
            customerInfo.setUserMoney(userMoney);
            this.customerInfoMapper.updateByPrimaryKeySelective(customerInfo);
            resultMap.put("num",3);
            resultMap.put("data",number);
            return resultMap;
        }else{
            resultMap.put("num",3);
            resultMap.put("data",1);
            return resultMap;
        }
    }

    @Override
    public Map<String,Object> getCustomerSignIn() throws ParseException {
        UserInfo user = UserInterceptor.getUserInfo();
        if (user==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        PageHelper.orderBy("id desc");
        CustomerSignRecord signRecord=new CustomerSignRecord();
        signRecord.setCustomerCode(user.getUserCode());
        List<CustomerSignRecord> oldSignRecord=signRecordMapper.select(signRecord);

        Map<String,Object> resultMap=new HashMap<>();
        Customer customer=new Customer();
        customer.setUserCode(user.getUserCode());
        customer=customerMapper.selectOne(customer);

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Boolean flag=customer.getCreateTime().before(sdf.parse("2020-05-08 11:28:04"));

        if (flag){
            resultMap.put("isNew",0);
            resultMap.put("data",1);
            resultMap.put("isSign",0);
            return resultMap;
        }

        if (!CollectionUtils.isEmpty(oldSignRecord)){

            signRecord=oldSignRecord.get(0);
            oldSignRecord=oldSignRecord.stream().sorted(Comparator.comparing(CustomerSignRecord::getId)).collect(Collectors.toList());

            //)
            if (signRecord.getContinuousDay()<3&&(DateUtil.betweenDay(new Date(),signRecord.getSignDate(),false))<=1&&(!DateUtil.isSameDay(new Date(),signRecord.getSignDate()))){//可以签到
                resultMap.put("isNew",1);
                resultMap.put("isSign",1);
                resultMap.put("data",oldSignRecord);
                return resultMap;
            }else{
                resultMap.put("isSign",0);//不可签到
                resultMap.put("isNew",0);
                resultMap.put("data",oldSignRecord);
                return resultMap;
            }
        }else{//从未签到过
            resultMap.put("isSign",1);
            resultMap.put("isNew",1);
            resultMap.put("data",0);
            return resultMap;
        }
    }

    private String findAddressBySupplier() {
        PageHelper.orderBy("create_time asc");
        Example example = new Example(Customer.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isRobot", 0);
        criteria.andEqualTo("isSupplier", 1);
        criteria.andEqualTo("flag", 0);
        List<Customer> list = this.customerMapper.selectByExample(example);
        StringBuilder builder = new StringBuilder();
        for (Customer customer : list) {
            String userMobile = customer.getUserMobile();
            if (org.springframework.util.StringUtils.isEmpty(userMobile)) {
                continue;
            }
            builder.append(userMobile + ",");
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }
    /**
     * 根据条件查询,返回唯一结果
     *
     * @param example 查询条件
     * @return Customer
     */
    private Customer findOnlyOneByExample(Example example) {
        // 只查询一条记录
        List<Customer> customerList = this.customerMapper.selectByExample(example);
        if (customerList.size() > 0) {
            return customerList.get(0);
        }
        return null;
    }


    public int customerMinus(Long customerCode, Double userMoney, Double userScore, Long lastModifyId, Integer type, String rechargeCode) {
        int rowCount = customerInfoMapper.customerMinus(customerCode, userMoney, userScore, lastModifyId);
        if (rowCount > 0) {
            CustomerRechargeRecord customerRechargeRecord = new CustomerRechargeRecord();
            customerRechargeRecord.setAmount(userMoney);
            customerRechargeRecord.setCreateId(lastModifyId);
            customerRechargeRecord.setCustomerCode(customerCode);
            customerRechargeRecord.setIntegral(userScore);
            customerRechargeRecord.setType(type);
            customerRechargeRecord.setPayment(PayStatusConstant.PAY_FROM_BACKSTAGE);
            customerRechargeRecord.setCreateTime(new Date());
            if (!org.springframework.util.StringUtils.isEmpty(rechargeCode)) {
                customerRechargeRecord.setRechargeCode(rechargeCode);
            }
            int rowResult = this.customerRechargeRecordMapper.insertSelective(customerRechargeRecord);
            if (rowResult!=1) {
                return 0;
            }
            return 1;
        }
        return 0;
    }


    /**
     * 三级分销
     * @param inviteId
     */
    private void threeLevelDistribution(Double actualPay, String inviteId,Long userCode){

        OrderDistribution distribution = new OrderDistribution();
        distribution.setOrderNo("后台充值分销");            //  订单编号
        distribution.setOrderTotal(actualPay); //  订单总额（扣除最小支付金额）
        distribution.setRemindLayer(1);                         //  提点层级
        distribution.setPurchaserCode(userCode); //  订单消费者编号
        distribution.setCreateTime(new Date());                 //  创建时间
        //  创建分销提点
        this.createDistribution(distribution,inviteId);
    }

    private void createDistribution(OrderDistribution distribution,String beneficiaryCode) {
        //  判断有收益人
        if (null == beneficiaryCode) {
            return;
        }
        int layer = distribution.getRemindLayer();
        String confKey = ConfigkeyConstant.MALL_ORDER_DISTRIBUTION_REMIND + layer;
        UserConf conf=new UserConf();
        conf.setConfKey(confKey);
        conf.setFlag(0);
        conf = userConfMapper.selectOne(conf);
        if (null == conf|| StringUtils.isEmpty(conf.getConfValue())){
            return;
        }
        try {
            //  获取提点比例
            Double remindSpec = Double.parseDouble(conf.getConfValue());
            distribution.setRemindSpec(remindSpec);
            //  计算提点金额
            Double remindMoney = ArithUtil.mul(remindSpec, distribution.getOrderTotal());
            distribution.setRemindMoney(remindMoney);
            //  获取收益人信息
            CustomerInfo customerInfo = new CustomerInfo();
            customerInfo.setInviteCode(beneficiaryCode);
            List<CustomerInfo> customerList = customerInfoMapper.select(customerInfo);
            if (customerList.isEmpty()) {
                return;
            }
            customerInfo = customerList.get(0);//chang
            distribution.setBeneficiaryCode(customerInfo.getCustomerCode());
            distribution.setId(null);
            //  保存提点记录
            this.orderDistributionMapper.insertSelective(distribution);
            //  更新收益人佣金余额
            double userMoney = ArithUtil.add(customerInfo.getEmployMoney(), remindMoney);
            customerInfo.setEmployMoney(userMoney);
            this.customerInfoMapper.updateByPrimaryKeySelective(customerInfo);
            //  创建下一级分销记录
            layer++;
            //  判断层级
            if (layer > 3){
                return;
            }
            distribution.setRemindLayer(layer);

            Customer customer=new Customer();
            customer.setUserCode(customerInfo.getCustomerCode());
            List<Customer> customerInfoList = customerMapper.select(customer);
            if(customerInfoList.isEmpty()) {
                return;
            }
            customer=customerInfoList.get(0);
            this.createDistribution(distribution,customer.getInviteId() == null ? null : customer.getInviteId());
        } catch (NumberFormatException e) {
            this.logger.error(e.getMessage());
        }
    }
}
