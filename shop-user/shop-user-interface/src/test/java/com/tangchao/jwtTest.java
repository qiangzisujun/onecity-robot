package com.tangchao;

import com.tangchao.shop.pojo.UserInfo;
import com.tangchao.shop.utils.JwtUtils;
import com.tangchao.shop.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.URL;
import java.security.PrivateKey;
import java.security.PublicKey;

public class jwtTest {
    private static final String publicKeyPath = "E:\\test\\rsa.pub";
    private static final String privateKeyPath = "E:\\test\\rsa.pri";

    private PrivateKey privateKey;
    private PublicKey publicKey;


    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(publicKeyPath, privateKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        privateKey = RsaUtils.getPrivateKey(privateKeyPath);
        publicKey = RsaUtils.getPublicKey(publicKeyPath);
    }

    @org.junit.Test
    public void generateToken() {
        //生成Token
        String s = JwtUtils.generateToken(new UserInfo(20L, "Jack"), privateKey);
        System.out.println("s = " + s);
    }


    @org.junit.Test
    public void parseToken() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOiI1IiwiaWF0IjoxNTY5NDYzNTM3LCJleHAiOjE1Njk2Njg3MzcsIm5iZiI6MTU2OTQ2MzUzNywianRpIjoiMTIwNzFmY2UzY2M2MWI2NTM5ZDM4YzE4YWRiNDBkNDEifQ.AbJtAkrHFiIr3ppQcCSWCm89ZbE_YrLn3nRUCtCTot0";        UserInfo userInfo = JwtUtils.getUserInfo(publicKey, token);
        System.out.println("id:" + userInfo.getUserCode());
        System.out.println("name:" + userInfo.getName());
    }

    @org.junit.Test
    public void parseToken1() {
    }

    @org.junit.Test
    public void getUserInfo() {
    }

    @org.junit.Test
    public void getUserInfo1() {
    }
}
