package com.tangchao.user.service;

import com.github.pagehelper.PageInfo;
import com.tangchao.shop.dto.adminDTO.ManagerUserDTO;
import com.tangchao.shop.pojo.Program;
import com.tangchao.shop.pojo.Role;
import com.tangchao.shop.vo.adminVo.ManagerUserVO;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/10/28 13:41
 */
public interface ManagerService {

    /**
     * 生成验证码
     * @return
     */
    String getKaptchaImage() throws IOException;

    /**
     * 后台用户登录
     * @param userDTO
     * @return
     */
    ManagerUserVO managerLogin(ManagerUserDTO userDTO, HttpServletRequest request);

    Map<String,Object> index(String userName);

    ManagerUserVO selectUserInfo(Long userCode);

    void managerLogout(Long userCode);


    PageInfo getManagerList(Long userId, String userName, Integer roleId, Integer pageNo, Integer pageSize);

    void updateManagerInfo(Long userId, ManagerUserVO vo);

    void deleteManagerInfo(Long userId, Map<String, Object> data);

    void insertManagerInfo(Long userId, ManagerUserVO vo);

    List<Role> getRoleList(Long userId, String roleName);

    void updateRoleInfo(Long userId, Map<String, Object> data);

    void deleteRoleInfo(Long userId, Map<String, Object> data);

    void insertRoleInfo(Long userId, Map<String, Object> data);

    void managerResetPassword(Long userId, Map<String, Object> data);

    PageInfo getProgramList(Long userId, Integer pageNo, Integer pageSize);

    void updateProgramList(Long userId, Program program);

    void deleteProgramList(Long userId, Map<String, Object> data);

    void insertProgramList(Long userId, Program program);
}
