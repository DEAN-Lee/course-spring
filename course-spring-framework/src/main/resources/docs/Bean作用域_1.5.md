# bean 作用域
Sprig容器中不仅可以对特定bean定义创建的对象中的各种依赖项和配置值，还可以控制从特定bean定义创建的对象的范围。
这种方法功能强大且灵活，因为您可以选择通过配置创建的对象的范围，而不必在Java类级别烘焙对象的范围。可以将bean定义为部署在多种作用域中的一种。
Spring框架支持6种作用域，其中4种作用域只有在使用支持web的ApplicationContext时才可用。您还可以创建自定义范围。

六个作用域

| 作用域 | 描述 | 
| :----:  | :----: | 
| singleton | (默认)将每个Spring IoC容器的单个bean定义定位到单个对象实例。  | 
| prototype | 将单个bean定义作用于任意数量的对象实例。 | 
| request | 将单个bean定义定位到单个HTTP请求的生命周期。也就是说，每个HTTP请求都有它自己的bean实例，该实例是在单个bean定义的背面创建的。仅在支持web的Spring ApplicationContext上下文中有效。 | 
| session | 将单个bean定义作用于HTTP会话的生命周期。仅在支持web的Spring ApplicationContext上下文中有效。 | 
| application | 将单个bean定义作用于ServletContext的生命周期。仅在支持web的Spring ApplicationContext上下文中有效。 | 
| websocket | 将单个bean定义作用于WebSocket的生命周期。仅在支持web的Spring ApplicationContext上下文中有效。 | 

## The Singleton Scope 单例模式 默认模式
只管理一个单例bean的共享实例，并且所有对具有一个或多个ID的bean的请求都与该bean定义相匹配，从而导致Spring容器返回一个特定的bean实例。

当您定义一个bean定义并且它的作用域是一个单例对象时，Spring IoC容器会创建该bean定义定义的对象的一个实例。
这个单一实例存储在这样的单例bean的缓存中，对这个已命名bean的所有后续请求和引用都会返回缓存的对象。
下图显示了单例作用域的工作方式

![image](https://github.com/DEAN-Lee/img-rep/blob/master/springframework/9609a24842c14f5e8a4a3751428c7dd.png)

## The Prototype Scope 原型模式
bean部署的非单例原型范围导致每次对特定bean发出请求时都创建一个新的bean实例。
也就是说，该bean被注入到另一个bean中，或者您通过容器上的getBean()方法调用请求它。作为规则，您应该对所有有状态bean使用原型作用域，对无状态bean使用单例作用域。

![image](https://raw.githubusercontent.com/DEAN-Lee/img-rep/master/springframework/20200826154125.png)

## 单例模式 vs 原型模式
当您使用带有原型bean依赖项的单例范围bean时，请注意依赖项是在实例化时解析的。
因此，如果您依赖地将一个原型作用域的bean注入到一个单例作用域的bean中，那么一个新的原型bean将被实例化，然后依赖地注入到单例bean中。原型实例是提供给单例作用域bean的唯一实例。

但是，假设您希望单例作用域bean在运行时重复获得原型作用域bean的新实例。您不能依赖地将原型作用域的bean注入到单例bean中，
因为该注入只在Spring容器实例化单例bean并解析和注入其依赖项时发生一次。如果您不止一次地需要原型bean在运行时的新实例，请参阅方法注入

## request、session、application webSocket
请求、会话、应用程序和websocket作用域只有在使用web感知的Spring ApplicationContext实现(如XmlWebApplicationContext)时才可用。
如果您将这些作用域与常规的Spring IoC容器(如ClassPathXmlApplicationContext)一起使用，就会抛出一个IllegalStateException，该异常报告一个未知的bean作用域。

## 自定义作用域
bean作用域机制是可扩展的。您可以定义自己的作用域，甚至可以重新定义现有的作用域，尽管后者被认为是不好的做法，而且不能覆盖内置的singleton和prototype作用域。
### 创建自定义作用域
创建自定义作用域需要实现org.springframework.beans.factory.config.Scope接口。

Scope接口有四个方法，用于从范围中获取对象、从范围中删除对象以及销毁它们。

* Object get(String name, ObjectFactory<?> objectFactory)
会话作用域实现返回会话作用域的bean(如果它不存在，则在将其绑定到会话以供将来引用之后，该方法返回该bean的一个新实例)。
* getConversationId
获取底层范围的对话标识符。这个标识符对于每个范围都是不同的。对于会话范围的实现，此标识符可以是会话标识符。
* registerDestructionCallback
注册一个回调，当范围销毁或范围中指定的对象销毁时，该范围应调用该回调
* remove
会话作用域实现从基础会话中删除会话作用域bean。应该返回该对象，但如果没有找到具有指定名称的对象，则可以返回null。下面的方法将对象从基础范围中移除
### 使用自定义作用域
在编写和测试一个或多个自定义作用域实现之后，您需要让Spring容器知道您的新作用域。下面的方法是向Spring容器注册新范围的中心方法
```$xslt
void registerScope(String scopeName, Scope scope);
```
此方法在ConfigurableBeanFactory接口上声明，该接口可通过Spring附带的大多数具体ApplicationContext实现上的BeanFactory属性使用。

registerScope(..)方法的第一个参数是与作用域关联的惟一名称。此类名称在Spring容器本身中的例子有singleton和prototype。
registerScope(..)方法的第二个参数是您希望注册和使用的自定义范围实现的一个实际实例。
```
Scope threadScope = new SimpleThreadScope();
beanFactory.registerScope("thread", threadScope);
```
XML配置
```
<bean id="..." class="..." scope="thread">

```
使用自定义范围实现，您不必局限于对范围的编程式注册。您还可以使用CustomScopeConfigurer类声明性地进行范围注册，如下面的示例所示
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:aop="http://www.springframework.org/schema/aop"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/aop
        https://www.springframework.org/schema/aop/spring-aop.xsd">

    <bean class="org.springframework.beans.factory.config.CustomScopeConfigurer">
        <property name="scopes">
            <map>
                <entry key="thread">
                    <bean class="org.springframework.context.support.SimpleThreadScope"/>
                </entry>
            </map>
        </property>
    </bean>

    <bean id="thing2" class="x.y.Thing2" scope="thread">
        <property name="name" value="Rick"/>
        <aop:scoped-proxy/>
    </bean>

    <bean id="thing1" class="x.y.Thing1">
        <property name="thing2" ref="thing2"/>
    </bean>

</beans>
```

