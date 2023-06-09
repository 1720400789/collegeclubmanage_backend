package com.zj.managesys.dto;

import com.zj.managesys.entity.Club;
import lombok.Data;

import java.io.Serializable;

@Data
public class Club_Chairman extends Club implements Serializable {

    private String name;

    private String account;
}
