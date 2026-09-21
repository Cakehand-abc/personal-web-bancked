package com.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.common.Result;
import com.blog.entity.Message;
import com.blog.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api")
public class MessageController {

    @Autowired
    private MessageService messageService;
    
    private final String[] colors = {"#fef3c7", "#fce7f3", "#dbeafe", "#dcfce7", "#e0e7ff"};
    private final Random random = new Random();

    // ================= 前台接口 =================

    @GetMapping("/messages/list")
    public Result<List<Message>> listMessages() {
        // 前台展示最新的 50 条留言
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Message::getCreateTime).last("LIMIT 50");
        return Result.success(messageService.list(wrapper));
    }

    @PostMapping("/messages")
    public Result<String> addMessage(@RequestBody Message message) {
        if (message.getNickname() == null || message.getNickname().trim().isEmpty()) {
            message.setNickname("匿名游客");
        }
        if (message.getContent() == null || message.getContent().trim().isEmpty()) {
            return Result.error("留言内容不能为空");
        }
        
        // 随机分配颜色和旋转角度
        if (message.getColor() == null) {
            message.setColor(colors[random.nextInt(colors.length)]);
        }
        if (message.getRotation() == null) {
            message.setRotation(random.nextInt(9) - 4); // -4 to 4
        }
        
        message.setCreateTime(LocalDateTime.now());
        messageService.save(message);
        return Result.success("留言成功");
    }

    @DeleteMapping("/messages/{id}")
    public Result<String> deleteFrontMessage(@PathVariable Long id) {
        org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return Result.error(401, "无权删除留言，请登录管理员账号");
        }
        messageService.removeById(id);
        return Result.success("删除成功");
    }

    // ================= 后台管理接口 =================

    @GetMapping("/admin/messages/page")
    public Result<Page<Message>> pageAdminMessages(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Message> pageParam = new Page<>(current, size);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Message::getCreateTime);
        return Result.success(messageService.page(pageParam, wrapper));
    }

    @DeleteMapping("/admin/messages/{id}")
    public Result<String> deleteMessage(@PathVariable Long id) {
        messageService.removeById(id);
        return Result.success("删除成功");
    }
}
