package com.campus.campusactivity.service;

import com.campus.campusactivity.entity.User;
import com.campus.campusactivity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 用户注册
     */
    public String register(User user) {
        // 检查账号是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            return "账号已存在";
        }
        // 密码加密后存入数据库
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "注册成功";
    }

    /**
     * 用户登录校验
     */
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return null;
        }
        // 比对加密后的密码
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }
}
