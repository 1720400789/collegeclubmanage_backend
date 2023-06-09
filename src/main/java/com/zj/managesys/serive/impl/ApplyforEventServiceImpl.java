package com.zj.managesys.serive.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.managesys.entity.ApplyforEvent;
import com.zj.managesys.mapper.ApplyforEventMapper;
import com.zj.managesys.serive.ApplyforEventService;
import org.springframework.stereotype.Service;

@Service
public class ApplyforEventServiceImpl extends ServiceImpl<ApplyforEventMapper, ApplyforEvent> implements ApplyforEventService {
}
