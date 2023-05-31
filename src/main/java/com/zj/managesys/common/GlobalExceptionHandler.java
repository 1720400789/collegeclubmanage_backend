package com.zj.managesys.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

//添加AOP拦截器，所有注解为RestController和Controller注解的都会被拦截到
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody//因为拦截到的异常要处理成响应的格式，所以这里要加一个ResPonseBody表示return值以响应的方式出去
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());

        //如果违反sql建表的唯一约束
        //报错：Duplicate entry 'zhangsan' for key 'employee.idx_username'
        if(ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");//根据空格分割异常信息，并返回给字符串数组，那么索引为2的字符串就是冲突的username
            String msg = split[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    /**
     * 在全局设置异常捕获方法，ex.getMessage直接捕获CustomException的异常信息，并封装为R返回给前端，这样异常信息就可以提示到前端了
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        log.error(ex.getMessage());

        return R.error(ex.getMessage());
    }
}
