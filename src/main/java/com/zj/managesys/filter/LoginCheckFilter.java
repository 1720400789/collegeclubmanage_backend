package com.zj.managesys.filter;

import com.alibaba.fastjson.JSON;
import com.zj.managesys.common.BaseContext;
import com.zj.managesys.common.R;
import com.zj.managesys.common.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经完成登录
 */
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter{
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        ResponseWrapper wrapperResponse = new ResponseWrapper((HttpServletResponse)response);//转换成代理类
        byte[] content = wrapperResponse.getContent();//获取返回值

        //1、获取本次请求的URI
        String requestURI = request.getRequestURI();// /backend/index.html

        log.info("拦截到请求：{}",requestURI);

        //定义不需要处理的请求路径
        String[] urls = new String[]{
                "/admin/login",
                "/admin/logout"
        };

        //2、判断本次请求是否需要处理
        boolean check = check(urls, requestURI);
        //3、如果不需要处理，则直接放行
        if(check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //拿到请求头里面的token（未登录是没有该token的）
        String token = request.getHeader("u-token");
        log.info("请求携带的token:{}", token);
        //4-1、判断登录状态，如果已登录，则直接放行
        log.info("session:{}", request.getSession().getAttribute("administrator"));
        if(request.getSession().getAttribute("administrator") != null || token != null){

            Long admId = (Long) request.getSession().getAttribute("administrator");
            log.info("用户已登录，用户id为：{}",admId != null ? admId : Long.parseLong(token));
            BaseContext.setCurrentId(admId != null ? admId : Long.parseLong(token));

            filterChain.doFilter(request,response);
            return;
        }

        //4-2、判断登录状态，如果已登录，则直接放行
//        if(request.getSession().getAttribute("user") != null){
//            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("user"));
//
//            Long userId = (Long) request.getSession().getAttribute("user");
//            BaseContext.setCurrentId(userId);
//
//            filterChain.doFilter(request,response);
//            return;
//        }

        log.info("用户未登录");
        filterChain.doFilter(request, wrapperResponse);
        String ciphertext = null;
        try {
            ciphertext  = JSON.toJSONString(R.error("NOTLOGIN"));
        }catch (Exception e){
            e.printStackTrace();
        }
        //5、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
//        response.setContentType("application/json; charset=utf-8");
//        response.getWriter().print(JSON.toJSONString(R.error("NOTLOGIN")));
//        filterChain.doFilter(request,response);
        response.setContentLength(ciphertext.getBytes().length);
        ServletOutputStream out = response.getOutputStream();
        out.write(ciphertext.getBytes());
        out.flush();
        out.close();
        response.flushBuffer();
        return;
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
