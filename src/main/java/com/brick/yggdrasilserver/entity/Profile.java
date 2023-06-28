package com.brick.yggdrasilserver.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Profile {

    private String id;
    private String name;
    private List<Map<String, Object>> properties;
}
