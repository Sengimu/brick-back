package com.brick.yggdrasilserver.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.img.ImgUtil;
import com.brick.yggdrasilserver.mapper.WebProfileMapper;
import com.brick.yggdrasilserver.service.IProfileService;
import com.brick.yggdrasilserver.service.IWebProfileServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class WebProfileServiceImpl implements IWebProfileServiceImpl {

    @Autowired
    private WebProfileMapper webProfileMapper;

    @Autowired
    private IProfileService iProfileService;

    @Autowired
    private HttpServletRequest request;

    public String getHeadById(String id, long x) {

        try {
            String hash = "steven".equals(id) ? "steven" : webProfileMapper.getFilenameById(id);
            String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            URL requestUrl = new URL(url + "/csl/textures/" + hash + ".png");

            InputStream urlStream = requestUrl.openStream();
            ByteArrayOutputStream cacheStream = new ByteArrayOutputStream();
            ImgUtil.cut(urlStream, cacheStream, new Rectangle(8, 8, 8, 8));


            InputStream inputStream = new ByteArrayInputStream(cacheStream.toByteArray());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            ImgUtil.scale(inputStream, outputStream, x);

            return Base64.encode(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean createProfile(String profileName, int userId) {

        String uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + profileName).getBytes(StandardCharsets.UTF_8)).toString().replace("-", "");

        if (webProfileMapper.selectUserProfile(userId) == 0 && webProfileMapper.selectExistProfileByUserId(uuid) == 0) {

            return webProfileMapper.createProfile(uuid, profileName, userId) && iProfileService.putTexture(uuid, "SKIN", "steven", "default");
        }

        return false;
    }
}
