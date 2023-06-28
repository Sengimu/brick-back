package com.brick.yggdrasilserver.controller.yggdrasilController;

import com.brick.yggdrasilserver.common.R;
import com.brick.yggdrasilserver.entity.Profile;
import com.brick.yggdrasilserver.service.ISessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/yggdrasil")
public class SessionController {

    @Autowired
    private ISessionService iSessionService;

    @PostMapping("/sessionserver/session/minecraft/join")
    public R join(@RequestBody Map<String, Object> params) {

        String accessToken = params.get("accessToken").toString();
        String selectedProfile = params.get("selectedProfile").toString();
        String serverId = params.get("serverId").toString();

        if (iSessionService.checkJoin(accessToken, selectedProfile, serverId)) {
            return new R(204, null);
        }

        return new R(403, "ForbiddenOperationException", "Null");
    }

    @GetMapping("/sessionserver/session/minecraft/hasJoined")
    public R hasJoined(@RequestParam("username") String username, @RequestParam("serverId") String serverId) {

        Profile profile = iSessionService.checkHasJoined(username, serverId);
        if (profile != null) {
            return new R(profile);
        }

        return new R(204, null);
    }
}
