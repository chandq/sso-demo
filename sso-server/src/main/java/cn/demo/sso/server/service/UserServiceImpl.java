package cn.demo.sso.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.demo.sso.server.dao.UserDao;
import cn.demo.sso.server.dao.cache.RedisDao;
import cn.demo.sso.server.entity.TokenToURL;
import cn.demo.sso.server.entity.User;

/**   
 * @ClassName:  UserServiceImpl   
 * @author: chendq 
 * @date:   2017年8月14日 上午8:22:48   
 *     
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private RedisDao redisDao;

    @Override
    public boolean login(String username, String password) {
        User user = userDao.getByName(username);
        
        if (user != null && password.equals(user.getPassword())) {
            return true;
        }
        return false;
    }

    @Override
    public TokenToURL registerClient(String token, String logoutURL, String jsessionId) {
        TokenToURL t = redisDao.getTokenToURL(token);
        
        if (t == null) {
            return null;
        }
        
        t.addLogoutURL(logoutURL);
        t.addJsessionId(jsessionId);
        redisDao.putTokenToURL(t);
        
        return t;
    }

    @Override
    public void createTokenToURL(String token, String username) {
        TokenToURL t = new TokenToURL();
        t.setToken(token);
        t.setUsername(username);
        
        redisDao.putTokenToURL(t);
    }

    @Override
    public TokenToURL getTokenToURLByToken(String token) {
        return redisDao.getTokenToURL(token);
    }
    
    @Override
    public void delTokenToURL(String token) {
        redisDao.delTokenToURL(token);
    }
}
