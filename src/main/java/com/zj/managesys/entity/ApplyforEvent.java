package com.zj.managesys.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDateTime;

@Data
public class ApplyforEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String eventname;

    private Long clubId;

    private Long memberId;

    private String eventDescription;

    private Integer status;

    private Date startTime;

    private Date endTime;

    private String spot;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}
