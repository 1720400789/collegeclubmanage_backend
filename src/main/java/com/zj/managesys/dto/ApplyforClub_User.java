package com.zj.managesys.dto;

import com.zj.managesys.entity.Applyforclub;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户申请成立社团的多表联立实体类
 */
@Data
public class ApplyforClub_User extends Applyforclub implements Serializable {
    private String mAccount;

    private String mName;
}
