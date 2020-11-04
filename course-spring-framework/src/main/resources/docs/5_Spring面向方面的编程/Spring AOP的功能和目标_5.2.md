# Spring AOP的功能和目标
Spring AOP是用纯Java实现的。不需要特殊的编译过程。Spring AOP不需要控制类加载器的层次结构，因此适合在servlet容器或应用服务器中使用。

Spring AOP目前只支持方法执行连接点(通知方法在Spring bean上的执行)。虽然可以在不破坏Spring AOP核心api的情况下添加对字段拦截的支持，
但没有实现字段拦截。如果需要通知字段访问和更新连接点，请考虑使用AspectJ之类的语言。

Spring AOP实现AOP的方法不同于大多数其他AOP框架。其目的不是提供最完整的AOP实现(尽管Spring AOP非常有能力)
。相反，其目的是提供AOP实现和Spring IoC之间的紧密集成，以帮助解决企业应用程序中的常见问题。

因此，例如，Spring框架的AOP功能通常与Spring IoC容器一起使用。方面是通过使用普通的bean定义语法来配置的(尽管这允许强大的“自动代理”功能)。
这是与其他AOP实现的一个关键区别。使用Spring AOP不能轻松或有效地完成一些事情，比如通知非常细粒度的对象(通常是域对象)。在这种情况下，AspectJ是最好的选择。
然而，我们的经验是，Spring AOP为企业级Java应用程序中的大多数问题提供了一个优秀的解决方案，而这些问题都是符合AOP的。

Spring AOP从来没有努力与AspectJ竞争来提供一个全面的AOP解决方案。我们相信基于代理的框架(如Spring AOP)和成熟的框架(如AspectJ)都是有价值的，
它们是互补的，而不是竞争的。Spring与AspectJ无缝地集成了Spring AOP和IoC，以便在一致的基于Spring的应用程序体系结构中启用AOP的所有使用。
这种集成不会影响Spring AOP API或AOP Alliance API。Spring AOP保持向后兼容。有关Spring AOP api的讨论，请参阅下一章。

> Spring框架的核心原则之一是非侵入性。这种思想认为，您不应该被迫将特定于框架的类和接口引入到您的业务或领域模型中。然而，在某些地方，
> Spring框架确实为您提供了将特定于Spring框架的依赖项引入代码库的选项。给您提供这些选项的理由是，在某些场景中，用这种方式可能更容易阅读或编写特定的功能片段。
> 然而，Spring框架(几乎)总是为您提供选择:您可以自由地做出明智的决定，以确定哪个选项最适合您的特定用例或场景。
>
> 与本章相关的一个选择是选择哪个AOP框架(以及哪种AOP风格)。您可以选择AspectJ、Spring AOP或两者都有。您还可以选择@AspectJ注释风格的方法或Spring XML配置风格的方法。
> 本章选择首先介绍@AspectJ风格的方法并不意味着Spring团队更喜欢@AspectJ注释风格的方法，而不是Spring XML配置风格的方法。
>
> 有关每种风格的“为什么和在哪里”的更完整的讨论，请参见选择使用哪种AOP声明风格。