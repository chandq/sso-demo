package cn.demo.sso.server.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import cn.demo.sso.server.entity.TokenToURL;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**   
 * @ClassName:  RedisDao   
 * @Description:Redis缓存访问对象   
 * @author: chendq 
 * @date:   2017年8月13日 下午9:58:41   
 */
public class RedisDao {
    private JedisPool jedisPool;
    
    public RedisDao(String host, int port) {
    	JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(500);
        config.setMaxIdle(5);
        config.setMaxWaitMillis(1000*10);
        //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
        config.setTestOnBorrow(true);
        //new JedisPool(config, ADDR, PORT, TIMEOUT, AUTH);
        jedisPool = new JedisPool(config, host, port, 10000, "123456");
    }
    
    private RuntimeSchema<TokenToURL> schema = RuntimeSchema.createFrom(TokenToURL.class);
    
    public TokenToURL getTokenToURL(String token) {
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "token:" +  token;
                //redis内部没有实现序列化操作
                //get -> byte[] -> 反序列化 -> object(ClientURL)
                byte[] bytes = jedis.get(key.getBytes());                
                if (bytes != null) {
                    //空对象
                    TokenToURL t = schema.newMessage();
                    //填充空对象
                    ProtostuffIOUtil.mergeFrom(bytes, t, schema);
                    return t;
                }
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return null;
    }
    
    public String putTokenToURL(TokenToURL t) {
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "token:" +  t.getToken();
                //get -> byte[] -> 反序列化 -> object(clientURL)
                byte[] bytes = ProtostuffIOUtil.toByteArray(t, schema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                int timeout = 60 * 60;//一小时
                String result = jedis.setex(key.getBytes(), timeout, bytes);
                return result;
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return null;
    }
    
    public void delTokenToURL(String token) {
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "token:" +  token;
                jedis.del(key);
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
}
