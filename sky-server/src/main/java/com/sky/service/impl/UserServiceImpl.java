package com.sky.service.impl;

import com.google.gson.Gson;
import com.sky.constant.MessageConstant;
import com.sky.constant.ThirdPartyConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final WeChatProperties weChatProperties;
    private final Gson gson;
    private final UserMapper userMapper;

    /**
     * 微信登录
     *
     * @param userLoginDTO 微信登录参数
     * @return
     */
    @Override
    public User wxlogin(UserLoginDTO userLoginDTO) {
        HashMap<?, ?> map = getOpenId(userLoginDTO.getCode());
        if (!map.containsKey("openid")) {
            log.info("微信登录失败，openid获取失败:{}",map.get("errmsg"));
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //获取openid
        String openid = (String) map.get("openid");

        //判断是否为新用户,是新用户就自动注册
        User user = userMapper.getUserByOpenId(openid);
        if (Objects.isNull(user)) {
            //到这里显然user是为空的，直接复用
            user = User
                    .builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            //由于Mapper使用useGenerateKey，且设置keyProperty，所以结果会返回给user对象
            userMapper.insert(user);
        }
        return user;
    }

    // 调用微信官方提供的微信登录接口
    private HashMap<?, ?> getOpenId(String code) {
        //封装参数
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("appid", weChatProperties.getAppid());
        hashMap.put("secret", weChatProperties.getSecret());
        hashMap.put("js_code", code);
        hashMap.put("grant_type", "authorization_code");
        //调用微信官方提供的微信登录接口
        String result = HttpClientUtil.doGet(ThirdPartyConstant.WX_LOGIN_URL, hashMap);
        //如果没有返回用户的Openid那么登录失败
        return gson.fromJson(result, HashMap.class);
    }
}
