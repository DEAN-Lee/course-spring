# _**bean自定义特殊**_
Spring框架提供了许多接口，您可以使用这些接口来定制bean的性质。 
总共三个部分:
* 生命周期回调
* ApplicationContextAware和BeanNameAware
* 其他Aware相关接口
## 生命周期回调
协助容器管理bean的生命周期，可以去实现InitializingBean 和 DisposableBean 接口。
容器对前者调用afterPropertiesSet()，对后者调用destroy()，让bean在初始化和销毁bean时执行某些操作。

>JSR-250 @PostConstruct和@PreDestroy注释通常被认为是在现代Spring应用程序中接收生命周期回调的最佳实践。
使用这些注释意味着您的bean不会耦合到特定于spring的接口。有关详细信息，请参见使用@PostConstruct和@PreDestroy。<br>
如果您不希望使用JSR-250注释，但是您仍然希望消除耦合，请考虑init-method和destroy-method bean定义元数据。

最后，Spring框架使用BeanPostProcessor实现来处理它可以找到的任何回调接口，并调用适当的方法。
如果您需要定制特性或Spring默认不提供的其他生命周期行为，您可以自己实现BeanPostProcessor。有关更多信息，请参见容器扩展点

除了初始化和销毁回调之外，spring管理的对象还可以实现生命周期接口，以便这些对象可以参与启动和关闭过程，就像容器自己的生命周期所驱动的那样。

Initialization Callbacks 初始回调
---
org.springframework.beans.factory.InitializingBean接口允许容器在bean上设置了所有必要的属性之后执行初始化工作。
InitializingBean接口指定一个方法
`
void afterPropertiesSet() throws Exception;
`
我们建议您不要使用InitializingBean接口，因为它不必要地将代码与Spring结合在一起。
另外，我们建议使用@PostConstruct注释或指定POJO初始化方法。
在基于xml的配置元数据的情况下，可以使用init-method属性指定具有无效无参数签名的方法的名称。使用Java配置，您可以使用@Bean的initMethod属性。
参见接收生命周期回调。考虑以下示例
```$xslt
JAVA Code
public class ExampleBean {

    public void init() {
        // do some initialization work
    }
}
XML CONFIG
<bean id="exampleInitBean" class="examples.ExampleBean" init-method="init"/>
```
前面的示例与下面的示例(由两个清单组成)具有几乎完全相同的效果
```$xslt
<bean id="exampleInitBean" class="examples.AnotherExampleBean"/>

public class AnotherExampleBean implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        // do some initialization work
    }
}
```
对比两个示例。第一个示例没有与Spring代码耦合。

Destruction Callbacks 销毁回调
---
org.springframework.beans.factory.DisposableBean 接口允许容器在bean上设置了所有必要的属性之后执行初始化工作。
DisposableBean 接口指定一个方法
`
void destroy() throws Exception;
`
我们建议您不要使用一次性bean回调接口，因为它不必要地将代码与Spring结合在一起。另外，我们建议使用@PreDestroy注释或指定bean定义支持的泛型方法。对
于基于xml的配置元数据，您可以使用上的destroy-method属性。使用Java配置，您可以使用@Bean的destroyMethod属性。
参见接收生命周期回调。考虑以下定义:
```
<bean id="exampleInitBean" class="examples.ExampleBean" destroy-method="cleanup"/>

public class ExampleBean {

    public void cleanup() {
        // do some destruction work (like releasing pooled connections)
    }
}

```
前面的定义与下面的定义具有几乎完全相同的效果:
```$xslt
<bean id="exampleInitBean" class="examples.AnotherExampleBean"/>

public class AnotherExampleBean implements DisposableBean {

    @Override
    public void destroy() {
        // do some destruction work (like releasing pooled connections)
    }
}

```
对比两个示例。第一个示例没有与Spring代码耦合。

Default Initialization and Destroy Methods 默认初始销毁方法
---
当您编写初始化和销毁不使用特定于spring的InitializingBean和DisposableBean回调接口的方法回调时，您通常编写具有init()、initialize()、dispose()等名称的方法。
理想情况下，这种生命周期回调方法的名称在整个项目中标准化，以便所有开发人员使用相同的方法名称并确保一致性。

您可以配置Spring容器来查找已命名的初始化，并销毁每个bean上的回调方法名称。这意味着，作为应用程序开发人员，您可以编写应用程序类并使用名为init()的初始化回调，
而不必为每个bean定义配置init-method="init"属性。Spring IoC容器在创建bean时调用该方法(并且与前面描述的标准生命周期回调契约一致)。
该特性还强制对初始化和销毁方法回调执行一致的命名约定。

假设您的初始化回调方法名为init()，而销毁回调方法名为destroy()。然后，您的类类似于下面示例中的类。
```java
public class DefaultBlogService implements BlogService {

    private BlogDao blogDao;

    public void setBlogDao(BlogDao blogDao) {
        this.blogDao = blogDao;
    }

    // this is (unsurprisingly) the initialization callback method
    public void init() {
        if (this.blogDao == null) {
            throw new IllegalStateException("The [blogDao] property must be set.");
        }
    }
}
```
``` xml
<beans default-init-method="init">

    <bean id="blogService" class="com.something.DefaultBlogService">
        <property name="blogDao" ref="blogDao" />
    </bean>

</beans>
```
在顶级元素属性中出现的default-init-method属性会导致Spring IoC容器识别bean类中名为init的方法作为初始化方法回调。
在创建和组装bean时，如果bean类有这样的方法，则会在适当的时候调用它。

您可以使用顶级元素上的default-destroy-method属性来配置销毁方法回调(也就是在XML中)。

如果现有的bean类已经有了根据约定命名的回调方法，那么您可以通过使用本身的init-method和destroy-method属性来指定(在XML中，也就是)方法名来覆盖默认值。

组合生命周期机制
--
从spring2.5开始，您有三种控制bean生命周期行为的选项
* InitializingBean 和 DisposableBean 接口实现
* 自定义init()和destroy()方法
* @PostConstruct和@PreDestroy注释。您可以将这些机制来控制给定的bean。
> 如果为一个bean配置了多个生命周期机制，并且每个机制都配置了不同的方法名，那么每个配置的方法都按照注释后面列出的顺序运行。
> 但是，如果为多个生命周期机制的初始化方法配置了相同的方法名，例如为init()，那么该方法将运行一次。

初始方法使用不同的初始化方法名称为同一个bean配置了多个生命周期机制,按照下述顺序调用：
1. @PostConstruct 注释的方法，
2. InitializingBean回调接口定义的afterPropertiesSet()
3. 自定义配置的init()方法

销毁方法的调用顺序相同
1. 使用@PreDestroy 注释的方法，
2. DisposableBean回调接口定义的destroy()
3. 自定义配置的destroy()方法

结束回调函数
---
Lifecycle接口为任何有自己生命周期需求的对象(比如启动和停止某些后台进程)定义了基本方法
```java
public interface Lifecycle {
    void start();

    void stop();

    boolean isRunning();
}
```
任何spring管理的对象都可以实现生命周期接口。然后，当ApplicationContext本身接收到启动和停止信号时(例如，对于运行时的停止/重启场景)，
它将这些调用级联到该上下文中定义的所有生命周期实现。它通过委托给LifecycleProcessor来实现这一点，如下面的清单所示
```java
public interface LifecycleProcessor extends Lifecycle {

    void onRefresh();

    void onClose();
}
```
注意，LifecycleProcessor本身就是生命周期接口的扩展。它还添加了另外两个方法，用于对刷新和关闭上下文做出反应。
>请注意常规的org.springframework.context.Lifecycle 接口是一个用于显式启动和停止通知的普通契约，并不意味着在上下文刷新时自动启动。
>对于特定bean的自动启动(包括启动阶段)的细粒度控制，可以考虑实现org.springframework.context.SmartLifecycle代替
>另外，请注意，停止通知不能保证在销毁之前发出。在常规关闭时，所有生命周期bean首先会在传播常规销毁回调之前收到停止通知。
>但是，在上下文生存期的热刷新或停止刷新尝试时，只调用destroy方法。

启动和关闭调用的顺序可能很重要。如果任何两个对象之间存在依赖关系，依赖端在依赖项之后开始，在依赖项之前停止。然而，有时直接的依赖关系是未知的。
您可能只知道某种类型的对象应该先于另一种类型的对象启动。在这些情况下，SmartLifecycle接口定义了另一个选项，即在其父类接口phase上定义的getPhase()方法。
下面的清单显示了Phase接口的定义
```java
public interface Phased {

    int getPhase();
}
```
下面的清单显示了SmartLifecycle接口的定义
```java
public interface SmartLifecycle extends Lifecycle, Phased {

    boolean isAutoStartup();

    void stop(Runnable callback);
}
```
开始时,最低的相位开始的第一个对象。当停止时，按相反的顺序执行。因此，实现SmartLifecycle并其getPhase()方法返回整数的对象。最小值应该是最先开始和最后停止的值。
另一个序列，一个整数相位值。最大值表示应该最后启动并首先停止该对象(可能是因为它依赖于正在运行的其他进程)。当考虑状态值时，
同样重要的是要知道对于任何没有实现SmartLifecycle的正常生命周期对象，其默认的状态值是0。因此，任何负状态值都表示一个对象应该在那些标准组件之前开始(并在它们之后停止)。适用于任何正状态值。

SmartLifecycle定义的stop方法接受回调。任何实现都必须在实现关闭过程完成后调用回调函数run()方法。这可以在必要时实现异步关闭，
因为LifecycleProcessor接口的默认实现DefaultLifecycleProcessor会等待每个状态中对象组调用该回调的超时值。
每个状态的默认超时时间为30秒。您可以通过在上下文中定义名为lifecycleProcessor的bean来覆盖默认的生命周期处理器实例。如果只想修改超时，那么定义以下内容就足够了
```xml
<bean id="lifecycleProcessor" class="org.springframework.context.support.DefaultLifecycleProcessor">
    <!-- timeout value in milliseconds -->
    <property name="timeoutPerShutdownPhase" value="10000"/>
</bean>
```
正如前面提到的，LifecycleProcessor接口还定义了刷新和关闭上下文的回调方法。后者驱动关闭过程，就好像是显式地调用了stop()，但是它发生在上下文关闭时。
另一方面，“刷新”回调可以实现SmartLifecycle bean的另一个特性。当上下文被刷新时(在所有对象被实例化和初始化之后)，回调被调用。
这时，默认的生命周期处理器会检查每个SmartLifecycle对象的isAutoStartup()方法返回的布尔值。如果为真，则在该点启动该对象，
而不是等待上下文或其自己的start()方法的显式调用(与上下文刷新不同，对于标准上下文实现，上下文启动不会自动发生)。如上所述，相位值和任何“依赖”关系决定启动顺序。

Spring的IoC容器关闭优雅地在非web应用程序中
---
本节仅适用于非web应用程序。基于web的Spring ApplicationContext实现已经准备好了代码，可以在相关的web应用程序关闭时优雅地关闭Spring IoC容器

如果在非web应用程序环境中(例如，在富客户机桌面环境中)使用Spring s IoC容器，请向JVM注册一个关机钩子。这样做可以确保优雅地关闭，
并调用单例bean上的相关销毁方法，从而释放所有资源。您仍然必须正确配置和实现这些销毁回调。

要注册关闭钩子，请调用registerShutdownHook()方法，该方法在ConfigurableApplicationContext接口上声明，如下面的示例所示
```java

public final class Boot {

    public static void main(final String[] args) throws Exception {
        ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");

        // add a shutdown hook for the above context...
        ctx.registerShutdownHook();

        // app runs here...

        // main method exits, hook is called prior to the app shutting down...
    }}
```
  
## ApplicationContextAware 和 BeanNameAware
当ApplicationContext创建一个实现org.springframework.context.ApplicationContextAware接口的对象时，该实例通过对ApplicationContext的提供设置。
ApplicationContextAware 接口信息
```
public interface ApplicationContextAware {

    void setApplicationContext(ApplicationContext applicationContext) throws BeansException;
}
```
因此，bean可以通过ApplicationContext接口或通过将引用转换为该接口的已知子类(比如ConfigurableApplicationContext，它公开了其他功能)，
以编程方式操作创建它们的ApplicationContext。另一种用途，是对其他bean进行编程检索。有时这个功能是有用的。但是，通常应该避免使用它，因为它将代码与Spring结合在一起，
并且不遵循控制反转风格，在这种风格中协作者作为属性提供给bean。ApplicationContext的其他方法提供了对文件资源的访问、发布应用程序事件和访问消息源。

自动装配是获得对ApplicationContext的引用的另一种选择。传统的构造函数和byType自动装配模式(如在自动装配协作器中所描述的)可以分别为构造函数参数或setter方法参数提供类型ApplicationContext的依赖关系。
为了获得更大的灵活性，包括自动装配字段和多个参数方法的能力，可以使用基于注解的自动装配特性。如果你这样做了，ApplicationContext就会自动生成一个字段、构造函数参数或方法参数，
如果有问题的字段、构造函数或方法带有@Autowired注解，那么这些参数期望得到ApplicationContext类型。

当ApplicationContext创建一个实现org.springframework.beans.factory.BeanNameAware接口的类，为类提供对其关联对象定义中定义的名称的引用。下面的清单显示了BeanNameAware接口的定义
```
public interface BeanNameAware {

    void setBeanName(String name) throws BeansException;
}
```
在填充普通bean属性之后，但在初始化回调(如InitializingBean、afterPropertiesSet或自定义初始化方法)之前调用回调。

##  其他发现接口
除了applicationcontextAware和BeanNameAware之外，Spring还提供了广泛的可发现回调接口，让bean向容器表明它们需要某种基础设施依赖关系。
作为一般规则，名称表示依赖类型。下表总结了最重要的感知接口。

| 名称 | 描述 | 
| :----:  | :----: | 
| ApplicationContextAware | Declaring ApplicationContext  | 
| ApplicationEventPublisherAware | Event publisher of the enclosing ApplicationContext. | 
| BeanClassLoaderAware | BeanClassLoaderAware | 
| BeanFactoryAware | 	Declaring BeanFactory. | 
| BeanNameAware | 	Name of the declaring bean. | 
| BootstrapContextAware | Resource adapter BootstrapContext the container runs in. Typically available only in JCA-aware ApplicationContext instances. | 
| LoadTimeWeaverAware |	Defined weaver for processing class definition at load time.| 
| MessageSourceAware | Configured strategy for resolving messages (with support for parametrization and internationalization). | 
| NotificationPublisherAware | Spring JMX notification publisher. | 
| ResourceLoaderAware |  Configured loader for low-level access to resources. | 
| ServletConfigAware |	Current ServletConfig the container runs in. Valid only in a web-aware Spring ApplicationContext. | 
| ServletContextAware |  ServletContextAware | 

请再次注意，使用这些接口将您的代码绑定到Spring API，并且不遵循控制反转样式。因此，我们建议将它们用于需要对容器进行编程访问的基础设施bean。