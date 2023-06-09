package com.zj.managesys.dto;

import com.zj.managesys.entity.Applyforjoin;
import lombok.Data;

import java.io.Serializable;

@Data
public class ApplyJoinClub_User extends Applyforjoin implements Serializable {
    //用户账号
    private String mAccount;
    //用户姓名
    private String mName;
    //用户头像
    private String mPicture;
    //用户年级
    private Integer grade;
    //用户所学专业
    private String major;
    //用户联系电话
    private String mPhone;

    private Integer mSex;
}
