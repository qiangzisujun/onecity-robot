package com.tangchao.shop.annotation.support;

import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.shop.annotation.LoginUser;
import com.tangchao.shop.config.JwtProperties;
import com.tangchao.shop.pojo.UserInfo;
import com.tangchao.shop.utils.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;


public class LoginUserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    public static final String LOGIN_TOKEN_KEY = "token";

    private static final String KEY_PREFIX = "user:logout:token";

    private JwtProperties prop;

    private StringRedisTemplate redisTemplate;

    public LoginUserHandlerMethodArgumentResolver(JwtProperties prop, StringRedisTemplate redisTemplate) {
        this.prop = prop;
        this.redisTemplate=redisTemplate;
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
        UserInfo user=null;
        try{
            user = JwtUtils.getUserInfo(prop.getPublicKey(), token);
            String key=KEY_PREFIX+user.getName();
            String jwt = redisTemplate.opsForValue().get(key);
            if (StringUtils.isBlank(jwt)){
                return null;
            }
            return user.getUserCode();
        }catch (Exception e){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
    }

}
