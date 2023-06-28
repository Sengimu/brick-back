package com.brick.yggdrasilserver.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson2.JSON;
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
import java.util.*;

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

            Profile profile = new Profile();

            String id = tb_profile.get("id").toString();
            String name = tb_profile.get("name").toString();

            profile.setId(id);
            profile.setName(name);
            profile.setProperties(getProfileProperties(id));

            result.add(profile);
        }

        return result;
    }

    public Profile getProfileById(String id) {

        Profile result = new Profile();

        Map<String, Object> tb_profile = profileMapper.selectProfileById(id);

        result.setId(id);
        result.setName(tb_profile.get("name").toString());
        result.setProperties(getProfileProperties(id));

        return result;
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
            if (getProfileByProfileName(name) != null) {
                result.add(getProfileByProfileName(name));
            }
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

        Profile result = new Profile();

        Map<String, Object> tb_profile = profileMapper.selectProfileByProfileName(name);

        if (tb_profile != null) {
            result.setId(tb_profile.get("id").toString());
            result.setName(tb_profile.get("name").toString());
            result.setProperties(getProfileProperties(tb_profile.get("id").toString()));

            return result;
        }

        return null;
    }

    private List<Map<String, Object>> getProfileProperties(String id) {

        List<Map<String, Object>> properties = new ArrayList<>();

        List<Map<String, Object>> tb_textures = profileMapper.selectTexturesById(id);

        Texture texture = new Texture();

        for (Map<String, Object> tb_texture : tb_textures) {
            texture.setProfileId(tb_texture.get("id").toString());
            texture.setProfileName(tb_texture.get("name").toString());
            texture.setTimestamp(Long.parseLong(tb_texture.get("timestamp").toString()));
            String type = tb_texture.get("type").toString();
            String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +
                    "/textures/" + tb_texture.get("filename").toString();
            texture.setTextures(type, url, tb_texture.get("model").toString());

            Map<String, Object> textures = new HashMap<>();
            textures.put("name", "textures");
            String value = Base64.encode(JSON.toJSONString(texture));
            textures.put("value", value);
            Sign sign = SecureUtil.sign(SignAlgorithm.SHA1withRSA, privateKey, publicKey);
            textures.put("signature", Base64.encode(sign.sign(value.getBytes())));
            properties.add(textures);

            Map<String, Object> uploadableTextures = new HashMap<>();
            uploadableTextures.put("name", "uploadableTextures");
            uploadableTextures.put("value", "skin");
            properties.add(uploadableTextures);
        }

        return properties;
    }
}
