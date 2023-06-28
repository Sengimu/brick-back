package com.brick.yggdrasilserver.service;

public interface IWebUserService {

    /**
     * 注册，密码存储md5加密值
     *
     * @param username 邮箱
     * @param password 密码
     * @return 注册结果
     */
    boolean register(String username, String password);
}
