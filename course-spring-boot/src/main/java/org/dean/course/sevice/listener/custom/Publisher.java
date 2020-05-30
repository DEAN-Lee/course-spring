package org.dean.course.sevice.listener.custom;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 发布消息类
 */
@Service
public class Publisher implements ApplicationContextAware {
    @Autowired
    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;

    }

    public void publishMyApplicationEvent(Float price) {
        /*以price 对象作为事件源*/
        /*这就是要实现ApplicationContextAware 接口的原因，因为需要当前应用的ApplicationContext 对象*/
        MyGoldFuturesEvent event = new MyGoldFuturesEvent(price);
        /*使用ApplicationContext 对象的publishEvent 方法发布事件*/
        this.context.publishEvent(event);
    }

}
