# container
ApplicationContext 负责对容器内对象的实例化、配置和组装bean。容器通过读取配置元数据获取关于实例化、配置和组织的指令。配置元数据方式可以是
传统的xml,java注解以及java-based配置方式。
ApplicationContext 接口主要有ClassPathXmlApplicationContext or FileSystemXmlApplicationContext 实习类，平时也用的最多。
下图为IoC Container如何工作原理图：<br>
![image](https://github.com/DEAN-Lee/img-rep/blob/master/springframework/20200821150053.png)
应用程序类与配置数据相结合，在创建并初始化ApplicationContext之后，您就有了一个完全配置和可执行的系统或应用程序。
## 配置元数据
容器通过配置元数据获取到如何实例、配置和组装对象信息。
### 配置元数据方式
* 传统的XML定义 : 传统的XML格式<beans><brean></bean></beans>
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="..." class="...">  
        <!-- collaborators and configuration for this bean go here -->
    </bean>

    <bean id="..." class="...">
        <!-- collaborators and configuration for this bean go here -->
    </bean>

    <!-- more bean definitions go here -->
</beans>
```
* 基于注释的配置:Spring 2.5引入了对基于注释的配置元数据的支持。
* JAVA-Based 配置：Spring 3.0引入。Spring JavaConfig 项目是这个配置的基数。@Configuration, @Bean, @Import, and @DependsOn
基于这些注解实现

**配置元数据至少有一个bean定义初始管理**
元数据配置不需要过度颗粒细化，因为开通过分层的加载，另外可通过spring 切面来加载外部bean到容器中。
## 初始容器
ApplicationContext 提供了构造函数的方法去加载配置元数据。例如
```
ApplicationContext context = new ClassPathXmlApplicationContext("services.xml", "daos.xml");
```
## 使用容器
ApplicationContext是一个高级工厂的接口，该工厂能够维护不同bean及其依赖项的注册表。可以使用T getBean(String name, Class<T> requiredType)
获取使用的bean对象。通常项目中也很少会直接使用getBean方法，一般直接在配置元数据配置了相关依赖。
```
   // create and configure beans
    ApplicationContext context = new ClassPathXmlApplicationContext("conf/services.xml", "conf/daos.xml");
    
    // retrieve configured instance
    UserService service = context.getBean("userService", UserService.class);
    
    // use configured instance
    service.getAccountDao().printUserList();
```

