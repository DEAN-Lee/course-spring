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

