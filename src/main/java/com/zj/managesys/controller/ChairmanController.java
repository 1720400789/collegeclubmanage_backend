package com.zj.managesys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zj.managesys.common.R;
import com.zj.managesys.dto.Club_Chairman;
import com.zj.managesys.entity.AdminClub;
import com.zj.managesys.entity.Administrator;
import com.zj.managesys.entity.Club;
import com.zj.managesys.serive.AdminClubService;
import com.zj.managesys.serive.AdministratorService;
import com.zj.managesys.serive.ClubSerivce;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/chairman")
@CrossOrigin
public class ChairmanController {

    @Autowired
    private AdminClubService adminClubService;

    @Autowired
    private ClubSerivce clubSerivce;

    @Autowired
    private AdministratorService administratorService;

    @GetMapping("/clubList")
    public R<List> getClubList(Long id){
        log.info("管理员id：{}", id);

        LambdaQueryWrapper<AdminClub> adminClubLambdaQueryWrapper = new LambdaQueryWrapper<>();
        adminClubLambdaQueryWrapper.eq(AdminClub::getAdminId, id);
        List<AdminClub> adminClubs = adminClubService.list(adminClubLambdaQueryWrapper);

        Page<Club> page = new Page<>(1, 20);
        Page<Club_Chairman> pageDto = new Page<>(1, 20);

        LambdaQueryWrapper<Club> clubLambdaQueryWrapper = new LambdaQueryWrapper<>();
        for(AdminClub item: adminClubs){
            clubLambdaQueryWrapper.eq(Club::getId, item.getClubId()).or();
        }
        clubSerivce.page(page, clubLambdaQueryWrapper);

        BeanUtils.copyProperties(page, pageDto, "records");
        List<Club> records = page.getRecords();
        List<Club_Chairman> list = records.stream().map((item) -> {
            Club_Chairman dto = new Club_Chairman();
            BeanUtils.copyProperties(item, dto);
            Administrator administrator = administratorService.getById(item.getChairmanId());
            dto.setName(administrator.getName());
            dto.setAccount(administrator.getAccount());
            return dto;
        }).collect(Collectors.toList());

        pageDto.setRecords(list);
        return R.success(pageDto.getRecords());
    }
}
