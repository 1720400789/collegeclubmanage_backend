package com.zj.managesys.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDateTime;

@Data
public class Applyforclub implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String applyName;

    private Long memberId;

    private String clubDescription;

    private Integer status;

    private Integer level;

    private Integer ifExit;

    private Date birthTime;

    private String birthMan;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}
