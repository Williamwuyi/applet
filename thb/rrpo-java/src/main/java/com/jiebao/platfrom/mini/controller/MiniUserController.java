package com.jiebao.platfrom.mini.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiebao.platfrom.common.authentication.JWTToken;
import com.jiebao.platfrom.common.authentication.JWTUtil;
import com.jiebao.platfrom.common.domain.ActiveUser;
import com.jiebao.platfrom.common.domain.JiebaoConstant;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.exception.RedisConnectException;
import com.jiebao.platfrom.common.properties.JiebaoProperties;
import com.jiebao.platfrom.common.service.RedisService;
import com.jiebao.platfrom.common.utils.*;
import com.jiebao.platfrom.mini.config.WxMaConfiguration;
import com.jiebao.platfrom.system.domain.LoginLog;
import com.jiebao.platfrom.system.domain.User;
import com.jiebao.platfrom.system.service.LoginLogService;
import com.jiebao.platfrom.system.service.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 小程序用户操作接口
 */

@RestController
@RequestMapping(value = "/mini/user")
@Api(tags = "小程序-用户登录及授权")
@Slf4j
public class MiniUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private LoginLogService loginLogService;

    @Autowired
    private JiebaoProperties properties;

    @Autowired
    private ObjectMapper mapper;

    /**
     * 小程序用户登录
     *
     * @param code
     * @return
     */
    @PostMapping("/login/{appid}")
    public JiebaoResponse Login(@PathVariable String appid, String code, HttpServletRequest request) {
        appid = "wx67029b2a97acdda1";
        if (StringUtils.isBlank(code)) {
            return new JiebaoResponse().message("jscode不能为空！").put("status", JiebaoConstant.STATUS_CODE_SUCCESS);
        }
        final WxMaService wxService = WxMaConfiguration.getMaService(appid);
        try {
            WxMaJscode2SessionResult session = wxService.getUserService().getSessionInfo(code);
            String sessionKey = session.getSessionKey();
            String openid = session.getOpenid();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("openid", openid);
            jsonObject.put("sessionKey", sessionKey);
            //TODO 可以增加自己的逻辑，关联业务相关数据
            User user = userService.getByOpenid(openid);
            if (null == user) { //用户不存在
                String sk = UUID.randomUUID().toString().replace("-", "");
                redisService.set(JiebaoConstant.MINI_LOGIN_TEMP_TOKEN + sk, jsonObject.toJSONString(), 900 * 1000L);
                return new JiebaoResponse().message("用户未注册！").put("status", JiebaoConstant.STATUS_CODE_NOT_REGISTRY).put("sk", sk).put("userInfo", null);
            } else {    //用户已注册
                return this.attest(user, request);
            }
        } catch (WxErrorException | RedisConnectException e) {
            log.error(e.getMessage(), e);
            return new JiebaoResponse().message("登录异常，请重试！" + JiebaoConstant.STATUS_CODE_ERROR);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new JiebaoResponse().message("登录异常，请重试！" + JiebaoConstant.STATUS_CODE_ERROR);
        }
    }

    @PostMapping(value = "/quickLogin/{appid}")
    public JiebaoResponse quickLogin(@PathVariable String appid, String sk, String encryptedData, String iv, HttpServletRequest request) {
        if (StringUtils.isBlank(sk)) {
            return new JiebaoResponse().message("token不能为空！").put("status", JiebaoConstant.STATUS_CODE_SUCCESS);
        }
        if (StringUtils.isBlank(encryptedData)) {
            return new JiebaoResponse().message("encryptedData不能为空！").put("status", JiebaoConstant.STATUS_CODE_SUCCESS);
        }
        if (StringUtils.isBlank(iv)) {
            return new JiebaoResponse().message("iv不能为空！").put("status", JiebaoConstant.STATUS_CODE_SUCCESS);
        }
        String result = null;
        try {
            result = redisService.get(JiebaoConstant.MINI_LOGIN_TEMP_TOKEN + sk);
        } catch (RedisConnectException e) {
            return new JiebaoResponse().message("快速登录认证异常，请重试！" + JiebaoConstant.STATUS_CODE_ERROR);
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        String openid = jsonObject.get("openid").toString();
        String sessionKey = jsonObject.get("sessionKey").toString();

        final WxMaService wxService = WxMaConfiguration.getMaService(appid);
        WxMaPhoneNumberInfo phoneNoInfo = wxService.getUserService().getPhoneNoInfo(sessionKey, encryptedData, iv);
        String phone = phoneNoInfo.getPurePhoneNumber();
        User user = new User();
        user.setOpenid(openid);
        user.setCreateTime(new Date());
        user.setUsername(phone);
        user.setStatus("1");
        user.setType(1);
        user.setMobile(phone);
        user.setPassword(MD5Util.encrypt(phone, RandomUtil.generateRandomStr(6)));
        if (userService.save(user)) {
            return this.attest(user, request);
        } else {
            return new JiebaoResponse().message("快速登录认证异常，请重试！" + JiebaoConstant.STATUS_CODE_ERROR);
        }
    }

    private JiebaoResponse attest(User user, HttpServletRequest request) {
        try {
            this.userService.updateLoginTime(user.getUsername());
            // 保存登录记录
            LoginLog loginLog = new LoginLog();
            loginLog.setUsername(user.getUsername());
            loginLog.setDeptId(user.getDeptId());
            loginLog.setUserId(user.getUserId());
            this.loginLogService.saveLoginLog(loginLog);
            String token = JiebaoUtil.encryptToken(JWTUtil.sign(user.getUsername(), user.getPassword()));
            LocalDateTime expireTime = LocalDateTime.now().plusSeconds(properties.getShiro().getJwtTimeOut());
            String expireTimeStr = DateUtil.formatFullTime(expireTime);
            JWTToken jwtToken = new JWTToken(token, expireTimeStr);

            String userId = this.saveTokenToRedis(user, jwtToken, request);
            user.setId(userId);

            Map<String, Object> userInfo = this.generateUserInfo(jwtToken, user);
            return new JiebaoResponse().message("认证成功！").put("status", JiebaoConstant.STATUS_CODE_SUCCESS).put("token", jwtToken.getToken()).put("userInfo", userInfo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new JiebaoResponse().message("快速登录认证异常，请重试！" + JiebaoConstant.STATUS_CODE_ERROR);
        }
    }


    private String saveTokenToRedis(User user, JWTToken token, HttpServletRequest request) throws RedisConnectException, JsonProcessingException {
        String ip = IPUtil.getIpAddr(request);

        // 构建在线用户
        ActiveUser activeUser = new ActiveUser();
        activeUser.setUsername(user.getUsername());
        activeUser.setIp(ip);
        activeUser.setToken(token.getToken());
        activeUser.setLoginAddress(AddressUtil.getCityInfo(ip));

        // zset 存储登录用户，score 为过期时间戳
        this.redisService.zadd(JiebaoConstant.ACTIVE_USERS_ZSET_PREFIX, Double.valueOf(token.getExipreAt()), mapper.writeValueAsString(activeUser));
        // redis 中存储这个加密 token，key = 前缀 + 加密 token + .ip
        this.redisService.set(JiebaoConstant.TOKEN_CACHE_PREFIX + token.getToken() + StringPool.DOT + ip, token.getToken(), properties.getShiro().getJwtTimeOut() * 1000);

        return activeUser.getId();
    }

    private Map<String, Object> generateUserInfo(JWTToken token, User user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("token", token.getToken());
        //userInfo.put("expireTime", token.getExipreAt());

        user.setPassword("it's a secret");
        user.setOpenid("it's a secret");
        userInfo.put("user", user);
        return userInfo;
    }

}
