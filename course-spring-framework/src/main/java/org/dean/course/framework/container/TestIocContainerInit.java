package org.dean.course.framework.container;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestIocContainerInit {
    private String temp;
    public static void main(String[] args) {
        // create and configure beans
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("conf/services.xml");
        System.out.println("TestIocContainerInit contextId  = " + context.getId());
        context.registerShutdownHook();
        // retrieve configured instance
        UserServiceSetDI service = context.getBean("userService", UserServiceSetDI.class);
        UserServiceSetDI userService2 = context.getBean("userService2", UserServiceSetDI.class);
        UserServiceAnnotationDemo userServiceAnnotationDemo = context.getBean("userServiceAnnotationDemo", UserServiceAnnotationDemo.class);
        UserServiceConstructorDIDemo constructorDIDemo = context.getBean("userServiceConstructorDIDemo", UserServiceConstructorDIDemo.class);
        AccountInitializingBeanDao initializingBeanDao = context.getBean("initializingBeanDao", AccountInitializingBeanDao.class);

        // use configured instance
        service.getAccountDao().printAccountList();
        userService2.getAccountDao().printAccountList();
        constructorDIDemo.getAccountDao().printAccountList();

        //lazy
        UserInfoLazyInitDao userInfoLazyInitDao = context.getBean("userInfoLazyInitDao", UserInfoLazyInitDao.class);

        initializingBeanDao.printUserList();
//        System.out.println(" context.refresh()*******************");
//        context.refresh();
        System.out.println(initializingBeanDao.getContext().getId());


        userService2.printAccountList();
        userServiceAnnotationDemo.printAccountList();

    }
}
