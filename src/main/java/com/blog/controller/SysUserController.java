package com.blog.controller;

import com.blog.common.Result;
import com.blog.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * [管理端] 创建新的管理员账号 (如面试官专用账号)
     */
    @PostMapping("/admin/create")
    public Result<String> createAdmin(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || password == null) {
            return Result.error(400, "账号和密码不能为空");
        }
        try {
            sysUserService.createAdmin(username, password);
            return Result.success("创建管理员账号成功");
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }

    /**
     * [管理端] 删除某个管理员账号
     */
    @DeleteMapping("/admin/{id}")
    public Result<String> deleteAdmin(@PathVariable Long id) {
        // 注意：实际项目中应该限制不能删除超级管理员自身（这里为了简化暂时只做删除逻辑）
        sysUserService.removeById(id);
        return Result.success("删除管理员成功");
    }
}
