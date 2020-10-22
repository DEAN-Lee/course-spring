# 注册一个LoadTimeWeaver
   
当类装入Java虚拟机(JVM)时，Spring使用LoadTimeWeaver动态地转换类。

要启用加载时编织，可以将@ enableloadtime织造添加到一个@Configuration类中，如下面的示例所示:
```java
@Configuration
@EnableLoadTimeWeaving
public class AppConfig {
}
```
另外，对于XML配置，您可以使用context:load-time-weaver元素
```xml
<beans>
    <context:load-time-weaver/>
</beans>
```
一旦为ApplicationContext配置好，该ApplicationContext中的任何bean都可以实现LoadTimeWeaverAware，从而接收到加载时weaver实例的引用。
这与Spring s JPA支持结合起来特别有用，其中加载时编织对于JPA类转换可能是必要的。更多细节请参考LocalContainerEntityManagerFactoryBean javadoc。
有关AspectJ加载时编织的更多信息，请参见Spring框架中使用AspectJ进行的加载时编织。