package com.blog.controller;

import com.blog.common.Result;
import com.blog.service.SysUserService;
import com.blog.vo.LoginDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 账号密码登录接口 (由于在 SecurityConfig 中配置了放行，这个接口不需要携带 Token)
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO dto) {
        try {
            String token = sysUserService.login(dto);
            return Result.success(token);
        } catch (Exception e) {
            return Result.error(401, e.getMessage());
        }
    }

    /**
     * 一键初始化超级管理员账号 (只能调一次)
     */
    @GetMapping("/init")
    public Result<String> init() {
        try {
            sysUserService.initMasterAccount();
            return Result.success("初始化超级管理员成功！账号: admin, 密码: 您设置的密码");
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }
}
