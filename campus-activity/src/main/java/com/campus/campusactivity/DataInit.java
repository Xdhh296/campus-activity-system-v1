package com.campus.campusactivity;

import com.campus.campusactivity.entity.User;
import com.campus.campusactivity.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInit implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInit(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 启动时自动检测：不存在admin账号就创建，默认账号admin/密码admin123
        if (userRepository.findByUsername("admin") == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("admin");
            admin.setName("系统管理员");
            admin.setAccountStatus(1);
            userRepository.save(admin);
            System.out.println("===== 管理员账号已自动创建：账号 admin，密码 admin123 =====");
        }
    }
}
