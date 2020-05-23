package com.tangchao.shop.config;


import com.tangchao.shop.utils.RsaUtils;
import lombok.Data;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.security.PublicKey;

@Data
@Configuration
public class JwtProperties {

    private String pubKeyPath;
    private PublicKey publicKey;//公钥


    //类一旦实例化后，就应该读取公钥和私钥
    @PostConstruct
    public void init() throws Exception {
        URL pubPath = this.getClass().getClassLoader().getResource("rsa.pub");
        pubKeyPath = pubPath.getPath().substring(1, pubPath.getPath().length());
        //读取公钥和私钥
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
    }
}
