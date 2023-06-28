package com.brick.yggdrasilserver.entity;

import lombok.Data;

@Data
public class Token {
    private String accessToken;
    private String clientToken;
    private String profileId;
    private String serverId;
}
