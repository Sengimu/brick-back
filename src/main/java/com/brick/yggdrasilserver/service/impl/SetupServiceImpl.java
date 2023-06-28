package com.brick.yggdrasilserver.service.impl;

import com.brick.yggdrasilserver.mapper.DataBaseMapper;
import com.brick.yggdrasilserver.service.ISetupService;
import com.brick.yggdrasilserver.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class SetupServiceImpl implements ISetupService {

    @Autowired
    private DataBaseMapper dataBaseMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Value("${mysql.database}")
    private String tableSchema;

    public boolean getSetupToken() {

        String token = UUID.randomUUID().toString().replace("-", "");
        log.info("初始化token: " + token + ", 请在两分钟内使用");
        redisUtils.set("setupToken", token, 60 * 2);
        return true;
    }

    public boolean verifySetupToken(String setupToken) {

        return setupToken.equals(redisUtils.get("setupToken"));
    }

    public boolean createTable() {

        int before = dataBaseMapper.getTables(tableSchema);
        dataBaseMapper.createTable();
        int after = dataBaseMapper.getTables(tableSchema);

        if (after - before == 4) {
            return dataBaseMapper.insertTest() > 0;
        }

        return false;
    }
}
