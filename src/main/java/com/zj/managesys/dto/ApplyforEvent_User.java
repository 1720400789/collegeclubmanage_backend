package com.zj.managesys.dto;

import com.zj.managesys.entity.ApplyforEvent;
import lombok.Data;

import java.io.Serializable;

@Data
public class ApplyforEvent_User extends ApplyforEvent implements Serializable {
    private String mAccount;

    private String mName;

    private String clubName;
}
