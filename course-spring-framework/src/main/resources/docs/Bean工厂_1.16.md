# Bean工厂
BeanFactory API为Spring的IoC功能提供了底层基础。它的特定契约主要用于与Spring的其他部分和相关的第三方框架的集成，它的DefaultListableBeanFactory
实现是高级别的GenericApplicationContext容器中的一个关键委托。

BeanFactory和相关接口(如BeanFactoryAware、InitializingBean、DisposableBean)是其他框架组件的重要集成点。由于不需要任何注释甚至反射，
它们允许容器与其组件之间进行非常有效的交互。应用程序级bean可能使用相同的回调接口，但通常更倾向于通过注释或编程配置进行声明性依赖注入。

请注意，核心BeanFactory API级别及其DefaultListableBeanFactory实现没有对要使用的配置格式或任何组件注释进行假设。
所有这些风格都通过扩展(比如XmlBeanDefinitionReader和AutowiredAnnotationBeanPostProcessor)实现，并在共享的BeanDefinition对象上进行操作，
作为核心元数据表示。这就是使Spring的容器如此灵活和可扩展的本质所在。

## BeanFactory or ApplicationContext?

本节解释BeanFactory和ApplicationContext容器级别之间的差异，以及引导的含义。

您应该使用ApplicationContext，除非您有很好的理由不这样做，使用GenericApplicationContext和它的子类AnnotationConfigApplicationContext作为自定义引导的通用实现。
这些是用于所有常见目的的Spring核心容器的主要入口点:加载配置文件、触发类路径扫描、以编程方式注册bean定义和带注释的类，以及(从5.0开始)注册功能性bean定义。

因为ApplicationContext包含了BeanFactory的所有功能，所以一般建议它优于普通的BeanFactory，除非需要对bean处理进行完全控制的场景除外。
在一个ApplicationContext中(比如GenericApplicationContext实现)，可以通过约定检测几种类型的bean(即，通过bean名称或bean类型，特别是后处理器)，
而普通的DefaultListableBeanFactory不知道任何特殊的bean。

对于许多扩展的容器特性，如注释处理和AOP代理，BeanPostProcessor扩展点是必不可少的。如果只使用普通的DefaultListableBeanFactory，
默认情况下不会检测到这种后处理器并激活它。这种情况可能令人困惑，因为您的bean配置实际上没有任何错误。相反，在这样的场景中，容器需要通过附加的设置进行完全引导。

下表列出了BeanFactory和ApplicationContext接口和实现提供的特性。

|特性|BeanFactory|ApplicationContext|
|---|---|---|
|Bean 实例化和装配|Yes|Yes|
|集成的生命周期管理|No|Yes|
|自动BeanPostProcessor注册|No|Yes|
|自动BeanFactoryPostProcessor注册|No|Yes|   
|方便的消息源访问(用于内部化)|No|Yes|   
|内置ApplicationEvent发布机制|No|Yes|

要显式地使用DefaultListableBeanFactory注册一个bean后处理器，您需要通过编程调用addBeanPostProcessor，如下面的示例所示
```java
DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
// populate the factory with bean definitions

// now register any needed BeanPostProcessor instances
factory.addBeanPostProcessor(new AutowiredAnnotationBeanPostProcessor());
factory.addBeanPostProcessor(new MyBeanPostProcessor());

// now start using the factory
```   

要将BeanFactoryPostProcessor应用到普通DefaultListableBeanFactory，您需要调用它的postProcessBeanFactory方法，如下面的示例所示
```java
DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
reader.loadBeanDefinitions(new FileSystemResource("beans.xml"));

// bring in some property values from a Properties file
PropertySourcesPlaceholderConfigurer cfg = new PropertySourcesPlaceholderConfigurer();
cfg.setLocation(new FileSystemResource("jdbc.properties"));

// now actually do the replacement
cfg.postProcessBeanFactory(factory);
```

在这两种情况下,明确登记步骤是不方便的,这就是为什么各种ApplicationContext变体都优于纯DefaultListableBeanFactory回弹应用程序,
尤其是当依靠BeanFactoryPostProcessor和BeanPostProcessor实例扩展容器功能在一个典型的企业设置。

> 一个AnnotationConfigApplicationContext注册了所有公共注释后处理器，并可能通过配置注释(如@EnableTransactionManagement)在后台引入额外的处理器。
>在Spring的基于注解的配置模型的抽象层上，bean后处理器的概念仅仅成为内部容器的细节。