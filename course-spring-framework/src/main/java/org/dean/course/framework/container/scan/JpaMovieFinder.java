package org.dean.course.framework.container.scan;

import org.springframework.stereotype.Repository;

@Repository
public class JpaMovieFinder implements MovieFinder {
    public void printMovie() {
        System.out.println(this.getClass().getName());
    }
}
