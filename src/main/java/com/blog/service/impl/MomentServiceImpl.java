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
        if (moment.getCreateTime() == null) {
            moment.setCreateTime(LocalDateTime.now());
        }
        this.save(moment);

        // 2. 保存无限嵌套的多媒体文件（图片/视频混合）
        List<MomentMediaDTO> mediaList = dto.getMediaList();
        if (mediaList != null && !mediaList.isEmpty()) {
            for (MomentMediaDTO mediaDTO : mediaList) {
                MomentMedia media = new MomentMedia();
                BeanUtils.copyProperties(mediaDTO, media);
                media.setMomentId(moment.getId()); // 绑定动态主键
                momentMediaMapper.insert(media);
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
}
