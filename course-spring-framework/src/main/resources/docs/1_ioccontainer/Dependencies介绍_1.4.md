# Dependencies介绍
## Dependency Injection
使用DI原则，代码会更清晰，并且当向对象提供它们的依赖时，解耦会更有效。
对象不查找其依赖项，也不知道依赖项的位置或类。因此，您的类变得更容易测试，特别是当依赖关系在接口或抽象基类上时，
它们允许在单元测试中使用存根或模拟实现
## 主要注入方式
* 构造器注入
* set设置注入
### 构造器注入
构造函数参数解析匹配使用参数类型。如果bean定义的构造函数参数中不存在潜在的歧义，
那么构造函数参数在bean定义中定义的顺序就是实例化bean时将这些参数提供给适当的构造函数的顺序。
```
java Code
package x.y;

public class ThingOne {

    public ThingOne(ThingTwo thingTwo, ThingThree thingThree) {
        // ...
    }
}

xml config
<beans>
    <bean id="beanOne" class="x.y.ThingOne">
        <constructor-arg ref="beanTwo"/>
        <constructor-arg ref="beanThree"/>
    </bean>

    <bean id="beanTwo" class="x.y.ThingTwo"/>

    <bean id="beanThree" class="x.y.ThingThree"/>
</beans>

```
构造器参数类型匹配方式
* 构造参数类型匹配 
* 构造参数索引匹配
* 构造参数名称匹配 必须使用调试模式编译 或者 @ConstructorProperties 显示注解
```
package examples;

public class ExampleBean {

    // Number of years to calculate the Ultimate Answer
    private int years;

    // The Answer to Life, the Universe, and Everything
    private String ultimateAnswer;

    public ExampleBean(int years, String ultimateAnswer) {
        this.years = years;
        this.ultimateAnswer = ultimateAnswer;
    }
}
构造参数类型匹配 
<bean id="exampleBean" class="examples.ExampleBean">
    <constructor-arg type="int" value="7500000"/>
    <constructor-arg type="java.lang.String" value="42"/>
</bean>
构造参数索引匹配
<bean id="exampleBean" class="examples.ExampleBean">
    <constructor-arg index="0" value="7500000"/>
    <constructor-arg index="1" value="42"/>
</bean>
构造参数名称匹配
<bean id="exampleBean" class="examples.ExampleBean">
    <constructor-arg name="years" value="7500000"/>
    <constructor-arg name="ultimateAnswer" value="42"/>
</bean>
```
### set设置注入
基于setter的DI是由容器在调用无参数构造函数或无参数静态工厂方法来实例化bean之后调用bean上的setter方法来完成的。
```
java
package org.dean.course.framework.container;

public class UserServiceSetDI {
    private UserInfoDao userInfoDao;
    private AccountDao accountDao;

    public UserInfoDao getUserInfoDao() {
        return userInfoDao;
    }

    public AccountDao getAccountDao() {
        return accountDao;
    }

    public void setUserInfoDao(UserInfoDao userInfoDao) {
        this.userInfoDao = userInfoDao;
    }


    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }
}

xml config
  <bean id="userService" class="org.dean.course.framework.container.UserServiceSetDI">
        <property name="accountDao" ref="accountDao"/>
        <property name="userInfoDao" ref="userInfoDao"/>
        <!-- additional collaborators and configuration for this bean go here -->
    </bean>
```

## 构造注入 vs set设值注入
由于可以混合使用基于构造器和基于setter的DI，对于强制依赖项使用构造器注入，而对于可选依赖项使用setter方法或配置方法，这是一个很好的经验法则。
请注意，在setter方法上使用@Required注释可以使属性成为必需的依赖项;但是，使用对参数进行编程验证的构造函数注入更可取。

Spring团队通常提倡构造函数注入，因为它允许您将应用程序组件实现为不可变对象，并确保所需的依赖关系不为空。
此外，构造器注入的组件总是以完全初始化的状态返回给客户端(调用)代码。顺便提一下，大量的构造函数参数是一种糟糕的代码味道，
这意味着类可能有太多的责任，应该进行重构以更好地解决问题的适当分离。

Setter注入应该主要用于可选的依赖项，这些依赖项可以在类中分配合理的默认值。
否则，必须在代码使用依赖项的任何地方执行非空检查。setter注入的一个好处是，setter方法使该类的对象易于稍后重新配置或重新注入。
因此，通过JMX mbean进行管理是setter注入的一个引人注目的用例。

使用对特定类最有意义的DI样式。有时，在处理没有源代码的第三方类时，需要自己做出选择。
例如，如果第三方类不公开任何setter方法，那么构造函数注入可能是DI的唯一可用形式。

## 依赖解析过程
依赖解析过程如下:
* ApplicationContext 创建从元数据中获取的bean对象以及对象信息。元数据从指定的XML配置、注解配置以及Java代码中获取
* 对于每个bean，其依赖关系都以属性、构造函数参数或静态工厂方法的参数(如果使用静态工厂方法而不是普通构造函数)的形式表示。当bean被实际创建时，这些依赖项被提供给bean。
* 每个属性或构造函数参数都是要设置的值的实际定义，或者是对容器中另一个bean的引用。
* 作为值的每个属性或构造函数参数都从其指定的格式转换为该属性或构造函数参数的实际类型。默认情况下，Spring可以将字符串格式提供的值转换为所有内置类型，比如int、long、string、boolean等等。

在创建容器时，Spring容器验证每个bean的配置。
但是，bean属性本身在实际创建bean之前是不会设置的。在创建容器时，将创建单例范围并设置为预实例化(缺省值)的bean。
作用域在Bean作用域中定义。否则，只在请求bean时创建它。创建一个bean可能会导致创建一个bean图，因为创建和分配了bean的依赖项及其依赖项的依赖项(等等)。
注意，这些依赖项之间的解析不匹配可能会在后期出现，即在第一次创建受影响的bean时出现。

## 循环依赖
类A通过构造函数注入需要一个类B的实例，类B通过构造函数注入需要一个类A的实例。如果您为类A和类B配置了相互注入的bean，
那么Spring IoC容器在运行时检测到这个循环引用，并抛出BeanCurrentlyInCreationException。

如果您主要使用构造函数注入，那么可以创建一个无法解决的循环依赖场景。

您通常可以相信Spring会做正确的事情。它在容器加载时检测配置问题，例如对不存在的bean的引用和循环依赖关系。
Spring尽可能晚地设置属性并解析依赖项，当bean实际创建时。这意味着已正确加载的Spring容器在您请求对象时，如果在创建该对象或其依赖项时出现问题，则稍后可以生成异常。
例如，bean会由于缺少或无效的属性而抛出异常。某些配置问题的潜在延迟可见性是ApplicationContext实现在默认情况下预实例化单例bean的原因。
在实际需要之前创建这些bean需要花费一些前期时间和内存，在创建ApplicationContext时发现配置问题，而不是稍后发现。
您仍然可以覆盖这个默认行为，以便单例bean可以惰性地初始化，而不是预先实例化。

如果不存在循环依赖关系，那么当一个或多个协作bean被注入到依赖bean中时，每个协作bean在被注入到依赖bean之前都已被完全配置。
这意味着,如果bean依赖bean B, B Spring IoC容器完全配置bean之前调用bean的setter方法A。
换句话说,bean实例化(如果它不是一个单例预先实例化),其设置依赖项,相关的生命周期方法(如InitializingBean init方法或配置回调方法)调用。

## 懒加载
默认情况下，作为初始化过程的一部分，ApplicationContext实现会急切地创建和配置所有的单例bean。
通常，这种预实例化是可取的，因为配置或周围环境中的错误是立即发现的，而不是几小时甚至几天后发现的。
当这种情况不合适时，您可以通过将bean定义标记为延迟初始化来防止单例bean的预实例化。延迟初始化的bean告诉IoC容器在第一次请求bean实例时(而不是在启动时)创建bean实例。
```$xslt
<bean id="lazy" class="com.something.ExpensiveToCreateBean" lazy-init="true"/>
<bean name="not.lazy" class="com.something.AnotherBean"/>

```
当ApplicationContext使用前面的配置时，当ApplicationContext启动时，惰性bean不会急切地预实例化，而ApplicationContext不会。惰性bean被急切地预实例化。
还可以在容器级别控制延迟初始化,使用 beans 的default-lazy-init 属性
```$xslt
<beans default-lazy-init="true">
    <!-- no beans will be pre-instantiated... -->
</beans>
```
## Autowiring Collaborators 自动装配
Spring容器可以自动连接协作bean之间的关系。您可以通过检查ApplicationContext的内容，让Spring为您的bean自动解析协作者(其他bean)。自动装配有一下优势
* 自动装配可以显著减少指定属性或构造函数参数的需要。
* 自动装配可以更新一个配置对象演变。例如，如果需要向类添加依赖项，则无需修改配置即可自动满足该依赖项。
因此，自动装配在开发过程中特别有用，当代码库变得更加稳定时，无需切换到显式连接。

自动装配四中模式：

| 模式 | 描述 | 
| :----:  | :----:  | 
| no | (默认)没有自动装配。Bean引用必须由ref元素定义。对于较大的部署，不建议更改默认设置，因为显式地指定collaborator可以提供更好的控制和清晰度。在某种程度上，它记录了系统的结构。  | 
| byName | 按属性名称自动装配。Spring寻找与需要自动实现的属性同名的bean。例如，如果一个bean定义按名称设置为autowire，并且它包含一个master属性(也就是说，它有一个setMaster(..)方法)，那么Spring将查找一个名为master的bean定义，并使用它来设置该属性。 | 
| byType | 如果容器中恰好存在该属性类型的一个bean，则允许该属性自动实现。如果存在多个，就会抛出一个致命异常，这表明您不能对该bean使用byType自动装配。如果没有匹配的bean，则什么也不会发生(没有设置属性)。 | 
| constructor | 类似于byType，但适用于构造函数参数。如果容器中没有构造函数参数类型的确切bean，就会引发致命错误。 | 

## 方法注入 Method Injection
在大多数应用程序场景中，容器中的大多数bean都是单例的。当一个单例bean需要与另一个单例bean协作时，或者一个非单例bean需要与另一个非单例bean协作时，
通常通过将一个bean定义为另一个bean的属性来处理依赖关系。当bean的生命周期不同时，问题就出现了。假设单例bean A需要使用非单例(原型)bean B，
可能在对A的每次方法调用上都是如此，容器只创建一次单例bean A，因此只获得一次设置属性的机会。容器不能在每次需要bean B的时候都向bean A提供一个新的实例。
### Lookup Method Injection
查找方法注入是容器覆盖容器管理bean上的方法并返回容器中另一个已命名bean的查找结果的能力。查找通常涉及原型bean，如上一节所述的场景。
Spring框架通过使用来自CGLIB库的字节码生成动态生成覆盖该方法的子类来实现这种方法注入。
如果引用方注入的方法是抽象的，则动态生成的子类实现该方法。否则，动态生成的子类将覆盖在原始类中定义的具体方法
引用需要遵循下面生成方法规则
```$xslt
<public|protected> [abstract] <return-type> theMethodName(no-arguments);
```
示例
```$xslt
package fiona.apple;

// no more Spring imports!

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
xml config
<!-- a stateful bean deployed as a prototype (non-singleton) -->
<bean id="myCommand" class="fiona.apple.AsyncCommand" scope="prototype">
    <!-- inject dependencies here as required -->
</bean>

<!-- commandProcessor uses statefulCommandHelper -->
<bean id="commandManager" class="fiona.apple.CommandManager">
    <lookup-method name="createCommand" bean="myCommand"/>
</bean>

```
标识为commandManager的bean在需要myCommand bean的新实例时调用它自己的createCommand()方法。
如果真的需要将myCommand bean部署为原型，则必须小心。如果是单例，则每次都返回相同的myCommand bean实例。

另外，在基于注释的组件模型中，您可以通过@Lookup注释声明查找方法.示例
```
public abstract class CommandManager {

    public Object process(Object commandState) {
        Command command = createCommand();
        command.setState(commandState);
        return command.execute();
    }

    @Lookup("myCommand")
    protected abstract Command createCommand();
}
```
### Arbitrary Method Replacement
任意方法替换是方法注入的一种不如查找方法注入的方式。忽略，几乎很少用到。
