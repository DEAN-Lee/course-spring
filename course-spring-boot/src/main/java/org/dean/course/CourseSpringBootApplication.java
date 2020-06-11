package org.dean.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URL;
import java.util.Arrays;

/**
 * spring-boot入口
 *
 */
@SpringBootApplication
public class CourseSpringBootApplication {

    public static void main(String[] args) {
        URL[] urls=sun.misc.Launcher.getBootstrapClassPath().getURLs();

        for (URL url : urls) {
            System.out.println(url.toExternalForm());
        }
        SpringApplication.run(CourseSpringBootApplication.class, args);
    }

}
