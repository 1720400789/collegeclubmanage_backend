package com.zj.managesys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zj.managesys.common.R;
import com.zj.managesys.dto.AdminList;
import com.zj.managesys.entity.Administrator;
import com.zj.managesys.entity.User;
import com.zj.managesys.serive.AdministratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/admin")
@CrossOrigin
public class AdministratorController {

    @Autowired
    private AdministratorService service;

    /**
     * 管理员系统登录
     * @param request
     * @param administrator
     * @return
     */
    @PostMapping("/login")
    public R<Administrator> login(HttpServletRequest request, @RequestBody Administrator administrator){
//        log.info("登录用户名：{}", administrator.getAccount());
        //1、将页面提交的密码password进行md5解密处理
        String password = administrator.getPassword();
        if(!"admin".equals(administrator.getAccount()) && !"test".equals(administrator.getAccount())){
            password = DigestUtils.md5DigestAsHex(password.getBytes());
        }

        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Administrator> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Administrator::getAccount,administrator.getAccount());
        Administrator adm = service.getOne(queryWrapper);

        //3、如果没有查询到则返回登录失败结果
        if(adm == null){
            return R.error("登录失败");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if(!adm.getPassword().equals(password)){
            log.info("password:{}", password);
            return R.error("登录失败");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(adm.getStatus() == 0){
            return R.error("账号已禁用");
        }

        //6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("administrator",adm.getId());
        log.info("employee：{}", request.getSession().getAttribute("administrator"));
        return R.success(adm);
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        //构造分页构造器对象
        Page<Administrator> pageInfo = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Administrator> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null, Administrator::getName, name);
        queryWrapper.like(name != null, Administrator::getAccount, name);
        //添加排序条件
        queryWrapper.orderByDesc(Administrator::getCreateTime);

        //执行分页查询
        service.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    @PostMapping("/add")
    public R<String> add(@RequestBody Administrator administrator){
        LambdaQueryWrapper<Administrator> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Administrator::getAccount, administrator.getAccount());
        Administrator administrator1 = service.getOne(queryWrapper);
        if(administrator1 != null) return R.error(administrator.getAccount() + " 已存在！");
        administrator.setPassword(DigestUtils.md5DigestAsHex(administrator.getPassword().getBytes()));
        service.save(administrator);
        return R.success("新增管理员成功");
    }

    @PutMapping("/edit")
    public R<String> update(@RequestBody Administrator administrator){
        administrator.setPassword(DigestUtils.md5DigestAsHex(administrator.getPassword().getBytes()));
        service.updateById(administrator);
        return R.success("修改管理员信息成功");
    }

    @PutMapping
    public R<String> updateStatus(@RequestBody AdminList adminList){
        log.info("ids:{}", adminList.getIds().length);
        String[] arr = adminList.getIds();
        Long[] arrId = new Long[30];
        LambdaUpdateWrapper<Administrator> updateWrapper = new LambdaUpdateWrapper<>();
        for(int i = 0; i < arr.length; i ++){
            arrId[i] = Long.parseLong(arr[i]);
            updateWrapper.eq(Administrator::getId, arrId[i]).or();
            log.info("ids:{}", arrId[i]);
        }
        updateWrapper.set(Administrator::getStatus, "1".equals(adminList.getFlag()) ? new Integer(0): new Integer(1));
        service.update(null, updateWrapper);
        return R.success("修改成功");
    }
}
