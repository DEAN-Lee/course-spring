package org.dean.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * springApplication.run-->ConfigurableApplicationContext-->ApplicationContext-->AbstractApplicationContext#refresh()
 *
 */
@SpringBootApplication
public class CourseSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseSpringBootApplication.class, args);
    }

}
