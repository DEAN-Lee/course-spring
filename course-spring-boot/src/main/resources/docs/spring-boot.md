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
    * 获取SpringContextInitializer 使用SpringFactoriesLoader 加载springboot多个配置下自动配置文件META-INF/spring.factories 默认配置初始类
    * 获取ApplicationListener  使用SpringFactoriesLoader 加载springboot多个配置下自动配置文件META-INF/spring.factories 默认配置的监听类
* 设置StopWatch启动时间监听、开启监听
* 设置SpringApplicationRunListeners 监听
* 发布SpringApplicationRunListeners 通知其他监听类
* 设置默认启动参数applicationArguments
* 设置条件环境变量信息prepareEnvironment 去除忽略配合
* 创建ApplicationContext的子类AnnotationConfigServletWebServerApplicationContext
* 获取自动装配的异常收集报告
* 预处理prepareContext
* refreshContext()加载或者刷新applicationContext
* 加载完成ApplicationContext 回调
* StopWatch停止监听
* 启动事件监听

## 自动装配
* SpringFactoriesLoader 加载过个META-INF/spring.factories






    
    
    
    
    
    
    
    

