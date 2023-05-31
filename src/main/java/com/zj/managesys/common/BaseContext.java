package com.zj.managesys.common;

/**
 * 基于TreadLocal封装工具类，用户保存和获取当前登录用户id
 * ThreadLocal是JDK提供的线程里的一个变量，具有线程隔离性，同线程内共享，不同线程之间不能互相访问
 * 由于LoginCheckFilter、EmployeeController的update方法和MyMetaObjectHandler的updateFill是同一线程的
 * 所以我们可以利用TreadLocal存储session（employee 存的是emp.getId()）来在这条线程内传输
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置值
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
