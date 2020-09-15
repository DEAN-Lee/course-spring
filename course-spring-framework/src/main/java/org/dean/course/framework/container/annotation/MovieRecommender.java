package org.dean.course.framework.container.annotation;

import org.springframework.beans.factory.annotation.Autowired;

public class MovieRecommender {
    @Autowired
    @MovieQualifier(format=Format.VHS, genre="Action")
    private MovieCatalog actionVhsCatalog;

    @Autowired
    @MovieQualifier(format=Format.VHS, genre="Comedy")
    private MovieCatalog comedyVhsCatalog;

    @Autowired
    @MovieQualifier(format=Format.DVD, genre="Action")
    private MovieCatalog actionDvdCatalog;

    @Autowired
    @MovieQualifier(format=Format.BLURAY, genre="Comedy")
    private MovieCatalog comedyBluRayCatalog;

    public void printDemo(){
        System.out.println(actionVhsCatalog);
        actionVhsCatalog.print();
        System.out.println(comedyVhsCatalog);
        comedyVhsCatalog.print();
        actionDvdCatalog.print();
        comedyBluRayCatalog.print();
    }
}
