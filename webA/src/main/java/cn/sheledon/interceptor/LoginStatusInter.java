package cn.sheledon.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


/**
 * @author sheledon
 */
public class LoginStatusInter implements HandlerInterceptor {
    /**
     * 拦截所有请求，检查是否登录session
     * 如果登录，放行
     * 否则重定向到SSOServer，与SSOServer建立全局对话
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, Object handler) throws Exception {
        System.out.println("经过了拦截器===================");
        //session存在则说明已成功建立会话
        if (request.getSession().getAttribute("user")!=null){
            System.out.println("已经建立会话");
            return true;
        }
        //重定向到sso服务器
        response.setHeader("REDIRECT","REDIRECT");
        response.setHeader("CONTENTPATH","http://www.sso.sheledon.com:7000/server/toLogin.do?service=webA");
        return false;
    }

}
