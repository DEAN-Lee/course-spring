# 内置的资源实现
Spring包括以下资源实现:
* UrlResource

* ClassPathResource

* FileSystemResource

* ServletContextResource

* InputStreamResource

* ByteArrayResource

## UrlResource
UrlResource包装了java.net.URL，可以用来访问通常可以通过URL访问的任何对象，比如文件、HTTP目标、FTP目标和其他。所有URL都有标准化的字符串表示，
这样就可以使用适当的标准化前缀来表示不同类型的URL。这包括file:用于访问文件系统路径，http:用于通过http协议访问资源，ftp:用于通过ftp访问资源，等等。

UrlResource是由Java代码显式使用UrlResource构造函数创建的，但通常是在调用API方法时隐式创建的，该API方法接受表示路径的字符串参数。
对于后一种情况，JavaBeans PropertyEditor最终决定要创建哪种类型的资源。如果路径字符串包含众所周知的前缀(例如classpath:)，它将为该前缀创建一个适当的专用资源。
但是，如果不能识别前缀，则假定该字符串是标准的URL字符串，并创建UrlResource。

## ClassPathResource
这个类表示应该从类路径获取的资源。它使用线程上下文类装入器、给定的类装入器或给定的类装入资源。

他的资源实现支持java.io格式的解决方案。如果类路径资源驻留在文件系统中，但不适用于驻留在jar中的类路径资源，并且没有(由servlet引擎或任何环境)扩展到文件系统。
为了解决这个问题，各种资源实现总是支持作为java.net.URL的解析。

ClassPathResource是由Java代码显式使用ClassPathResource构造函数创建的，但通常是在调用API方法时隐式创建的，该API方法接受表示路径的字符串参数。
对于后一种情况，JavaBeans PropertyEditor识别字符串路径上的特殊前缀classpath:，并在这种情况下创建ClassPathResource。

## FileSystemResource
实现java.io.file和java.nio.file.path路径处理。它支持文件和URL格式的解析。

## ServletContextResource
这是ServletContext资源的一个资源实现，它解释了相关web应用程序根目录中的相对路径。

它始终支持流访问和URL访问，但允许java.io.file仅当扩展web应用程序归档并且资源在文件系统上物理时才进行文件访问。不管它是在文件系统上进行扩展，
还是直接从JAR或其他地方(如数据库)访问它，实际上都依赖于Servlet容器。

## InputStreamResource
InputStreamResource是给定InputStream的资源实现。只有在没有适用的特定资源实现时才应该使用它。特别是，尽可能使用ByteArrayResource或任何基于文件的资源实现。

与其他资源实现相比，这是一个针对已打开资源的描述符。因此，它从isOpen()返回true。如果需要在某个地方保存资源描述符，或者需要多次读取流，请不要使用它。

## ByteArrayResource
这是给定字节数组的资源实现。它为给定的字节数组创建ByteArrayInputStream。

它用于从任何给定的字节数组加载内容，而不必求助于一次性使用的InputStreamResource。