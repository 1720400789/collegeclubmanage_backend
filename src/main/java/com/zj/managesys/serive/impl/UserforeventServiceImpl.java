package com.zj.managesys.serive.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.managesys.entity.Userforevent;
import com.zj.managesys.mapper.UserforeventMapper;
import com.zj.managesys.serive.UserforeventService;
import org.springframework.stereotype.Service;

@Service
public class UserforeventServiceImpl extends ServiceImpl<UserforeventMapper, Userforevent> implements UserforeventService {
}
