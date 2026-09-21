package com.blog.controller;

import com.blog.common.Result;
import com.blog.entity.GalleryAlbum;
import com.blog.entity.GalleryPhoto;
import com.blog.service.GalleryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gallery")
public class GalleryController {

    @Autowired
    private GalleryService galleryService;

    /**
     * [前台/管理端] 查询所有相册及其关联的照片列表
     */
    @GetMapping("/albums")
    public Result<List<GalleryAlbum>> getAlbums() {
        return Result.success(galleryService.listAlbumsWithPhotos());
    }

    /**
     * [管理端] 保存或修改相册
     */
    @PostMapping("/admin/albums")
    public Result<String> saveAlbum(@RequestBody GalleryAlbum album) {
        galleryService.saveOrUpdateAlbum(album);
        return Result.success("相册已成功保存");
    }

    /**
     * [管理端] 删除相册及其关联的照片
     */
    @DeleteMapping("/admin/albums/{id}")
    public Result<String> deleteAlbum(@PathVariable String id) {
        galleryService.deleteAlbum(id);
        return Result.success("相册已成功删除");
    }

    /**
     * [管理端] 新增或修改单张照片
     */
    @PostMapping("/admin/photos")
    public Result<String> savePhoto(@RequestBody GalleryPhoto photo) {
        galleryService.addOrUpdatePhoto(photo);
        return Result.success("照片已成功保存");
    }

    /**
     * [管理端] 删除单张照片
     */
    @DeleteMapping("/admin/photos/{id}")
    public Result<String> deletePhoto(@PathVariable Long id) {
        galleryService.deletePhoto(id);
        return Result.success("照片已成功删除");
    }
}
