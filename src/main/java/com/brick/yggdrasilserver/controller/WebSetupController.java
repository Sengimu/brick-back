package com.brick.yggdrasilserver.controller;

import cn.hutool.core.codec.Base64;
import com.brick.yggdrasilserver.common.AjaxResult;
import com.brick.yggdrasilserver.service.ISetupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Map;

@RestController
@RequestMapping("/web/setup")
public class WebSetupController {

    @Autowired
    private ISetupService iSetupService;

    @GetMapping("/getSetupToken")
    public AjaxResult getSetupToken() {

        return iSetupService.getSetupToken() ? AjaxResult.success() : AjaxResult.error();
    }

    @PostMapping("/verifySetupToken")
    public AjaxResult verify(@RequestBody Map<String, Object> params) {

        String setupToken = params.get("setupToken").toString();

        if (iSetupService.verifySetupToken(setupToken)) {
            try {
                iSetupService.createTable();
                KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
                gen.initialize(4096);
                KeyPair pair = gen.generateKeyPair();

                String privateKey = Base64.encode(pair.getPrivate().getEncoded());
                String publicKey = Base64.encode(pair.getPublic().getEncoded());

                return AjaxResult.success("验证成功", Map.of("privateKey", privateKey, "publicKey", publicKey));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return AjaxResult.error("验证失败，可能token已过期");
    }
}
