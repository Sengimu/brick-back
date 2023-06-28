package com.brick.yggdrasilserver.controller;

import com.brick.yggdrasilserver.common.AjaxResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/web/info")
public class WebInfoController {

    @Autowired
    private HttpServletRequest request;

    @Value("${server-info.serverName}")
    private String serverName;

    @GetMapping("/getInfo")
    public AjaxResult getInfo() {

        HttpSession session = request.getSession();

        Map<String, Object> result = new HashMap<>();
        result.put("title", serverName);

        if (session.getAttribute("user") != null) {
            result.put("user", session.getAttribute("user"));
        }

        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String logoUrl = url + "/cus/images/logo.png";
        result.put("logoUrl", logoUrl);
        List<String> bgUrls = new ArrayList<>();
        String bgUrl = url + "/cus/images/bg.jpg";
        bgUrls.add(bgUrl);
        result.put("bgUrls", bgUrls);

        return AjaxResult.success(result);
    }

}
