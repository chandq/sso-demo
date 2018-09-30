package cn.demo.sso.client1.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**   
 * @ClassName:  UserController   
 * @author: chendq 
 * @date:   2017年8月14日 下午5:42:03   
 *     
 */
@Controller
@RequestMapping("/user")
public class UserController {
    
    @RequestMapping(value = {"/info"})
    public ModelAndView info() {
        ModelAndView mav = new ModelAndView("info");
        return mav;
    }
    
    @RequestMapping(value = {"/logout"}, method = {RequestMethod.GET})
    @ResponseBody
    public String logout(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws IOException {
    	System.out.println("client1: " + session.getAttribute("username") + " local session has destroyed.");
        session.invalidate();
        return "success";      
    }
}
