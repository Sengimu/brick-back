package com.brick.yggdrasilserver.service;

public interface IWebProfileServiceImpl {

    /**
     * 通过uuid获取头部材质
     *
     * @param id uuid
     * @param x  倍率
     * @return base64编码的头部材质字符串
     */
    String getHeadById(String id, long x);

    /**
     * 创建新角色
     *
     * @param profileName 角色名
     * @param userId      userId
     * @return 创建结果
     */
    boolean createProfile(String profileName, int userId);
}
