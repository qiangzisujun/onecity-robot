package com.tangchao.shop.utils;


import com.tangchao.shop.constants.JwtConstants;
import com.tangchao.shop.pojo.UserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.joda.time.DateTime;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author liuzhiqiang
 */
public class JwtUtils {


    /**
     * 生成Token
     *
     * @param userInfo
     * @param privateKey
     * @return
     */
    public static String generateToken(UserInfo userInfo, PrivateKey privateKey) {
        return Jwts.builder()
                .claim(JwtConstants.JWT_KEY_ID, userInfo.getUserCode())
                .claim(JwtConstants.JWT_KEY_USER_NAME, userInfo.getName())
                .setExpiration(DateTime.now().plusDays(JwtConstants.EXPIRE_DAYS).toDate())
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }

    /**
     * 生成Token
     *
     * @param userInfo
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String generateToken(UserInfo userInfo, byte[] privateKey) throws Exception {
        return Jwts.builder()
                .claim(JwtConstants.JWT_KEY_ID, userInfo.getUserCode())
                .claim(JwtConstants.JWT_KEY_USER_NAME, userInfo.getName())
                .setExpiration(DateTime.now().plus(JwtConstants.EXPIRE_DAYS).toDate())
                .signWith(SignatureAlgorithm.ES256, RsaUtils.getPrivateKey(privateKey))
                .compact();
    }

    /**
     * 公钥解析Token
     *
     * @param publicKey
     * @param token
     * @return
     */
    public static Jws<Claims> parseToken(PublicKey publicKey, String token) {
        return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);
    }


    /**
     * 公钥解析Token
     *
     * @param publicKey
     * @param token
     * @return
     * @throws Exception
     */
    public static Jws<Claims> parseToken(byte[] publicKey, String token) throws Exception {
        return Jwts.parser().setSigningKey(RsaUtils.getPublicKey(publicKey)).parseClaimsJws(token);
    }


    /**
     * 从Token中获取用户信息（使用公钥解析）
     *
     * @param publicKey
     * @param token
     * @return
     */
    public static UserInfo getUserInfo(PublicKey publicKey, String token) {
        Jws<Claims> claimsJws = parseToken(publicKey, token);
        Claims body = claimsJws.getBody();
        return new UserInfo(
                ObjectUtils.toLong(body.get(JwtConstants.JWT_KEY_ID)),
                ObjectUtils.toString(body.get(JwtConstants.JWT_KEY_USER_NAME))
        );
    }

    /**
     * 从Token中获取用户信息（使用公钥解析）
     *
     * @param publicKey
     * @param token
     * @return
     * @throws Exception
     */
    public static UserInfo getUserInfo(byte[] publicKey, String token) throws Exception {
        Jws<Claims> claimsJws = parseToken(publicKey, token);
        Claims body = claimsJws.getBody();
        return new UserInfo(
                ObjectUtils.toLong(body.get(JwtConstants.JWT_KEY_ID)),
                ObjectUtils.toString(body.get(JwtConstants.JWT_KEY_USER_NAME))
        );
    }
}
