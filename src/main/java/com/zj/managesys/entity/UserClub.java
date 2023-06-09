package com.zj.managesys.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserClub implements Serializable {
    private Long clubId;

    private Long userId;
}
