package com.zj.managesys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zj.managesys.common.R;
import com.zj.managesys.dto.Userforevent_User;
import com.zj.managesys.entity.*;
import com.zj.managesys.serive.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/userevent")
@CrossOrigin
public class UserEventController {

    @Autowired
    private UserEventService userEventService;

    @Autowired
    private UserforeventService userforeventService;

    @Autowired
    private ApplyforEventService applyforEventService;

    @Autowired
    private UserService userService;

    @GetMapping("/page")
    public R<Page> getUserforEventPage(int page, int pageSize, int status, Long clubid, Long admid){
        log.info("信息：{},{},{},{},{}", page, pageSize, status, clubid, admid);

        Page<ApplyforEvent> list = new Page<>(page, pageSize);
        Page<Userforevent_User> pageDto = new Page<>(page, pageSize);
//        List<ApplyforEvent> list = new ArrayList<>();
//        List<Userforevent_User> listDto = new ArrayList<>();
        //查询所有信息
        if(clubid == 0){
            LambdaQueryWrapper<ApplyforEvent> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ApplyforEvent::getStatus, 1);
            queryWrapper.eq(ApplyforEvent::getMemberId, admid);
            applyforEventService.page(list, queryWrapper);
        }else{
            //查询对应社团id信息
            LambdaQueryWrapper<ApplyforEvent> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ApplyforEvent::getStatus, 1);
            queryWrapper.eq(ApplyforEvent::getClubId, clubid);
            queryWrapper.eq(ApplyforEvent::getMemberId, admid);
            applyforEventService.page(list,queryWrapper);
        }

        if(list.getRecords().isEmpty()) return R.error("无数据");
        LambdaQueryWrapper<Userforevent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Userforevent::getStatus, status);
        List<ApplyforEvent> alist = list.getRecords();
//        List<ApplyforEvent> finalList = list;
        wrapper.and(wp -> {
            for(ApplyforEvent item : alist){
                wp.or(wq -> wq.eq(Userforevent::getEventId, item.getId()));
            }
        });
        Page<Userforevent> page1 = new Page<>(page, pageSize);
        userforeventService.page(page1, wrapper);
        List<Userforevent> listInfo = page1.getRecords();

        if(listInfo.isEmpty()) return R.error("无数据");

        BeanUtils.copyProperties(page1, pageDto, "records");
        List<Userforevent_User> listDto = listInfo.stream().map((item) -> {
            Userforevent_User dto = new Userforevent_User();
            BeanUtils.copyProperties(item, dto);
            User user = userService.getById(item.getUserId());
            if(user != null){
                dto.setGrade(user.getGrade());
                dto.setMajor(user.getMajor());
                dto.setMName(user.getMName());
                dto.setPicture(user.getPicture());
                dto.setSex(user.getSex());
                dto.setMPhone(user.getMPhone());
            }
            ApplyforEvent event = applyforEventService.getById(item.getEventId());
            dto.setEventName(event.getEventname());
            return dto;
        }).collect(Collectors.toList());

        pageDto.setRecords(listDto);
        return R.success(pageDto);
    }

    @Transactional
    @PutMapping("/pass")
    public R<String> passApply(Long id){
        Userforevent userforevent = userforeventService.getById(id);

        UserEvent userEvent = new UserEvent();
        userEvent.setEventId(userforevent.getEventId());
        userEvent.setUserId(userforevent.getUserId());
        userEvent.setJoinTime(LocalDateTime.now());

        userEventService.save(userEvent);
        userforevent.setStatus(1);
        userforeventService.updateById(userforevent);

        return R.success("通过审批");
    }

    @PutMapping("/dispass")
    public R<String> dispassApply(Long id){
        Userforevent userforevent = userforeventService.getById(id);
        userforevent.setStatus(2);
        userforeventService.updateById(userforevent);

        return R.success("已驳回申请");
    }
}
