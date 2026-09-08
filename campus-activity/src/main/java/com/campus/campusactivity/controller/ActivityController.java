package com.campus.campusactivity.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ActivityController {

    @GetMapping("/index")
    public String index(HttpSession session) {
        if (session.getAttribute("loginUser") == null) {
            return "redirect:/login";
        }
        return "index";
    }

    @GetMapping("/teacher/dashboard")
    public String teacherDashboard(HttpSession session) {
        if (session.getAttribute("loginUser") == null) {
            return "redirect:/login";
        }
        return "teacher/dashboard";
    }
}
