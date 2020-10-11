package org.dean.course.framework.container.scan;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.Environment;

public class ScanAnnotationDemo {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        System.out.println("TestIocContainerInit contextId  = " + context.getId());
        SimpleMovieLister simpleMovieLister = context.getBean("simpleMovieLister", SimpleMovieLister.class);
        JpaMovieFinder movieFinder = context.getBean("jpaMovieFinder", JpaMovieFinder.class);
        simpleMovieLister.getMovie();
        movieFinder.printMovie();

//        ApplicationContext ctx = new GenericApplicationContext();
        Environment env = context.getEnvironment();
        boolean containsMyProperty = env.containsProperty("my-property");
        System.out.println("Does my environment contain the 'my-property' property? " + containsMyProperty);
    }
}
