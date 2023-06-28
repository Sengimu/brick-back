package com.brick.yggdrasilserver.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class User {

    private int id;
    private String username;
    private String password;
    private List<Map<String, Object>> properties;

}
