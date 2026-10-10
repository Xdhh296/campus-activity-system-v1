package com.campus.campusactivity.controller;

import com.campus.campusactivity.entity.Activity;
import com.campus.campusactivity.entity.Enrollment;
import com.campus.campusactivity.entity.User;
import com.campus.campusactivity.service.ActivityService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    // 统一角色跳转工具：根据用户角色跳到对应首页
    private String getHomeByRole(User user) {
        if ("teacher".equals(user.getRole())) return "redirect:/teacher/dashboard";
        if ("admin".equals(user.getRole())) return "redirect:/admin/dashboard";
        return "redirect:/index";
    }

    // ========== 学生端（仅学生可访问） ==========
    @GetMapping("/index")
    public String index(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";
        if (!"student".equals(user.getRole())) return getHomeByRole(user);

        List<Activity> list = activityService.listOnline();
        List<Long> enrolledIds = activityService.listEnrolledIds(user.getId());
        List<Enrollment> myEnrolls = activityService.listMyEnrollments(user.getId());
        Map<Long, Integer> enrollStatusMap = myEnrolls.stream()
                .collect(Collectors.toMap(Enrollment::getActivityId, Enrollment::getStatus));

        model.addAttribute("list", list);
        model.addAttribute("enrolledIds", enrolledIds);
        model.addAttribute("enrollStatusMap", enrollStatusMap);
        return "index";
    }

    @GetMapping("/activity/enroll/{id}")
    public String enroll(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";
        if (!"student".equals(user.getRole())) return getHomeByRole(user);

        String msg = activityService.enroll(id, user.getId());
        ra.addFlashAttribute("msg", msg);
        return "redirect:/index";
    }

    @GetMapping("/activity/cancel/{id}")
    public String cancel(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";
        if (!"student".equals(user.getRole())) return getHomeByRole(user);

        String msg = activityService.cancel(id, user.getId());
        ra.addFlashAttribute("msg", msg);
        return "redirect:/index";
    }

    // ========== 教师端（仅教师可访问） ==========
    @GetMapping("/teacher/dashboard")
    public String teacherDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";
        if (!"teacher".equals(user.getRole())) return getHomeByRole(user);

        List<Activity> list = activityService.listByTeacher(user.getId());
        model.addAttribute("list", list);
        return "teacher/dashboard";
    }

    @GetMapping("/teacher/activity/add")
    public String toAdd(HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";
        if (!"teacher".equals(user.getRole())) return getHomeByRole(user);
        return "teacher/activityAdd";
    }

    @PostMapping("/teacher/activity/save")
    public String save(Activity activity, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";
        if (!"teacher".equals(user.getRole())) return getHomeByRole(user);

        activityService.addActivity(activity, user.getId());
        return "redirect:/teacher/dashboard";
    }

    @GetMapping("/teacher/activity/online/{id}")
    public String online(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";
        if (!"teacher".equals(user.getRole())) return getHomeByRole(user);

        boolean success = activityService.online(id, user.getId());
        if (!success) ra.addFlashAttribute("msg", "无权操作他人活动");
        return "redirect:/teacher/dashboard";
    }

    @GetMapping("/teacher/activity/offline/{id}")
    public String offline(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) return "redirect:/login";
        if (!"teacher".equals(user.getRole())) return getHomeByRole(user);

        boolean success = activityService.offline(id, user.getId());
        if (!success) ra.addFlashAttribute("msg", "无权操作他人活动");
        return "redirect:/teacher/dashboard";
    }
}
