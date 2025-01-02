package com.example.cocktail;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 定義密鑰和有效期
    private final String secret = "yourSuperSecretKeyHereThatIsVeryLong";
    private final long expiration = 86400000; // Token期限24小時
    private final Key key = Keys.hmacShaKeyFor(secret.getBytes());

    // 生成Token
    public String generateToken(String userAccount, String role) {
        String token = Jwts.builder()
                .setSubject(userAccount)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // 過期時間
                .signWith(key, SignatureAlgorithm.HS256) // 使用密鑰和算法簽名
                .compact();//生成Token字串
        return token;
    }

    // 提取用戶名
    public String extractAccount(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject(); // 提取主體（用戶名）
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("無法提取用戶名，Token 無效或已過期");
        }
    }

    // 提取角色
    public String extractRole(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String role = claims.get("role", String.class);
            if (role == null) {
                throw new RuntimeException("Token 中未包含角色");
            }
            return role;
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("無法提取角色，Token 無效或已過期：" + e.getMessage());
        }
    }


    // 驗證 Token 並提取所有聲明（Claims）
    public Claims validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key) // 設置密鑰
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims;
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Token 無效或已過期：" + e.getMessage());
        }
    }
}
