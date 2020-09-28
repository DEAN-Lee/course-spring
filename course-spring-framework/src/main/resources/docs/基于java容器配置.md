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
 @Bean是一个方法级注释，是XML的直接类比<bean>.该注释支持<bean>的屬性。例如：*内置-方法*销毁-方法*自动装配*名称。

您可以在注释了@ configuration或注释了@ component的类中使用@Bean注释。

### 声明Bean
要声明一个bean，您可以使用@Bean注释对方法进行注释。您可以使用这个方法在一个ApplicationContext中注册一个指定为方法返回值的类型的bean定义。
默认情况下，bean名称与方法名称相同。下面的示例显示了一个@Bean方法声明
```java
@Configuration
public class AppConfig {

    @Bean
    public TransferServiceImpl transferService() {
        return new TransferServiceImpl();
    }
}
```
前面的配置与后面的Spring XML完全相同
```xml
<beans>
    <bean id="transferService" class="com.acme.TransferServiceImpl"/>
</beans>
```
这两个声明都使名为transferService的bean在ApplicationContext中可用，绑定到类型为TransferServiceImpl的对象实例，如下面的文本图像所示
```text
transferService -> com.acme.TransferServiceImpl
```
您还可以使用接口(或基类)返回类型声明@Bean方法，如下面的示例所示
```java
@Configuration
public class AppConfig {

    @Bean
    public TransferService transferService() {
        return new TransferServiceImpl();
    }
}
```
但是，这将预先类型预测的可见性限制为指定的接口类型(TransferService)。然后，容器只知道完整类型(TransferServiceImpl)一次，受影响的单例bean就被实例化了。
非惰性单例bean根据它们的声明顺序被实例化，因此您可能会看到不同的类型匹配结果，这取决于其他组件何时尝试通过一个未声明的类型进行匹配(例如@Autowired TransferServiceImpl，
它只在transferService bean被实例化后才解析)。

>如果您始终通过声明的服务接口引用您的类型，那么您的@Bean返回类型可以安全地加入设计决策。但是，对于实现多个接口的组件，
>或者对于可能由其实现类型引用的组件，声明可能最特定的返回类型(至少与引用bean的注入点所要求的特定程度相同)更为安全。

### Bean的依赖关系
带@ bean注释的方法可以有任意数量的参数，用于描述构建该bean所需的依赖关系。例如，如果我们的TransferService需要一个AccountRepository，
我们可以用一个方法参数来具体化这个依赖关系，如下面的示例所示
```java
@Configuration
public class AppConfig {

    @Bean
    public TransferService transferService(AccountRepository accountRepository) {
        return new TransferServiceImpl(accountRepository);
    }
}
```
解析机制与基于构造器的依赖项注入非常相似。更多细节请参阅相关部分。
### 接受生命周期回调
任何用@Bean注释定义的类都支持常规的生命周期回调，并且可以使用JSR-250中的@PostConstruct和@PreDestroy注释。更多细节请参见JSR-250注释。

常规的Spring生命周期回调也得到了完全支持。如果一个bean实现了InitializingBean、DisposableBean或Lifecycle，容器将调用它们各自的方法。

也完全支持*感知接口的标准集(例如BeanFactoryAware、BeanNameAware、MessageSourceAware、applicationcontext taware等)。

@Bean注释支持指定任意的初始化和销毁回调方法，很像bean元素上的Spring XML s init-method和destroy-method属性，如下面的示例所示
  
```java
public class BeanOne {

    public void init() {
        // initialization logic
    }
}

public class BeanTwo {

    public void cleanup() {
        // destruction logic
    }
}

@Configuration
public class AppConfig {

    @Bean(initMethod = "init")
    public BeanOne beanOne() {
        return new BeanOne();
    }

    @Bean(destroyMethod = "cleanup")
    public BeanTwo beanTwo() {
        return new BeanTwo();
    }
}
```

> 默认情况下，使用Java配置定义的具有公共关闭或关闭方法的bean将与销毁回调一起被自动征用。如果您有一个公共关闭或关闭方法，并且不希望在容器关闭时调用它，
>那么您可以将@Bean(destroyMethod="")添加到bean定义中，以禁用默认(推断)模式。
> 
>默认情况下，您可能希望对使用JNDI获取的资源执行此操作，因为它的生命周期是在应用程序之外管理的。特别是，确保总是对数据源执行此操作，因为在Java EE应用程序服务器上这是有问题的。
>
> 下面的示例显示如何防止数据源的自动销毁回调
> ```java
> @Bean(destroyMethod="")
> public DataSource dataSource() throws NamingException {
>     return (DataSource) jndiTemplate.lookup("MyDS");
> }
> ```
> 
>@ bean方法,你通常使用程序化的JNDI查找,通过使用Spring年代JndiTemplate JndiLocatorDelegate助手或直接使用JNDI InitialContext但不是JndiObjectFactoryBean变体(这将迫使你声明返回类型作为FactoryBean类型,
>而不是实际的目标类型,因此很难使用交叉引用调用@ bean方法,打算在其他参考所提供的资源)。

对于上面示例中的BeanOne，在构造期间直接调用init()方法同样有效，如下面的示例所示

```java
@Configuration
public class AppConfig {

    @Bean
    public BeanOne beanOne() {
        BeanOne beanOne = new BeanOne();
        beanOne.init();
        return beanOne;
    }

    // ...
}
```
> 当您直接在Java中工作时，您可以对对象做任何您喜欢的事情，而不总是需要依赖容器的生命周期。

### 指定bean作用域
Spring包含@Scope注释，以便您可以指定bean的范围。

### 使用@Scope注释
您可以指定用@Bean注释定义的bean应该具有特定的范围。您可以使用Bean作用域部分中指定的任何标准作用域。

默认范围是singleton，但是您可以用@Scope注释覆盖它，如下面的示例所示
```java
@Configuration
public class MyConfiguration {

    @Bean
    @Scope("prototype")
    public Encryptor encryptor() {
        // ...
    }
}
```
### @Scope和作用域内的代理
Spring提供了一种通过作用域代理处理作用域依赖关系的方便方法。在使用XML配置时，创建此类代理的最简单方法是<aop:scoped-proxy/>元素配置。
用@Scope注释配置Java中的bean可以提供与proxyMode属性同等的支持。默认是没有代理(ScopedProxyMode. no)，但是您可以指定copedProxyMode.TARGET_CLASS或ScopedProxyMode.INTERFACES。

如果您使用Java将范围代理示例从XML参考文档(请参阅范围代理)移植到我们的@Bean，它类似于以下情况
```java
// an HTTP Session-scoped bean exposed as a proxy
@Bean
@SessionScope
public UserPreferences userPreferences() {
    return new UserPreferences();
}

@Bean
public Service userService() {
    UserService service = new SimpleUserService();
    // a reference to the proxied userPreferences bean
    service.setUserPreferences(userPreferences());
    return service;
}
```

### 定制Bean命名
默认情况下，配置类使用@Bean方法的名称作为生成的bean的名称。但是，可以使用name属性覆盖此功能，如下面的示例所示
```java
@Configuration
public class AppConfig {

    @Bean(name = "myThing")
    public Thing thing() {
        return new Thing();
    }
}
```
### Bean别名
正如在命名bean中所讨论的，有时希望给单个bean起多个名称，否则称为bean混叠。为此，@Bean注释的name属性接受一个字符串数组。下面的示例展示了如何为一个bean设置大量别名
```java
@Configuration
public class AppConfig {

    @Bean({"dataSource", "subsystemA-dataSource", "subsystemB-dataSource"})
    public DataSource dataSource() {
        // instantiate, configure and return DataSource bean...
    }
}
```

### bean描述
有时，为bean提供更详细的文本描述是有帮助的。当为了监视目的而公开bean(可能通过JMX)时，这尤其有用。

要向@Bean添加描述，可以使用@Description注释，如下面的示例所示
```java
@Configuration
public class AppConfig {

    @Bean
    @Description("Provides a basic example of a bean")
    public Thing thing() {
        return new Thing();
    }
}
```

## 使用@Configuration注释
@Configuration是一个类级注释，指示对象是bean定义的源。@Configuration类通过公共的@Bean注释方法声明bean。在@Configuration类上调用@Bean方法也可以用来定义bean间的依赖关系。
有关一般介绍，请参阅基本概念:@Bean和@Configuration。

### 注入Inter-bean依赖性
当bean彼此具有依赖关系时，表达这种依赖关系就像让一个bean方法调用另一个bean方法一样简单，如下面的示例所示
```java
@Configuration
public class AppConfig {

    @Bean
    public BeanOne beanOne() {
        return new BeanOne(beanTwo());
    }

    @Bean
    public BeanTwo beanTwo() {
        return new BeanTwo();
    }
}
```
在前面的示例中，beanOne通过构造函数注入接收到对beanTwo的引用。
> 仅当@Bean方法在@Configuration类中声明时，这种声明bean间依赖关系的方法才有效。不能通过使用纯@Component类来声明bean之间的依赖关系。

### 查询方法注入
如前所述，查找方法注入是一种高级特性，应该很少使用。在单例作用域bean与原型作用域bean有依赖关系的情况下，它非常有用。
将Java用于这种类型的配置提供了一种实现这种模式的自然方法。下面的示例展示了如何使用查找方法注入
```java
public abstract class CommandManager {
    public Object process(Object commandState) {
        // grab a new instance of the appropriate Command interface
        Command command = createCommand();
        // set the state on the (hopefully brand new) Command instance
        command.setState(commandState);
        return command.execute();
    }

    // okay... but where is the implementation of this method?
    protected abstract Command createCommand();
}
```
通过使用Java配置，您可以创建CommandManager的一个子类，其中抽象createCommand()方法被重写，其方式是查找新的(原型)命令对象。下面的示例展示了如何做到这一点
```java
@Bean
@Scope("prototype")
public AsyncCommand asyncCommand() {
    AsyncCommand command = new AsyncCommand();
    // inject dependencies here as required
    return command;
}

@Bean
public CommandManager commandManager() {
    // return new anonymous implementation of CommandManager with createCommand()
    // overridden to return a new prototype Command object
    return new CommandManager() {
        protected Command createCommand() {
            return asyncCommand();
        }
    }
}
```




