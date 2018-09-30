package cn.demo.sso.server.service;

import cn.demo.sso.server.entity.TokenToURL;

/**   
 * @ClassName:  UserService   
 * @author: chendq 
 * @date:   2017年8月14日 上午8:20:55   
 *     
 */
public interface UserService {

    public boolean login(String username, String password);
    
    public void createTokenToURL(String token, String username);
    
    public TokenToURL registerClient(String token, String logoutURL, String jsessionId);

    public TokenToURL getTokenToURLByToken(String token);

    public void delTokenToURL(String token);
}
