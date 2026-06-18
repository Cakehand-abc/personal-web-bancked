package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.SysUser;
import com.blog.mapper.SysUserMapper;
import com.blog.service.SysUserService;
import com.blog.utils.JwtUtils;
import com.blog.vo.LoginDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public String login(LoginDTO dto) {
        // 1. 根据用户名查询数据库
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, dto.getUsername());
        SysUser user = this.getOne(wrapper);

        // 2. 如果查不到用户，或者密码不匹配（用 BCrypt 解析比对）
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("账号或密码错误");
        }

        // 3. 登录成功，签发 JWT 令牌
        return jwtUtils.generateToken(user.getUsername());
    }

    @Override
    public void initMasterAccount() {
        // 检查是否已经有账号了
        if (this.count() > 0) {
            throw new RuntimeException("系统已经初始化过管理员，不能重复初始化！");
        }
        SysUser master = new SysUser();
        master.setUsername("admin"); // 您可以随后在数据库中改成胡吉涵
        master.setPassword(passwordEncoder.encode("Hujihan608624")); // 存入 BCrypt 加密后的密文
        master.setNickname("超级管理员");
        this.save(master);
    }

    @Override
    public void createAdmin(String username, String rawPassword) {
        // 检查账号是否已存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        if (this.count(wrapper) > 0) {
            throw new RuntimeException("账号已存在！");
        }
        
        SysUser admin = new SysUser();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(rawPassword));
        admin.setNickname("管理员");
        this.save(admin);
    }
}
