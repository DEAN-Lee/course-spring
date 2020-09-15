package org.dean.course.framework.container.annotation;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestAnnotationInit {
    public static void main(String[] args) {
        // create and configure beans
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("conf/annotationServices.xml");
        System.out.println("TestIocContainerInit contextId  = " + context.getId());
        MovieRecommender movieRecommender = context.getBean("movieRecommender", MovieRecommender.class);

        movieRecommender.printDemo();
    }
}
