package com.brick.yggdrasilserver.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.brick.yggdrasilserver.mapper.WebUserMapper;
import com.brick.yggdrasilserver.service.IWebUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebUserServiceImpl implements IWebUserService {

    @Autowired
    private WebUserMapper webUserMapper;

    public boolean register(String username, String password) {

        if (webUserMapper.exist(username) > 0) {
            return false;
        }

        if (webUserMapper.register(username, DigestUtil.md5Hex(password)) > 0) {
            return webUserMapper.registerUserProperty(webUserMapper.selectUserIdByUsername(username));
        }

        return false;
    }
}
