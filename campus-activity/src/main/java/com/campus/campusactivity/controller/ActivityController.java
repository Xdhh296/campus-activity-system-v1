package com.campus.campusactivity.controller;

import com.campus.campusactivity.entity.Activity;
import com.campus.campusactivity.entity.User;
import com.campus.campusactivity.service.ActivityService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    // ========== 学生端 ==========
    @GetMapping("/index")
    public String index(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        List<Activity> list = activityService.listOnline();
        List<Long> enrolledIds = activityService.listEnrolledIds(user.getId());
        model.addAttribute("list", list);
        model.addAttribute("enrolledIds", enrolledIds);
        return "index";
    }

    @GetMapping("/activity/enroll/{id}")
    public String enroll(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        String msg = activityService.enroll(id, user.getId());
        ra.addFlashAttribute("msg", msg);
        return "redirect:/index";
    }

    @GetMapping("/activity/cancel/{id}")
    public String cancel(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";

        String msg = activityService.cancel(id, user.getId());
        ra.addFlashAttribute("msg", msg);
        return "redirect:/index";
    }

    // ========== 教师端 ==========
    @GetMapping("/teacher/dashboard")
    public String teacherDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null || !"teacher".equals(user.getRole())) return "redirect:/login";

        List<Activity> list = activityService.listByTeacher(user.getId());
        model.addAttribute("list", list);
        return "teacher/dashboard";
    }

    @GetMapping("/teacher/activity/add")
    public String toAdd(HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null || !"teacher".equals(user.getRole())) return "redirect:/login";
        return "teacher/activityAdd";
    }

    @PostMapping("/teacher/activity/save")
    public String save(Activity activity, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null || !"teacher".equals(user.getRole())) return "redirect:/login";

        activityService.addActivity(activity, user.getId());
        return "redirect:/teacher/dashboard";
    }

    @GetMapping("/teacher/activity/online/{id}")
    public String online(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null || !"teacher".equals(user.getRole())) return "redirect:/login";

        activityService.online(id);
        return "redirect:/teacher/dashboard";
    }

    @GetMapping("/teacher/activity/offline/{id}")
    public String offline(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null || !"teacher".equals(user.getRole())) return "redirect:/login";

        activityService.offline(id);
        return "redirect:/teacher/dashboard";
    }
}
