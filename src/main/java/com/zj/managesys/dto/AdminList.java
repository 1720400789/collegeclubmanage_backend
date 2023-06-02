package com.zj.managesys.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminList implements Serializable {

    private String flag;

    private String[] ids;
}
