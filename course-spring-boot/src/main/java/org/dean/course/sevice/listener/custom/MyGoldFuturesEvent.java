package org.dean.course.sevice.listener.custom;

import org.springframework.context.ApplicationEvent;

/**
 * 被观察者-目标类
 */
public class MyGoldFuturesEvent extends ApplicationEvent {

    public MyGoldFuturesEvent(Object source) {
        super(source);
        System.out.println("MyEvent 构造器执行");
    }

    public void echo() {
        System.out.println("模拟业务逻辑执行");
    }
}
