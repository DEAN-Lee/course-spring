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
