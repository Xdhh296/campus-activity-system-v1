package com.campus.campusactivity.service;

import com.campus.campusactivity.entity.Activity;
import com.campus.campusactivity.entity.Enrollment;
import com.campus.campusactivity.repository.ActivityRepository;
import com.campus.campusactivity.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    // 学生：查所有上架活动
    public List<Activity> listOnline() {
        return activityRepository.findByStatus(1);
    }

    // 教师：查自己发布的所有活动
    public List<Activity> listByTeacher(Long teacherId) {
        return activityRepository.findByTeacherId(teacherId);
    }

    // 发布活动（默认上架）
    public void addActivity(Activity activity, Long teacherId) {
        activity.setTeacherId(teacherId);
        activity.setStatus(1);
        activity.setRemain(activity.getTotal());
        activityRepository.save(activity);
    }

    // 上架活动
    public void online(Long id) {
        Activity activity = activityRepository.findById(id).orElse(null);
        if (activity != null) {
            activity.setStatus(1);
            activityRepository.save(activity);
        }
    }

    // 下架活动
    public void offline(Long id) {
        Activity activity = activityRepository.findById(id).orElse(null);
        if (activity != null) {
            activity.setStatus(0);
            activityRepository.save(activity);
        }
    }

    // 学生报名
    @Transactional
    public String enroll(Long activityId, Long userId) {
        if (enrollmentRepository.existsByActivityIdAndUserId(activityId, userId)) {
            return "已报名该活动";
        }
        Activity activity = activityRepository.findById(activityId).orElse(null);
        if (activity == null || activity.getStatus() == 0) {
            return "活动不存在或已下架";
        }
        if (activity.getRemain() <= 0) {
            return "名额已满";
        }
        activity.setRemain(activity.getRemain() - 1);
        activityRepository.save(activity);

        Enrollment enrollment = new Enrollment();
        enrollment.setActivityId(activityId);
        enrollment.setUserId(userId);
        enrollmentRepository.save(enrollment);
        return "报名成功";
    }

    // 学生取消报名
    @Transactional
    public String cancel(Long activityId, Long userId) {
        Enrollment enrollment = enrollmentRepository.findByActivityIdAndUserId(activityId, userId);
        if (enrollment == null) {
            return "未报名该活动";
        }
        Activity activity = activityRepository.findById(activityId).orElse(null);
        if (activity != null) {
            activity.setRemain(activity.getRemain() + 1);
            activityRepository.save(activity);
        }
        enrollmentRepository.delete(enrollment);
        return "取消成功";
    }

    // 查学生已报名的活动ID
    public List<Long> listEnrolledIds(Long userId) {
        return enrollmentRepository.findByUserId(userId)
                .stream()
                .map(Enrollment::getActivityId)
                .toList();
    }
}
