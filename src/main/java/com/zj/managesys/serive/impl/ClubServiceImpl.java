package com.zj.managesys.serive.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.managesys.entity.Club;
import com.zj.managesys.mapper.ClubMapper;
import com.zj.managesys.serive.ClubSerivce;
import org.springframework.stereotype.Service;

@Service
public class ClubServiceImpl extends ServiceImpl<ClubMapper, Club> implements ClubSerivce {
}
