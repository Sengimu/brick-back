package com.brick.yggdrasilserver.service;


public interface ISetupService {

    /**
     * 获取初始化token
     *
     * @return 获取结果
     */
    boolean getSetupToken();

    /**
     * 验证初始化token
     *
     * @param setupToken setupToken
     * @return 验证结果
     */
    boolean verifySetupToken(String setupToken);

    /**
     * 创建表
     *
     * @return 创建结果
     */
    boolean createTable();
}
