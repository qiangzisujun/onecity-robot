package com.tangchao.shop.config;

import com.tangchao.shop.utils.RsaUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.URL;
import java.security.PrivateKey;
import java.security.PublicKey;

@Data
@ConfigurationProperties(prefix = "tangchao")
public class JwtProperties {

    private static final String SECRET = "tangchaokeji";
    private String pubKeyPath;
    private String priKeyPath;
    private int expire;

    private PublicKey publicKey;
    private PrivateKey privateKey;

    private static final Logger logger = LoggerFactory.getLogger(JwtProperties.class);

    //类一旦实例化后，就应该读取公钥和私钥
    @PostConstruct
    public void init() throws Exception {
        try {

            // System.out.println(String.valueOf(JwtProperties.class.getResourceAsStream("rsa.pub")));
            //公钥或私钥不存在，先生成
            File pubKey = new File(String.valueOf(JwtProperties.class.getResourceAsStream("rsa.pub")));
            File priKey = new File(priKeyPath);
            if (!pubKey.exists() || !priKey.exists()) {
                RsaUtils.generateKey(pubKeyPath, priKeyPath, SECRET);
            }

            //读取公钥和私钥
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
            this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
        } catch (Exception e) {
            logger.error("初始化公钥和私钥失败！", e);
            throw new RuntimeException();
        }

    }
}
