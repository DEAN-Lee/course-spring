package org.dean.course.sevice.listener;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class MyListener implements ApplicationListener<ApplicationEvent> {
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        System.out.println("有监听事件被触发:" + event.getClass());
        if (event instanceof ApplicationEvent) {
            System.out.println("事件类型: applicationContext 初始化完毕" + event.getSource().toString());
        }
    }
}
