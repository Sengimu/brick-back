package com.brick.yggdrasilserver.service;

import com.brick.yggdrasilserver.entity.Profile;
import com.brick.yggdrasilserver.entity.Token;
import com.brick.yggdrasilserver.entity.User;

public interface IUserService {

    /**
     * 通过邮箱和密码验证登录
     *
     * @param username 邮箱
     * @param password 密码
     * @return 对应的用户
     */
    User getUser(String username, String password);

    /**
     * @param profile     角色
     * @param clientToken clientToken
     * @return 登录token
     */
    Token getToken(int id, Profile profile, String clientToken);

    /**
     * 刷新token
     *
     * @param accessToken accessToken
     * @param clientToken clientToken
     * @param profile     新角色
     * @return 新token
     */
    Token refreshToken(String accessToken, String clientToken, Profile profile);

    /**
     * 通过uuid获取用户
     *
     * @param profileId profileId
     * @return 用户
     */
    User refreshGetUserById(String profileId);

    /**
     * 验证token，如果clientToken存在应检查
     *
     * @param token accessToken和clientToken
     * @return 验证结果
     */
    boolean validate(String... token);

    /**
     * 吊销token
     *
     * @param accessToken accessToken
     * @return 吊销结果
     */
    boolean invalidate(String accessToken);

    /**
     * 通过用户id删除所有token
     *
     * @param userId userId
     * @return 删除结果
     */
    boolean signOut(int userId);
}
