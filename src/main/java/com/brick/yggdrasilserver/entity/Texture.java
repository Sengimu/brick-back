package com.brick.yggdrasilserver.entity;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Texture {

    private long timestamp;
    private String profileId;
    private String profileName;
    private Map<String, Object> textures = new HashMap<>();

    public void setTextures(String type, String url, String model) {

        Map<String, Object> value = new HashMap<>();
        value.put("url", url);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("model", model);
        value.put("metadata", metadata);

        if (this.textures != null) {
            this.textures.put(type.toUpperCase(), value);
        } else {
            this.textures = Map.of(type.toUpperCase(), value);
        }


    }
}
