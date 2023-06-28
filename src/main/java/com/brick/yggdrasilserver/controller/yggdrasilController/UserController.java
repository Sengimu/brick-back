package com.brick.yggdrasilserver.controller.yggdrasilController;

import com.alibaba.fastjson2.JSON;
import com.brick.yggdrasilserver.common.R;
import com.brick.yggdrasilserver.entity.Profile;
import com.brick.yggdrasilserver.entity.Token;
import com.brick.yggdrasilserver.entity.User;
import com.brick.yggdrasilserver.service.IProfileService;
import com.brick.yggdrasilserver.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/yggdrasil")
public class UserController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProfileService iProfileService;

    @PostMapping("/authserver/authenticate")
    public R authenticate(@RequestBody Map<String, Object> params) {

        //结果集
        Map<String, Object> result = new HashMap<>();

        //用户
        User user = iUserService.getUser(params.get("username").toString(), params.get("password").toString());
        if (user == null) {
            return new R(403, "ForbiddenOperationException", "Invalid credentials. Invalid username or password.");
        }
        result.put("user", user);

        //角色
        List<Profile> profiles = iProfileService.getProfilesByUserId(user.getId());
        if (profiles.size() == 0) {
            return new R(403, "ForbiddenOperationException", "Null profiles.");
        }
        result.put("selectedProfile", profiles.get(0));
        result.put("availableProfiles", profiles);

        //令牌
        Token token = iUserService.getToken(user.getId(), profiles.get(0), params.get("clientToken") != null ? params.get("clientToken").toString() : null);

        result.put("accessToken", token.getAccessToken());
        result.put("clientToken", token.getClientToken());

        return new R(result);
    }

    @PostMapping("/authserver/refresh")
    public R refresh(@RequestBody Map<String, Object> params) {

        String accessToken = params.get("accessToken").toString();

        Map<String, Object> result = new HashMap<>();

        String clientToken = "";
        if (params.containsKey("clientToken")) {
            clientToken = params.get("clientToken").toString();
        }

        Profile profile = new Profile();
        if (params.containsKey("selectedProfile")) {
            String paramsJsonStr = JSON.toJSONString(params.get("selectedProfile"));
            profile = JSON.parseObject(paramsJsonStr, Profile.class);
        } else {
            profile = iProfileService.getProfileByAccessToken(accessToken);
        }
        if (profile == null) {
            return new R(403, "ForbiddenOperationException", "Invalid token.");
        }
        result.put("selectedProfile", profile);

        Token token = iUserService.refreshToken(accessToken, clientToken, profile);
        if (token == null) {
            return new R(403, "ForbiddenOperationException", "Invalid token.");
        }
        result.put("accessToken", token.getAccessToken());
        result.put("clientToken", token.getClientToken());

        User user = iUserService.refreshGetUserById(profile.getId());
        result.put("user", user);

        return new R(result);
    }

    @PostMapping("/authserver/validate")
    public R validate(@RequestBody Map<String, Object> params) {

        boolean flag;
        if (params.containsKey("clientToken")) {
            flag = iUserService.validate(params.get("accessToken").toString(), params.get("clientToken").toString());
        } else {
            flag = iUserService.validate(params.get("accessToken").toString());
        }

        if (flag) {
            return new R(204, null);
        }

        return new R(403, "ForbiddenOperationException", "Invalid token.");
    }

    @PostMapping("/authserver/invalidate")
    public R invalidate(@RequestBody Map<String, Object> params) {

        iUserService.invalidate(params.get("accessToken").toString());
        return new R(204, null);
    }

    @PostMapping("/authserver/signout")
    public R signOut(@RequestBody Map<String, Object> params) {

        User user = iUserService.getUser(params.get("username").toString(), params.get("password").toString());
        if (user == null) {
            return new R(403, "ForbiddenOperationException", "Invalid credentials. Invalid username or password.");
        }

        if (iUserService.signOut(user.getId())) {
            return new R(204, null);
        }

        return new R(403, "ForbiddenOperationException", "Invalid token.");
    }
}
