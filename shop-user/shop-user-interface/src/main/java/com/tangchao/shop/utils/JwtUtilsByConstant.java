package com.tangchao.shop.utils;

import com.tangchao.shop.constants.JwtConstants;
import com.tangchao.shop.pojo.UserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.joda.time.DateTime;

import java.util.Date;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/12/30 16:14
 */
public class JwtUtilsByConstant {

    /**
     * 生成Token
     *
     * @param userInfo
     * @return
     */
    public static String generateToken(UserInfo userInfo) {
        return Jwts.builder()
                .claim(JwtConstants.JWT_KEY_ID, userInfo.getUserCode())
                .claim(JwtConstants.JWT_KEY_USER_NAME, userInfo.getName())
                .setExpiration(DateTime.now().plusSeconds(30).toDate())
                .signWith(SignatureAlgorithm.HS512, JwtConstants.SECRET)
                .compact();
    }


    /**
     * 公钥解析Token
     *
     * @param token
     * @return
     * @throws Exception
     */
    public static Claims parseToken(String token){
        return Jwts.parser().setSigningKey(JwtConstants.SECRET).parseClaimsJws(token).getBody();
    }



    /**
     * 从Token中获取用户信息
     * @param token
     * @return
     * @throws Exception
     */
    public static UserInfo getUserInfo(String token){
        Claims claimsJws = parseToken(token);
        return new UserInfo(
                ObjectUtils.toLong(claimsJws.get(JwtConstants.JWT_KEY_ID)),
                ObjectUtils.toString(claimsJws.get(JwtConstants.JWT_KEY_USER_NAME))
        );
    }


    /**
     * 判断令牌是否过期
     *
     * @param token 令牌
     * @return 是否过期
     */
    public static Boolean isTokenExpired(String token) {
        try {
            Claims claimsJws = parseToken(token);
            Date expiration = claimsJws.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) throws Exception {
        //生成Token
        //String s = generateToken(new UserInfo(20L, "Jack"));
       // System.out.println("s = " + s);
        UserInfo user=getUserInfo("eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MTA3NDgwNzQzNzI5MTcxNzIzNCwidXNlcm5hbWUiOiIxODMqKioqMjM3NyIsImV4cCI6MTU4MDI5Mzk0MH0.bNhsATr3RdJNApL7MynTK_bsVMxFCvMBCios4q6w6HlzK5jqpDX4_27u8_xIOCTVce5BupagSA6z5V_gpA9Ou_whtiyXAyDtrsqmL2af_W1pAnC7We2vpfUw8oMDRx-9aExvcgzPGWsJewc6rhS6zT3cM8JG2YGtilwuDVtpIRI");
        System.out.println(user.getName());
    }
}
