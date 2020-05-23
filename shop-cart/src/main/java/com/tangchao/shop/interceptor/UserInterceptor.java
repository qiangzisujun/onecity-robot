package com.tangchao.shop.interceptor;

import com.tangchao.shop.config.JwtProperties;
import com.tangchao.shop.pojo.UserInfo;
import com.tangchao.shop.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class UserInterceptor implements HandlerInterceptor {
    private static final String LOGIN_TOKEN_KEY = "Authorization";

    private JwtProperties prop;

    private static final ThreadLocal<UserInfo> tl = new ThreadLocal<>();

    public UserInterceptor(JwtProperties prop) {
        this.prop = prop;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取请求头部中的token
        String token = request.getHeader(LOGIN_TOKEN_KEY);
        try {
            String url = request.getRequestURL().toString();
            if (!StringUtils.isBlank(token) && url.indexOf("/user/login") < 0) {
                UserInfo user = JwtUtils.getUserInfo(prop.getPublicKey(), token);
                //传递user
                tl.set(user);
            }
            //放行
            return true;
        } catch (Exception e) {
            log.error("[购物车服务] 解析用户身份失败.", e);
            //throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
            return true;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //最后用完数据一定要清空
        tl.remove();
    }

    public static UserInfo getUserInfo() {
        return tl.get();
    }
}
