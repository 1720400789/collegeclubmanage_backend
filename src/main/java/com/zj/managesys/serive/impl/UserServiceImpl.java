package com.zj.managesys.serive.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.managesys.entity.User;
import com.zj.managesys.mapper.UserMapper;
import com.zj.managesys.serive.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
