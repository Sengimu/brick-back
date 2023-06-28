package com.brick.yggdrasilserver.controller.yggdrasilController;

import com.brick.yggdrasilserver.common.R;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/yggdrasil")
public class ExtendController {

    @Autowired
    private HttpServletRequest request;

    @Value("${server-info.serverName}")
    private String serverName;

    @Value("${server-info.implementationName}")
    private String implementationName;

    @Value("${server-info.implementationVersion}")
    private String implementationVersion;

    @Value("${publicKey}")
    private String publicKey;

    @GetMapping
    public R secondApi() {
        return mainInfo();
    }

    @GetMapping("/")
    public R mainInfo() {

        Map<String, Object> result = new HashMap<>();

        Map<String, Object> meta = new HashMap<>();
        meta.put("serverName", serverName);
        meta.put("implementationName", implementationName);
        meta.put("implementationVersion", implementationVersion);
        Map<String, Object> links = new HashMap<>();
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String url2 = request.getServerName();
        links.put("homepage", url);
        links.put("register", url + "/register");
        meta.put("links", links);

        List<String> skinDomains = new ArrayList<>();
        skinDomains.add(url);
        skinDomains.add(url2);

        result.put("meta", meta);
        result.put("skinDomains", skinDomains);
        result.put("signaturePublickey", "-----BEGIN PUBLIC KEY-----\n" +
                publicKey +
                "\n-----END PUBLIC KEY-----\n");

        return new R(result);
    }
}
