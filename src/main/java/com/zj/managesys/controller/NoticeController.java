package com.zj.managesys.controller;

import com.zj.managesys.common.R;
import com.zj.managesys.entity.Clubannounce;
import com.zj.managesys.serive.ClubAnnounceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/notice")
@CrossOrigin
public class NoticeController {

    @Autowired
    private ClubAnnounceService clubAnnounceService;

    @PostMapping("/add")
    private R<String> addAnnounce(@RequestBody Clubannounce clubannounce){
        log.info("信息：{}", clubannounce.toString());

        clubannounce.setStartTime(LocalDateTime.now());
        clubAnnounceService.save(clubannounce);

        return R.success("发布成功");
    }
}
