package cn.demo.sso.server.dao;

import cn.demo.sso.server.entity.User;

/**   
 * @ClassName:  UserDao   
 * @Description:用户数据访问对象   
 * @author: chendq 
 * @date:   2017年8月13日 下午9:55:33   
 *     
 */
public interface UserDao {
    
    User getById(long id);
    
    User getByName(String username);
    
}
