package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.Moment;
import com.blog.entity.MomentMedia;
import com.blog.mapper.MomentMapper;
import com.blog.mapper.MomentMediaMapper;
import com.blog.service.MomentService;
import com.blog.vo.MomentDTO;
import com.blog.vo.MomentMediaDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MomentServiceImpl extends ServiceImpl<MomentMapper, Moment> implements MomentService {

    @Autowired
    private MomentMediaMapper momentMediaMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMoment(MomentDTO dto) {
        // 1. 保存文字主体
        Moment moment = new Moment();
        BeanUtils.copyProperties(dto, moment);
        if (moment.getTag() == null || moment.getTag().trim().isEmpty()) {
            moment.setTag("生活随笔");
        }
        if (moment.getLikes() == null) {
            moment.setLikes(0);
        }
        if (moment.getCreateTime() == null) {
            moment.setCreateTime(LocalDateTime.now());
        }
        if (moment.getId() != null) {
            this.updateById(moment);
        } else {
            this.save(moment);
        }

        // 2. 保存无限嵌套的多媒体文件（图片/视频混合）
        List<MomentMediaDTO> mediaList = dto.getMediaList();
        if (mediaList != null) {
            if (dto.getId() != null) {
                LambdaQueryWrapper<MomentMedia> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(MomentMedia::getMomentId, dto.getId());
                momentMediaMapper.delete(wrapper);
            }
            if (!mediaList.isEmpty()) {
                for (MomentMediaDTO mediaDTO : mediaList) {
                    MomentMedia media = new MomentMedia();
                    BeanUtils.copyProperties(mediaDTO, media);
                    media.setMomentId(moment.getId()); // 绑定动态主键
                    momentMediaMapper.insert(media);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMoment(Long id) {
        // 1. 删主体
        this.removeById(id);

        // 2. 删从表的所有多媒体记录
        LambdaQueryWrapper<MomentMedia> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MomentMedia::getMomentId, id);
        momentMediaMapper.delete(wrapper);
    }

    @Override
    public List<Moment> listWithMedia() {
        LambdaQueryWrapper<Moment> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Moment::getCreateTime);
        List<Moment> list = this.list(wrapper);
        if (list != null && !list.isEmpty()) {
            List<Long> ids = list.stream().map(Moment::getId).toList();
            LambdaQueryWrapper<MomentMedia> mediaWrapper = new LambdaQueryWrapper<>();
            mediaWrapper.in(MomentMedia::getMomentId, ids);
            List<MomentMedia> mediaList = momentMediaMapper.selectList(mediaWrapper);
            if (mediaList != null && !mediaList.isEmpty()) {
                java.util.Map<Long, List<MomentMedia>> mediaMap = mediaList.stream()
                        .collect(java.util.stream.Collectors.groupingBy(MomentMedia::getMomentId));
                for (Moment m : list) {
                    m.setMediaList(mediaMap.getOrDefault(m.getId(), java.util.Collections.emptyList()));
                }
            } else {
                for (Moment m : list) {
                    m.setMediaList(java.util.Collections.emptyList());
                }
            }
        }
        return list;
    }

    @Override
    public Integer likeMoment(Long id) {
        Moment moment = this.getById(id);
        if (moment != null) {
            int newLikes = (moment.getLikes() == null ? 0 : moment.getLikes()) + 1;
            moment.setLikes(newLikes);
            this.updateById(moment);
            return newLikes;
        }
        return 0;
    }
}
