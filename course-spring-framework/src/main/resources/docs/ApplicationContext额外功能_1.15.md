# ApplicationContext额外功能
正如在引言中所讨论的，org.springframework.beans.factory包提供了管理和操作bean的基本功能，包括以编程的方式。
org.springframework.Context包添加了ApplicationContext接口，该接口扩展了BeanFactory接口，此外还扩展了其他接口，以更面向应用程序框架的风格提供额外的功能。
许多人以一种完全声明式的方式使用ApplicationContext，甚至不是通过编程来创建它，而是依赖于支持类(如ContextLoader)来自动实例化一个ApplicationContext，
作为Java EE web应用程序的正常启动过程的一部分。

为了以更面向框架的风格增强BeanFactory功能，上下文包还提供了以下功能
* 通过MessageSource接口访问i18n风格的消息。
* 通过ResourceLoader接口访问资源，例如url和文件。
* 事件发布，即通过使用ApplicationEventPublisher接口发布到实现ApplicationListener接口的bean。
* 通过HierarchicalBeanFactory接口加载多个(分层的)上下文，让每个上下文都关注于一个特定的层，比如应用程序的web层。

## MessageSource信息国际化
ApplicationContext接口扩展了一个名为MessageSource的接口，因此提供了国际化(i18n)功能。Spring还提供了HierarchicalMessageSource接口，该接口可以分层解析消息。
这些接口一起提供了Spring实现消息解析的基础。在这些接口上定义的方法包括
* String getMessage(String code, Object[] args, String default, Locale loc)：用于从MessageSource检索消息的基本方法。如果未找到指定语言环境的消息，
则使用默认消息。通过使用标准库提供的MessageFormat功能，传入的任何参数都将成为替换值。
* String getMessage(String code, Object[] args, Locale loc)：基本上与前面的方法相同，但有一个区别:不能指定缺省消息。如果找不到消息，则抛出NoSuchMessageException。
* String getMessage(MessageSourceResolvable resolvable, Locale locale)：上述方法中使用的所有属性也包装在名为MessageSourceResolvable类中，该类可用于此方法。

加载ApplicationContext时，它会自动搜索上下文中定义的MessageSource bean。bean的名称必须是messageSource。如果找到这样一个bean，对前面方法的所有调用都将委托给消息源。
如果没有找到消息源，ApplicationContext将尝试查找包含同名bean的父消息源。如果是，则使用该bean作为消息源。如果ApplicationContext找不到任何消息源，
则实例化一个空的DelegatingMessageSource，以便能够接受对上面定义的方法的调用。

Spring提供了两个消息源实现，ResourceBundleMessageSource和StaticMessageSource。两者都实现了HierarchicalMessageSource以执行嵌套消息传递。
很少使用StaticMessageSource，但它提供了将消息添加到源的编程方法。下面的示例展示了ResourceBundleMessageSource。
```xml
<beans>
    <bean id="messageSource"
            class="org.springframework.context.support.ResourceBundleMessageSource">
        <property name="basenames">
            <list>
                <value>format</value>
                <value>exceptions</value>
                <value>windows</value>
            </list>
        </property>
    </bean>
</beans>
```
该示例假设在类路径中定义了三个资源包，分别称为format、exception和windows。解析消息的任何请求都是用通过ResourceBundle对象解析消息的jdk标准方法处理的。
对于本示例，假设上述两个资源包文件的内容如下
```text
 # in format.properties
    message=Alligators rock!

 # in exceptions.properties
    argument.required=The {0} argument is required.

```
下一个示例显示了运行MessageSource功能的程序。记住，所有ApplicationContext实现也是MessageSource实现，因此可以cast到MessageSource接口。
```java
public static void main(String[] args) {
    MessageSource resources = new ClassPathXmlApplicationContext("beans.xml");
    String message = resources.getMessage("message", null, "Default", Locale.ENGLISH);
    System.out.println(message);
}
```
上述程序的输出结果如下所示
```text
Alligators rock!
```

总之，MessageSource是在一个名为bean的文件中定义的xml文件，它存在于类路径的根。messageSource bean定义通过其basenames属性引用大量资源包。
在列表中传递给basenames属性的三个文件作为类路径的根文件存在，它们被称为format.properties,exceptions.properties,和windows.properties,分别。

下一个示例显示了传递给消息查找的参数。这些参数被转换为字符串对象，并插入到查找消息中的占位符中。
```xml
<beans>

    <!-- this MessageSource is being used in a web application -->
    <bean id="messageSource" class="org.springframework.context.support.ResourceBundleMessageSource">
        <property name="basename" value="exceptions"/>
    </bean>

    <!-- lets inject the above MessageSource into this POJO -->
    <bean id="example" class="com.something.Example">
        <property name="messages" ref="messageSource"/>
    </bean>

</beans>
```
```java
public class Example {

    private MessageSource messages;

    public void setMessages(MessageSource messages) {
        this.messages = messages;
    }

    public void execute() {
        String message = this.messages.getMessage("argument.required",
            new Object [] {"userDao"}, "Required", Locale.ENGLISH);
        System.out.println(message);
    }
}
```
调用execute()方法的结果输出如下所示
```text
The userDao argument is required.
```

关于国际化(i18n)， Spring的各种MessageSource实现遵循与标准JDK ResourceBundle相同的语言环境解析和回退规则。
简而言之，继续前面定义的示例messageSource，如果您希望根据英国(en-GB)地区解析消息，您将创建名为format_en_GB.properties, exceptions_en_GB.properties, 
 和windows_en_GB.properties。
 
 通常，语言环境解析由应用程序的周围环境管理。在下面的示例中，手动指定解析(英国)消息所依据的语言环境
 ```text
# in exceptions_en_GB.properties
argument.required=Ebagum lad, the ''{0}'' argument is required, I say, required.
```
```java
public static void main(final String[] args) {
    MessageSource resources = new ClassPathXmlApplicationContext("beans.xml");
    String message = resources.getMessage("argument.required",
        new Object [] {"userDao"}, "Required", Locale.UK);
    System.out.println(message);
}
```
运行上述程序的结果输出如下
```text
Ebagum lad, the 'userDao' argument is required, I say, required.

```

您还可以使用MessageSourceAware接口来获取对已定义的任何消息源的引用。当创建和配置bean时，
在实现MessageSourceAware接口的ApplicationContext中定义的任何bean都将被注入应用上下文的MessageSource。

>作为ResourceBundleMessageSource的替代方案，Spring提供了一个ReloadableResourceBundleMessageSource类。这个变体支持相同的bundle文件格式，
>但是比基于JDK的标准ResourceBundleMessageSource实现更加灵活。特别是，它允许从任何Spring资源位置读取文件(不仅仅是从类路径)，
>并支持bundle属性文件的热重新加载(同时有效地缓存它们)。有关详细信息，请参见ReloadableResourceBundleMessageSource javadoc。

## 标准和自定义事件
ApplicationContext中的事件处理是通过ApplicationEvent类和ApplicationListener接口提供的。如果实现ApplicationListener接口的bean被部署到上下文中，
那么每当一个ApplicationEvent被发布到ApplicationContext时，该bean就会得到通知。本质上，这就是标准的观察者设计模式。

> 从Spring 4.2开始，事件基础设施已经得到了显著改进，并提供了一个基于注释的模型，以及发布任意事件的能力(也就是说，不需要从ApplicationEvent扩展的对象)。
>当发布这样的对象时，我们为您将其包装在事件中。

下表描述了Spring提供的标准事件

|事件| 说明   |
|---|---|
|ContextRefreshedEvent|当初始化或刷新ApplicationContext时发布(例如，通过使用ConfigurableApplicationContext接口上的refresh()方法)。<br/>在这里，initialized意味着加载所有bean，检测和激活后处理器bean，预实例化单例，并且ApplicationContext对象已经准备好可以使用了。<br/>只要上下文没有被关闭，刷新就可以被触发多次，前提是所选的ApplicationContext实际上支持这种热刷新。例如，XmlWebApplicationContext支持热刷新，而GenericApplicationContext不支持。
|ContextStartedEvent|通过使用ConfigurableApplicationContext接口上的start()方法启动ApplicationContext时发布。<br/>在这里，started意味着所有生命周期bean都收到一个显式的启动信号。<br/>通常，这个信号用于在显式停止后重新启动bean，但是它也可以用于启动尚未配置为自动启动的组件(例如，尚未在初始化时启动的组件)。|
|ContextStoppedEvent|在使用ConfigurableApplicationContext接口上的stop()方法停止ApplicationContext时发布。<br/>在这里，stopped意味着所有生命周期bean都接收到一个显式的停止信号。一个停止的上下文可以通过一个start()调用重新启动。|
|ContextClosedEvent|通过使用ConfigurableApplicationContext接口上的close()方法或通过JVM关机钩子关闭ApplicationContext时发布。<br/>在这里，“关闭”意味着所有的单例bean将被销毁。一旦上下文关闭，它就会到达生命的终点，无法刷新或重新启动。|
|RequestHandledEvent|一个特定于web的事件，告诉所有bean一个HTTP请求已经得到服务。此事件在请求完成后发布。此事件仅适用于使用Spring s DispatcherServlet的web应用程序。|
|ServletRequestHandledEvent|RequestHandledEvent的一个子类，用于添加特定于servlet的上下文信息。|

您还可以创建和发布自己的自定义事件。下面的示例展示了一个扩展Spring的ApplicationEvent基类的简单类
```java
public class BlockedListEvent extends ApplicationEvent {

    private final String address;
    private final String content;

    public BlockedListEvent(Object source, String address, String content) {
        super(source);
        this.address = address;
        this.content = content;
    }

    // accessor and other methods...
}
```
要发布自定义ApplicationEvent，请调用ApplicationEventPublisher上的publishEvent()方法。通常，这是通过创建实现ApplicationEventPublisherAware的类并将其注册为Spring bean来完成的。下面的示例展示了这样一个类.
```java
public class EmailService implements ApplicationEventPublisherAware {

    private List<String> blockedList;
    private ApplicationEventPublisher publisher;

    public void setBlockedList(List<String> blockedList) {
        this.blockedList = blockedList;
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void sendEmail(String address, String content) {
        if (blockedList.contains(address)) {
            publisher.publishEvent(new BlockedListEvent(this, address, content));
            return;
        }
        // send email...
    }
}
```
在配置时，Spring容器检测到EmailService实现了ApplicationEventPublisherAware，并自动调用setApplicationEventPublisher()。实际上，传入的参数就是Spring容器本身。您正在通过应用程序的ApplicationEventPublisher接口与应用程序上下文交互。

要接收自定义ApplicationEvent，您可以创建一个实现ApplicationListener的类，并将其注册为一个Spring bean。下面的示例展示了这样一个类.
```java
public class BlockedListNotifier implements ApplicationListener<BlockedListEvent> {

    private String notificationAddress;

    public void setNotificationAddress(String notificationAddress) {
        this.notificationAddress = notificationAddress;
    }

    public void onApplicationEvent(BlockedListEvent event) {
        // notify appropriate parties via notificationAddress...
    }
}
```
注意，ApplicationListener通常是用自定义事件的类型参数化的(在前面的示例中是BlockedListEvent)。这意味着onApplicationEvent()方法可以保持类型安全，避免向下强制转换。
您可以注册任意数量的事件监听器，但是请注意，默认情况下，事件监听器同步接收事件。这意味着publishEvent()方法会阻塞，直到所有监听器都完成了事件的处理。 
这种同步和单线程方法的一个优点是，当侦听器接收到事件时，如果事务上下文可用，它将在发布程序的事务上下文内操作。
如果需要另一种事件发布策略，请参阅javadoc for Spring s ApplicationEventMulticaster接口和SimpleApplicationEventMulticaster实现以获得配置选项。

下面的示例显示了用于注册和配置上面每个类的bean定义
```xml
<bean id="emailService" class="example.EmailService">
    <property name="blockedList">
        <list>
            <value>known.spammer@example.org</value>
            <value>known.hacker@example.org</value>
            <value>john.doe@example.org</value>
        </list>
    </property>
</bean>

<bean id="blockedListNotifier" class="example.BlockedListNotifier">
    <property name="notificationAddress" value="blockedlist@example.org"/>
</bean>
```

将它们放在一起，当调用emailService bean的sendEmail()方法时，如果有任何需要阻止的电子邮件消息，则发布类型为BlockedListEvent的自定义事件。
blockedListNotifier bean注册为一个ApplicationListener并接收BlockedListEvent，此时它可以通知适当的方。

>Spring事件机制是为相同应用程序上下文中的Spring bean之间的简单通信而设计的。然而，对于更复杂的企业集成需求，单独维护的Spring integration项目提供了构建轻量级、面向模式、事件驱动架构的完整支持，这些架构构建在众所周知的Spring编程模型之上。

### Annotation-based Event Listeners 基于注解的事件监听
从Spring 4.2开始，您可以使用@EventListener注释在托管bean的任何公共方法上注册事件监听器。可以像下面这样重写BlockedListNotifier
```java
public class BlockedListNotifier {

    private String notificationAddress;

    public void setNotificationAddress(String notificationAddress) {
        this.notificationAddress = notificationAddress;
    }

    @EventListener
    public void processBlockedListEvent(BlockedListEvent event) {
        // notify appropriate parties via notificationAddress...
    }
}
```
方法签名再次声明它侦听的事件类型，但是这次使用了灵活的名称，并且没有实现特定的侦听器接口。只要实际事件类型在其实现层次结构中解析泛型参数，就可以通过泛型缩小事件类型.

如果您的方法应该侦听多个事件，或者您希望在不使用任何参数的情况下定义它，那么还可以在注释本身上指定事件类型。下面的示例展示了如何做到这一点
```java
@EventListener({ContextStartedEvent.class, ContextRefreshedEvent.class})
public void handleContextStart() {
    // ...
}
```

还可以通过使用定义SpEL表达式的注释的条件属性来添加额外的运行时过滤，该注释应该与针对特定事件实际调用方法相匹配。

下面的例子展示了我们的通知程序如何被重写，只有在事件的内容属性等于my-event时才被调用:
```java
@EventListener(condition = "#blEvent.content == 'my-event'")
public void processBlockedListEvent(BlockedListEvent blockedListEvent) {
    // notify appropriate parties via notificationAddress...
}
```
每个SpEL表达式根据一个专用上下文计算。下表列出了上下文可用的项目，以便您可以使用它们进行条件事件处理:

|名称|位置|说明|示例|
|---|---|---|---|
|Event|root object|  实际的ApplicationEvent。|#root.event 或 event|
|数组参数|root object|  用于调用方法的参数(作为对象数组)。|#root.args 或 args; args[0]访问第一个参数等。|
|参数名称|评估环境|  任何方法参数的名称。如果由于某种原因，名称不可用(例如，因为在已编译的字节代码中没有调试信息)，也可以使用#a<#arg>语法使用单个参数，其中<#arg>表示参数索引(从0开始)。|#blEvent或#a0(您还可以使用#p0或#p<#arg>参数表示法作为别名)|

请注意,#root.event。event允许您访问基础事件，即使您的方法签名实际上引用了已发布的任意对象。

如果你需要发布一个事件作为处理另一个事件的结果，你可以改变方法签名来返回应该发布的事件，如下面的例子所示:
```java
@EventListener
public ListUpdateEvent handleBlockedListEvent(BlockedListEvent event) {
    // notify appropriate parties via notificationAddress and
    // then publish a ListUpdateEvent...
}
```
> 异步监听器不支持此特性。

这个新方法为上面方法处理的每个BlockedListEvent发布一个新的ListUpdateEvent。如果需要发布多个事件，则可以返回一个事件集合。

### 异步监听
如果需要一个特定的侦听器异步处理事件，可以重用常规的@Async支持。下面的例子展示了如何做到这一点:
```java
@EventListener
@Async
public void processBlockedListEvent(BlockedListEvent event) {
    // BlockedListEvent is processed in a separate thread
}
```

在使用异步事件时要注意以下限制:
* 如果异步事件侦听器抛出异常，则该异常不会传播到调用者。有关更多细节，请参阅AsyncUncaughtExceptionHandler。
* 异步事件侦听器方法不能通过返回值来发布后续事件。如果您需要发布另一个事件作为处理的结果，注入一个ApplicationEventPublisher来手动发布事件。

### 排序监听
如果需要先调用一个监听器，可以在方法声明中添加@Order注释，如下例所示:
```java
@EventListener
@Order(42)
public void processBlockedListEvent(BlockedListEvent event) {
    // notify appropriate parties via notificationAddress...
}
```

### 通用的事件
还可以使用泛型来进一步定义事件的结构。考虑使用EntityCreatedEvent<t>，其中T是所创建的实际实体的类型。</t>例如，您可以创建以下侦听器定义来只为一个人接收EntityCreatedEvent:
```java
@EventListener
public void onPersonCreated(EntityCreatedEvent<Person> event) {
    // ...
}
```
由于类型擦除，只有在触发的事件解析了事件侦听器筛选的泛型参数(即，类似类PersonCreatedEvent扩展了EntityCreatedEvent{…})时，这种方法才有效。</person>

在某些情况下，如果所有事件都遵循相同的结构(前面示例中的事件也应该如此)，那么这可能会变得非常乏味。在这种情况下，您可以实现ResolvableTypeProvider来指导运行时环境所提供的框架。下面的事件展示了如何做到这一点:

```java
public class EntityCreatedEvent<T> extends ApplicationEvent implements ResolvableTypeProvider {

    public EntityCreatedEvent(T entity) {
        super(entity);
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forInstance(getSource()));
    }
}
```

>这不仅适用于ApplicationEvent，也适用于任何作为事件发送的对象。