package com.zj.managesys.dto;

import com.zj.managesys.entity.Userforevent;
import lombok.Data;

import java.io.Serializable;

@Data
public class Userforevent_User extends Userforevent implements Serializable {
    private String eventName;

    private String mName;

    private String picture;

    private Integer grade;

    private String major;

    private Integer sex;

    private String mPhone;
}
