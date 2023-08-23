package com.zj.managesys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zj.managesys.common.R;
import com.zj.managesys.dto.AdminList;
import com.zj.managesys.entity.Administrator;
import com.zj.managesys.entity.User;
import com.zj.managesys.serive.UserService;
import com.zj.managesys.serive.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public R<User> login(HttpServletRequest request, @RequestParam String mAccount, @RequestParam String mPassword){
        log.info("登录用户名：{}", mAccount);
        //1、将页面提交的密码password进行md5解密处理
        String password = mPassword;
        if(!"test".equals(mAccount))
            password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库
        //下面做sql语句的拼接，select * from user where m_account = ?
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getMAccount,mAccount);
        User adm = userService.getOne(queryWrapper);

        //3、如果没有查询到则返回登录失败结果
        if(adm == null){
            return R.error("登录失败");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if(!adm.getMPassword().equals(password)){
            log.info("password:{}", password);
            return R.error("登录失败");
        }

        if(adm.getStatus() == 0){
            return R.error("账号已禁用");
        }

        //6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("user",adm.getId());
        log.info("employee：{}", request.getSession().getAttribute("user"));
        return R.success(adm);
//        return R.success(new User());
    }

    @PostMapping("/register")
    public R<String> register(@RequestBody User user){
        log.info("前端返回信息：{}", user);
        //看是否重名
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getMAccount, user.getMAccount());
        User user1 = userService.getOne(queryWrapper);
        if(user1 != null) return R.error("账号重名");

        user.setMPassword(DigestUtils.md5DigestAsHex(user.getMPassword().getBytes()));

        userService.save(user);//insert操作insert into ... () values （？，？，？）

        return R.success("注册成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        //构造分页构造器对象
        Page<User> pageInfo = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件select * from user where maccount like ? or mname like ?;
        queryWrapper.like(name != null, User::getMAccount, name).or();
        queryWrapper.like(name != null, User::getMName, name);
        //添加排序条件
        queryWrapper.orderByDesc(User::getCreateTime);

        //执行分页查询
        userService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    @PostMapping("/add")
    public R<String> add(@RequestBody User user){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getMAccount, user.getMAccount());
        User user1 = userService.getOne(queryWrapper);
        if(user1 != null) return R.error(user.getMAccount() + " 已存在！");
        user.setMPassword(DigestUtils.md5DigestAsHex(user.getMPassword().getBytes()));
        userService.save(user);
        return R.success("新增用户成功");
    }

    @PutMapping("/edit")
    public R<String> update(@RequestBody User user){
        user.setMPassword(DigestUtils.md5DigestAsHex(user.getMPassword().getBytes()));
        userService.updateById(user);
        return R.success("修改用户信息成功");
    }

    @PutMapping
    public R<String> updateStatus(@RequestBody AdminList adminList){
        log.info("ids:{}", adminList.getIds().length);
        String[] arr = adminList.getIds();
        Long[] arrId = new Long[30];
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        for(int i = 0; i < arr.length; i ++){
            arrId[i] = Long.parseLong(arr[i]);
            updateWrapper.eq(User::getId, arrId[i]).or();
            log.info("ids:{}", arrId[i]);
        }
        updateWrapper.set(User::getStatus, "1".equals(adminList.getFlag()) ? new Integer(0): new Integer(1));
        userService.update(null, updateWrapper);
        return R.success("修改成功");
    }

    @GetMapping("/getSelfIden")
    public R<User> getSelfIden(Long id){
//        log.info("用户id：{}", id);

        return R.success(userService.getById(id));
    }

    @PutMapping("/editUserSelf")
    public R<String> editUserSelf(@RequestBody User user){
        log.info("用户id：{}", user.getId());

        LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(User::getId, user.getId());
        userService.update(user, lambdaUpdateWrapper);

        return R.success("修改个人信息成功");
    }
}
