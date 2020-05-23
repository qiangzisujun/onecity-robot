package com.tangchao.user.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.tangchao.common.constant.ConfigkeyConstant;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.common.utils.IPAddressUtil;
import com.tangchao.common.utils.MD5Util;
import com.tangchao.common.utils.PasswordUtil;
import com.tangchao.common.utils.StringUtil;
import com.tangchao.shop.config.JwtProperties;
import com.tangchao.shop.dto.adminDTO.ManagerUserDTO;
import com.tangchao.shop.mapper.ManagerMapper;
import com.tangchao.shop.mapper.MenuMapper;
import com.tangchao.shop.mapper.ProgramMapper;
import com.tangchao.shop.mapper.RoleMapper;
import com.tangchao.shop.pojo.*;
import com.tangchao.shop.util.TreeUtil;
import com.tangchao.shop.utils.JwtUtils;
import com.tangchao.shop.vo.adminVo.ManagerUserVO;
import com.tangchao.user.service.CmsConfigService;
import com.tangchao.user.service.ManagerService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sun.misc.BASE64Encoder;
import tk.mybatis.mapper.entity.Example;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/10/28 13:43
 */
@Service
public class ManagerServiceImpl implements ManagerService {

    private static final String KEY_PREFIX = "user:logout:token";

    private static final Logger logger = LoggerFactory.getLogger(CustomerScoreDetailServiceImpl.class);

    @Autowired
    private Producer captchaProducer;

    @Autowired
    private Producer captchaProducerMath;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ManagerMapper managerMapper;

    @Autowired
    private JwtProperties pro;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private CmsConfigService configService;

    @Autowired
    private ProgramMapper programMapper;



    @Override
    public String getKaptchaImage() throws IOException {
        String type="char";
        String capStr = null;
        String code = null;
        BufferedImage bi = null;
        if ("math".equals(type))
        {
            String capText = captchaProducerMath.createText();
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            bi = captchaProducerMath.createImage(capStr);
        }
        else if ("char".equals(type))
        {
            capStr = code = captchaProducer.createText();
            bi = captchaProducer.createImage(capStr);
        }
        redisTemplate.opsForValue().set(Constants.KAPTCHA_SESSION_KEY, code, 1, TimeUnit.MINUTES);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();//io流
        ImageIO.write(bi, "png", baos);//写入流中
        byte[] bytes = baos.toByteArray();//转换成字节
        BASE64Encoder encoder = new BASE64Encoder();
        //转换成base64串
        String png_base = encoder.encodeBuffer(bytes).trim();
        //删除 \r\n
        String str = png_base.replaceAll("\n", "").replaceAll("\r", "");
        return str;
    }

    @Override
    public ManagerUserVO managerLogin(ManagerUserDTO userDTO, HttpServletRequest request) {
        String cacheCode = redisTemplate.opsForValue().get(Constants.KAPTCHA_SESSION_KEY);
        //判断验证码
        if(true){
            String userName = userDTO.getUserName();
            Manager user=new Manager();
            user.setUserName(userDTO.getUserName());
            List<Manager> userList=managerMapper.select(user);
            if (!userList.isEmpty()){
                String str=PasswordUtil.contrastPassword(userDTO.getUserName(),userDTO.getPassword()).toString();
                if (!str.equals(userList.get(0).getUserPwd())){
                    throw new CustomerException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
                }
                ManagerUserVO vo=new ManagerUserVO();
                vo.setUserName(userList.get(0).getUserName());
                vo.setUserPortrait(userList.get(0).getUserPortrait());
                String token = JwtUtils.generateToken(new UserInfo(userList.get(0).getId(), userList.get(0).getUserName()), pro.getPrivateKey());
                vo.setToken(token);
                String key=KEY_PREFIX+userList.get(0).getUserName();
                redisTemplate.opsForValue().set(key, token, 30, TimeUnit.DAYS);
                logger.info("登录ip="+ IPAddressUtil.getClientIpAddress(request));
                return vo;
            }else{
                throw new CustomerException(ExceptionEnum.INVALID_USER_NOT_FOND);
            }
            //Subject currentUser = SecurityUtils.getSubject();
            /*if(!currentUser.isAuthenticated()) {
                UsernamePasswordToken token =new UsernamePasswordToken(userName,userDTO.getPassword());
                try {
                    currentUser.login(token);
                    Object object=currentUser.getPrincipal();
                    ManagerUserVO vo=new ManagerUserVO();
                    return vo;
                    //view.setViewName("redirect:admin/index");
                }catch (UnknownAccountException uae) {
                    logger.info("对用户[" + userName + "]进行登录验证..验证未通过,未知账户");
                    redirectAttributes.addFlashAttribute("message", "未知账户");
                } catch (IncorrectCredentialsException ice) {
                    logger.info("对用户[" + userName + "]进行登录验证..验证未通过,错误的凭证");
                    redirectAttributes.addFlashAttribute("message", "用户名或密码不正确");
                } catch (LockedAccountException lae) {
                    logger.info("对用户[" + userName + "]进行登录验证..验证未通过,账户已锁定");
                    redirectAttributes.addFlashAttribute("message", "账户已锁定");
                } catch (ExcessiveAttemptsException eae) {
                    logger.info("对用户[" + userName + "]进行登录验证..验证未通过,错误次数过多");
                    redirectAttributes.addFlashAttribute("message", "用户名或密码错误次数过多");
                } catch (AuthenticationException ae) {
                    //通过处理Shiro的运行时AuthenticationException就可以控制用户登录失败或密码错误时的情景
                    logger.info("对用户[" + userName + "]进行登录验证..验证未通过,堆栈轨迹如下");
                    ae.printStackTrace();
                    redirectAttributes.addFlashAttribute("message", "用户名或密码不正确");
                }
            }*/
        }else{
            throw new CustomerException(ExceptionEnum.INVALID_VERIFY_CODE);
        }
    }

    @Override
    public Map<String,Object> index(String userName){
        if (StringUtils.isBlank(userName)){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        Map<String,Object> resultMap=new HashMap<>();
        Manager user=new Manager();
        user.setUserName(userName);
        List<Manager> userList=managerMapper.select(user);

        Role role=new Role();
        role.setId(userList.get(0).getRoleId());
        role=roleMapper.selectOne(role);

        List<String> ids = StringUtil.splitString(role.getMenuIds(), StringUtil.COMMA);
        List<Menu> menuList=null;
        if (ids.size() > 0){
            //  查询条件构造器
            Example example = new Example(Menu.class);
            Example.Criteria criteria = example.createCriteria();
            //  未删除状态的菜单
            criteria.andEqualTo("flag",0);
            criteria.andIn("id",ids);
            menuList =menuMapper.selectByExample(example);
            // 树形排序

        }
        // 获取可操作的菜单
        menuList = TreeUtil.tree(menuList);
        resultMap.put("menuList",menuList);
        return resultMap;

    }

    @Override
    public ManagerUserVO selectUserInfo(Long userCode) {
        if (userCode==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        ManagerUserVO vo=new ManagerUserVO();
        Manager user=new Manager();
        user.setId(userCode);
        List<Manager> userList=managerMapper.select(user);
        if (userList.isEmpty()){
            throw new CustomerException(ExceptionEnum.INVALID_USER_NOT_FOND);
        }
        vo.setUserName(userList.get(0).getUserName());
        vo.setUserPortrait(userList.get(0).getUserPortrait());
        return vo;
    }

    @Override
    public void managerLogout(Long userCode) {
        Manager user=managerMapper.selectByPrimaryKey(userCode);
        String key=KEY_PREFIX+user.getUserName();
        String str=redisTemplate.opsForValue().get(key);
        if (!StringUtils.isBlank(str)){
            redisTemplate.delete(key);
        }
    }

    @Override
    public PageInfo getManagerList(Long userId, String userName, Integer roleId, Integer pageNo, Integer pageSize) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        PageHelper.startPage(pageNo,pageSize);
        PageHelper.orderBy("createTime desc");
        Example example = new Example(Manager.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andNotEqualTo("flag", -1);
        if (!StringUtils.isBlank(userName)){
            criteria.andLike("userName","%"+userName+"%");
        }
        if (roleId!=null){
            criteria.andEqualTo("roleId",roleId);
        }
        List<Manager> list=managerMapper.selectByExample(example);

        List<ManagerUserVO> managerUserVOS=new ArrayList<>();


        for (Manager manager:list){
            ManagerUserVO vo=new ManagerUserVO();
            vo.setUserName(manager.getUserName());
            vo.setUserPortrait(manager.getUserPortrait());
            vo.setMailbox(manager.getUserEmail());
            vo.setMobilePhone(manager.getUserMobile());
            vo.setUserRealName(manager.getUserRealname());
            vo.setId(manager.getId());

            Role role=new Role();
            role.setId(manager.getRoleId());
            List<Role> roles=roleMapper.select(role);
            String str="";
            for(Role r:roles){
                str+=r.getRoleName()+"   ";
            }
            vo.setRoleName(str);
            managerUserVOS.add(vo);
        }

        return new PageInfo(managerUserVOS);
    }

    @Override
    public void updateManagerInfo(Long userId, ManagerUserVO vo) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        Manager manager=managerMapper.selectByPrimaryKey(vo.getId());
        if (manager==null){
            throw new CustomerException(ExceptionEnum.INVALID_USER_NOT_FOND);
        }
        manager.setLastModifyId(userId);
        manager.setLastModifyTime(new Date());
        manager.setRoleId(vo.getRoleId());
        manager.setUserName(vo.getUserName());
        manager.setUserEmail(vo.getMailbox());
        manager.setUserMobile(vo.getMobilePhone());
        manager.setUserRealname(vo.getUserRealName());

        if (!StringUtils.isBlank(vo.getPassword())){
            String str=PasswordUtil.contrastPassword(manager.getUserName(),vo.getPassword()).toString();
            manager.setUserPwd(str);
        }
        int count=managerMapper.updateByPrimaryKeySelective(manager);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void deleteManagerInfo(Long userId, Map<String, Object> data) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        String id=data.get("id").toString();
        Manager manager=managerMapper.selectByPrimaryKey(id);
        if (manager==null){
            throw new CustomerException(ExceptionEnum.INVALID_USER_NOT_FOND);
        }
        manager.setFlag(-1);
        manager.setLastModifyId(userId);
        manager.setLastModifyTime(new Date());
        int count=managerMapper.updateByPrimaryKeySelective(manager);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void insertManagerInfo(Long userId, ManagerUserVO vo) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        if (vo==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        Manager manager=new  Manager();
        manager.setFlag(0);
        manager.setLastModifyId(userId);
        manager.setLastModifyTime(new Date());
        manager.setCreateId(userId);
        manager.setCreateTime(new Date());
        UserConf conf=configService.selectCmsValue(ConfigkeyConstant.BOSS_USER_DEFAULT_PORTRAIT);
        if (conf!=null){
            manager.setUserPortrait(conf.getConfValue());
        }
        manager.setUserName(vo.getUserName());
        manager.setUserRealname(vo.getUserRealName());
        manager.setUserEmail(vo.getMailbox());
        manager.setUserMobile(vo.getMobilePhone());
        manager.setRoleId(vo.getRoleId());
        if (!StringUtils.isBlank(vo.getPassword())){
            String str=PasswordUtil.contrastPassword(manager.getUserName(),vo.getPassword()).toString();
            manager.setUserPwd(str);
        }
        UserConf confPWD=configService.selectCmsValue(ConfigkeyConstant.BOSS_USER_DEFAULT_LOGIN_PWD);
        if (confPWD!=null){
            manager.setUserPortrait(conf.getConfValue());
        }
        int count=managerMapper.insertSelective(manager);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public List<Role> getRoleList(Long userId, String roleName) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        Role role=new Role();
        role.setRoleStatus(0);
        if (!StringUtils.isBlank(roleName)){
            role.setRoleName(roleName);
        }
        List<Role> list=roleMapper.select(role);
        return list;
    }

    @Override
    public void updateRoleInfo(Long userId, Map<String, Object> data) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        String  id=data.get("id").toString();
        Role role=new Role();
        role.setId(id);
        role=roleMapper.selectOne(role);
        if (role==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        int count=roleMapper.updateByPrimaryKeySelective(role);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void deleteRoleInfo(Long userId, Map<String, Object> data) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        String  id=data.get("id").toString();
        Role role=new Role();
        role.setId(id);
        role=roleMapper.selectOne(role);
        if (role==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        role.setRoleStatus(-1);
        role.setLastModifyId(userId);
        role.setLastModifyTime(new Date());
        int count=roleMapper.updateByPrimaryKeySelective(role);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void insertRoleInfo(Long userId, Map<String, Object> data) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        String  roleName=data.get("roleName").toString();
        if (StringUtils.isBlank(roleName)){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        Role role=new Role();
        role.setRoleStatus(0);
        role.setLastModifyId(userId);
        role.setLastModifyTime(new Date());
        role.setCreateId(userId);
        role.setCreateTime(new Date());
        role.setRoleName(roleName);
        int count=roleMapper.updateByPrimaryKeySelective(role);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }

    }

    @Override
    public void managerResetPassword(Long userId, Map<String, Object> data) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        Long id=Long.valueOf(data.get("id").toString());
        Manager manager=managerMapper.selectByPrimaryKey(id);
        if(manager==null){
            throw new CustomerException(ExceptionEnum.INVALID_USER_NOT_FOND);
        }

        UserConf confPWD=configService.selectCmsValue(ConfigkeyConstant.BOSS_USER_DEFAULT_LOGIN_PWD);
        if (confPWD!=null){
            String str=PasswordUtil.contrastPassword(manager.getUserName(),confPWD.getConfValue()).toString();
            manager.setUserPwd(str);
        }
        int count=managerMapper.updateByPrimaryKeySelective(manager);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public PageInfo getProgramList(Long userId, Integer pageNo, Integer pageSize) {
        /*if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }*/
        PageHelper.startPage(pageNo,pageSize);
        Program program=new Program();
        program.setFlag(0);
        List<Program> list=programMapper.select(program);
        return new PageInfo(list);
    }

    @Override
    public void updateProgramList(Long userId, Program program) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        Program programInfo=programMapper.selectByPrimaryKey(program.getId());
        if (programInfo==null){
            throw new CustomerException(ExceptionEnum.CONFIG_NOT_FOND);
        }
        programInfo.setLastModifyId(userId);
        programInfo.setLastModifyTime(new Date());
        int count=programMapper.updateByPrimaryKeySelective(programInfo);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void deleteProgramList(Long userId, Map<String, Object> data) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        List<String> ids= (List<String>) data.get("ids");
        Program programInfo=programMapper.selectByPrimaryKey(ids.get(0));
        if (programInfo==null){
            throw new CustomerException(ExceptionEnum.CONFIG_NOT_FOND);
        }
        programInfo.setFlag(-1);
        programInfo.setLastModifyId(userId);
        programInfo.setLastModifyTime(new Date());
        int count=programMapper.updateByPrimaryKeySelective(programInfo);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void insertProgramList(Long userId, Program program) {
        if (userId==null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        Program programInfo=new Program();
        programInfo.setLastModifyId(userId);
        programInfo.setLastModifyTime(new Date());
        int count=programMapper.insertSelective(programInfo);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }
}
