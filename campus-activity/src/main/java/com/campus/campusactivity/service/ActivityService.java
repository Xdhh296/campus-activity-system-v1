package com.campus.campusactivity.service;

import com.campus.campusactivity.entity.Activity;
import com.campus.campusactivity.entity.Enrollment;
import com.campus.campusactivity.entity.User;
import com.campus.campusactivity.repository.ActivityRepository;
import com.campus.campusactivity.repository.EnrollmentRepository;
import com.campus.campusactivity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityService {

    // 状态常量
    private static final int STATUS_OFFICIAL = 0;  // 正式报名
    private static final int STATUS_WAIT = 1;       // 候补报名
    private static final int STATUS_CANCEL = 2;     // 已取消

    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private UserRepository userRepository;

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

    // 学生报名/候补
    @Transactional
    public String enroll(Long activityId, Long userId) {
        // 前置校验：账号是否被停用
        User user = userRepository.findById(userId).orElse(null);
        if (user != null && user.getAccountStatus() != null && user.getAccountStatus() == 0) {
            return "账号状态异常，暂无法报名";
        }
        // 校验是否已经报名/候补过
        if (enrollmentRepository.existsByActivityIdAndUserIdAndStatusNot(activityId, userId, STATUS_CANCEL)) {
            return "您已报名/候补过该活动";
        }
        Activity activity = activityRepository.findById(activityId).orElse(null);
        if (activity == null || activity.getStatus() == 0) {
            return "活动不存在或已下架";
        }
        Enrollment enrollment = new Enrollment();
        enrollment.setActivityId(activityId);
        enrollment.setUserId(userId);

        if (activity.getRemain() > 0) {
            // 有名额：正式报名
            activity.setRemain(activity.getRemain() - 1);
            activityRepository.save(activity);
            enrollment.setStatus(STATUS_OFFICIAL);
            enrollmentRepository.save(enrollment);
            return "报名成功";
        } else {
            // 满员：加入候补队列
            enrollment.setStatus(STATUS_WAIT);
            enrollment.setWaitTime(LocalDateTime.now());
            enrollmentRepository.save(enrollment);
            // 计算当前候补排位
            int waitRank = enrollmentRepository.findByActivityIdAndStatusOrderByWaitTimeAsc(activityId, STATUS_WAIT).size();
            return "活动名额已满，已加入候补队列，当前排位第" + waitRank;
        }
    }

    // 学生取消报名/候补
    @Transactional
    public String cancel(Long activityId, Long userId) {
        Enrollment enrollment = enrollmentRepository.findByActivityIdAndUserIdAndStatusNot(activityId, userId, STATUS_CANCEL);
        if (enrollment == null) {
            return "未报名/候补该活动";
        }
        // 先记录原来的报名状态（正式/候补）
        Integer oldStatus = enrollment.getStatus();
        Activity activity = activityRepository.findById(activityId).orElse(null);
        // 把当前记录标记为已取消
        enrollment.setStatus(STATUS_CANCEL);
        enrollmentRepository.save(enrollment);

        // 只有取消正式报名时，才释放名额并触发候补补位
        if (activity != null && oldStatus == STATUS_OFFICIAL) {
            activity.setRemain(activity.getRemain() + 1);
            // 自动顺位补位：找最早的候补
            List<Enrollment> waitList = enrollmentRepository.findByActivityIdAndStatusOrderByWaitTimeAsc(activityId, STATUS_WAIT);
            if (!waitList.isEmpty()) {
                Enrollment firstWait = waitList.get(0);
                firstWait.setStatus(STATUS_OFFICIAL);
                enrollmentRepository.save(firstWait);
                activity.setRemain(activity.getRemain() - 1);
            }
            activityRepository.save(activity);
        }
        return "取消成功";
    }

    // 查学生已正式报名的活动ID（用于页面按钮判断）
    public List<Long> listEnrolledIds(Long userId) {
        return enrollmentRepository.findByUserIdAndStatusNot(userId, STATUS_CANCEL)
                .stream()
                .filter(e -> e.getStatus() == STATUS_OFFICIAL)
                .map(Enrollment::getActivityId)
                .toList();
    }

    // 查学生所有未取消的报名/候补记录（用于页面展示状态）
    public List<Enrollment> listMyEnrollments(Long userId) {
        return enrollmentRepository.findByUserIdAndStatusNot(userId, STATUS_CANCEL);
    }
}
