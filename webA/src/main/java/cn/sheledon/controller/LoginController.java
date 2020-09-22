package cn.sheledon.controller;

import cn.sheledon.http.HttpClientUtil;
import cn.sheledon.pojo.ResultInfo;
import cn.sheledon.pojo.User;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sheledon
 */
@Controller
@RequestMapping("/login")
public class LoginController {
    /**
     *
     * @param token
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/verify")
    @ResponseBody
    public ResultInfo verify(@RequestParam("token") String token, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (checkCookieHasToken(request,token)){
            JSONObject jsonObject=verifyToken(token);
            if (jsonObject.getBoolean("success")){
                User user=jsonObject.getObject("object",User.class);
                HttpSession session=request.getSession(true);
                session.setAttribute("user",user);
                response.sendRedirect("/index.html");
                return ResultInfo.builder().success(true).build();
            }
        }
        return ResultInfo.builder().success(false).build();
    }
    /**
     * 检查是否存在token，存在则去SSOServer验证
     * @param token
     * @return
     * @throws IOException
     */
    private JSONObject verifyToken(String token) throws IOException {
        Map<String,String> par=new HashMap<>();
        par.put("token",token);
        JSONObject jsonObject=HttpClientUtil.doGet("www.sso.sheledon.com:7000","server/verifyToken.do",par);
        return jsonObject;
    }

    private boolean checkCookieHasToken(HttpServletRequest request,String token){
        Cookie [] cookies=request.getCookies();
        String cookieToken=null;
        for (Cookie c:cookies){
            if (c.getName().equals("token")){
                cookieToken=c.getValue();
            }
        }
        if (cookieToken!=null && cookieToken.equals(token)){
            return true;
        }
        return false;
    }
}
