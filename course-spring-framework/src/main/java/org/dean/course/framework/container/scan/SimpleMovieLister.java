package org.dean.course.framework.container.scan;

import org.springframework.stereotype.Service;

@Service
public class SimpleMovieLister {
    private MovieFinder movieFinder;

    public SimpleMovieLister(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }

    public void getMovie() {
        System.out.println("step 1:" + this.getClass().getName());
        movieFinder.printMovie();
    }
}
