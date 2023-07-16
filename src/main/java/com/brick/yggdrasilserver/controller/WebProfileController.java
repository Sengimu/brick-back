package com.brick.yggdrasilserver.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import com.brick.yggdrasilserver.common.AjaxResult;
import com.brick.yggdrasilserver.common.R;
import com.brick.yggdrasilserver.entity.Profile;
import com.brick.yggdrasilserver.entity.User;
import com.brick.yggdrasilserver.service.IProfileService;
import com.brick.yggdrasilserver.service.IWebProfileServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/web/profile")
public class WebProfileController {

    @Autowired
    private IWebProfileServiceImpl iWebProfileService;

    @Autowired
    private IProfileService iProfileService;

    @Autowired
    private HttpServletRequest request;

    @GetMapping("/getHeadById/{userId}")
    public AjaxResult getTextureHead(@PathVariable("userId") int userId) {

        List<Profile> profiles = iProfileService.getProfilesByUserId(userId);

        String headBase64String = profiles.size() > 0 ? iWebProfileService.getHeadById(profiles.get(0).getId(), 4L) : iWebProfileService.getHeadById("steven", 4L);
        if (headBase64String != null) {

            return AjaxResult.success(Map.of("headBase64Str", headBase64String));
        }

        return AjaxResult.error();
    }

    @PostMapping("/getProfilesByUserId/{userId}")
    public AjaxResult getProfiles(@PathVariable("userId") int userId) {

        List<Profile> profiles = iProfileService.getProfilesByUserId(userId);

        List<Map<String, Object>> result = new ArrayList<>();

        if (profiles.size() > 0) {
            for (Profile profile : profiles) {

                Map<String, Object> map = new HashMap<>();
                map.put("id", profile.getId());
                map.put("name", profile.getName());

                String headBase64String = iWebProfileService.getHeadById(profile.getId(), 4L);
                if (headBase64String != null) {
                    map.put("headBase64Str", headBase64String);
                }
                result.add(map);
            }
        }

        return AjaxResult.success(result);
    }

    @DeleteMapping("/{uuid}/{textureType}")
    public AjaxResult deleteProfileTexture(@PathVariable("uuid") String uuid, @PathVariable("textureType") String textureType) {

        if (iProfileService.deleteTexture(uuid)) {
            return AjaxResult.success("删除成功");
        }

        return AjaxResult.error("删除失败");
    }

    @PostMapping("/uploadTexture")
    public AjaxResult putProfileTexture(@RequestParam("file") MultipartFile file, @RequestParam("id") String id, @RequestParam("model") String model, @RequestParam("textureType") String textureType) {

        if (file == null || file.getSize() > 1024 * 50) {
            return AjaxResult.error("文件过大或不存在");
        }

        if (!"png".equals(FileUtil.getSuffix(file.getOriginalFilename()).toLowerCase())) {
            return AjaxResult.error("只可以以png为后缀");
        }

        model = model.equals("slim") ? "slim" : "default";

        String filename = iProfileService.uploadTextureImage(file, textureType);
        if (filename != null && iProfileService.putTexture(id, textureType, filename, model)) {
            return AjaxResult.success("上传成功");
        }

        return AjaxResult.error("上传异常，请联系管理员");
    }

    @PostMapping("/createProfile")
    public AjaxResult createProfile(@RequestBody Map<String, Object> params) {

        String createProfileName = params.get("createProfileName").toString();
        Pattern pattern = Pattern.compile("(\\w){3,16}");
        Matcher matcher = pattern.matcher(createProfileName);
        if (!matcher.matches()) {
            return AjaxResult.error("角色名必须由3-16位小写大写字母数字及下划线组成");
        }

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            if (iWebProfileService.createProfile(createProfileName, user.getId())) {
                return AjaxResult.success("创建成功");
            } else {
                return AjaxResult.error("角色名已存在或你已无法创建更多角色");
            }
        }

        return AjaxResult.error("创建异常请联系管理员");
    }
}
