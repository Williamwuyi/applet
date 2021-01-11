package com.jiebao.platfrom.common.authentication;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiebao.platfrom.common.domain.ActiveUser;
import com.jiebao.platfrom.common.domain.JiebaoConstant;
import com.jiebao.platfrom.common.exception.RedisConnectException;
import com.jiebao.platfrom.common.service.RedisService;
import com.jiebao.platfrom.common.utils.*;
import com.jiebao.platfrom.system.domain.User;
import com.jiebao.platfrom.system.manager.UserManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * 自定义实现 ShiroRealm，包含认证和授权两大模块
 *
 * @author Jiebao.cn
 */
@Slf4j
public class ShiroRealm extends AuthorizingRealm {

    @Autowired
    private RedisService redisService;
    @Autowired
    private UserManager userManager;
    @Value("${jiebao.shiro.jwtTimeOut}")
    private int jwtTimeOut;
    @Autowired
    private ObjectMapper mapper;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    /**`
     * 授权模块，获取用户角色和权限
     *
     * @param token token
     * @return AuthorizationInfo 权限信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection token) {
        String username = JWTUtil.getUsername(token.toString());

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        // 获取用户角色集
        Set<String> roleSet = userManager.getUserRoles(username);
        simpleAuthorizationInfo.setRoles(roleSet);

        // 获取用户权限集
        Set<String> permissionSet = userManager.getUserPermissions(username);
        simpleAuthorizationInfo.setStringPermissions(permissionSet);
        return simpleAuthorizationInfo;
    }

    /**
     * 用户认证
     *
     * @param authenticationToken 身份认证 token
     * @return AuthenticationInfo 身份认证信息
     * @throws AuthenticationException 认证相关异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // 这里的 token是从 JWTFilter 的 executeLogin 方法传递过来的，已经经过了解密
        String token = (String) authenticationToken.getCredentials();

        // 从 redis里获取这个 token
        HttpServletRequest request = HttpContextUtil.getHttpServletRequest();
        String ip = IPUtil.getIpAddr(request);

        String encryptToken = JiebaoUtil.encryptToken(token);
        String encryptTokenInRedis = null;
        try {
            encryptTokenInRedis = redisService.get(JiebaoConstant.TOKEN_CACHE_PREFIX + encryptToken + "." + ip);
        } catch (Exception ignore) {
        }
        // 如果找不到，说明已经失效
        if (StringUtils.isBlank(encryptTokenInRedis))
            throw new AuthenticationException("token已经过期");

        String username = JWTUtil.getUsername(token);

        if (StringUtils.isBlank(username))
            throw new AuthenticationException("token校验不通过");

        try {
            redisService.set(JiebaoConstant.TOKEN_CACHE_PREFIX + encryptToken + StringPool.DOT + ip, encryptToken, jwtTimeOut * 1000L);
            LocalDateTime expireTime = LocalDateTime.now().plusSeconds(jwtTimeOut);
            String expireTimeStr = DateUtil.formatFullTime(expireTime);
            updateZrem(username, ip, expireTimeStr);
        } catch (RedisConnectException e) {
            log.warn("ShiroRealm：Refresh user token exception");
        } catch (IOException e) {
            log.warn("ShiroRealm：Refresh user zrem exception");
        }

        // 通过用户名查询用户信息
        User user = userManager.getUser(username);

        if (user == null)
            throw new AuthenticationException("用户名或密码错误");
        if (!JWTUtil.verify(token, username, user.getPassword()))
            throw new AuthenticationException("token校验不通过");
        return new SimpleAuthenticationInfo(token, token, "jiebao_shiro_realm");
    }

    /**
     * 更新在线用户有序集合
     * @param username
     * @param ip
     * @param expireTimeStr
     * @throws RedisConnectException
     * @throws IOException
     */
    private void updateZrem (String username, String ip, String expireTimeStr) throws RedisConnectException, IOException {
        String now = DateUtil.formatFullTime(LocalDateTime.now());
        Set<String> userOnlineStringSet = redisService.zrangeByScore(JiebaoConstant.ACTIVE_USERS_ZSET_PREFIX, now, "+inf");
        ActiveUser kickoutUser = null;
        String kickoutUserString = "";
        for (String userOnlineString : userOnlineStringSet) {
            ActiveUser activeUser = mapper.readValue(userOnlineString, ActiveUser.class);
            if (StringUtils.equals(activeUser.getUsername(), username) && StringUtils.equals(activeUser.getIp(), ip)) {
                kickoutUser = activeUser;
                kickoutUserString = userOnlineString;
            }
        }
        if (kickoutUser != null && StringUtils.isNotBlank(kickoutUserString)) {
            // 删除 zset中的记录
            redisService.zrem(JiebaoConstant.ACTIVE_USERS_ZSET_PREFIX, kickoutUserString);
            redisService.zadd(JiebaoConstant.ACTIVE_USERS_ZSET_PREFIX, Double.valueOf(expireTimeStr), mapper.writeValueAsString(kickoutUser));
        }

    }

}
