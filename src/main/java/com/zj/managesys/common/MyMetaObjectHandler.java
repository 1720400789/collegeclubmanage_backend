package com.zj.managesys.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


/**
 * 自定义元数据处理器，与实体类中@TableFiled对应使用
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入操作字段填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]...");
        log.info(metaObject.toString());
        if(metaObject.hasSetter("createTime"))
            metaObject.setValue("createTime", LocalDateTime.now());
        if(metaObject.hasSetter("updateTime"))
            metaObject.setValue("updateTime", LocalDateTime.now());
        if(metaObject.hasSetter("createUser"))
            metaObject.setValue("createUser", BaseContext.getCurrentId());
        if(metaObject.hasSetter("updateUser"))
            metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

    /**
     * 更新操作自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {

        Long id = Thread.currentThread().getId();
        log.info("线程id为：{}", id);

        log.info("公共字段自动填充[update]...");
        log.info(metaObject.toString());
        if(metaObject.hasSetter("updateTime"))
            metaObject.setValue("updateTime", LocalDateTime.now());
        if(metaObject.hasSetter("updateUser"))
            metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
}
