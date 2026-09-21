package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.SysUser;
import com.blog.vo.LoginDTO;

public interface SysUserService extends IService<SysUser> {

    /**
     * 账号密码登录
     */
    String login(LoginDTO dto);

    /**
     * 账号密码登录 (支持双 Token + Redis 存储)
     */
    com.blog.vo.TokenVO loginWithTokens(LoginDTO dto);

    /**
     * 初始化第一个超级管理员账号 (仅当数据库为空时可用)
     */
    void initMasterAccount();

    /**
     * [管理端] 创建新的管理员账号 (供面试官等使用)
     */
    void createAdmin(String username, String rawPassword);

}
