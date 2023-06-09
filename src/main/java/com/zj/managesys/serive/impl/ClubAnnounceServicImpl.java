package com.zj.managesys.serive.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.managesys.entity.Clubannounce;
import com.zj.managesys.mapper.ClubAnnounceMapper;
import com.zj.managesys.serive.ClubAnnounceService;
import org.springframework.stereotype.Service;

@Service
public class ClubAnnounceServicImpl extends ServiceImpl<ClubAnnounceMapper, Clubannounce> implements ClubAnnounceService {
}
