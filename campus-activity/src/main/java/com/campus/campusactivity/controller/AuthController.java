package com.campus.campusactivity.controller;

import com.campus.campusactivity.entity.User;
import com.campus.campusactivity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // 跳转登录页
    @GetMapping("/login")
    public String toLogin() {
        return "login";
    }

    // 跳转注册页
    @GetMapping("/register")
    public String toRegister() {
        return "register";
    }

    // 执行注册
    @PostMapping("/register")
    public String register(User user, Model model) {
        String result = userService.register(user);
        if ("注册成功".equals(result)) {
            // 注册成功跳转到登录页
            return "redirect:/login";
        }
        // 注册失败带回提示信息
        model.addAttribute("msg", result);
        return "register";
    }

    // 执行登录
    @PostMapping("/login")
    public String login(String username, String password, HttpSession session, Model model) {
        User user = userService.login(username, password);
        if (user == null) {
            model.addAttribute("msg", "账号或密码错误");
            return "login";
        }
        // 登录成功，用户信息存入 session
        session.setAttribute("loginUser", user);
        // 按角色跳转不同首页
        if ("teacher".equals(user.getRole())) {
            return "redirect:/teacher/dashboard";
        } else {
            return "redirect:/index";
        }
    }

    // 退出登录
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
