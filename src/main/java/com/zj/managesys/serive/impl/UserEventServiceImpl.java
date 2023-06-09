package com.zj.managesys.serive.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.managesys.entity.UserEvent;
import com.zj.managesys.mapper.UserEventMapper;
import com.zj.managesys.serive.UserEventService;
import org.springframework.stereotype.Service;

@Service
public class UserEventServiceImpl extends ServiceImpl<UserEventMapper, UserEvent> implements UserEventService {
}
