# 编写基于java的配置
基于Spring java的配置特性允许编写注释，这可以降低配置的复杂性。。

## 使用@Import注释
尽管<import/>元素在Spring XML文件中用于帮助模块化配置，@Import注释允许从另一个配置类加载@Bean定义，
如下面的示例所示。
```java
@Configuration
public class ConfigA {

    @Bean
    public A a() {
        return new A();
    }
}

@Configuration
@Import(ConfigA.class)
public class ConfigB {

    @Bean
    public B b() {
        return new B();
    }
}
```
现在，在实例化上下文时，不需要同时指定config .class和config .class，只需要显式地提供ConfigB，如下面的示例所示
```java
public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(ConfigB.class);

    // now both beans A and B will be available...
    A a = ctx.getBean(A.class);
    B b = ctx.getBean(B.class);
}
```
这种方法简化了容器实例化，因为只需要处理一个类，而不是要求您在构造期间记住可能大量的@Configuration类。

>在Spring Framework 4.2中，@Import还支持对常规组件类的引用，类似于注释configapplicationcontext.register 。
>如果您想要避免组件扫描，通过使用几个配置类作为入口点显式地定义所有组件，这一点特别有用。

### 注入导入的@Bean定义的依赖项
前面的示例可以工作，但过于简单。在大多数实际场景中，bean跨配置类相互依赖。在使用XML时，这不是问题，因为不涉及任何编译器，
您可以声明ref="someBean"，并相信Spring会在容器初始化期间解决这个问题。在使用@Configuration类时，Java编译器会对配置模型施加约束，
因为对其他bean的引用必须是有效的Java语法。

幸运的是，解决这个问题很简单。正如我们已经讨论过的，@Bean方法可以有任意数量的描述bean依赖关系的参数。考虑以下更真实的场景，其中有几个@Configuration类，
每个类都依赖于其他类中声明的bean
```java
@Configuration
public class ServiceConfig {

    @Bean
    public TransferService transferService(AccountRepository accountRepository) {
        return new TransferServiceImpl(accountRepository);
    }
}

@Configuration
public class RepositoryConfig {

    @Bean
    public AccountRepository accountRepository(DataSource dataSource) {
        return new JdbcAccountRepository(dataSource);
    }
}

@Configuration
@Import({ServiceConfig.class, RepositoryConfig.class})
public class SystemTestConfig {

    @Bean
    public DataSource dataSource() {
        // return new DataSource
    }
}

public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(SystemTestConfig.class);
    // everything wires up across configuration classes...
    TransferService transferService = ctx.getBean(TransferService.class);
    transferService.transfer(100.00, "A123", "C456");
}
```
还有另一种方法可以达到同样的效果。请记住，@Configuration类最终只是容器中的另一个bean:
这意味着它们可以利用与任何其他bean相同的@Autowired和@Value注入以及其他特性。

>确保以这种方式注入的依赖项只属于最简单的类型。@Configuration类在上下文初始化过程中很早就被处理，
>强制以这种方式注入依赖项可能会导致意外的早期初始化。尽可能使用基于参数的注入，如前面的示例所示。
>
>另外，对于通过@Bean定义的BeanPostProcessor和BeanFactoryPostProcessor要特别小心。这些方法通常应该声明为静态的@Bean方法，
>而不是触发其包含的配置类的实例化。否则，@Autowired和@Value可能不能在配置类本身上工作，因为它可以在AutowiredAnnotationBeanPostProcessor之前创建它作为一个bean实例。
>
 下面的示例展示了如何将一个bean自动拖动到另一个bean
 ```java
@Configuration
public class ServiceConfig {

    @Autowired
    private AccountRepository accountRepository;

    @Bean
    public TransferService transferService() {
        return new TransferServiceImpl(accountRepository);
    }
}

@Configuration
public class RepositoryConfig {

    private final DataSource dataSource;

    public RepositoryConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public AccountRepository accountRepository() {
        return new JdbcAccountRepository(dataSource);
    }
}

@Configuration
@Import({ServiceConfig.class, RepositoryConfig.class})
public class SystemTestConfig {

    @Bean
    public DataSource dataSource() {
        // return new DataSource
    }
}

public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(SystemTestConfig.class);
    // everything wires up across configuration classes...
    TransferService transferService = ctx.getBean(TransferService.class);
    transferService.transfer(100.00, "A123", "C456");
}
```
> 在Spring Framework 4.3中只支持@Configuration类中的构造函数注入。还要注意，如果目标bean只定义了一个构造函数，就没有必要指定@Autowired。

### 完全限定导入的bean以方便导航
在前面的场景中，使用@Autowired工作得很好，并且提供了所需的模块化，但是确定autowired bean定义在哪里声明仍然有点不明确。
例如，作为一个正在查看ServiceConfig的开发人员，您如何知道@Autowired AccountRepository bean在哪里声明?它在代码中没有明确表示，这可能很好。
请记住，用于Eclipse的Spring工具提供了一些工具，可以呈现显示如何连接所有内容的图形，这可能是您所需要的全部内容。而且，Java IDE可以很容易地找到AccountRepository
类型的所有声明和使用，并快速地向您显示返回该类型的@Bean方法的位置。

如果这种模糊性是不可接受的，并且您希望在IDE中从一个@Configuration类直接导航到另一个，请考虑自动装配配置类本身。下面的示例展示了如何做到这一点
```java
@Configuration
public class ServiceConfig {

    @Autowired
    private RepositoryConfig repositoryConfig;

    @Bean
    public TransferService transferService() {
        // navigate 'through' the config class to the @Bean method!
        return new TransferServiceImpl(repositoryConfig.accountRepository());
    }
}
```
在上述情况下，定义AccountRepository是完全明确的。但是，ServiceConfig现在与RepositoryConfig紧密耦合。这就是权衡。
这种紧密耦合可以通过使用基于接口或基于抽象类的@Configuration类得到一定程度的缓解。考虑以下示例
```java
@Configuration
public class ServiceConfig {

    @Autowired
    private RepositoryConfig repositoryConfig;

    @Bean
    public TransferService transferService() {
        return new TransferServiceImpl(repositoryConfig.accountRepository());
    }
}

@Configuration
public interface RepositoryConfig {

    @Bean
    AccountRepository accountRepository();
}

@Configuration
public class DefaultRepositoryConfig implements RepositoryConfig {

    @Bean
    public AccountRepository accountRepository() {
        return new JdbcAccountRepository(...);
    }
}

@Configuration
@Import({ServiceConfig.class, DefaultRepositoryConfig.class})  // import the concrete config!
public class SystemTestConfig {

    @Bean
    public DataSource dataSource() {
        // return DataSource
    }

}

public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(SystemTestConfig.class);
    TransferService transferService = ctx.getBean(TransferService.class);
    transferService.transfer(100.00, "A123", "C456");
}
```
现在ServiceConfig相对于具体的DefaultRepositoryConfig是松散耦合的，而且内置的IDE工具仍然有用:您可以轻松地获得RepositoryConfig实现的类型层次结构。
通过这种方式，导航@Configuration类及其依赖项与导航基于接口的代码的通常过程没有什么不同。

> 如果你想影响某些豆类的启动创建订单,考虑将其中一些声明为@Lazy(用于创建在第一次访问,而不是在启动时)或@DependsOn某些其他bean(确保特定的其他bean创建当前bean之前,超出后者直接依赖暗示)。

## 有条件地包括@Configuration类或@Bean方法
根据任意的系统状态，有条件地启用或禁用一个完整的@Configuration类，甚至是单个的@Bean方法，这通常很有用。
一个常见的例子是只有在Spring环境中启用了特定的概要文件时才使用@Profile注释来激活Bean(参见Bean定义概要文件了解详细信息)。

@Profile注释实际上是通过使用一个更灵活的注释@Conditional实现的。@Conditional注释表示特定的org.springframework.context.annotation。在注册@Bean之前应该咨询的条件实现。

Condition接口的实现提供了一个matches()方法，该方法返回true或false。例如，下面的清单显示了用于@Profile的实际条件实现
```java
@Override
public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    // Read the @Profile annotation attributes
    MultiValueMap<String, Object> attrs = metadata.getAllAnnotationAttributes(Profile.class.getName());
    if (attrs != null) {
        for (Object value : attrs.get("value")) {
            if (context.getEnvironment().acceptsProfiles(((String[]) value))) {
                return true;
            }
        }
        return false;
    }
    return true;
}
```
详细解析参考@Conditional文档

## 结合Java和XML配置
Spring @Configuration类支持的目标并不是100%完全替代Spring XML。有些工具(如Spring XML名称空间)仍然是配置容器的理想方式。在XML方便或必要的情况下,
你有一个选择:要么以XML为中心的方法通过使用容器实例化,例如,ClassPathXmlApplicationContext或实例化它以java为中心的方法通过使用所和@ImportResource注释导入XML。

### 使用以xml为中心的@Configuration类
最好是从XML引导Spring容器，并以特别的方式包含@Configuration类。例如，在使用Spring XML的大型现有代码库中，
更容易根据需要创建@Configuration类并从现有XML文件中包含它们。在本节的后面，我们将介绍在这种以xml为中心的情况下使用@Configuration类的选项。

### 将@Configuration类声明为普通Spring<bean>标签
请记住，@Configuration类最终是容器中的bean定义。在本系列示例中，我们创建了一个名为AppConfig的@Configuration类，并将其包含在其中座位
<bean>元素。因为<context:annotation-config/>打开时，容器识别@Configuration注释并正确处理在AppConfig中声明的@Bean方法。

下面的示例展示了Java中的一个普通配置类。
```java
@Configuration
public class AppConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public AccountRepository accountRepository() {
        return new JdbcAccountRepository(dataSource);
    }

    @Bean
    public TransferService transferService() {
        return new TransferService(accountRepository());
    }
}
```
下面的示例展示了一个样例system-test-config.xml文件的一部分
```xml
<beans>
    <!-- enable processing of annotations such as @Autowired and @Configuration -->
    <context:annotation-config/>
    <context:property-placeholder location="classpath:/com/acme/jdbc.properties"/>

    <bean class="com.acme.AppConfig"/>

    <bean class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="url" value="${jdbc.url}"/>
        <property name="username" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
    </bean>
</beans>
```
下面的示例展示了一个可能的jdbc。属性文件
```text
jdbc.url=jdbc:hsqldb:hsql://localhost/xdb
jdbc.username=sa
jdbc.password=
```
```java
public static void main(String[] args) {
    ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/com/acme/system-test-config.xml");
    TransferService transferService = ctx.getBean(TransferService.class);
    // ...
}
```
> 在system-test-config.xml文件,AppConfig 没有声明id元素。虽然这样做是可以接受的，但是没有必要这样做，因为没有其他bean引用过它，
>而且不太可能通过名称显式地从容器中获取它。类似地，数据源bean只根据类型自动生成，因此显式bean id并不是严格要求的。

### 使用<context:component-scan/> 选址@Configuration 类
因为@Configuration是用@Component进行元注释的，所以带有@Configuration注释的类是组件扫描的自动候选对象。使用前面示例中描述的相同场景，
我们可以重新定义system-test-config.xml，以利用组件扫描的优势。我们不需要显式声明<context:annotation-config/> 因为
<context:component-scan/>启用相同的功能。

下面的示例显示修改后的system-test . config.xml文件
```xml
<beans>
    <!-- picks up and registers AppConfig as a bean definition -->
    <context:component-scan base-package="com.acme"/>
    <context:property-placeholder location="classpath:/com/acme/jdbc.properties"/>

    <bean class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="url" value="${jdbc.url}"/>
        <property name="username" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
    </bean>
</beans>
```

### 使用@ImportResource以类为中心使用XML
在以@Configuration类作为配置容器的主要机制的应用程序中，仍然可能需要使用至少一些XML。在这些场景中，您可以使用@ImportResource并只定义所需的XML。
这样做可以实现以java为中心的方法来配置容器，并将XML保持在最低限度。下面的示例(包括一个配置类、一个定义bean的XML文件、一个属性文件和主类)
展示了如何使用@ImportResource注释来实现以java为中心的配置，该配置根据需要使用XML

```java
@Configuration
@ImportResource("classpath:/com/acme/properties-config.xml")
public class AppConfig {

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    @Bean
    public DataSource dataSource() {
        return new DriverManagerDataSource(url, username, password);
    }
}
```
```text
properties-config.xml
<beans>
    <context:property-placeholder location="classpath:/com/acme/jdbc.properties"/>
</beans>

jdbc.properties
jdbc.url=jdbc:hsqldb:hsql://localhost/xdb
jdbc.username=sa
jdbc.password=
```

```java
public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
    TransferService transferService = ctx.getBean(TransferService.class);
    // ...
}
```


