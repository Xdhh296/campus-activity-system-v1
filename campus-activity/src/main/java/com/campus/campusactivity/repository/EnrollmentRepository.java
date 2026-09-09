package com.campus.campusactivity.repository;

import com.campus.campusactivity.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    // 判断某个用户是否已经报名某个活动
    boolean existsByActivityIdAndUserId(Long activityId, Long userId);
    // 查询单条报名记录
    Enrollment findByActivityIdAndUserId(Long activityId, Long userId);
    // 查询某个用户的所有报名记录
    List<Enrollment> findByUserId(Long userId);
}
