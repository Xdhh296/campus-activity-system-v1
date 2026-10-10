package com.campus.campusactivity.repository;

import com.campus.campusactivity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 根据账号查询用户
    User findByUsername(String username);
    // 检查账号是否已存在
    boolean existsByUsername(String username);
    // 按角色查询所有用户（管理员端用）
    List<User> findByRole(String role);
}
