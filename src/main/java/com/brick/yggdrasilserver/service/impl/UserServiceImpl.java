package com.brick.yggdrasilserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.brick.yggdrasilserver.entity.Profile;
import com.brick.yggdrasilserver.entity.Token;
import com.brick.yggdrasilserver.entity.User;
import com.brick.yggdrasilserver.mapper.UserMapper;
import com.brick.yggdrasilserver.service.IUserService;
import com.brick.yggdrasilserver.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Value("${server-info.maxTimes}")
    private int maxTimes;

    public User getUser(String username, String password) {

        if (checkLoginTime(username) == 0) {
            return null;
        }

        User user = userMapper.selectUserByUsernameAndPassword(username, DigestUtil.md5Hex(password));
        if (user != null) {
            user.setProperties(userMapper.getUserProperties(user.getId()));
            return user;
        }

        return null;
    }

    public Token getToken(int id, Profile profile, String clientToken) {

        Token token = new Token();

        token.setProfileId(profile.getId());

        String accessToken = UUID.randomUUID().toString().replace("-", "");
        token.setAccessToken(accessToken);

        if (clientToken == null) {
            clientToken = UUID.randomUUID().toString().replace("-", "");
        }
        token.setClientToken(clientToken);

        redisUtils.hSet(token.getAccessToken(), BeanUtil.beanToMap(token), 60 * 60 * 2);
        redisUtils.lSet(String.valueOf(id), token.getAccessToken(), 60 * 60 * 2);

        return token;
    }

    public Token refreshToken(String accessToken, String clientToken, Profile profile) {

        Token token = new Token();

        if ("".equals(clientToken)) {
            clientToken = UUID.randomUUID().toString().replace("-", "");
        }
        token.setClientToken(clientToken);

        if (redisUtils.del(accessToken)) {
            String newAccessToken = UUID.randomUUID().toString().replace("-", "");
            token.setAccessToken(newAccessToken);
            token.setProfileId(profile.getId());
            redisUtils.hSet(token.getAccessToken(), BeanUtil.beanToMap(token), 60 * 60 * 2);
            return token;
        }

        return null;
    }

    public User refreshGetUserById(String profileId) {

        int userId = userMapper.selectUserIdById(profileId);

        User user = userMapper.selectUserByUserId(userId);
        if (user != null) {
            user.setProperties(userMapper.getUserProperties(user.getId()));
            return user;
        }

        return null;
    }

    public boolean validate(String... token) {

        if (token.length > 1) {
            return redisUtils.exist(token[0]) && redisUtils.hGet(token[0], "clientToken").equals(token[1]);
        } else {
            return redisUtils.exist(token[0]);
        }
    }

    public boolean invalidate(String accessToken) {

        return redisUtils.exist(accessToken) && redisUtils.del(accessToken);
    }

    public boolean signOut(int userId) {

        boolean flag = false;

        for (String accessToken : redisUtils.lGetAll(String.valueOf(userId))) {
            flag = redisUtils.del(accessToken);
        }

        return flag;
    }

    private int checkLoginTime(String username) {

        if (!redisUtils.exist(username)) {
            redisUtils.set(username, "1", 60 * 5);
            return 1;
        } else {
            int time = Integer.parseInt(redisUtils.get(username)) + 1;
            if (time <= maxTimes) {
                redisUtils.set(username, Integer.toString(time), 0);
                return 1;
            }
        }

        return 0;
    }

}
