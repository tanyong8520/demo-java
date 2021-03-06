package com.tany.demo.filter;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@WebFilter(filterName="customFilter",urlPatterns="/*")
@Order(2)
public class CustomFilter implements Filter{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("CustomFilter初始化......");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        StringBuffer url = ((HttpServletRequest) servletRequest).getRequestURL();
        System.out.println("CustomFilter指定过滤器操作.:"+url.toString());
        //执行操作后必须doFilter
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        System.out.println("CustomFilter过滤器销毁");
    }
}
