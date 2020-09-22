package cn.sheledon.controller;

import cn.sheledon.pojo.ResultInfo;
import cn.sheledon.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author sheledon
 */
@Controller
@RequestMapping("/server")
public class LoginServerController {

    @Autowired
    private RedisTemplate redisTemplate;
    private static Map<String,String> serviceMap;

    /**
     * 在类加载时执行
     */
    static{
        serviceMap=new HashMap<>();
        InputStream in=LoginServerController.class.getClassLoader().getResourceAsStream("properties/serviceMap.properties");
        Properties properties=new Properties();
        try {
            properties.load(in);
            Enumeration<?> enumeration = properties.propertyNames();
            while (enumeration.hasMoreElements()){
                String name= (String) enumeration.nextElement();
                serviceMap.put(name,properties.getProperty(name));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据服务名返回登录页面
     * @param service
     * @return
     */
    @RequestMapping("/toLogin")
    public String toLogin(String service,HttpServletRequest request,HttpServletResponse response) throws IOException {
        //检查是否已经建立了全局对话
        String token=checkCookieHasToken(request);
        String page=service+"Page";
        String host=service+"Host";
        if (token!=null){
            response.sendRedirect(request.getScheme()+"://"+serviceMap.get(host)+"/login/verify.do?token="+token);
        }else if (serviceMap.containsKey(page)){
            return  serviceMap.get(page);
        }
        return null;
    }
    /**
     * 登录操作，为了方便不调用数据库检查user
     * @param user
     * @param service
     */
    @RequestMapping("/login")
    @ResponseBody
    public ResultInfo login(User user, @RequestParam("service") String service, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String host=service+"Host";
        if (serviceMap.containsKey(host)){
            String token= UUID.randomUUID().toString();
            redisTemplate.boundValueOps(token).set(user.getUserName());
            //重定向
            Cookie cookie=new Cookie("token",token);
            //Cookie一级域名共享
            cookie.setDomain(".sheledon.com");
            cookie.setPath("/");
            response.addCookie(cookie);
            return ResultInfo.builder().success(true).message(request.getScheme()+"://"+serviceMap.get(host)+"/login/verify.do?token="+token).build();
        }
        return ResultInfo.builder().success(false).build();
    }
    /**
     * 系统检验token时调用
     * @param token
     * @return
     */
    @RequestMapping("/verifyToken")
    @ResponseBody
    public ResultInfo verifyToken(String token){
        if (token!=null){
            String userName= (String) redisTemplate.boundValueOps(token).get();
            User user=User.builder().userName(userName).build();
            if (user!=null){
                return ResultInfo.builder().success(true).object(user).build();
            }
        }
        return ResultInfo.builder().success(false).build();
    }
    private String checkCookieHasToken(HttpServletRequest request){
        Cookie [] cookies=request.getCookies();
        if(cookies!=null){
            for (Cookie c:cookies){
                if (c.getName().equals("token")){
                    Object o=redisTemplate.boundValueOps(c.getValue()).get();
                    if (o!=null){
                        return c.getValue();
                    }
                }
            }
        }
        return null;
    }
}
