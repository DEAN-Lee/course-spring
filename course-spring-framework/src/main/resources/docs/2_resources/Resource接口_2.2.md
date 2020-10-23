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

