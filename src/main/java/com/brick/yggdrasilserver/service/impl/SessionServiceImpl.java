package com.brick.yggdrasilserver.service.impl;

import com.brick.yggdrasilserver.entity.Profile;
import com.brick.yggdrasilserver.service.IProfileService;
import com.brick.yggdrasilserver.service.ISessionService;
import com.brick.yggdrasilserver.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SessionServiceImpl implements ISessionService {

    @Autowired
    private IProfileService iProfileService;

    @Autowired
    private RedisUtils redisUtils;

    public boolean checkJoin(String accessToken, String selectedProfile, String serverId) {

        if (!redisUtils.exist(accessToken)) {
            return false;
        }

        String tb_selectedProfile = redisUtils.hGet(accessToken, "profileId");
        if (!selectedProfile.equals(tb_selectedProfile)) {
            return false;
        }

        redisUtils.hSet(serverId, Map.of("selectedProfile", selectedProfile), 1);

        return true;
    }

    public Profile checkHasJoined(String username, String serverId) {

        if (!redisUtils.exist(serverId)) {
            return null;
        }

        String selectedProfile = redisUtils.hGet(serverId, "selectedProfile");
        Profile profile = iProfileService.getProfileById(selectedProfile);
        if (profile != null && username.equals(profile.getName())) {
            return profile;
        }
        return null;
    }
}
