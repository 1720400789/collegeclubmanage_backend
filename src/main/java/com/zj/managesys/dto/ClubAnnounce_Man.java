package com.zj.managesys.dto;

import com.zj.managesys.entity.Clubannounce;
import lombok.Data;

@Data
public class ClubAnnounce_Man extends Clubannounce {
    private String admName;

    private String admPhone;
}
