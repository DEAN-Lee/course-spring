package org.dean.course.framework.container;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestIocContainerInit {
    public static void main(String[] args) {
        // create and configure beans
        ApplicationContext context = new ClassPathXmlApplicationContext("conf/services.xml", "conf/daos.xml");

        // retrieve configured instance
        UserServiceSetDI service = context.getBean("userService", UserServiceSetDI.class);
        UserServiceSetDI userService2 = context.getBean("userService2", UserServiceSetDI.class);
        UserServiceConstructorDIDemo constructorDIDemo = context.getBean("userServiceConstructorDIDemo", UserServiceConstructorDIDemo.class);

        // use configured instance
        service.getAccountDao().printUserList();
        userService2.getAccountDao().printUserList();
        constructorDIDemo.getAccountDao().printUserList();

    }
}
