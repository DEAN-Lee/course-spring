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

