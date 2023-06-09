package com.zj.managesys.entity;

import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

@Data
public class UserEvent implements Serializable {

    private Long userId;

    private Long eventId;

    private Date joinTime;
}
