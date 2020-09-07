# Spring容器可插入扩展
通常，应用程序开发人员不需要子类化ApplicationContext实现类。相反，可以通过插入特殊集成接口的实现来扩展Spring IoC容器。接下来的几节将描述这些集成接口。
## 通过使用BeanPostProcessor定制bean
BeanPostProcessor接口定义了回调方法，您可以实现这些方法来提供您自己的(或覆盖容器默认值)实例化逻辑、依赖项解析逻辑等等。
如果您想在Spring容器完成实例化、配置和初始化bean之后实现一些自定义逻辑，您可以插入一个或多个自定义BeanPostProcessor实现。

您可以配置多个BeanPostProcessor实例，并且可以通过设置order属性来控制这些BeanPostProcessor实例运行的顺序。
只有当BeanPostProcessor实现了有序接口时，才能设置此属性。如果您编写自己的BeanPostProcessor，也应该考虑实现Ordered接口。
有关详细信息，请参阅BeanPostProcessor和有序接口的javadoc。请参见BeanPostProcessor实例的编程注册说明。
> BeanPostProcessor实例操作bean(或对象)实例。也就是说，Spring IoC容器实例化一个bean实例，然后BeanPostProcessor实例执行它们的工作
>
>BeanPostProcessor实例的作用域为每个容器。只有当您使用容器层次结构时，这才是相关的。如果在一个容器中定义BeanPostProcessor，
>那么它只对该容器中的bean进行后处理。换句话说，在一个容器中定义的bean不会被另一个容器中定义的BeanPostProcessor进行后处理，即使这两个容器属于同一层次结构。
>
>要更改实际的bean定义(即定义bean的蓝图)，您需要使用BeanFactoryPostProcessor，正如在使用BeanFactoryPostProcessor自定义配置元数据中所描述的那样。

org.springframework.beans.factory.config.BeanPostProcessor接口恰好由两个回调方法组成。当这样一个类在容器中注册为后处理器,
容器每创建bean实例,在调用容器初始化方法(例如InitializingBean.afterPropertiesSet()或任何声明的init方法)之前和在任何bean初始化回调之后，后处理器都会从容器获得一个回调。
后处理器可以对bean实例采取任何操作，包括完全忽略回调。bean后处理器通常检查回调接口，或者用代理包装bean。为了提供代理包装逻辑，一些Spring AOP基础实现类实现方式为beanPostProcessor。

ApplicationContext自动检测在实现BeanPostProcessor接口的配置元数据中定义的任何bean。ApplicationContext将这些bean注册为后处理器，
以便稍后在bean创建时调用它们。Bean后处理器可以与其他Bean相同的方式部署在容器中。

注意，当通过在配置类上使用@Bean工厂方法声明BeanPostProcessor时，工厂方法的返回类型应该是实现类本身，
或者至少是org.springframework.bean .factory.config.BeanPostProcessor接口，清楚地表明该bean的后处理器特性。否则，ApplicationContext不能在完全创建它之前按类型自动检测它。
由于BeanPostProcessor需要尽早实例化，以便应用于上下文中其他bean的初始化，因此这种早期类型检测非常关键。

> #### 以编程方式注册BeanPostProcessor实例
>虽然推荐的BeanPostProcessor注册方法是通过ApplicationContext自动检测(如前所述)，但是您可以通过使用addBeanPostProcessor方法通过编程方式针对ConfigurableBeanFactory注册它们。
>当您需要在注册前计算条件逻辑时，或者甚至在跨层次结构中的上下文复制bean post处理器时，这可能非常有用。
>但是请注意，以编程方式添加的BeanPostProcessor实例不遵循Ordered接口。在这里，注册的顺序决定了执行的顺序。
>还要注意，无论任何显式排序如何，编程注册的BeanPostProcessor实例总是在通过自动检测注册的实例之前处理。

> #### 	BeanPostProcessor实例和AOP自动代理
> 实现BeanPostProcessor接口的类是特殊的，容器会以不同的方式处理它们。作为ApplicationContext特殊启动阶段的一部分，
> 所有BeanPostProcessor实例和它们直接引用的bean都在启动时实例化。
>接下来，以排序的方式注册所有BeanPostProcessor实例，并应用于容器中进一步的所有bean。
>因为AOP自动代理是作为BeanPostProcessor本身实现的，所以无论是BeanPostProcessor实例还是它们直接引用的bean都不适合自动代理，因此，它们没有将方面编织到其中。
>
>对于任何这样的bean，您都应该看到一个信息日志消息
>Bean someBean is not eligible for getting processed by all BeanPostProcessor interfaces (for example: not eligible for auto-proxying)
>
>如果您使用自动装配将bean连接到BeanPostProcessor或者@Resource(可能会回到自动装配)，Spring可能会在搜索类型匹配依赖项候选时访问意外的bean，因此，使它们不符合自动代理或其他类型的bean后处理的条件。
>例如，如果您有一个注释为@Resource的依赖项，其中字段或setter名称并不直接对应于bean的声明名称，并且没有使用name属性，那么Spring将访问其他bean以按类型匹配它们。
>

下面的示例展示了如何在ApplicationContext中编写、注册和使用BeanPostProcessor实例。

### BeanPostProcessor-style
第一个示例演示了基本用法。示例展示了一个自定义BeanPostProcessor实现，它在容器创建每个bean时调用该bean的toString()方法，并将结果字符串打印到系统控制台。

下面的清单显示了自定义BeanPostProcessor实现类定义
```java
public class InstantiationTracingBeanPostProcessor implements BeanPostProcessor {

    // simply return the instantiated bean as-is
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean; // we could potentially return any object reference here...
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("Bean '" + beanName + "' created : " + bean.toString());
        return bean;
    }
}

```
下面的bean元素使用实例化InstantiationTracingBeanPostProcessor 
``` xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:lang="http://www.springframework.org/schema/lang"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/lang
        https://www.springframework.org/schema/lang/spring-lang.xsd">

    <lang:groovy id="messenger"
            script-source="classpath:org/springframework/scripting/groovy/Messenger.groovy">
        <lang:property name="message" value="Fiona Apple Is Just So Dreamy."/>
    </lang:groovy>

    <!--
    when the above bean (messenger) is instantiated, this custom
    BeanPostProcessor implementation will output the fact to the system console
    -->
    <bean class="scripting.InstantiationTracingBeanPostProcessor"/>

</beans>
```
请注意实例化InstantiationTracingBeanPostProcessor是如何定义的。它甚至没有名称，而且，因为它是一个bean，所以可以像其他bean一样进行依赖注入。
(前面的配置还定义了一个由Groovy脚本支持的bean。Spring的动态语言支持在名为动态语言支持的章节中有详细说明。)

### 例子:RequiredAnnotationBeanPostProcessor
将回调接口或注释与自定义BeanPostProcessor实现结合使用是扩展Spring IoC容器的一种常见方法。
一个例子是Spring的RequiredAnnotationBeanPostProcessor实现，它与Spring发行版一起发布，确保用(任意)注释标记的bean上的JavaBean属性实际上(配置为)依赖注入一个值。

## 使用BeanFactoryPostProcessor自定义配置元数据
我们关注另外一个延伸点org.springframework.beans.factory.config.BeanFactoryPostProcessor。此接口的语义类似于BeanPostProcessor的语义，
有一个主要的区别:BeanFactoryPostProcessor操作bean配置元数据。也就是说，Spring IoC容器允许BeanFactoryPostProcessor在容器实例化BeanFactoryPostProcessor实例之外的
任何bean之前读取配置元数据并可能更改它。

您可以配置多个BeanFactoryPostProcessor实例，并且您可以通过设置order属性来控制这些BeanFactoryPostProcessor实例运行的顺序。但是，
您只能在BeanFactoryPostProcessor实现了有序接口的情况下设置此属性。如果您编写自己的BeanFactoryPostProcessor，也应该考虑实现Ordered接口。
有关更多细节，请参阅BeanFactoryPostProcessor和Ordered接口的javadoc。

>如果您希望更改实际的bean实例(即从配置元数据创建的对象)，则需要使用BeanPostProcessor(在前面通过使用BeanPostProcessor定制bean中进行了描述)。
>虽然在BeanFactoryPostProcessor中使用bean实例在技术上是可行的(例如，通过使用BeanFactory.getBean())，但是这样做会导致过早的bean实例化，违反标准的容器生命周期。
>这可能会导致负面的副作用，比如绕过bean的后处理。
>
>另外，BeanFactoryPostProcessor实例的作用域为每个容器。这只有在使用容器层次结构时才有用。如果您在一个容器中定义了BeanFactoryPostProcessor，那么它只应用于该容器中的bean定义。
>一个容器中的Bean定义不会被另一个容器中的BeanFactoryPostProcessor实例进行后处理，即使这两个容器属于同一层次结构。

当bean工厂后处理器在ApplicationContext中声明时，它将自动运行，以便对定义容器的配置元数据应用更改。Spring包括许多预定义的bean工厂后处理器，
如PropertyOverrideConfigurer和PropertySourcesPlaceholderConfigurer。例如，您还可以使用自定义BeanFactoryPostProcessor来注册自定义属性编辑器。

ApplicationContext自动检测部署到其中实现BeanFactoryPostProcessor接口的任何bean。在适当的时候，它将这些bean用作bean工厂的后处理器。可以像部署任何其他bean一样部署这些后处理bean。

>与BeanPostProcessors一样，您通常不希望将BeanFactoryPostProcessors配置为延迟初始化。
>如果没有其他bean引用bean(工厂)后处理器，则该后处理器根本不会实例化。因此，将其标记为延迟初始化将被忽略，并且即使在声明元素时将default-lazy-init属性设置为true,
> Bean(工厂)后处理器也将被快速实例化。

### 示例:类名替换PropertySourcesPlaceholderConfigurer

您可以使用PropertySourcesPlaceholderConfigurer使用标准的Java属性格式将bean定义中的属性值外部化到单独的文件中。这样，部署应用程序的人员就可以自定义特定于环境的属性，
比如数据库url和密码，而无需修改主XML定义文件或容器文件的复杂性或风险。
考虑以下基于xml的配置元数据片段，其中定义了具有占位符值的数据源
```xml
<bean class="org.springframework.context.support.PropertySourcesPlaceholderConfigurer">
    <property name="locations" value="classpath:com/something/jdbc.properties"/>
</bean>

<bean id="dataSource" destroy-method="close"
        class="org.apache.commons.dbcp.BasicDataSource">
    <property name="driverClassName" value="${jdbc.driverClassName}"/>
    <property name="url" value="${jdbc.url}"/>
    <property name="username" value="${jdbc.username}"/>
    <property name="password" value="${jdbc.password}"/>
</bean>
```
该示例显示了从外部属性文件配置的属性。在运行时，PropertySourcesPlaceholderConfigurer应用于替换数据源的某些属性的元数据。
要替换的值指定为表单${property-name}的占位符，该表单遵循Ant、log4j和JSP EL样式。

实际值来自标准Java属性格式的另一个文件
```
jdbc.driverClassName=org.hsqldb.jdbcDriver
jdbc.url=jdbc:hsqldb:hsql://production:9002
jdbc.username=sa
jdbc.password=root
```

因此,${jdbc.username}字符串在运行时被替换为值'sa'，对于属性文件中匹配键的其他占位符值也适用同样的方法。
PropertySourcesPlaceholderConfigurer检查bean定义的大多数属性和属性中的占位符。此外，您还可以自定义占位符前缀和后缀。

使用Spring 2.5中引入的上下文名称空间，您可以用一个专用的配置元素配置属性占位符。
您可以在location属性中以逗号分隔的列表形式提供一个或多个位置，如下面的示例所示
```xml
<context:property-placeholder location="classpath:com/something/jdbc.properties"/>
```

PropertySourcesPlaceholderConfigurer不仅在您指定的属性文件中查找属性。
默认情况下，如果在指定的属性文件中找不到属性，它将检查Spring环境属性和常规Java系统属性。

>您可以使用PropertySourcesPlaceholderConfigurer来替换类名，当您必须在运行时选择特定的实现类时，这有时很有用。
>下面的示例展示了如何做到这一点
>
>xml
><bean class="org.springframework.beans.factory.config.PropertySourcesPlaceholderConfigurer">
>    <property name="locations">
>        <value>classpath:com/something/strategy.properties</value>
>    </property>
>    <property name="properties">
>        <value>custom.strategy.class=com.something.DefaultStrategy</value>
>    </property>
></bean>
>
><bean id="serviceStrategy" class="${custom.strategy.class}"/>
>
>如果不能在运行时将类解析为有效类，则在即将创建bean时，即在ApplicationContext非延迟init bean的预实例化esingletons()阶段，对bean的解析将失败。
>

### 例子:PropertyOverrideConfigurer
PropertyOverrideConfigurer是另一个bean工厂后处理器，它类似于PropertySourcesPlaceholderConfigurer，但与后者不同的是，
原始定义可以有bean属性的默认值，也可以没有值。如果覆盖属性文件没有针对某个bean属性的条目，则使用默认上下文定义。

请注意，bean定义不知道被覆盖，因此从XML定义文件中不能立即看出正在使用覆盖配置器。在多个PropertyOverrideConfigurer实例为同一个bean属性定义不同值的情况下，
由于覆盖机制，最后一个实例胜出。

属性文件配置行采用以下格式
```text
beanName.property=value
```
下面的清单显示了该格式的示例
```text
dataSource.driverClassName=com.mysql.jdbc.Driver
dataSource.url=jdbc:mysql:mydb
```

这个示例文件可以与一个容器定义一起使用，该容器定义包含一个名为dataSource的bean，该bean具有driver和url属性。

还支持复合属性名，只要路径的每个组件(除了被覆盖的final属性)都是非空的(假定由构造函数初始化)。在下面的示例中，tom bean的fred属性的bob属性的sammy属性被设置为标量值123
```text
tom.fred.bob.sammy=123
```
> 指定的重写值总是文字值。它们没有被转换为bean引用。当XML bean定义中的原始值指定了一个bean引用时，这个约定也适用。

由于在Spring 2.5中引入了上下文名称空间，所以可以用专用的配置元素配置属性重写，如下面的示例所示
```xml
<context:property-override location="classpath:override.properties"/>
```

## 使用FactoryBean自定义实例化逻辑
您可以实现org.springframework.beans.factory.FactoryBean接口为本身是工厂的对象提供。


