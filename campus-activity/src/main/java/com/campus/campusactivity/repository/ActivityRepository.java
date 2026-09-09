package com.campus.campusactivity.repository;

import com.campus.campusactivity.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    // 查询所有上架的活动
    List<Activity> findByStatus(Integer status);
    // 查询某个教师发布的所有活动
    List<Activity> findByTeacherId(Long teacherId);
}
