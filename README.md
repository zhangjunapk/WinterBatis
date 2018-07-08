# WinterBatis
实现功能: 
* 类与对象放到容器进行管理
* 对需要增强的对象通过动态代理生成代理类并放到容器中
* 为mapper创建代理类，并在调用里面方法的时候动态生成sql然后通过sql获得结果返回
* 为需要增强的对象进行增强(如果有接口，使用jdk动态代理，没有就用cglib)
* 对加了Autowired注解的字段进行注入
* 将url和方法/对象的映射保存到容器
* 在浏览器向服务器发送请求的时候，为Controller动态注入request response(如果有的话)
* 在浏览器向服务器发送请求是，为方法动态注入参数列表
* 返回页面/json的处理
---

@BasePackage({"org.zj.winterbatis.service","org.zj.winterbatis.controller"})
@MapperScan("org.zj.winterbatis.dao")
@AspectScan({"org.zj.winterbatis.aspect"})
@IsMaven(true)
@ViewPrefix("/html/")
@ViewSuffix(".html")
@WebPath("webapp")
@DataSource(username = "root",password = "",url="jdbc:mysql://localhost:3306/bilibili",driver = "com.mysql.jdbc.Driver")


``@BasePackage({"org.zj.winterbatis.service","org.zj.winterbatis.controller"})
  @MapperScan("org.zj.winterbatis.dao")
  @AspectScan({"org.zj.winterbatis.aspect"})
  @IsMaven(true)
  @ViewPrefix("/html/")
  @ViewSuffix(".html")
  @WebPath("webapp")
  @DataSource(username = "root",password = "",url="jdbc:mysql://localhost:3306/bilibili",driver = "com.mysql.jdbc.Driver")``
