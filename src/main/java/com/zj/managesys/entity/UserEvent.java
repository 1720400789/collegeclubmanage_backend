package com.zj.managesys.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserEvent implements Serializable {

    private Long userId;

    private Long eventId;

    private LocalDateTime joinTime;
}
