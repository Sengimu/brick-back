package com.brick.yggdrasilserver.service;

import com.brick.yggdrasilserver.entity.Profile;

public interface ISessionService {

    /**
     * 通过参数验证用户合法性并记录redis
     *
     * @param accessToken     accessToken
     * @param selectedProfile 选择的角色
     * @param serverId        serverId
     * @return 验证结果
     */
    boolean checkJoin(String accessToken, String selectedProfile, String serverId);

    /**
     * 通过参数比对redis记录验证合法性
     *
     * @param username 角色名
     * @param serverId serverId
     * @return 对应的角色
     */
    Profile checkHasJoined(String username, String serverId);
}
