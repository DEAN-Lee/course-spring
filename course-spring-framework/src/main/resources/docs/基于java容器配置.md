# 基于java容器配置
本节介绍如何在Java代码中使用注释来配置Spring容器。它包括以下主题
* 基本概念：@Bean和@Configuration
* 使用@Bean注释
* 使用@Configuration注解
* 编写基于java的配置
* Bean定义配置文件
* 使用@PropertySource
* 语句中的占位符决议

## 基本概念：@Bean和@Configuration
Spring的新java配置支持中的核心构件是@ configuration注释的类和@ bean注释的方法。

@Bean注释用于指示方法实例化、配置和初始化将由Spring IoC容器管理的新对象。对于那些熟悉Spring的 XML配置的人来说，@Bean注释扮演着与元素相同的角色.
您可以对任何Spring @Component使用@ bean注释的方法。但是，它们最常与@Configuration bean一起使用。

用@Configuration注释类表明它的主要用途是作为bean定义的源。此外，@Configuration类通过调用同一类中的其他@Bean方法来定义bean间的依赖关系。
最简单的@Configuration类如下所示
```java
@Configuration
public class AppConfig {

    @Bean
    public MyService myService() {
        return new MyServiceImpl();
    }
}
```
前面的AppConfig类相当于下面的Spring XML:
```xml
<beans>
    <bean id="myService" class="com.acme.services.MyServiceImpl"/>
</beans>
```                                            
>                   全局 @Configuration vs lite @Bean模式
> 当@Bean方法在没有使用@Configuration注释的类中声明时，它们被称为在lite模式下处理。
> 在@Component甚至在普通旧类中声明的Bean方法被认为是lite，包含类的主要目的不同，而@Bean方法则是一种额外的好处。
> 例如，服务组件可以通过每个适用组件类上的附加@Bean方法向容器公开管理视图。在这种情况下，@Bean方法是一种通用工厂方法机制。
>
> 与完整的@Configuration不同，lite @Bean方法不能声明bean之间的依赖关系。
> 相反，它们对包含它们的组件的内部状态进行操作，并可选地对它们可能声明的参数进行操作。因此，这样的@Bean方法不应该调用其他的@Bean方法。
> 每个这样的方法字面上只是一个特定bean引用的工厂方法，没有任何特殊的运行时语义。
> 这里的积极副作用是，在运行时不需要应用CGLIB子类，因此在类设计方面没有限制(也就是说，包含的类可能是final等等)。
>
> 在常见的场景中，@Bean方法要在@Configuration类中声明，以确保始终使用full模式，并因此将交叉方法引用重定向到容器的生命周期管理。
> 这可以防止通过常规Java调用意外地调用相同的@Bean方法，这有助于减少在lite模式下操作时难以跟踪的细微错误。

下面几节将深入讨论@Bean和@Configuration注释。但是，首先，我们将介绍使用基于java的配置创建spring容器的各种方法。

## 使用AnnotationConfigApplicationContext实例化Spring容器
下面的小节记录了Spring 3.0中引入的Spring的AnnotationConfigApplicationContext。这个通用的ApplicationContext实现不仅能够接受@Configuration类作为输入，
还能够接受普通的@Component类和用JSR-330元数据注释的类。

当提供@Configuration类作为输入时，@Configuration类本身被注册为bean定义，类中声明的所有@Bean方法也被注册为bean定义。

当提供了@Component和JSR-330类时，它们被注册为bean定义，并且假设在需要时在这些类中使用DI元数据，如@Autowired或@Inject。

### 结构简洁
与实例化ClassPathXmlApplicationContext时使用Spring XML文件作为输入的方式非常相似，在实例化AnnotationConfigApplicationContext时可以使用@Configuration类作为输入。这允许使用完全无xml的Spring容器，如下面的示例所示
```java
public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
    MyService myService = ctx.getBean(MyService.class);
    myService.doStuff();
}
```
正如前面提到的，AnnotationConfigApplicationContext并不仅限于处理@Configuration类。任何@Component或JSR-330注释类都可以作为输入提供给构造函数，如下面的示例所示
```java
public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(MyServiceImpl.class, Dependency1.class, Dependency2.class);
    MyService myService = ctx.getBean(MyService.class);
    myService.doStuff();
}
```
前面的示例假设MyServiceImpl、Dependency1和Dependency2使用Spring依赖注入注释，如@Autowired注解。

### 使用register(Class<?>…​)以编程方式构建容器
您可以使用无参数构造函数实例化一个AnnotationConfigApplicationContext，然后使用register()方法对其进行配置。当以编程方式构建AnnotationConfigApplicationContext时，这种方法特别有用。下面的示例展示了如何做到这一点
```java
public static void main(String[] args) {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.register(AppConfig.class, OtherConfig.class);
    ctx.register(AdditionalConfig.class);
    ctx.refresh();
    MyService myService = ctx.getBean(MyService.class);
    myService.doStuff();
}
```

### 启用扫描组件scan(string)
要启用组件扫描，可以按照如下方式注释@Configuration类
```java
@Configuration
@ComponentScan(basePackages = "com.acme") 
public class AppConfig  {
    ...
}
```
该注释支持组件扫描。
> 有经验的Spring用户可能熟悉来自Spring s context: namespace的等价XML声明，如下面的示例所示
```xml
<beans>
    <context:component-scan base-package="com.acme"/>
</beans>
```
在前面的示例中，扫描com.acme包以查找任何@ component注释的类，并且这些类被注册为容器内的Spring bean定义.
AnnotationConfigApplicationContext公开了scan(String)方法，以支持相同的组件扫描功能，如下面的示例所示
```java
public static void main(String[] args) {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.scan("com.acme");
    ctx.refresh();
    MyService myService = ctx.getBean(MyService.class);
}
```
> 请记住，@Configuration类是用@Component进行元注释的，因此它们是组件扫描的候选对象。在前面的示例中，假设AppConfig在com.acme包(或下面的任何包)中声明，
> 它在调用scan()期间被拾取。在refresh()时，它的所有@Bean方法都被处理并在容器中注册为bean定义。

### 支持带有注释的Web应用程序
AnnotationConfigApplicationContext的WebApplicationContext变体可以通过AnnotationConfigWebApplicationContext获得。您可以在配置Spring ContextLoaderListener servlet监听器、
Spring MVC DispatcherServlet等时使用此实现。下面的web.xml代码片段配置了一个典型的Spring MVC web应用程序(注意contextClass上下文参数和init参数的使用)。
```xml
<web-app>
    <!-- Configure ContextLoaderListener to use AnnotationConfigWebApplicationContext
        instead of the default XmlWebApplicationContext -->
    <context-param>
        <param-name>contextClass</param-name>
        <param-value>
            org.springframework.web.context.support.AnnotationConfigWebApplicationContext
        </param-value>
    </context-param>

    <!-- Configuration locations must consist of one or more comma- or space-delimited
        fully-qualified @Configuration classes. Fully-qualified packages may also be
        specified for component-scanning -->
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>com.acme.AppConfig</param-value>
    </context-param>

    <!-- Bootstrap the root application context as usual using ContextLoaderListener -->
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>

    <!-- Declare a Spring MVC DispatcherServlet as usual -->
    <servlet>
        <servlet-name>dispatcher</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <!-- Configure DispatcherServlet to use AnnotationConfigWebApplicationContext
            instead of the default XmlWebApplicationContext -->
        <init-param>
            <param-name>contextClass</param-name>
            <param-value>
                org.springframework.web.context.support.AnnotationConfigWebApplicationContext
            </param-value>
        </init-param>
        <!-- Again, config locations must consist of one or more comma- or space-delimited
            and fully-qualified @Configuration classes -->
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>com.acme.web.MvcConfig</param-value>
        </init-param>
    </servlet>

    <!-- map all requests for /app/* to the dispatcher servlet -->
    <servlet-mapping>
        <servlet-name>dispatcher</servlet-name>
        <url-pattern>/app/*</url-pattern>
    </servlet-mapping>
</web-app>
```

## 使用@Bean注释
 





  
