package cn.demo.sso.server.listener;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.demo.sso.server.entity.TokenToURL;
import cn.demo.sso.server.service.UserService;

/**
 * Session Bean implementation class LogoutListener
 */

public class LogoutListener implements HttpSessionListener, ServletContextListener {

	private UserService userService;

	/**
	 * Default constructor.
	 */
	public LogoutListener() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		servletContext.setAttribute("userCounter", new AtomicInteger().get());

		// ApplicationContext context = (ApplicationContext)
		// servletContext.getAttribute(
		// WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		// WebApplicationContext context =
		// WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/spring-*.xml");

		// initialize service when spring context initialized,
		// 建立对应的service当spring上下文初始化之后
		// 使用spring框架中已经初始化的memberService
		userService = (UserService) context.getBean(UserService.class);
		System.out.println("SessionListener.contextInitialized(ServletContextEvent sce)");
	}

	/**
	 * @see HttpSessionListener#sessionCreated(HttpSessionEvent)
	 */
	@Override
	public void sessionCreated(HttpSessionEvent event) {
		ServletContext ctx = event.getSession().getServletContext();
		AtomicInteger userCounter = new AtomicInteger((int) ctx.getAttribute("userCounter"));

		int userCount = userCounter.incrementAndGet();
		System.out.println("sessionCreated, userCount incremented to :" + userCount);
	}

	/**
	 * @see HttpSessionListener#sessionDestroyed(HttpSessionEvent)
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		ServletContext ctx = session.getServletContext();
		String ssoToken = (String) session.getAttribute("sso-token");
		try {
			System.out.println("userService: " + userService);
			TokenToURL t = userService.getTokenToURLByToken(ssoToken);
			List<String> logoutURLs = t.getLogoutURLs();
			List<String> jsessionIds = t.getJsessionids();
			// PostMethod postMethod = null;
			// HttpClient httpClient = null;
			// 用于注销所有子系统
			for (int i = 0; i < logoutURLs.size(); i++) {
				String logoutURL = logoutURLs.get(i);
				String jessionId = jsessionIds.get(i);
				URL url = new URL(logoutURL);
				String domain = url.getHost();
				String path = url.getPath();
				System.out.println("域名：" + domain);
				// postMethod.addParameter("token", ssoToken);
				BasicCookieStore cookieStore = new BasicCookieStore();
				BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", jessionId);
				cookie.setDomain(domain); // 项目IP
				cookie.setPath("/" + path.substring(1, path.indexOf("/", 1))); // JSESSIONID的存储路径
				cookieStore.addCookie(cookie);
				HttpClient client = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
				List<NameValuePair> params = new ArrayList<>();
				params.add(new BasicNameValuePair("token", ssoToken));
				String str = "";
				// 转换为键值对
				str = EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8));
				final HttpGet requestGET = new HttpGet(logoutURL + "?" + str);
				System.out.println("requestGET: " + requestGET.toString());
				HttpResponse res = client.execute(requestGET);
				if (res.getStatusLine().getStatusCode() == 200) {
					HttpEntity entity = res.getEntity();
					System.out.println("entity:  " + entity);
				}
				requestGET.releaseConnection();
			}
			userService.delTokenToURL(ssoToken);
			AtomicInteger userCounter = new AtomicInteger((int) ctx.getAttribute("userCounter"));

			int userCount = userCounter.decrementAndGet();
			System.out.println("SSO Server: " + t.getUsername() + " global session has destroyed，---------- userCount decremented to:" + userCount);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO 自动生成的方法存根

	}

}
