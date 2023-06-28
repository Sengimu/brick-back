package com.brick.yggdrasilserver.controller.yggdrasilController;

import com.brick.yggdrasilserver.common.R;
import com.brick.yggdrasilserver.service.IProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class OtherController {

    @Autowired
    private IProfileService iProfileService;

    @GetMapping("/textures/{hash}")
    public R texture(@PathVariable String hash, HttpServletRequest request, HttpServletResponse response) {

        try {
            response.setHeader("Content-Type", "image/png");
            request.getRequestDispatcher("/csl/textures/" + hash + ".png").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new R(200, null);
    }

    @GetMapping("/csl/{name}.json")
    public Map<String, Object> getTexture(@PathVariable("name") String name) {

        Map<String, Object> result = new HashMap<>();
        result.put("username", name);

        Map<String, Object> skins = new HashMap<>();
        List<Map<String, Object>> textures = iProfileService.getTextureByName(name);
        for (Map<String, Object> texture : textures) {
            if (texture.get("type").toString().equals("skin")) {
                skins.put(texture.get("model").toString(), texture.get("filename").toString());
            } else if (texture.get("type").toString().equals("cape")) {
                result.put("cape", texture.get("filename").toString());
            }
        }
        result.put("skins", skins);

        return result;
    }
}
