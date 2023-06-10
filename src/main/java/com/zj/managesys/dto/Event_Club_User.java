package com.zj.managesys.dto;

import com.zj.managesys.entity.ApplyforEvent;
import lombok.Data;

import java.io.Serializable;

@Data
public class Event_Club_User extends ApplyforEvent implements Serializable {

    private String admName;

    private String admPhone;

    private String clubName;
}
