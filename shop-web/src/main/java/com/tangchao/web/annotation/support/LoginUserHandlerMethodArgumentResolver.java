package com.tangchao.web.annotation.support;

import com.tangchao.shop.config.JwtProperties;
import com.tangchao.shop.pojo.UserInfo;
import com.tangchao.shop.utils.JwtUtils;
import com.tangchao.web.annotation.LoginUser;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;


public class LoginUserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    public static final String LOGIN_TOKEN_KEY = "Authorization";

    private JwtProperties prop;

    public LoginUserHandlerMethodArgumentResolver(JwtProperties prop) {
        this.prop = prop;
    }


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean flag = parameter.hasParameterAnnotation(LoginUser.class);
        return flag;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer container,
                                  NativeWebRequest request, WebDataBinderFactory factory) throws Exception {

        String token = request.getHeader(LOGIN_TOKEN_KEY);
        if (token == null || token.isEmpty()) {
            return null;
        }
        UserInfo user = JwtUtils.getUserInfo(prop.getPublicKey(), token);
        return user.getUserCode();
    }

}
