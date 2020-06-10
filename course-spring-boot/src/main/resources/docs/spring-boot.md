# Spring-boot
跟踪源码了解spring boot 相关知识点
## 启动流程
* 设置启动类
* 设置启动注解@SpringBootApplication
* SpringApplication.run启动当 当前类当做参数传入
* 执行SpringApplication.run 静态方法
* run方法内,创建SpringApplication.
* SpringApplication构造方法
    * 设置资源加载器
    * 设置主资源加载
    * 设置项目启动类型：none、servlet、reactive
    * 获取SpringContextInitializer 使用SpringFactoriesLoader 加载springboot下自动配置文件META-INF/spring.factories 默认配置初始类
    * 获取ApplicationListener  使用SpringFactoriesLoader 加载springboot下自动配置文件META-INF/spring.factories 默认配置的监听类
    * 设置启动时间监听、开启监听
    
    
    

