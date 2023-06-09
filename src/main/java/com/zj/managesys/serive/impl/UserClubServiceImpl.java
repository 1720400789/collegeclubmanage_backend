package com.zj.managesys.serive.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.managesys.entity.UserClub;
import com.zj.managesys.mapper.UserClubMapper;
import com.zj.managesys.serive.UserClubService;
import org.springframework.stereotype.Service;

@Service
public class UserClubServiceImpl extends ServiceImpl<UserClubMapper, UserClub> implements UserClubService {
}
