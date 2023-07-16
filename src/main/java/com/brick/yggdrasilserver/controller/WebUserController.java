package com.brick.yggdrasilserver.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.brick.yggdrasilserver.common.AjaxResult;
import com.brick.yggdrasilserver.entity.Profile;
import com.brick.yggdrasilserver.entity.Token;
import com.brick.yggdrasilserver.entity.User;
import com.brick.yggdrasilserver.service.IProfileService;
import com.brick.yggdrasilserver.service.IUserService;
import com.brick.yggdrasilserver.service.IWebProfileServiceImpl;
import com.brick.yggdrasilserver.service.IWebUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/web/user")
public class WebUserController {

    @Autowired
    private IWebUserService iWebUserService;

    @Autowired
    private IWebProfileServiceImpl iWebProfileService;

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProfileService iProfileService;

    @Autowired
    private HttpServletRequest request;

    @GetMapping("/getVerifyCode")
    public AjaxResult getVerifyCode() {

        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(100, 40, 4, 100);
        if (lineCaptcha.getCode() != null && !lineCaptcha.getCode().equals("")) {
            HttpSession session = request.getSession();
            session.setAttribute("verifyCode", lineCaptcha.getCode());
            return AjaxResult.success(Map.of("verifyCodeBase64String", lineCaptcha.getImageBase64()));
        }

        return AjaxResult.error();
    }

    @PostMapping("/register")
    public AjaxResult register(@RequestBody Map<String, Object> params) {

        if (verifyParams(params).get("code").toString().equals("-1")) {
            return verifyParams(params);
        }

        String username = params.get("username").toString();
        String password = params.get("password").toString();

        if (iWebUserService.register(username, password)) {
            return AjaxResult.success("注册成功，请登录");
        }

        return AjaxResult.error("注册失败，可能账号已存在");
    }

    @PostMapping("/login")
    public AjaxResult login(@RequestBody Map<String, Object> params) {

        if (verifyParams(params).get("code").toString().equals("-1")) {
            return verifyParams(params);
        }

        String username = params.get("username").toString();
        String password = params.get("password").toString();

        User user = iUserService.getUser(username, password);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            return AjaxResult.success("登录成功", user);
        }

        return AjaxResult.error("登录失败，账号不存在或输入错误");
    }

    @GetMapping("/checkLogin")
    public AjaxResult checkLogin() {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user != null) {
            return AjaxResult.success("已登录", user);
        }

        return AjaxResult.error("未登录");
    }

    private AjaxResult verifyParams(Map<String, Object> params) {

        String email = params.get("username").toString();
        Pattern pattern = Pattern.compile("(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w{2,3}){1,3})");
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            return AjaxResult.error("邮箱格式不正确");
        }

        if (params.get("password").toString().equals("")) {
            return AjaxResult.error("密码不可以空白");
        }

        if (params.containsKey("confirmPassword")) {
            if (params.get("password").toString().length() < 8) {
                return AjaxResult.error("密码不可以小于八位");
            }
            if (!params.get("password").toString().equals(params.get("confirmPassword").toString())) {
                return AjaxResult.error("两次密码输入不一致");
            }
        }

        HttpSession session = request.getSession();
        if (session.getAttribute("verifyCode") == null) {
            return AjaxResult.error("验证码失效，请刷新后重试");
        }

        if (!session.getAttribute("verifyCode").toString().equals(params.get("verifyCode").toString())) {
            return AjaxResult.error("验证码错误");
        }

        return AjaxResult.success();
    }
}
