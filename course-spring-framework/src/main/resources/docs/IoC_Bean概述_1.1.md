# Spring IoC容器和bean的介绍
组成应用程序的主要对象,由Spring IoC容器管理的对象称为bean。bean是由Spring IoC容器实例化、组装和管理的对象。
另外,bean仅仅是在应用程序中许多对象之一。bean及其之间的依赖关系反映在容器使用的配置文件数据中。
## IoC 核心实现
控制反转（Inversion of Control (IoC)） 又称 依赖注入（dependency injection DI）
## IoC/DI
依赖注入: IoC容器在bean创建是注入依赖<br>
控制反转: bean实例化的过程，由原来bean本身创建关系，变成IoC去处理关系，因此得名控制的反转。
## spring ioc 核心实现包
* org.springframework.beans
* org.springframework.context
### BeanFactory vs ApplicationContext
* BeanFactory 
提供bean工厂接口提供访问IoC的入口，可以管理IoC容器内的任意类型对象。
* ApplicationContext
ApplicationContext 是BeanFactory接口的子类接口。添加一些自有的功能。功能如下：
* 更方便与SpringAop集成
* 消息资源处理
* 事件发布
* 应用层上下文例如WebApplicationContext的运用

总结：BeanFactory提供了配置框架和基本功能，而ApplicationContext添加了更多特定于企业的功能。



