package com.zj.managesys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zj.managesys.common.R;
import com.zj.managesys.dto.AdminList;
import com.zj.managesys.dto.Club_Chairman;
import com.zj.managesys.entity.*;
import com.zj.managesys.serive.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/club")
@CrossOrigin
public class ClubController {

    @Autowired
    private ClubSerivce clubSerivce;

    @Autowired
    private AdministratorService administratorService;

    @Autowired
    private AdminClubService adminClubService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserClubService userClubService;

    @Autowired
    private ApplyforjoinService applyforjoinService;
    /**
     * 查询所有申请建立社团
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page:{}, pagesize:{}, name:{}", page, pageSize, name);
        //构造分页构造器对象
        Page<Club> pageInfo = new Page<>(page, pageSize);
        Page<Club_Chairman> dtoPageInfo = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Club> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null, Club::getClubName, name);

//        queryWrapper.eq(!"NaN".equals(status), Applyforclub::getStatus, Integer.parseInt(status));
        //添加排序条件
        queryWrapper.orderByDesc(Club::getNum);

        //执行分页查询
        clubSerivce.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo, dtoPageInfo, "records");

        List<Club> records = pageInfo.getRecords();
        List<Club_Chairman> list = records.stream().map((item) -> {
            Club_Chairman dto = new Club_Chairman();
            BeanUtils.copyProperties(item, dto);
            Long uerId = item.getChairmanId();
            //根据id查询申请人信息
            Administrator administrator = administratorService.getById(uerId);
            if(administrator != null){
                dto.setAccount(administrator.getAccount());
                dto.setName(administrator.getName());
            }
            return dto;
        }).collect(Collectors.toList());

        dtoPageInfo.setRecords(list);
        return R.success(dtoPageInfo);
    }

    @Transactional
    @PostMapping("/add")
    public R<String> addClubWithAdmin(@RequestBody Club_Chairman clubChairman){
        log.info("要新增的社团信息:{}", clubChairman.getClubName());
        //根据account获取管理员信息
        LambdaQueryWrapper<Administrator> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Administrator::getAccount, clubChairman.getAccount());
        Administrator administrator = administratorService.getOne(queryWrapper);
        //根据account获取用户信息
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getMAccount, clubChairman.getAccount());
        User user = userService.getOne(userLambdaQueryWrapper);

        if(administrator != null){
            clubChairman.setChairmanId(administrator.getId());
            clubSerivce.save(clubChairman);
            AdminClub adminClub = new AdminClub();
            adminClub.setAdminId(administrator.getId());


            LambdaQueryWrapper<Club> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(Club::getClubName, clubChairman.getClubName());
            Club club = clubSerivce.getOne(queryWrapper1);
            adminClub.setClubId(club.getId());

            adminClubService.save(adminClub);
        }else if(user != null){
            clubChairman.setChairmanId(user.getId());
            clubSerivce.save(clubChairman);

            Administrator administrator1 = new Administrator();
            administrator1.setId(user.getId());
            administrator1.setPhone(user.getMPhone());
            administrator1.setAccount(user.getMAccount());
            administrator1.setPassword(user.getMPassword());
            administrator1.setName(user.getMName());
            administratorService.save(administrator1);

            AdminClub adminClub = new AdminClub();
            adminClub.setAdminId(administrator1.getId());
            LambdaQueryWrapper<Club> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(Club::getClubName, clubChairman.getClubName());
            Club club = clubSerivce.getOne(queryWrapper1);
            adminClub.setClubId(club.getId());

            adminClubService.save(adminClub);
        }else {
            return R.error("请填写正确的负责人账号");
        }


        return R.success("新增社团成功");
    }

    @Transactional
    @PutMapping("/edit")
    public R<String> updateClubWithAdmin(@RequestBody Club_Chairman clubChairman){
        log.info("要修改的社团信息:{}", clubChairman.getClubName());

        LambdaQueryWrapper<Administrator> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Administrator::getAccount, clubChairman.getAccount());
        Administrator administrator = administratorService.getOne(queryWrapper);

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getMAccount, clubChairman.getAccount());
        User user = userService.getOne(userLambdaQueryWrapper);

        if(administrator != null){//是管理员
            clubChairman.setChairmanId(administrator.getId());
            clubSerivce.updateById(clubChairman);

            AdminClub adminClub = new AdminClub();
            adminClub.setClubId(clubChairman.getId());
            adminClub.setAdminId(administrator.getId());

            LambdaUpdateWrapper<AdminClub> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(AdminClub::getClubId, clubChairman.getId());
            adminClubService.update(adminClub, lambdaUpdateWrapper);
        }else if(user != null){//是普通用户
            clubChairman.setChairmanId(user.getId());
            clubSerivce.updateById(clubChairman);

            Administrator administrator1 = new Administrator();
            administrator1.setId(user.getId());
            administrator1.setPhone(user.getMPhone());
            administrator1.setAccount(user.getMAccount());
            administrator1.setPassword(user.getMPassword());
            administrator1.setName(user.getMName());
            administratorService.save(administrator1);

            AdminClub adminClub = new AdminClub();
            adminClub.setAdminId(administrator1.getId());
            adminClub.setClubId(clubChairman.getId());

            LambdaUpdateWrapper<AdminClub> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(AdminClub::getClubId, clubChairman.getId());
            adminClubService.update(adminClub, lambdaUpdateWrapper);
        }else{
            return R.error("请填写正确的负责人账号");
        }

        return R.success("修改社团信息成功");
    }

    @Transactional
    @PutMapping("/handlestatus")
    public R<String> updateStatus(@RequestBody AdminList adminList){
        log.info("ids:{}", adminList.getIds().length);
        String[] arr = adminList.getIds();
        Long[] arrId = new Long[30];
        LambdaUpdateWrapper<Club> updateWrapper = new LambdaUpdateWrapper<>();
        for(int i = 0; i < arr.length; i ++){
            arrId[i] = Long.parseLong(arr[i]);
            updateWrapper.eq(Club::getId, arrId[i]).or();
            log.info("ids:{}", arrId[i]);
        }
        updateWrapper.set(Club::getStatus, "1".equals(adminList.getFlag()) ? new Integer(0): new Integer(1));
        clubSerivce.update(null, updateWrapper);
        return R.success("修改成功");
    }

    /**
     * 获取用户加入的社团信息
     * @param userid
     * @return
     */
    @GetMapping("/selfclubPage")
    public R<List> getMyClubPage(Long userid){
        log.info("用户：{}", userid);

        Page<UserClub> pageInfo = new Page<>(1, 20);
        Page<Club> page = new Page<>(1, 20);
        Page<Club_Chairman> pageDto = new Page<>(1, 20);

        LambdaQueryWrapper<UserClub> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserClub::getUserId, userid);
        userClubService.page(pageInfo, lambdaQueryWrapper);

        if(!pageInfo.getRecords().isEmpty()){
            List<UserClub> userClubList = pageInfo.getRecords();
            List<Long> clubIds = new ArrayList<>();
            for(UserClub item : userClubList){
                clubIds.add(item.getClubId());
            }

            LambdaQueryWrapper<Club> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            for(Long item: clubIds){
                lambdaQueryWrapper1.eq(Club::getId, item.intValue()).or();
            }
            clubSerivce.page(page, lambdaQueryWrapper1);

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
        }else {
            return R.error("还未加入任何社团");
        }

        return R.success(pageDto.getRecords());
    }

    /**
     * 获取社团的用户信息
     */
    @GetMapping("/userinclub")
    public R<List> getUserinClub(Long id){
        log.info("社团id：{}", id);

        Page<UserClub> page = new Page<>(1, 100);

        LambdaQueryWrapper<UserClub> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserClub::getClubId, id);
        userClubService.page(page, lambdaQueryWrapper);
        List<UserClub> userClubList = page.getRecords();

        if(userClubList.isEmpty())
            return R.error("没有成员");

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        for(UserClub item : userClubList){
            userLambdaQueryWrapper.eq(User::getId, item.getUserId()).or();
        }
        Page<User> userPage = new Page<>(1, 100);
        userService.page(userPage, userLambdaQueryWrapper);

        return R.success(userPage.getRecords());
    }

    @GetMapping("/clubPage")
    public R<List> getOtherclubPage(Long id) {
        log.info("用户id：{}", id);

        Page<UserClub> page = new Page<>(1, 20);
        LambdaQueryWrapper<UserClub> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserClub::getUserId, id);
        userClubService.page(page, queryWrapper);

        //获得已经加入的社团id
        List<Long> excludeClubIds = new ArrayList<>();
        for(UserClub item : page.getRecords()){
            excludeClubIds.add(item.getClubId());
        }

        //获得没有加入的社团信息
        LambdaQueryWrapper<Club> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.notIn(Club::getId, excludeClubIds);
        List<Club> otherClubList = clubSerivce.getBaseMapper().selectList(lambdaQueryWrapper);

        List<Club_Chairman> list = new ArrayList<>();
        if(!otherClubList.isEmpty()){
                list = otherClubList.stream().map((item) -> {
                Club_Chairman dto = new Club_Chairman();
                BeanUtils.copyProperties(item, dto);
                Administrator administrator = administratorService.getById(item.getChairmanId());
                dto.setName(administrator.getName());
                dto.setAccount(administrator.getAccount());
                return dto;
            }).collect(Collectors.toList());
        }else {
            return R.error("你已经全部加过了！");
        }

        return R.success(list);
    }

    /**
     * 根据社团id查询社团信息
     * @param id
     * @return
     */
    @GetMapping("/getclubByid")
    public R<Club> getClubById(Long id){
        log.info("社团id：{}", id);

        return R.success(clubSerivce.getById(id));
    }

    @PostMapping("/saveApplyForm")
    public R<String> saveForm(@RequestBody Applyforjoin applyforjoin){
        log.info("信息：{},{},{}", applyforjoin.getClubId(), applyforjoin.getMemberId(), applyforjoin.getDescription());

        LambdaQueryWrapper<Applyforjoin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Applyforjoin::getClubId, applyforjoin.getClubId());
        wrapper.eq(Applyforjoin::getMemberId, applyforjoin.getMemberId());
        Applyforjoin a = applyforjoinService.getOne(wrapper);
        if(a != null){
            return R.error("申请已提交，请耐心等待");
        }

        applyforjoinService.save(applyforjoin);
        return R.success("已提交申请");
    }
}
