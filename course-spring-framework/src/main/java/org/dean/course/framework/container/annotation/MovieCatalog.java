package org.dean.course.framework.container.annotation;

public class MovieCatalog {
    private String genre;
    private String format;

    public void print() {
        System.out.println("init:genre" + genre + " format:" + format);
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
