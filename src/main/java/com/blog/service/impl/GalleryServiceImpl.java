package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.GalleryAlbum;
import com.blog.entity.GalleryPhoto;
import com.blog.mapper.GalleryAlbumMapper;
import com.blog.mapper.GalleryPhotoMapper;
import com.blog.service.GalleryService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GalleryServiceImpl extends ServiceImpl<GalleryAlbumMapper, GalleryAlbum> implements GalleryService {

    @Autowired
    private GalleryPhotoMapper photoMapper;

    @PostConstruct
    public void initSeedData() {
        try {
            long albumCount = this.count();
            if (albumCount == 0) {
                // 初始化种子相册 1：可爱流萤
                GalleryAlbum album1 = new GalleryAlbum();
                album1.setId("firefly-2026");
                album1.setName("可爱流萤");
                album1.setDescription("飞萤之火自无梦的长夜亮起，绽放在终竞的明天。");
                album1.setLocation("崩坏：星穹铁道");
                album1.setDate("2026-01-01");
                album1.setCover("/assets/images/DesktopWallpaper/d1.avif");
                album1.setRawTags("崩坏星穹铁道,流萤,崩铁");
                album1.setSortOrder(1);
                album1.setCreateTime(LocalDateTime.now());
                this.save(album1);

                String[][] photos1 = {
                        {"/assets/images/DesktopWallpaper/d1.avif", "星空流萤", "漫天星辰下的轻声低语"},
                        {"/assets/images/DesktopWallpaper/d2.avif", "林间微光", "阳光穿过树梢的宁静午后"},
                        {"/assets/images/DesktopWallpaper/d3.avif", "暮光之城", "夕阳染红云层的梦幻时刻"},
                        {"/assets/images/DesktopWallpaper/d4.avif", "静谧湖畔", "倒映着无垠星夜的清澈湖水"},
                        {"/assets/images/DesktopWallpaper/d5.avif", "飞萤之约", "向着晨曦与光芒奔赴"},
                        {"/assets/images/DesktopWallpaper/d6.avif", "繁星璀璨", "宇宙浩瀚，我们都是星尘"},
                        {"/assets/images/avatar.avif", "流萤肖像", "清澈温暖的温柔微笑"},
                        {"https://s41.ax1x.com/2026/05/13/peXsfit.webp", "夏夜流萤", "草木繁茂的夏日微风"},
                        {"https://s41.ax1x.com/2026/05/13/peXyh79.jpg", "晨光依稀", "晨曦中的甜美回眸"}
                };

                for (int i = 0; i < photos1.length; i++) {
                    GalleryPhoto photo = new GalleryPhoto();
                    photo.setAlbumId("firefly-2026");
                    photo.setUrl(photos1[i][0]);
                    photo.setTitle(photos1[i][1]);
                    photo.setDescription(photos1[i][2]);
                    photo.setSortOrder(i + 1);
                    photo.setCreateTime(LocalDateTime.now());
                    photoMapper.insert(photo);
                }

                // 初始化种子相册 2：星穹铁道精选壁纸
                GalleryAlbum album2 = new GalleryAlbum();
                album2.setId("star-rail-wallpapers");
                album2.setName("星穹铁道精选壁纸");
                album2.setDescription("银河铁道漫游中的唯美光影与二次元摄影集锦。");
                album2.setLocation("崩坏：星穹铁道");
                album2.setDate("2026-02-01");
                album2.setCover("/assets/images/DesktopWallpaper/d3.avif");
                album2.setRawTags("崩坏星穹铁道,壁纸,崩铁");
                album2.setSortOrder(2);
                album2.setCreateTime(LocalDateTime.now());
                this.save(album2);

                String[][] photos2 = {
                        {"/assets/images/DesktopWallpaper/d3.avif", "暮色流年", "私密珍藏的美好时刻"},
                        {"/assets/images/DesktopWallpaper/d4.avif", "星夜低语", "星夜低语"},
                        {"/assets/images/DesktopWallpaper/d5.avif", "清风微拂", "清风微拂"},
                        {"/assets/images/DesktopWallpaper/d6.avif", "璀璨星尘", "璀璨星尘"}
                };

                for (int i = 0; i < photos2.length; i++) {
                    GalleryPhoto photo = new GalleryPhoto();
                    photo.setAlbumId("star-rail-wallpapers");
                    photo.setUrl(photos2[i][0]);
                    photo.setTitle(photos2[i][1]);
                    photo.setDescription(photos2[i][2]);
                    photo.setSortOrder(i + 1);
                    photo.setCreateTime(LocalDateTime.now());
                    photoMapper.insert(photo);
                }

                System.out.println("成功初始化光影长廊相册种子数据");
            }
        } catch (Exception e) {
            System.out.println("检查或初始化相册种子数据已跳过或稍后执行: " + e.getMessage());
        }
    }

    @Override
    public List<GalleryAlbum> listAlbumsWithPhotos() {
        if (this.count() == 0) {
            initSeedData();
        }

        List<GalleryAlbum> albums = this.list(new LambdaQueryWrapper<GalleryAlbum>()
                .orderByAsc(GalleryAlbum::getSortOrder)
                .orderByDesc(GalleryAlbum::getCreateTime));

        for (GalleryAlbum album : albums) {
            // 解析标签
            if (album.getRawTags() != null && !album.getRawTags().trim().isEmpty()) {
                album.setTags(Arrays.stream(album.getRawTags().split("[,，]"))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList()));
            } else {
                album.setTags(new ArrayList<>());
            }

            // 查询该相册下的所有照片
            List<GalleryPhoto> photos = photoMapper.selectList(new LambdaQueryWrapper<GalleryPhoto>()
                    .eq(GalleryPhoto::getAlbumId, album.getId())
                    .orderByAsc(GalleryPhoto::getSortOrder)
                    .orderByAsc(GalleryPhoto::getId));
            album.setPhotos(photos != null ? photos : new ArrayList<>());
        }

        return albums;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateAlbum(GalleryAlbum album) {
        if (album.getId() == null || album.getId().trim().isEmpty()) {
            album.setId("album-" + System.currentTimeMillis());
        }

        if (album.getTags() != null) {
            album.setRawTags(String.join(",", album.getTags()));
        }

        GalleryAlbum existing = this.getById(album.getId());
        if (existing != null) {
            this.updateById(album);
        } else {
            if (album.getCreateTime() == null) {
                album.setCreateTime(LocalDateTime.now());
            }
            this.save(album);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAlbum(String id) {
        // 删除该相册下关联的照片
        photoMapper.delete(new LambdaQueryWrapper<GalleryPhoto>().eq(GalleryPhoto::getAlbumId, id));
        // 删除相册本身
        this.removeById(id);
    }

    @Override
    public void addOrUpdatePhoto(GalleryPhoto photo) {
        if (photo.getCreateTime() == null) {
            photo.setCreateTime(LocalDateTime.now());
        }
        if (photo.getId() != null && photo.getId() > 0) {
            photoMapper.updateById(photo);
        } else {
            photoMapper.insert(photo);
        }
    }

    @Override
    public void deletePhoto(Long photoId) {
        photoMapper.deleteById(photoId);
    }
}
