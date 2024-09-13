package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Tan
 * @createTime: 2024/09/12 15:51
 * @description:
 */
@Service
public class UserServiceImpl implements UserService {

    // 微信服务接口地址
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Resource
    private WeChatProperties weChatProperties;

    @Resource
    private UserMapper userMapper;

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    public User wxLogin(UserLoginDTO userLoginDTO) {
        // 调用getOpenid方法获取用户的openid
        String openid = getOpenid(userLoginDTO.getCode());

        // 判断openid是否为空，如果为空表示登陆异常，抛出业务异常
        if(openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        // 判断当前微信用户的openid是否在用户表中
        User user = userMapper.getByOpenid(openid);

        // 如果不在，则认为是新用户，自动完成注册
        if(user == null){
            user = User.builder()
                        .openid(openid)
                        .createTime(LocalDateTime.now())
                        .build();

            userMapper.insert(user);

        }

        // 返回这个用户对象

        return user;
    }

    /**
     * 调用微信接口服务获取用户的openid
     * @param code
     * @return
     */
    private String getOpenid(String code){
        // 首先调用微信接口服务，获得当前微信用户的openid
        Map<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, map);

        // 将json解析为一个jsonObject对象
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");

        return openid;
    }



}
