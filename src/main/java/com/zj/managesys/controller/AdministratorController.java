package com.zj.managesys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zj.managesys.common.R;
import com.zj.managesys.entity.Administrator;
import com.zj.managesys.serive.AdministratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600, methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.DELETE, RequestMethod.PUT})
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
        if(!"admin".equals(administrator.getAccount())){
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
//            log.info("password:{}", password);
            return R.error("登录失败");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(adm.getStatus() == 0){
            return R.error("账号已禁用");
        }

        //6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("administrator",adm.getId());
        log.info("employee：{}", adm);
        return R.success(adm);
    }
}
