package com.aoyang.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

/**
 * @Author went
 * @Date 2020/4/28 10:59
 * @Version 1.0
 */
public class JwtUtil {

    //秘钥， 不可泄露
    private final static String BASE_64_ENCODED_SECRET_KEY = "S!eTDGJqLnIVF^B0";

    private static final String USER_KEY = "userId";

    //过期时间设置为2小时
    private static final long TOKEN_EXP = 2 * 3600000;

    /**
     * 生成token
     *
     * @param userId
     * @return 返回token
     */
    public static String createToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .claim(USER_KEY, userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXP))
                .signWith(SignatureAlgorithm.HS256, BASE_64_ENCODED_SECRET_KEY)
                .compact();
    }

    /**
     * 解析token
     *
     * @param token
     * @return 返回用户Id
     */
    public static String checkToken(String token) {
        final Claims claims = Jwts.parser().setSigningKey(BASE_64_ENCODED_SECRET_KEY).parseClaimsJws(token).getBody();
        return (String) claims.get(USER_KEY);
    }
}
