package com.zj.managesys.dto;

import com.zj.managesys.entity.Clubannounce;
import lombok.Data;

import java.io.Serializable;

@Data
public class ClubAnnounce_Man extends Clubannounce implements Serializable {
    private String admName;

    private String admPhone;
}
