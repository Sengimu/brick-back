package com.brick.yggdrasilserver.entity;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import com.alibaba.fastjson2.JSON;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Profile {

    private String id;
    private String name;
    private List<Map<String, Object>> properties;

    @Value("${privateKey}")
    private String privateKey;

    @Value("${publicKey}")
    private String publicKey;

    public Profile() {
    }

    public Profile(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setTextures(Texture texture) {

        Map<String, Object> textures = new HashMap<>();
        textures.put("name", "textures");
        String value = Base64.encode(JSON.toJSONString(texture));
        textures.put("value", value);
        Sign sign = SecureUtil.sign(SignAlgorithm.SHA1withRSA, privateKey, publicKey);
        textures.put("signature", Base64.encode(sign.sign(value.getBytes())));

        properties.add(textures);
    }

    public void setUploadableTextures(String type) {

        Map<String, Object> uploadableTextures = new HashMap<>();
        uploadableTextures.put("name", "uploadableTextures");
        uploadableTextures.put("value", "skin");

        properties.add(uploadableTextures);
    }
}
