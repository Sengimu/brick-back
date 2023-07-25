package com.brick.yggdrasilserver.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.brick.yggdrasilserver.common.FileConfig;
import com.brick.yggdrasilserver.entity.Profile;
import com.brick.yggdrasilserver.entity.Texture;
import com.brick.yggdrasilserver.mapper.ProfileMapper;
import com.brick.yggdrasilserver.service.IProfileService;
import com.brick.yggdrasilserver.utils.RedisUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ProfileServiceImpl implements IProfileService {

    @Autowired
    private ProfileMapper profileMapper;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private RedisUtils redisUtils;

    @Value("${privateKey}")
    private String privateKey;

    @Value("${publicKey}")
    private String publicKey;

    public List<Profile> getProfilesByUserId(int userId) {

        List<Profile> result = new ArrayList<>();

        List<Map<String, Object>> tb_profiles = profileMapper.selectProfilesByUserId(userId);

        for (Map<String, Object> tb_profile : tb_profiles) {

            String id = tb_profile.get("id").toString();
            String name = tb_profile.get("name").toString();

            Profile profile = new Profile(id, name);

            setProfileProperties(profile);

            result.add(profile);
         }

        return result;
    }

    public Profile getProfileById(String id) {

        Map<String, Object> tb_profile = profileMapper.selectProfileById(id);

        Profile profile = new Profile(id,tb_profile.get("name").toString());
        setProfileProperties(profile);

        return profile;
    }

    public Profile getProfileByAccessToken(String accessToken) {

        if (redisUtils.exist(accessToken)) {
            String uuid = redisUtils.hGet(accessToken, "profileId");

            return getProfileById(uuid);
        }

        return null;
    }

    public List<Profile> getProfilesByProfileName(List<String> names) {

        List<Profile> result = new ArrayList<>();
        for (String name : names) {
            result.add(getProfileByProfileName(name));
        }

        return result;
    }

    public String uploadTextureImage(MultipartFile file, String type) {

        String prefix = DigestUtil.sha256Hex(FileUtil.getPrefix(file.getOriginalFilename()));
        String suffix = FileUtil.getSuffix(file.getOriginalFilename());

        String filename = prefix + "." + suffix;
        try {
            InputStream in = file.getInputStream();
            BufferedOutputStream out;
            if (FileConfig.OS.toLowerCase().startsWith("win")) {
                out = FileUtil.getOutputStream(FileConfig.IMAGE_WIN_PATH + "\\upload\\textures\\" + filename);
            } else {
                out = FileUtil.getOutputStream(FileConfig.IMAGE_LIN_PATH + "/upload/textures/" + filename);
            }
            IoUtil.copy(in, out);
            in.close();
            out.close();
            return prefix;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean putTexture(String uuid, String type, String filename, String model) {

        Profile profile = getProfileById(uuid);
        if (profile == null) {
            return false;
        }

        profileMapper.deleteById(profile.getId());

        long timestamp = new Date().getTime();

        return profileMapper.putTexture(profile.getId(), profile.getName(), type.toUpperCase(), timestamp, filename, model) > 0;
    }

    public boolean deleteTexture(String uuid) {

        if (profileMapper.hasProfileTexture(uuid) > 0) {
            profileMapper.deleteTextureById(uuid);
            return true;
        }

        return false;
    }

    public List<Map<String, Object>> getTextureByName(String name) {
        return profileMapper.selectTextureByName(name);
    }

    private Profile getProfileByProfileName(String name) {

        Map<String, Object> tb_profile = profileMapper.selectProfileByProfileName(name);

        Profile profile = new Profile(tb_profile.get("id").toString(),name);
        setProfileProperties(profile);

        return profile;
    }

    private void setProfileProperties(Profile profile) {

        List<Map<String, Object>> tbTextures = profileMapper.selectTexturesById(profile.getId());

        for (Map<String, Object> tbTexture : tbTextures) {

            Texture texture = new Texture();

            texture.setProfileId(tbTexture.get("id").toString());
            texture.setProfileName(tbTexture.get("name").toString());
            texture.setTimestamp(Long.parseLong(tbTexture.get("timestamp").toString()));
            String type = tbTexture.get("type").toString();
            String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +
                    "/textures/" + tbTexture.get("filename").toString();
            texture.setTextures(type, url, tbTexture.get("model").toString());

            profile.setTextures(texture);
            profile.setUploadableTextures("skin");
        }
    }
}
