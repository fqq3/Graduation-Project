package com.fqq.futurecommunity.system.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fqq.futurecommunity.common.constant.Constants;
import com.fqq.futurecommunity.common.utils.RedisCache;
import com.fqq.futurecommunity.common.utils.UUIDUtils;
import com.fqq.futurecommunity.system.domain.LoginUser;
import com.fqq.futurecommunity.system.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import io.jsonwebtoken.security.Keys;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * @author: fqq
 * @Date: 2026/4/11 - 04 - 11 - 23:18
 * @Description: com.fqq.futurecommunity.system.service.impl
 * @version: 1.0
 */

/**
 * Token处理器
 **/
@Component
public class TokenServiceImpl implements TokenService {

    private static final String TOKEN_COOKIE = "Admin-Token";

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ObjectMapper objectMapper;

    //令牌自定义标识
    @Value("${token.header}")
    private String header;

    //令牌秘钥
    @Value("${token.secret}")
    private String secret;

    //令牌有效期
    @Value("${token.expireTime}")
    private int expireTime;

    //毫秒
    private static final long MILLIS_SECOND = 1000;

    //分钟
    private static final long MILLIS_MINUTE = MILLIS_SECOND * 60;

    //20分钟
    private static final long MILLIS_MINUTE_TEN = MILLIS_SECOND * 60 * 20;

    /**
     * 创建令牌
     * @param loginUser
     * @return: java.lang.String
     */
    @Override
    public String createToken(LoginUser loginUser) {

        //设置用户的唯一标识
        String userKey = UUIDUtils.randomUUID();
        loginUser.setToken(userKey);

        //todo 保存用户信息 刷新令牌
        refreshToken(loginUser);

        HashMap<String, Object> claims = new HashMap<>();
        claims.put(Constants.LOGIN_USER_KEY,userKey);
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return token;
    }

    @Override
    public void refreshToken(LoginUser loginUser) {

        loginUser.setLoginTime(System.currentTimeMillis());
        //过期时间30分钟
        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE);

        //根据UUID将loginUser缓存
        String userKey = getTokenKey(loginUser.getToken());
        redisCache.setCacheObject(userKey,loginUser,expireTime, TimeUnit.MINUTES);
    }

    @Override
    public LoginUser getLoginUser(HttpServletRequest request) {
        String token = getToken(request);
        if(!StringUtils.isEmpty(token)){
            Claims claims = parseToken(token);
            //解析对应的用户信息
            String uuid = (String) claims.get(Constants.LOGIN_USER_KEY);
            String userKey = getTokenKey(uuid);
            Object cacheObject = redisCache.getCacheObject(userKey);
            if (cacheObject == null) {
                return null;
            }
            if (cacheObject instanceof LoginUser loginUser) {
                return loginUser;
            }
            return objectMapper.convertValue(cacheObject, LoginUser.class);
        }
        return null;
    }

    @Override
    public void verifyToken(LoginUser loginUser) {
        Long expireTime = loginUser.getExpireTime();
        long currentTimeMillis = System.currentTimeMillis();
        //相差不足20分钟,自动刷新缓存
        if(expireTime - currentTimeMillis <= MILLIS_MINUTE_TEN){
            refreshToken(loginUser);
        }
    }

    @Override
    public void setLoginUser(LoginUser loginUser) {
        if(!Objects.isNull(loginUser) && !StringUtils.isEmpty(loginUser.getToken())){
            refreshToken(loginUser);
        }
    }

    @Override
    public void delLoginUser(String token) {
        if(!StringUtils.isEmpty(token)){
            String userKey = getTokenKey(token);
            redisCache.deleteObject(userKey);
        }
    }

    /**
     * 从令牌中获取数据声明
     * @param token
     * @return: java.lang.String
     */
    private Claims parseToken(String token){
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }

    //拼接tokenKey
    private String getTokenKey(String uuid){
        return Constants.LOGIN_TOKEN_KEY + uuid;
    }

    /**
     * 从请求头中获取 token
     * @param request
     * @return: java.lang.String
     */
    private String getToken(HttpServletRequest request){
        String token = request.getHeader(this.header);
        if (!StringUtils.hasText(token) && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (TOKEN_COOKIE.equals(cookie.getName()) && StringUtils.hasText(cookie.getValue())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        //JWT 标准写法 Authorization: Bearer aaa.bb.cc
        if(!StringUtils.isEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX)){
            token = token.replace(Constants.TOKEN_PREFIX,"");
        }
        return token;
    }


}

//
//@Component
//public class TokenServiceImpl implements TokenService {
//
//    @Autowired
//    private RedisCache redisCache;
//
//    // 令牌自定义标识
//    @Value("${token.header}")
//    private String header;
//
//    // 令牌秘钥
//    @Value("${token.secret}")
//    private String secret;
//
//    // 令牌有效期（默认30分钟）
//    @Value("${token.expireTime}")
//    private int expireTime;
//
//    //毫秒
//    private static final long MILLIS_SECOND = 1000;
//
//    //分钟
//    private static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;
//
//    //20分钟
//    private static final Long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;
//
//    /**
//     * 创建令牌
//     * @param loginUser
//     * @return: java.lang.String
//     */
//    @Override
//    public String createToken(LoginUser loginUser) {
//
//        //设置唯一用户标识
//        String userKey = UUIDUtils.randomUUID();
//        loginUser.setToken(userKey);
//
//        //Ttodo 刷新令牌保护用户信息
//        refreshToken(loginUser);
//
//        Map<String, Object> claims = new HashMap<>();
//        claims.put(Constants.LOGIN_USER_KEY, userKey);
//        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
//
//
//        //创建token, 将用户唯一标识 通过setClaims方法 保存到token中
//        String token = Jwts.builder()
//                .setClaims(claims)
//                .signWith(key,SignatureAlgorithm.HS512)
//                .compact();
//
//        return token;
//    }
//
//    /**
//     * 缓存用户信息&刷新令牌有效期
//     * @param loginUser
//     */
//    @Override
//    public void refreshToken(LoginUser loginUser) {
//        loginUser.setLoginTime(System.currentTimeMillis());
//        //过期时间30分钟
//        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE );
//        // 根据uuid将loginUser缓存
//        String userKey = getTokenKey(loginUser.getToken());
//        redisCache.setCacheObject(userKey,loginUser, Duration.ofMinutes(expireTime));//expireTime, TimeUnit.MINUTES
//    }
//
//    //拼接tokenkey
//    private String getTokenKey(String uuid) {
//        return Constants.LOGIN_TOKEN_KEY + uuid;
//    }
//    /**
//     * 从request的请求头中 获取token
//     * @param request
//     * @return: java.lang.String
//     */
//    private String getToken(HttpServletRequest request){
//
//        String token = request.getHeader(this.header);
//        if(!StringUtils.isEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX)){
//            token = token.replace(Constants.TOKEN_PREFIX,"");
//        }
//
//        return token;
//    }
//    /**
//     * 从Redis获取用户身份信息
//     * @param request
//     */
//    @Override
//    public LoginUser getLoginUser(HttpServletRequest request) {
//
//
//        //获取请求携带的token
//        String token = getToken(request);
//        if(!StringUtils.isEmpty(token)){
//            Claims claims = parseToken(token);
//            //解析对应的用户信息和权限信息
//            String uuid =(String) claims.get(Constants.LOGIN_USER_KEY);
//            String userKey = getTokenKey(uuid);
//            LoginUser loginUser =JSON.parseObject(
//                    JSON.toJSONString(redisCache.getCacheObject(userKey)),
//                    LoginUser.class);
//            return loginUser;
//        }
//
//        return null;
//    }
//
//    /**
//     * 从令牌中获取数据声明
//     *
//     * @param token 令牌
//     * @return 数据声明
//     */
//    private Claims parseToken(String token)
//    {
//        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
//
//        return Jwts.parser()
//                .setSigningKey(key)
//                .parseClaimsJws(token)
//                .getBody();
//    }
//    /**
//     * 验证令牌有效期,相差不足20分钟,自动刷新缓存
//     * @param loginUser
//     */
//    @Override
//    public void verifyToken(LoginUser loginUser){
//        Long expireTime = loginUser.getExpireTime();
//        long currentTimeMillis = System.currentTimeMillis();
//        if(expireTime - currentTimeMillis <= MILLIS_MINUTE_TEN){
//            refreshToken(loginUser);
//        }
//    }
//    /**
//     * 设置用户身份信息
//     */
//    @Override
//    public void setLoginUser(LoginUser loginUser){
//        if(!Objects.isNull(loginUser) && !StringUtils.isEmpty(loginUser.getToken())){
//            refreshToken(loginUser);
//        }
//    }
//
//    /**
//     * 删除用户身份信息
//     */
//    @Override
//    public void delLoginUser(String token){
//        if(!StringUtils.isEmpty(token)){
//            String userKey = getTokenKey(token);
//            redisCache.deleteObject(userKey);
//        }
//    }
//
//
//}


