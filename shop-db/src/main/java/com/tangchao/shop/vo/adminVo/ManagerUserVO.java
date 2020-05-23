package com.tangchao.shop.vo.adminVo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/10/28 14:00
 */
@Data
@ApiModel
public class ManagerUserVO implements Serializable {

    private List<String> menuUrlList = new ArrayList<>(0);

    //  权限列表
    private List<String> authorityList = new ArrayList<>(0);

    //  用户名
    private String userName;

    //  用户头像
    private String userPortrait;

    private String token;

    //用户标识
    private Long id;

    //真实姓名
    private String userRealName;

    //邮箱
    private String mailbox;

    //电话
    private String mobilePhone;

    //角色名称
    private String roleName;

    //密码
    private String password;

    //角色Id
    private String roleId;

}
