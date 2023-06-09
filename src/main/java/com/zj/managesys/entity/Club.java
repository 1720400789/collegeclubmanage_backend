package com.zj.managesys.entity;

import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

@Data
public class Club implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String clubName;

    private Long chairmanId;

    private String description;

    private String picture;

    private Integer level;

    private String major;

    private Date birthTime;

    private String birthMan;

    private Integer num;

    private Integer status;
}
