package cn.sheledon.controller;

import cn.sheledon.pojo.ResultInfo;
import cn.sheledon.pojo.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author sheledon
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/userName")
    public ResultInfo getUserName(HttpServletRequest request){
        HttpSession session=request.getSession();
        User user= (User) session.getAttribute("user");
        return ResultInfo.builder().success(true).object(user).build();
    }
}
