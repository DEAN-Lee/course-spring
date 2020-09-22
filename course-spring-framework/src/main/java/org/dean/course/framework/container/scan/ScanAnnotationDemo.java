package org.dean.course.framework.container.scan;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ScanAnnotationDemo {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        System.out.println("TestIocContainerInit contextId  = " + context.getId());
        SimpleMovieLister simpleMovieLister = context.getBean("simpleMovieLister", SimpleMovieLister.class);
        JpaMovieFinder movieFinder = context.getBean("jpaMovieFinder", JpaMovieFinder.class);
        simpleMovieLister.getMovie();
        movieFinder.printMovie();

    }
}
