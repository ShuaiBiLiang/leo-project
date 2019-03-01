package com.leo.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.leo.common.ServerResponse;
import com.leo.model.domain.LeoUser;
import com.leo.service.ILeoService;
import com.leo.service.LeoUserService;
import com.leo.service.impl.LeoUserServiceImpl;
import com.leo.util.SpringUtil;
import com.leo.util.UserThreadUtil;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class MyHandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        if(request.getMethod().equals("OPTIONS")){
            return true;
        }
        LeoUserService leoUserService = (LeoUserServiceImpl)SpringUtil.getBean("leoUserService");
        LeoUser leoUser = new LeoUser();
        leoUser.setToken(request.getHeader("X-Token"));
        LeoUser selectOne = leoUserService.selectOne(leoUser);
        if(selectOne==null || (selectOne!=null && selectOne.getEndtime()<System.currentTimeMillis())){
            response.reset();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter pw = response.getWriter();
            String ret = "";
            if(selectOne==null){
               ret = JSONObject.toJSONString(ServerResponse.createByErrorCodeMessage(99,"服务器重启(或你已被登出)！可以取消继续留在该页面，或者重新登录。"));
            }else {
                ret = JSONObject.toJSONString(ServerResponse.createByErrorCodeMessage(98,"账号已到期，请联系管理员登录。"));
            }
            pw.write(ret);
            pw.flush();
            pw.close();
            return false;
        }
        response.setHeader("name",selectOne.getName());
        response.setHeader("size",selectOne.getUseSize()+"");
        UserThreadUtil.setLeoUser(selectOne);
        System.out.println(selectOne);
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
        UserThreadUtil.clear();
//        System.out.println("---------postHandle--------");
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
//        System.out.println("---------afterCompletion--------");
    }
}
