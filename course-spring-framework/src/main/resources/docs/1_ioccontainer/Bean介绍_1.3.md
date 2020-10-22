# Bean介绍
IoC容器从配置元数据中读取bean信息,然后负责管理bean;
容器中Beans对象被定义成BeanDefinition对象，BeanDefinition对象中包含以下信息；
* 一个包限定的类名:通常是定义的bean的实际实现类。
* Bean行为配置元素，它声明Bean在容器中应该如何行为(作用域、生命周期回调，等等)。
* 对bean执行其工作所需的其他bean的引用。这些引用也称为协作者或依赖项。
* 在新创建的对象中要设置的其他配置设置，例如池的大小限制或在管理连接池的bean中要使用的连接数量。
## bean命名
每个bean只有少有一个标识符，这个标识在容器中必须是独一无二，不可以重复纸箱两个bean。
bean名称习惯是在命名bean时对实例字段名使用标准Java命名约定。也就是说，bean名称以小写字母开头，并使用驼峰命名规则。
例如accountManager、accountService、userDao、loginController等等。
## bean别名
在bean定义本身中，您可以为bean提供多个名称，方法是使用由id属性指定的最多一个名称和name属性中任意数量的其他名称的组合。
这些名称可以是相同bean的等价别名，并且在某些情况下非常有用，例如让应用程序中的每个组件通过使用特定于该组件本身的bean名称来引用公共依赖项。
在xml配置元数据中，可以这样配置别名：
```
<alias name="fromName" alias="toName"/>
```
在这种情况下，(在同一个容器中)名为fromName的bean在使用了这个别名定义之后，也可以被称为toName。
## bean实例化
bean定义本质上是创建一个或多个对象。当被请求时，容器查看已命名bean，并使用该bean定义封装的配置元数据来创建(或获取)实际对象。
bean实例化有三种方式
### 构造函数实例化
大多数Spring用户更喜欢实际的javabean，它只有一个默认的(无参数的)构造函数和适当的setter和getter方法，这些方法是根据容器中的属性建模的。
```
<bean id="exampleBean" class="examples.ExampleBean"/>
<bean name="anotherExample" class="examples.ExampleBeanTwo"/>
```
### 静态工厂方法实例化
在定义使用静态工厂方法创建的bean时，使用class属性指定包含静态工厂方法的类，并使用名为factory-method的属性指定工厂方法本身的名称。
您应该能够调用这个方法(带有可选参数，如后面所述)并返回一个活动对象，然后将其视为通过构造函数创建的。
这种bean定义的一个用途是在遗留代码中调用静态工厂。
```
xml 配置
<bean id="clientService"
    class="examples.ClientService"
    factory-method="createInstance"/>
java code
public class ClientService {
    private static ClientService clientService = new ClientService();
    private ClientService() {}

    public static ClientService createInstance() {
        return clientService;
    }
}

```
### 实例工厂方法进行实例化
实例工厂方法的实例化从容器中调用现有bean的非静态方法来创建新bean.
要使用这种机制，请将class属性保留为空，并在factory-bean属性中指定当前(或父或祖先)容器中bean的名称，
该容器包含要调用来创建对象的实例方法。使用factory-method属性设置工厂方法本身的名称。
```
xml 配置
<!-- the factory bean, which contains a method called createInstance() -->
<bean id="serviceLocator" class="examples.DefaultServiceLocator">
    <!-- inject any dependencies required by this locator bean -->
</bean>

<!-- the bean to be created via the factory bean -->
<bean id="clientService"
    factory-bean="serviceLocator"
    factory-method="createClientServiceInstance"/>

java Code
public class DefaultServiceLocator {

    private static ClientService clientService = new ClientServiceImpl();

    public ClientService createClientServiceInstance() {
        return clientService;
    }
}
```
一个工厂类还可以包含多个工厂方法，如下面的示例所示
```
xml 配置
<bean id="serviceLocator" class="examples.DefaultServiceLocator">
    <!-- inject any dependencies required by this locator bean -->
</bean>

<bean id="clientService"
    factory-bean="serviceLocator"
    factory-method="createClientServiceInstance"/>

<bean id="accountService"
    factory-bean="serviceLocator"
    factory-method="createAccountServiceInstance"/>

java Code 
public class DefaultServiceLocator {

    private static ClientService clientService = new ClientServiceImpl();

    private static AccountService accountService = new AccountServiceImpl();

    public ClientService createClientServiceInstance() {
        return clientService;
    }

    public AccountService createAccountServiceInstance() {
        return accountService;
    }
}

```
## 确定Bean的运行时类型
确定特定bean的运行时类型非常有意义。 在Bean元数据定义中的指定类只是初始的类引用，
与声明的工厂方法潜在地结合使用，或者如果本身是FactoryBean类，可能产生不同类型的Bean运行时，
或者在实例化层的工厂方法上完全不进行设置 （另外，通过特别的factory-bean名称来进行设置）。

找出特定bean的实际运行时类型的推荐方法是通过 BeanFactory.getType方法来调用指定的bean name。
这考虑了以上所有情况，而且 它返回的对象的类型，与BeanFactory.getBean方法返回的是同一个bean name。
