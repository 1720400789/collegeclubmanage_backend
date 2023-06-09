package com.zj.managesys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zj.managesys.common.R;
import com.zj.managesys.dto.ClubAnnounce_Man;
import com.zj.managesys.dto.Club_Chairman;
import com.zj.managesys.dto.Event_Club_User;
import com.zj.managesys.entity.*;
import com.zj.managesys.serive.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/event")
@CrossOrigin
public class EventController {

    @Autowired
    private ApplyforEventService applyforEventService;

    @Autowired
    private AdminClubService adminClubService;

    @Autowired
    private ClubAnnounceService clubAnnounceService;

    @Autowired
    private AdministratorService administratorService;

    @Autowired
    private UserEventService userEventService;

    @Autowired
    private ClubSerivce clubSerivce;

    @Autowired
    private UserforeventService userforeventService;

    @GetMapping("/page")
    public R<Page> getEventPage(int page, int pageSize, int status, Long clubid, Long admid){
        log.info("信息：{},{},{},{},{}", page, pageSize, status, clubid, admid);

        Page<ApplyforEvent> pageInfo = new Page<>(page, pageSize);
        if(clubid.intValue() == 0){
            LambdaQueryWrapper<AdminClub> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AdminClub::getAdminId, admid);
            List<AdminClub> adminClubList =  adminClubService.getBaseMapper().selectList(wrapper);

            LambdaQueryWrapper<ApplyforEvent> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ApplyforEvent::getStatus, status);
            queryWrapper.and(wrapperk -> {
                for(AdminClub item : adminClubList){
                    wrapperk.or(wq -> wq.eq(ApplyforEvent::getClubId, item.getClubId()));
                }
            });

            applyforEventService.page(pageInfo, queryWrapper);
            if(pageInfo.getRecords().isEmpty())
                return R.error("暂无数据");
        }else {
            LambdaQueryWrapper<ApplyforEvent> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ApplyforEvent::getStatus, status);
            wrapper.eq(ApplyforEvent::getClubId, clubid);
            wrapper.eq(ApplyforEvent::getMemberId, admid);
            applyforEventService.page(pageInfo, wrapper);
            if(pageInfo.getRecords().isEmpty())
                return R.error("暂无数据");
        }

        return R.success(pageInfo);
    }

    @PutMapping("/edit")
    public R<String> edit(@RequestBody ApplyforEvent applyforEvent){
        log.info("信息：{}", applyforEvent.toString());

        LambdaUpdateWrapper<ApplyforEvent> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ApplyforEvent::getId, applyforEvent.getId());
        wrapper.set(ApplyforEvent::getEventname, applyforEvent.getEventname());
        wrapper.set(ApplyforEvent::getEventDescription, applyforEvent.getEventDescription());
        wrapper.set(ApplyforEvent::getStartTime, applyforEvent.getStartTime());
        wrapper.set(ApplyforEvent::getEndTime, applyforEvent.getEndTime());
        wrapper.set(ApplyforEvent::getSpot, applyforEvent.getSpot());
        applyforEventService.update(applyforEvent, wrapper);

        return R.success("操作成功");
    }

    @DeleteMapping("/delete")
    public R<String> deleteEvent(Long id){
        log.info("信息：{}", id);

        applyforEventService.removeById(id);

        return R.success("操作成功！");
    }

    @PostMapping("/add")
    public R<String> addEvent(@RequestBody ApplyforEvent applyforEvent) {
        log.info("信息：{}", applyforEvent.toString());

        applyforEventService.save(applyforEvent);

        return R.success("申请活动成功");
    }

    @GetMapping("/getAllAnnoun")
    public R<List> getAllAnnouncement(){
        Page<Clubannounce> page = new Page<>(1,100);

        clubAnnounceService.page(page);
        List<Clubannounce> list = page.getRecords();
        List<ClubAnnounce_Man> listDto = list.stream().map((item) -> {
            ClubAnnounce_Man dto = new ClubAnnounce_Man();
            BeanUtils.copyProperties(item, dto);
            Administrator administrator = administratorService.getById(item.getManId());
            dto.setAdmName(administrator.getName());
            dto.setAdmPhone(administrator.getPhone());
            return dto;
        }).collect(Collectors.toList());

        if(listDto.isEmpty())
            return R.error("没有公告");

        return R.success(listDto);
    }

    @GetMapping("/getOtherEvent")
    public R<List> getOtherEvent(Long id){
        log.info("信息：{}", id);

        LambdaQueryWrapper<UserEvent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEvent::getUserId, id);
        List<UserEvent> list = userEventService.list(wrapper);

        List<ApplyforEvent> eventList = new ArrayList<>();
        if(list.isEmpty()){
            LambdaQueryWrapper<ApplyforEvent> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ApplyforEvent::getStatus, new Long(1));
            eventList = applyforEventService.list(queryWrapper);
        }else{
            LambdaQueryWrapper<ApplyforEvent> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ApplyforEvent::getStatus, new Long(1));
            queryWrapper.and(warpper -> {
                for(UserEvent item: list){
                    warpper.or((wq -> wq.eq(ApplyforEvent::getId, item.getEventId())));
                }
            });
            eventList = applyforEventService.list(queryWrapper);
        }
        if(eventList.isEmpty())
            return R.error("没有数据");

        List<Event_Club_User> idenList = eventList.stream().map((item) -> {
            Event_Club_User dto = new Event_Club_User();
            BeanUtils.copyProperties(item, dto);
            Long admId = item.getMemberId();
            Long clubId = item.getClubId();
            Administrator administrator = administratorService.getById(admId);
            Club club = clubSerivce.getById(clubId);
            dto.setAdmName(administrator.getName());
            dto.setAdmPhone(administrator.getPhone());
            dto.setClubName(club.getClubName());
            return dto;
        }).collect(Collectors.toList());

        return R.success(idenList);
    }

    @PostMapping("/subApplyForm")
    public R<String> subApplyForm(@RequestBody Userforevent userforevent){
        log.info("信息：{}", userforevent.toString());

        LambdaQueryWrapper<Userforevent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Userforevent::getUserId, userforevent.getUserId());
        wrapper.eq(Userforevent::getEventId, userforevent.getEventId());
        if(userforeventService.getOne(wrapper) != null)
            return R.error("请勿重复申请！");
        userforeventService.save(userforevent);
        return R.success("申请成功，请耐心等待");
    }
}
