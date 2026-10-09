package com.campus.campusactivity.repository;

import com.campus.campusactivity.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    // 判断某个用户是否在某个活动下有未取消的报名/候补记录
    boolean existsByActivityIdAndUserIdAndStatusNot(Long activityId, Long userId, Integer status);
    // 查询单条未取消的报名/候补记录
    Enrollment findByActivityIdAndUserIdAndStatusNot(Long activityId, Long userId, Integer status);
    // 查询某个用户的所有未取消报名记录
    List<Enrollment> findByUserIdAndStatusNot(Long userId, Integer status);
    // 查询某个活动下所有候补记录，按提交时间升序（排队顺序）
    List<Enrollment> findByActivityIdAndStatusOrderByWaitTimeAsc(Long activityId, Integer status);
}
