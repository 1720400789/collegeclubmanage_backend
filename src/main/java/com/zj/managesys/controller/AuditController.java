package com.zj.managesys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zj.managesys.common.R;
import com.zj.managesys.dto.AdminList;
import com.zj.managesys.dto.ApplyJoinClub_User;
import com.zj.managesys.dto.ApplyforClub_User;
import com.zj.managesys.dto.ApplyforEvent_User;
import com.zj.managesys.entity.*;
import com.zj.managesys.serive.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 审核各项申请的控制器
 */
@Slf4j
@RestController
@RequestMapping("/audit")
@CrossOrigin
public class AuditController {

    @Autowired
    private AdministratorService administratorService;

    @Autowired
    private ApplyforclubService applyforclubService;

    @Autowired
    private ClubSerivce clubSerivce;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminClubService adminClubService;

    @Autowired
    private ApplyforEventService applyforEventService;

    @Autowired
    private ApplyforjoinService applyforjoinService;

    @Autowired
    private UserClubService userClubService;

    /**
     * 审批通过用户建立社团
     * 前端携带的参数
     * @param applyforclub
     * @return
     */
    @Transactional//开启Spring事务
    @PostMapping("/aggreforclub")
    public R<String> aggreforclub(@RequestBody Applyforclub applyforclub){
        //修改ApplyforClub的status
        LambdaUpdateWrapper<Applyforclub> updateApplyforclubWrapper = new LambdaUpdateWrapper<>();
        updateApplyforclubWrapper.eq(Applyforclub::getId, applyforclub.getId());
        updateApplyforclubWrapper.set(Applyforclub::getStatus, new Integer(1));
        applyforclubService.update(null, updateApplyforclubWrapper);

        //向社团表中新增社团
        Club club = new Club();
        club.setId(applyforclub.getId());
        club.setClubName(applyforclub.getApplyName());
        club.setLevel(applyforclub.getLevel());
        club.setChairmanId(applyforclub.getMemberId());
        club.setNum(new Integer(0));
        //如果是补录的
        if(applyforclub.getIfExit() == new Integer(1)){
            club.setBirthTime(applyforclub.getBirthTime());
            club.setBirthMan(applyforclub.getBirthMan());
        }
        clubSerivce.save(club);

        //根据申请人id查询申请人信息
        User user = userService.getById(applyforclub.getMemberId());

        //向管理员表中新增管理员
        Administrator adm = administratorService.getById(user.getId());

        Administrator administrator = new Administrator();
        if(adm == null){
            administrator.setAccount(user.getMAccount());
            administrator.setId(user.getId());
            administrator.setPassword(user.getMPassword());
            administrator.setFlag(new Integer(1));//普通管理员
            administrator.setPhone(user.getMPhone());
            administrator.setName(user.getMName());
            administratorService.save(administrator);
        }

        //向admin_club表中新增关联
        AdminClub adminClub = new AdminClub();
        adminClub.setAdminId(user.getId());
        adminClub.setClubId(club.getId());
        adminClubService.save(adminClub);

        return R.success("通过审批");
    }

    @GetMapping("/clubpage")
    public R<Page> page(int page, int pageSize, String name, String status){
        log.info("page:{}, pagesize:{}, name:{}, status:{}", page, pageSize, name, status);
        //构造分页构造器对象
        Page<Applyforclub> pageInfo = new Page<>(page, pageSize);
        Page<ApplyforClub_User> dtoPageInfo = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Applyforclub> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null, Applyforclub::getApplyName, name);
        if(!"NaN".equals(status))
            queryWrapper.eq(Applyforclub::getStatus, Integer.parseInt(status)).or().eq(Applyforclub::getStatus, Integer.parseInt("2"));
        else
            queryWrapper.eq(Applyforclub::getStatus, 0);
//        queryWrapper.eq(!"NaN".equals(status), Applyforclub::getStatus, Integer.parseInt(status));
        //添加排序条件
        queryWrapper.orderByDesc(Applyforclub::getCreateTime);

        //执行分页查询
        applyforclubService.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo, dtoPageInfo, "records");

        List<Applyforclub> records = pageInfo.getRecords();
        List<ApplyforClub_User> list = records.stream().map((item) -> {
            ApplyforClub_User dto = new ApplyforClub_User();
            BeanUtils.copyProperties(item, dto);
            Long uerId = item.getMemberId();
            //根据id查询申请人信息
            User user = userService.getById(uerId);
            if(user != null){
                dto.setMAccount(user.getMAccount());
                dto.setMName(user.getMName());
            }
            return dto;
        }).collect(Collectors.toList());

        dtoPageInfo.setRecords(list);
        return R.success(dtoPageInfo);
    }

    @PostMapping("/disaggreforclub")
    public R<String> disaggreforclub(@RequestBody AdminList adminList){
        log.info("申请建立社团：{}", adminList);
        String[] arr = adminList.getIds();
        Long arrId = null;
        LambdaUpdateWrapper<Applyforclub> updateWrapper = new LambdaUpdateWrapper<>();
        arrId = Long.parseLong(arr[0]);
        updateWrapper.eq(Applyforclub::getId, arrId);
        updateWrapper.set(Applyforclub::getStatus, new Integer(2));
        applyforclubService.update(null, updateWrapper);
        return R.success("修改成功");
    }

    @GetMapping("/eventpage")
    public R<Page> eventPage(int page, int pageSize, String name, String status){
        log.info("page:{}, pagesize:{}, name:{}, status:{}", page, pageSize, name, status);

        Page<ApplyforEvent> pageInfo = new Page<>(page, pageSize);
        Page<ApplyforEvent_User> dtoPageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<ApplyforEvent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, ApplyforEvent::getEventname, name);
        if(!"NaN".equals(status))
            queryWrapper.eq(ApplyforEvent::getStatus, Integer.parseInt(status)).or().eq(ApplyforEvent::getStatus, Integer.parseInt("2"));
        else
            queryWrapper.eq(ApplyforEvent::getStatus, 0);
        queryWrapper.orderByDesc(ApplyforEvent::getCreateTime);

        applyforEventService.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo, dtoPageInfo, "records");

        List<ApplyforEvent> records = pageInfo.getRecords();
        List<ApplyforEvent_User> list = records.stream().map((item) -> {
            ApplyforEvent_User dto = new ApplyforEvent_User();
            BeanUtils.copyProperties(item, dto);
            Long uerId = item.getMemberId();
            //根据id查询申请人信息
            User user = userService.getById(uerId);
            if(user != null){
                dto.setMName(user.getMName());
                dto.setMAccount(user.getMAccount());
            }
            Long clubId = item.getClubId();
            Club club = clubSerivce.getById(clubId);
            if (club != null) {
                dto.setClubName(club.getClubName());
            }
            return dto;
        }).collect(Collectors.toList());

        dtoPageInfo.setRecords(list);
        return R.success(dtoPageInfo);

    }

    @Transactional
    @PostMapping("/statusforevent")
    public R<String> statusforevent(@RequestBody AdminList adminList){
        log.info("申请建立社团：{}", adminList);
        String[] arr = adminList.getIds();
        Long arrId = null;
        LambdaUpdateWrapper<ApplyforEvent> updateWrapper = new LambdaUpdateWrapper<>();
        arrId = Long.parseLong(arr[0]);
        updateWrapper.eq(ApplyforEvent::getId, arrId);
        updateWrapper.set(ApplyforEvent::getStatus, Integer.parseInt(adminList.getFlag()));
        applyforEventService.update(null, updateWrapper);

        return R.success("修改成功");
    }

    @GetMapping("/joinclubpage")
    public R<List> getjoinpage(Long admid, Long clubid) {
        log.info("要查询的申请所属社团：{}, 社团管理员：{}", clubid, admid);

        Page<Applyforjoin> pageInfo = new Page<>(1, 100);
        Page<ApplyJoinClub_User> pageDto = new Page<>(1, 100);

        if(admid != null){
            if(clubid.intValue() == 0){
                List<AdminClub> adminClubs = new ArrayList<>();
                LambdaQueryWrapper<AdminClub> adminClubLambdaWrapper = new LambdaQueryWrapper<>();
                adminClubLambdaWrapper.eq(AdminClub::getAdminId, admid);
                adminClubs = adminClubService.list(adminClubLambdaWrapper);

                LambdaQueryWrapper<Applyforjoin> applyforjoinLambdaQueryWrapper = new LambdaQueryWrapper<>();
//            applyforjoinLambdaQueryWrapper.orderByDesc(Applyforjoin::getCreateTime);
                applyforjoinLambdaQueryWrapper.eq(Applyforjoin::getStatus, 0);

                List<AdminClub> finalAdminClubs = adminClubs;
                applyforjoinLambdaQueryWrapper.and(warpper -> {
                    for(AdminClub item: finalAdminClubs){
//                    applyforjoinLambdaQueryWrapper.eq(Applyforjoin::getClubId, item.getClubId()).or();
                        warpper.or(wq -> wq.eq(Applyforjoin::getClubId, item.getClubId()));
                    }
                });

                applyforjoinService.page(pageInfo, applyforjoinLambdaQueryWrapper);

                BeanUtils.copyProperties(pageInfo, pageDto, "records");

                List<Applyforjoin> records = pageInfo.getRecords();
                List<ApplyJoinClub_User> list = records.stream().map((item) -> {
                    ApplyJoinClub_User dto = new ApplyJoinClub_User();
                    BeanUtils.copyProperties(item, dto);
                    Long userId = item.getMemberId();
                    User user = userService.getById(userId);
                    if(user != null){
                        dto.setMName(user.getMName());
                        dto.setMAccount(user.getMAccount());
                        dto.setGrade(user.getGrade());
                        dto.setMajor(user.getMajor());
                        dto.setMPicture(user.getPicture());
                        dto.setMPhone(user.getMPhone());
                        dto.setMSex(user.getSex());
                    }
                    return dto;
                }).collect(Collectors.toList());
                pageDto.setRecords(list);
            }else {
                LambdaQueryWrapper<Applyforjoin> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Applyforjoin::getClubId, clubid);
                queryWrapper.eq(Applyforjoin::getStatus, 0);

                applyforjoinService.page(pageInfo, queryWrapper);

                BeanUtils.copyProperties(pageInfo, pageDto, "records");

                List<Applyforjoin> records = pageInfo.getRecords();
                List<ApplyJoinClub_User> list = records.stream().map((item) -> {
                    ApplyJoinClub_User dto = new ApplyJoinClub_User();
                    BeanUtils.copyProperties(item, dto);
                    Long userId = item.getMemberId();
                    User user = userService.getById(userId);
                    if(user != null){
                        dto.setMName(user.getMName());
                        dto.setMAccount(user.getMAccount());
                        dto.setGrade(user.getGrade());
                        dto.setMajor(user.getMajor());
                        dto.setMPicture(user.getPicture());
                        dto.setMPhone(user.getMPhone());
                        dto.setMSex(user.getSex());
                    }
                    return dto;
                }).collect(Collectors.toList());
                pageDto.setRecords(list);
            }
        }else {
            return R.error("请重新登录");
        }


        return R.success(pageDto.getRecords());
    }

    @Transactional
    @PostMapping("/checkApply")
    public R<String> checkApply(Long id, int status){
        log.info("{}要修改的值：{}", id, status);

        Applyforjoin applyforjoin = applyforjoinService.getById(id);
        applyforjoin.setStatus(status);

        LambdaUpdateWrapper<Applyforjoin> applyforjoinLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        applyforjoinLambdaUpdateWrapper.eq(Applyforjoin::getId, id);
        applyforjoinService.update(applyforjoin, applyforjoinLambdaUpdateWrapper);

        if(status == 1){
            UserClub userClub = new UserClub();
            userClub.setUserId(applyforjoin.getMemberId());
            userClub.setClubId(applyforjoin.getClubId());
            userClubService.save(userClub);

            Club club = clubSerivce.getById(applyforjoin.getClubId());
            club.setNum(club.getNum() + 1);
            clubSerivce.updateById(club);
        }

        return R.success("审批完成");
    }
}
