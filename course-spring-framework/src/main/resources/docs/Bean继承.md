# Bean继承定义
bean定义可以包含很多配置信息,包括构造函数参数,属性值,和特定容器的信息,如初始化方法,静态工厂方法名称,等等。子bean定义从父bean定义继承配置数据。
子定义可以覆盖某些值，也可以根据需要添加其他值。使用父bean和子bean定义可以节省大量输入。实际上，这是一种模板形式。

如果您以编程方式使用ApplicationContext接口，则子bean定义由ChildBeanDefinition类表示。大多数用户不会在这个级别上使用它们。
相反，它们在类(如ClassPathXmlApplicationContext)中声明性地配置bean定义。当您使用基于xml的配置元数据时，
您可以通过使用父属性来指示子bean定义，并将父bean指定为该属性的值。
下面的示例展示了如何做到这一点
```xml
<bean id="inheritedTestBean" abstract="true"
        class="org.springframework.beans.TestBean">
    <property name="name" value="parent"/>
    <property name="age" value="1"/>
</bean>

<bean id="inheritsWithDifferentClass"
        class="org.springframework.beans.DerivedTestBean"
        parent="inheritedTestBean" init-method="initialize">  
    <property name="name" value="override"/>
    <!-- the age property value of 1 will be inherited from parent -->
</bean>
```

如果没有指定bean类，子bean定义使用父定义中的bean类，但是也可以覆盖它。在后一种情况下，子bean类必须与父bean兼容(也就是说，它必须接受父bean的属性值)。

子bean定义从父bean继承范围、构造函数参数值、属性值和方法覆盖，并提供添加新值的选项。您指定的任何范围、初始化方法、销毁方法或静态工厂方法设置都会覆盖相应的父设置。

其余的设置总是取自子定义:依赖、自动装配模式、依赖检查、单例和延迟init。

前面的示例通过使用抽象属性显式地将父bean定义标记为抽象。如果父定义没有指定类，则需要显式地将父bean定义标记为抽象，如下面的示例所示
```xml
<bean id="inheritedTestBeanWithoutClass" abstract="true">
    <property name="name" value="parent"/>
    <property name="age" value="1"/>
</bean>

<bean id="inheritsWithClass" class="org.springframework.beans.DerivedTestBean"
        parent="inheritedTestBeanWithoutClass" init-method="initialize">
    <property name="name" value="override"/>
    <!-- age will inherit the value of 1 from the parent bean definition-->
</bean>

```
父bean不能自己实例化，因为它不完整，而且它还显式地标记为抽象。当定义是抽象的时候，它只能作为纯模板bean定义使用，作为子定义的父定义。
试图单独使用这样一个抽象的父bean，通过引用它作为另一个bean的ref属性，或者使用父bean ID执行显式的getBean()调用，都会返回错误。
类似地，容器内部的预实例化esingletons()方法忽略被定义为抽象的bean定义。

>默认情况下，ApplicationContext预实例化所有单例。因此,它是重要的(至少对单例bean),如果你有一个(父)bean定义你只打算使用作为模板,这个定义指定了一个类,
>您必须确保设置抽象属性为true,否则应用程序上下文会(试图)pre-instantiate抽象的bean。
