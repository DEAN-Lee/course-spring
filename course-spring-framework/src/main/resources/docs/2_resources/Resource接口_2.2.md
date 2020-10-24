# Resource接口
Spring的资源接口是一个更强大的接口，用于对低级资源进行抽象访问。下面的清单显示了资源接口定义
```java
public interface Resource extends InputStreamSource {

    boolean exists();

    boolean isOpen();

    URL getURL() throws IOException;

    File getFile() throws IOException;

    Resource createRelative(String relativePath) throws IOException;

    String getFilename();

    String getDescription();
}
``` 
正如资源接口的定义所示，它扩展了InputStreamSource接口。下面的清单显示了InputStreamSource接口的定义
```java
public interface InputStreamSource {

    InputStream getInputStream() throws IOException;
}
```
资源接口的一些最重要的方法是:
* getInputStream():定位并打开资源，返回从资源读取的InputStream。预期每次调用都会返回一个新的InputStream。调用者负责关闭流。
  
*  exists():返回一个布尔值，指示该资源是否以物理形式实际存在。
  
* isOpen():返回一个布尔值，指示该资源是否表示一个打开流的句柄。如果为真，则不能多次读取InputStream，必须只读取一次，然后关闭以避免资源泄漏。除InputStreamResource外，对所有通常的资源实现返回false。
  
* getDescription():返回该资源的描述，用于处理该资源时的错误输出。这通常是资源的完全限定文件名或实际URL。

其他方法让您获得表示资源的实际URL或File对象(如果底层实现兼容并支持该功能)。

Spring本身广泛地使用资源抽象，当需要资源时，它作为许多方法签名中的参数类型。其他方法在某些Spring api(如各种ApplicationContext实现构造函数)在朴素的弦或简单的形式被用来创建一个资源适合上下文实现或通过特殊前缀弦上的路径,让调用者指定一个特定的资源实现必须创建和使用。

虽然在Spring和Spring中经常使用资源接口，但是在您自己的代码中作为一个通用实用程序类来使用它来访问资源实际上是非常有用的，
即使您的代码不知道或不关心Spring的任何其他部分。虽然这将您的代码与Spring结合在一起，但实际上它只将它与这一小组实用程序类结合在一起，
这些实用程序类可以更有效地替代URL，并且可以认为与为此目的使用的任何其他库是等效的。

> 资源抽象不会取代功能。它在可能的地方把它包装起来。例如，UrlResource包装了一个URL并使用包装后的URL来完成它的工作。


