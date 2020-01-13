package com.hxd.utils;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class TokenUtil {
    public static String creatToken(String username) {
        System.out.println(username);
        Date date = new Date();
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JWT") // 设置header
                .setHeaderParam("alg", "HS256").setIssuedAt(date) // 设置签发时间
                .setExpiration(new Date(date.getTime() + 1000 * 60 * 60))
                .claim("userName",username) // 设置内容
                .setIssuer("hxd")// 设置签发人
                .signWith(signatureAlgorithm, username.getBytes()); // 签名，需要算法和key
        //String token = builder.compact();

        return builder.compact();
    }
}
