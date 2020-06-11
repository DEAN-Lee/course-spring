package org.dean.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URL;
import java.util.Arrays;

/**
 * spring-boot入口
 */
@SpringBootApplication
public class CourseSpringBootApplication {

    public static void main(String[] args) {

        System.out.println("bootstrapClassLoader" + System.getProperty("sun.boot.class.path"));
        System.out.println("=================");
        System.out.println("ExtClassLoader" + System.getProperty("java.ext.dirs"));
        System.out.println("=================");
        System.out.println("AppClassLoader" + System.getProperty("java.class.path"));

        URL[] urls = sun.misc.Launcher.getBootstrapClassPath().getURLs();

        for (URL url : urls) {
            System.out.println(url.toExternalForm());
        }

        SpringApplication.run(CourseSpringBootApplication.class, args);
    }

}
