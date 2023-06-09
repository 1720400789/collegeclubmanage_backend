package com.zj.managesys.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Userforevent implements Serializable {
    private Long id;

    private Long userId;

    private Long eventId;

    private String selfDescription;

    private String selfStrength;

    private String selfReason;
}
