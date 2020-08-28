package org.dean.course.framework.container;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;

public class TestIocContainerInit {
    public static void main(String[] args) {
        // create and configure beans
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("conf/services.xml", "conf/daos.xml");
        context.registerShutdownHook();
        // retrieve configured instance
        UserServiceSetDI service = context.getBean("userService", UserServiceSetDI.class);
        UserServiceSetDI userService2 = context.getBean("userService2", UserServiceSetDI.class);
        UserServiceConstructorDIDemo constructorDIDemo = context.getBean("userServiceConstructorDIDemo", UserServiceConstructorDIDemo.class);
        AccountInitializingBeanDao initializingBeanDao = context.getBean("accountInitializingBeanDao", AccountInitializingBeanDao.class);

        // use configured instance
        service.getAccountDao().printUserList();
        userService2.getAccountDao().printUserList();
        constructorDIDemo.getAccountDao().printUserList();

        //lazy
        UserInfoLazyInitDao userInfoLazyInitDao = context.getBean("userInfoLazyInitDao", UserInfoLazyInitDao.class);

        initializingBeanDao.printUserList();
        System.out.println(" context.refresh();*******************");
        context.refresh();

    }
}
