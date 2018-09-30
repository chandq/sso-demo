package cn.demo.sso.client2.filter;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

/**   
 * @ClassName:  LoginFilter   
 * @author: chendq 
 * @date:   2017年8月14日 下午4:37:52   
 *     
 */
public class SSOFilter implements Filter {
	private String basePath = null;
//	private String authServer = "http://192.168.2.11/ssoserver";
	private String authServer = "http://localhost:8080/ssoserver";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession();
        String url = URLEncoder.encode(request.getRequestURL().toString(), "UTF-8");
        String token = request.getParameter("token");
        String username = (String) session.getAttribute("username");
//        String logoutURL = URLEncoder.encode("http://localhost:8082/client2/user/logout", "UTF-8");
        
        //判断是否已经登录
        if (null == username) {
            //判断是否在处理token验证逻辑(包含token参数)
            if (null != token && !"".equals(token)) {
                PostMethod postMethod = new PostMethod(authServer+"/user/token");
                postMethod.addParameter("token", token);
//              postMethod.addParameter("logoutURL", logoutURL); //用于注销所有子系统
                String logoutURL = URLEncoder.encode(basePath + "/user/logout", "UTF-8");
                postMethod.addParameter("logoutURL", logoutURL); //用于注销所有子系统
                postMethod.addParameter("JSESSIONID", session.getId());
                HttpClient httpClient = new HttpClient();
                try {
                    httpClient.executeMethod(postMethod);
                    username = postMethod.getResponseBodyAsString();
                    postMethod.releaseConnection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (null != username && !"".equals(username)) {
                    session.setAttribute("username", username);
                    session.setAttribute("token", token);
                    chain.doFilter(request, response);
                } else {
                    response.sendRedirect(authServer+"/index.jsp?service=" + url);
                }
            } else {
            	if (basePath == null) {
            		basePath = basePath(request);
            	}
                response.sendRedirect(authServer+"/index.jsp?service=" + url);
            }
        } else {
        	// 拦截client用户的注销请求，向sso认证中心发送注销请求
        	if (request.getRequestURL().toString().endsWith("clientlogout")) {  
                String localToken = (String)session.getAttribute("token");
        		response.sendRedirect(authServer+"/user/logout?service=" + url + "&token=" + localToken);
        	} else {
        		chain.doFilter(request, response);
        	}
        }
    }

    @Override
    public void destroy() {
        
    }

    private String basePath(HttpServletRequest request) {
    	return request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
    }

}
