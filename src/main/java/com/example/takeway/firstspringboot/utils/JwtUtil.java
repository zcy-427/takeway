package com.example.takeway.firstspringboot.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class JwtUtil {
    private static final String SECRET_KEY = "my_secret_key"; // 这个密钥应该放在安全的地方，且不要硬编码在代码中
    //生成JWT令牌，里面包含用户ID和过期时间
    public static String createToken(Long userId) {
        // 使用 HMAC256 算法和我们的机密密码生成钢印
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

        return JWT.create()
                .withClaim("userId", userId) // 把用户ID刻在手环上
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 设定手环有效期：24小时
                .sign(algorithm); // 盖上防伪钢印！
    }

    //验证JWT令牌是否合法，并提取用户ID
    public static Long getUserIdFromToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            DecodedJWT jwt = JWT.require(algorithm).build().verify(token); // 验证钢印
            return jwt.getClaim("userId").asLong(); // 把刻在手环上的用户ID读出来
        } catch (Exception e) {
            throw new RuntimeException("手环是假的或已过期！保安：给我打出去！");
        }
    }
}
