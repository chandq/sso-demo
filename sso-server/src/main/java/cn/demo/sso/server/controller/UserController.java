package cn.demo.sso.server.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.demo.sso.server.entity.TokenToURL;
import cn.demo.sso.server.entity.User;
import cn.demo.sso.server.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    private final static String BASE_PATH = "/ssoserver";
    
    @RequestMapping(value = {"/login"}, method = {RequestMethod.POST})
    public void login(User user, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        String service = request.getParameter("service");
        
        //登录成功时
        if (userService.login(user.getUsername(), user.getPassword())) {
            String token = UUID.randomUUID().toString();
            session.setAttribute("sso", user.getUsername());
            session.setAttribute("sso-token", token);
            
            userService.createTokenToURL(token, user.getUsername());
            
            if (null != service) {
                StringBuilder url = new StringBuilder();
                url.append(service);
                if (0 <= service.indexOf("?")) {
                    url.append("&");
                } else {
                    url.append("?");
                }
                url.append("token=").append(token);
                response.sendRedirect(url.toString());
            } else {
                response.sendRedirect(BASE_PATH + "/index.jsp");
            }
        } else {
            //登录失败时
            response.sendRedirect(BASE_PATH + "/index.jsp?service=" + service);
        }
    }
    
    @RequestMapping(value = {"/token"})
    @ResponseBody
    public String verifyToken(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws UnsupportedEncodingException {
        String token = req.getParameter("token");
        String logoutURL = URLDecoder.decode(req.getParameter("logoutURL"), "UTF-8");
        String jsessionId = req.getParameter("JSESSIONID");
        
        TokenToURL t = userService.registerClient(token, logoutURL, jsessionId);
        
        if (t != null && t.getUsername() != null) {
            return t.getUsername();
        }
        
        return null;
    }
    
    @RequestMapping(value = {"/logout"})
    public void logout(HttpServletRequest req, HttpServletResponse res, HttpSession session) {
    	String originURL = req.getParameter("service");
        System.out.println("客户机发起注销请求：" + req.getParameter("service"));
        
        if (session != null) {
        	//触发LogoutListener, 执行完sessionDestroyed方法中的代码后，再继续往下走（实现注销所有应用的会话后，对发起注销请求的应用重定向到认证中心的登录界面）
            session.invalidate(); 
        }
        try {
			res.sendRedirect(originURL.replaceAll("clientlogout", "info"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

}
