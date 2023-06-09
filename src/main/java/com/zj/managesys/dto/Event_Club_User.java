package com.zj.managesys.dto;

import com.zj.managesys.entity.ApplyforEvent;
import lombok.Data;

@Data
public class Event_Club_User extends ApplyforEvent {

    private String admName;

    private String admPhone;

    private String clubName;
}
