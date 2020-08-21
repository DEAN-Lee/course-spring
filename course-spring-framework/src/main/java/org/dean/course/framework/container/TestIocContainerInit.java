package org.dean.course.framework.container;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestIocContainerInit {
    public static void main(String[] args) {
        // create and configure beans
        ApplicationContext context = new ClassPathXmlApplicationContext("conf/services.xml", "conf/daos.xml");

        // retrieve configured instance
        UserService service = context.getBean("userService", UserService.class);

        // use configured instance
        service.getAccountDao().printUserList();

    }
}
