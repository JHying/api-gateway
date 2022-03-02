package tw.hyin.demo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import tw.hyin.demo.pojo.JwtPayload;
import tw.hyin.demo.utils.encryption.GenerateKeyUtil;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

/**
 * JWT 相關工具
 *
 * @author H-yin
 */
@Component // 透過配置文件給 static 型態賦值，宣告成 component
public class JwtUtil {

    private static final long EXPIRED_TIME = 300000;//5 分鐘
    private static final String SECRET = GenerateKeyUtil.KEY;//一般密文
    private static final String JWT_PAYLOAD_USER_KEY = "user";
    public static final String PREFIX = "Bearer";//Token 前綴
    public static final String HEADER_KEY = "Authorization";//存放 Token 的 Header Key

    /**
     * JWT token 生成
     *
     * @param userInfo 使用者資料
     */
    public static String generateToken(Object userInfo) throws JsonProcessingException {
        // 生成 token
        Map<String, Object> claims = new HashMap<>();
        claims.put(JWT_PAYLOAD_USER_KEY, JsonUtil.objToJson(userInfo));
        return Jwts.builder()
                //保存權限（角色）
                .setClaims(claims)
                //建立唯一識別
                .setId(createJTI())
                //有效期設置
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRED_TIME))
                //簽名設置
                .signWith(SignatureAlgorithm.HS256, SECRET).compact();//用密文來做雜湊運算
    }

    /**
     * 以私鑰生成 token
     *
     * @param userInfo   使用者資料 (將存於 payload)
     * @param privateKey 私鑰
     * @return token
     */
    public static String generateToken(Object userInfo, PrivateKey privateKey) throws Exception {
        // 生成 token
        Map<String, Object> claims = new HashMap<>();
        claims.put(JWT_PAYLOAD_USER_KEY, JsonUtil.objToJson(userInfo));
        return Jwts.builder()
                .setClaims(claims)
                .setId(createJTI())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRED_TIME))
                .signWith(SignatureAlgorithm.RS256, privateKey)//以非對稱的方法來簽證token
                .compact();
    }

    /**
     * 根據密文獲取 payload 資料
     */
    public static <T> JwtPayload<T> getPayload(String token, Class<T> userType) throws IOException {
        return restorePayload(parseToken(token), userType);
    }

    /**
     * 根據公鑰獲取 payload 資料
     */
    public static <T> JwtPayload<T> getPayload(String token, PublicKey publicKey, Class<T> userType) throws IOException {
        return restorePayload(parseToken(token, publicKey), userType);
    }

    /**
     * 解析 token
     */
    private static Claims parseToken(String token) {
        return Jwts.parser()
                // 驗簽
                .setSigningKey(SECRET)
                // 去掉 Bearer
                .parseClaimsJws(token.replace(PREFIX, ""))
                .getBody();
    }

    /**
     * 使用公鑰解析 token
     */
    private static Claims parseToken(String token, PublicKey publicKey) {
        return Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(token.replace(PREFIX, ""))
                .getBody();
    }

    /**
     * 取得 payload
     */
    private static <T> JwtPayload<T> restorePayload(Claims claims, Class<T> userType) throws IOException {
        if (!claims.isEmpty()) {
            JwtPayload<T> payload = new JwtPayload<>();
            payload.setId(claims.getId());
            payload.setUserInfo(JsonUtil.jsonToBean(claims.get(JWT_PAYLOAD_USER_KEY).toString(), userType));
            payload.setExpiration(claims.getExpiration());
            return payload;
        } else {
            return null;
        }
    }

    /**
     * 建立 jwt 的唯一身份標識 (JWT ID)，避免 replay attack
     *
     * @return jti
     */
    private static String createJTI() {
        return new String(Base64.getEncoder().encode(UUID.randomUUID().toString().getBytes()));
    }
}
