package com.tangchao.shop.config;

import com.tangchao.shop.annotation.support.LoginUserHandlerMethodArgumentResolver;
import com.tangchao.shop.interceptor.UserInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/10/29 11:42
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    protected final static Logger logger = LoggerFactory.getLogger(CorsConfig.class);

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
                .maxAge(3600)
                .allowCredentials(true);
        logger.info("*********************************跨域过滤器**************************");
    }

    @Autowired
    private JwtProperties prop;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new LoginUserHandlerMethodArgumentResolver(prop,redisTemplate));
    }
}
