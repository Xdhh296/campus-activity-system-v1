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

    // 管理员账号管理首页
    @GetMapping("/admin/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null || !"admin".equals(user.getRole())) return "redirect:/login";
        List<User> students = userService.listStudents();
        model.addAttribute("students", students);
        return "admin/studentList";
    }

    // 管理员：更新学生账号状态
    @GetMapping("/admin/user/updateStatus/{id}/{status}")
    public String updateStatus(@PathVariable Long id, @PathVariable Integer status, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null || !"admin".equals(user.getRole())) return "redirect:/login";
        userService.updateAccountStatus(id, status);
        return "redirect:/admin/dashboard";
    }
}
