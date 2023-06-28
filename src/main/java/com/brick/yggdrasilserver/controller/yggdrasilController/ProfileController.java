package com.brick.yggdrasilserver.controller.yggdrasilController;

import cn.hutool.core.io.FileUtil;
import com.brick.yggdrasilserver.common.R;
import com.brick.yggdrasilserver.entity.Profile;
import com.brick.yggdrasilserver.service.IProfileService;
import com.brick.yggdrasilserver.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/yggdrasil")
public class ProfileController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProfileService iProfileService;

    @Autowired
    private HttpServletRequest request;

    @GetMapping("/sessionserver/session/minecraft/profile/{uuid}")
    public R getProfileById(@PathVariable("uuid") String uuid, @RequestParam(value = "unsigned", required = false) String unsigned) {

        Profile profile = iProfileService.getProfileById(uuid);
        if (profile != null) {
            return new R(profile);
        }

        return new R(204, null);
    }

    @GetMapping("/api/profiles/minecraft")
    public R getProfilesByProfileName(@RequestBody List<String> params) {

        int toIndex = Math.min(params.size(), 5);

        List<Profile> result = iProfileService.getProfilesByProfileName(params.subList(0, toIndex));
        if (result.size() > 0) {
            return new R(result);
        }

        return new R(403, "ForbiddenOperationException", "Invalid token.");
    }

    @PutMapping("/api/user/profile/{uuid}/{textureType}")
    public R putProfileTexture(@RequestParam("model") String model, @RequestParam("file") MultipartFile file, @PathVariable("uuid") String uuid, @PathVariable("textureType") String textureType) {

        if (request.getHeader("Authorization") == null) {
            return new R(401, "Unauthorized");
        }

        String accessToken = request.getHeader("Authorization").split(" ")[1];
        if (!iUserService.validate(accessToken)) {
            return new R(401, "Unauthorized");
        }

        if (file == null || file.getSize() > 1024 * 50) {
            return new R(401, "Unauthorized");
        }

        if (!"png".equals(FileUtil.getSuffix(file.getOriginalFilename()).toLowerCase())) {
            return new R(401, "Unauthorized");
        }

        model = model.equals("slim") ? "slim" : "default";

        String filename = iProfileService.uploadTextureImage(file, textureType);
        if (filename != null && iProfileService.putTexture(uuid, textureType, filename, model)) {
            return new R(204, null);
        }

        return new R(401, "Unauthorized");
    }

    @DeleteMapping("/api/user/profile/{uuid}/{textureType}")
    public R deleteProfileTexture(@PathVariable("uuid") String uuid, @PathVariable("textureType") String textureType) {

        if (iProfileService.deleteTexture(uuid)) {
            return new R(204, null);
        }

        return new R(401, "Unauthorized");
    }
}
