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


  
