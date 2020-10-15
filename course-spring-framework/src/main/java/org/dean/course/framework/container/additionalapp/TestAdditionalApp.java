package org.dean.course.framework.container.additionalapp;

import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Locale;

public class TestAdditionalApp {
    public static void main(String[] args) {
        MessageSource resources = new ClassPathXmlApplicationContext("conf/additionalAppBean.xml");
        String message = resources.getMessage("message", null, "Default", Locale.ENGLISH);
        System.out.println(message);
    }
}
