# @AspectJ支持
@AspectJ引用了一种将方面声明为带有注释的常规Java类的样式。@AspectJ样式是由AspectJ项目作为AspectJ 5发行版的一部分引入的。
Spring使用AspectJ提供的用于切入点解析和匹配的库来解释与AspectJ 5相同的注释。然而，AOP运行时仍然是纯粹的Spring AOP，并且不依赖于AspectJ编译器或weaver。

> 使用AspectJ编译器和weaver可以使用完整的AspectJ语言，在与Spring应用程序一起使用AspectJ中对此进行了讨论。

## 支持@ aspectj的支持
要在Spring配置中使用@AspectJ方面，您需要启用Spring支持，以基于@AspectJ方面配置Spring AOP，并根据这些方面是否建议自动代理bean。
通过自动代理，我们的意思是，如果Spring确定一个bean由一个或多个方面通知，它会自动为该bean生成一个代理，以拦截方法调用，并确保通知按需执行。

@AspectJ支持可以通过XML或java风格的配置启用。在这两种情况下，您还需要确保AspectJ的aspectjweaver.jar库位于应用程序的类路径中(版本1.8或更高版本)。
这个库可以在AspectJ发行版的lib目录或Maven中央存储库中找到。

### 通过Java配置启用@AspectJ支持
要使用Java @Configuration启用@AspectJ支持，请添加@EnableAspectJAutoProxy注释，如下面的示例所示
```java
@Configuration
@EnableAspectJAutoProxy
public class AppConfig {

}
```
### 通过XML配置启用@AspectJ支持
要使用基于xml的配置启用@AspectJ支持，请使用aop:aspectj-autoproxy元素，如下面的示例所示
```xml
<aop:aspectj-autoproxy/>

```

这假设您使用XML基于模式配置中描述的模式支持。请参阅AOP模式了解如何在AOP名称空间中导入标记。
