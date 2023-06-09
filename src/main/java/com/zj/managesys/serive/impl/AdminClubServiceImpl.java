package com.zj.managesys.serive.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.managesys.entity.AdminClub;
import com.zj.managesys.mapper.AdminClubMapper;
import com.zj.managesys.serive.AdminClubService;
import org.springframework.stereotype.Service;

@Service
public class AdminClubServiceImpl extends ServiceImpl<AdminClubMapper, AdminClub> implements AdminClubService {
}
