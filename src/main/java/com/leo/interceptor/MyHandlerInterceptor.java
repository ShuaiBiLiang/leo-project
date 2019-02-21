package com.leo.interceptor;

import com.leo.model.domain.LeoUser;
import com.leo.service.ILeoService;
import com.leo.service.LeoUserService;
import com.leo.service.impl.LeoUserServiceImpl;
import com.leo.util.SpringUtil;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyHandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        System.out.println("---------preHandle--------");
        LeoUserService leoUserService = (LeoUserServiceImpl)SpringUtil.getBean("leoUserService");
        LeoUser leoUser = new LeoUser();
        leoUser.setToken(request.getHeader("X-Token"));
        System.out.println(leoUserService.select(new LeoUser()));
        return true;
    }

    /**
     * controller执行之后，且页面渲染之前调用
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {
        System.out.println("---------postHandle--------");
    }

    /**
     * 页面渲染之后调用，一般用于资源清理操作
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {
        System.out.println("---------afterCompletion--------");
    }
}
