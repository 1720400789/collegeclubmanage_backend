package com.zj.managesys.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
//@TableName("clubmember")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    @JsonProperty("mName")
    private String mName;
    //账号名
    @JsonProperty("mAccount")
    private String mAccount;
    @JsonProperty("mPassword")
    private String mPassword;
    @JsonProperty("mPhone")
    private String mPhone;

    private String picture;

    private Integer grade;

    private String major;

    private Integer sex;

    private String description;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
//
//    @TableField(fill = FieldFill.INSERT_UPDATE)
//    private LocalDateTime updateTime;
//
//    @TableField(fill = FieldFill.INSERT)
//    private Long createUser;
//
//    @TableField(fill = FieldFill.INSERT_UPDATE)
//    private Long updateUser;
}
