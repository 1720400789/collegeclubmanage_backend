package com.zj.managesys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("admin_club")
public class AdminClub implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long adminId;

    private Long clubId;
}
