package org.dean.course.framework.container.javaconfig;


import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class JavaConfigDemo {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        System.out.println("TestIocContainerInit contextId  = " + context.getId());
        MyService myService = context.getBean("myService", MyService.class);
        myService.printLog();
    }

}
