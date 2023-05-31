package com.zj.managesys.serive.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.managesys.entity.Administrator;
import com.zj.managesys.mapper.AdministratorMapper;
import com.zj.managesys.serive.AdministratorService;
import org.springframework.stereotype.Service;

@Service
public class AdministratorServiceImpl extends ServiceImpl<AdministratorMapper, Administrator> implements AdministratorService {
}
