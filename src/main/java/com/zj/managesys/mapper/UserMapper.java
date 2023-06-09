package com.zj.managesys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zj.managesys.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
