package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.GalleryAlbum;
import com.blog.entity.GalleryPhoto;

import java.util.List;

public interface GalleryService extends IService<GalleryAlbum> {

    /**
     * 查询所有相册及其关联的照片列表
     */
    List<GalleryAlbum> listAlbumsWithPhotos();

    /**
     * 保存或更新相册
     */
    void saveOrUpdateAlbum(GalleryAlbum album);

    /**
     * 删除相册及其关联的所有照片
     */
    void deleteAlbum(String id);

    /**
     * 保存或新增单张照片
     */
    void addOrUpdatePhoto(GalleryPhoto photo);

    /**
     * 删除单张照片
     */
    void deletePhoto(Long photoId);
}
