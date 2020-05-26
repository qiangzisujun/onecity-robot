package com.tangchao.common.utils;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/10/30 20:40
 */
public class PasswordUtil {

    public static Object contrastPassword(String userName,String password){
        String hashAlgorithmName = "MD5";
        int hashIterations = 1024;
        ByteSource credentialsSalt = ByteSource.Util.bytes(userName);
        Object obj = new SimpleHash(hashAlgorithmName, password, credentialsSalt, hashIterations);
        return obj;
    }

    public static void main(String[] args) {
        System.out.println(contrastPassword("admin","onecity2020"));
    }
}
