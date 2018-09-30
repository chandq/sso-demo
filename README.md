# sso-demo

## 简述
   可跨域的单点登录实现。原理参考自[http://www.cnblogs.com/ywlaker/p/6113927.html](http://www.cnblogs.com/ywlaker/p/6113927.html)
	单点登录机制参考：http://www.cnblogs.com/ywlaker/p/6113927.html
	单点登录注销流程分析参考：https://www.jianshu.com/p/23f8e3958460
	为了让下载依赖的jar包的速度更快，maven项目可以设置阿里云的中心镜像仓库（maven目录/conf/settings.xml）：
	<mirror>
		<id>alimaven</id>
		 <mirrorOf>central</mirrorOf>
		 <name>aliyun maven</name>
		 <url>http://maven.aliyun.com/nexus/content/repositories/central/</url>
	</mirror>
## 运行
1. 将代码clone下来并导入Eclipse.
2. 在mysql中运行项目的初始化脚本User.sql.
3. 配置sso-server的数据库连接和redis连接，在jdbc.properties中.
4. 通过mvn tomcat7:run启动sso-server,sso-client1和sso-client2项目.

## 访问
sso-client1 : http:localhost:8081/ssoclient1/user/info

sso-client2 : http:localhost:8082/ssoclient2/user/info
    