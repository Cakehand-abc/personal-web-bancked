package com.blog.controller;

import com.blog.common.Result;
import com.blog.entity.Project;
import com.blog.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    /**
     * [前台] 查询所有开源项目列表
     */
    @GetMapping("/list")
    public Result<List<Project>> getProjectList() {
        List<Project> list = projectService.list();
        return Result.success(list);
    }

    /**
     * [管理端] 新增开源项目
     */
    @PostMapping("/admin/save")
    public Result<String> saveProject(@RequestBody Project project) {
        projectService.save(project);
        return Result.success("新增项目成功");
    }

    /**
     * [管理端] 修改开源项目
     */
    @PutMapping("/admin/update")
    public Result<String> updateProject(@RequestBody Project project) {
        projectService.updateById(project);
        return Result.success("修改项目成功");
    }

    /**
     * [管理端] 删除开源项目
     */
    @DeleteMapping("/admin/{id}")
    public Result<String> deleteProject(@PathVariable Long id) {
        projectService.removeById(id);
        return Result.success("删除项目成功");
    }
}
