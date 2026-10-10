package com.campus.campusactivity.controller;

import com.campus.campusactivity.entity.User;
import com.campus.campusactivity.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    // 统一角色跳转工具
    private String getHomeByRole(User user) {
        if ("teacher".equals(user.getRole())) return "redirect:/teacher/dashboard";
        if ("student".equals(user.getRole())) return "redirect:/index";
        return "redirect:/login";
    }

    // 管理员账号管理首页（仅admin可访问）
    @GetMapping("/admin/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";
        if (!"admin".equals(user.getRole())) return getHomeByRole(user);

        List<User> students = userService.listStudents();
        model.addAttribute("students", students);
        return "admin/studentList";
    }

    // 管理员：更新学生账号状态（仅admin可操作）
    @GetMapping("/admin/user/updateStatus/{id}/{status}")
    public String updateStatus(@PathVariable Long id, @PathVariable Integer status, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";
        if (!"admin".equals(user.getRole())) return getHomeByRole(user);

        userService.updateAccountStatus(id, status);
        return "redirect:/admin/dashboard";
    }
}
