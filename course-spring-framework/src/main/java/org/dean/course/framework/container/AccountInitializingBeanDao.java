package org.dean.course.framework.container;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;

public class AccountInitializingBeanDao implements InitializingBean, BeanNameAware, ApplicationContextAware {
    private String userName;

    private ApplicationContext context;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public  void printUserList(){
        System.out.println("printUserList!! userName" + userName);
    }

    public AccountInitializingBeanDao() {
        System.out.println("AccountInitializingBeanDao init ");
    }

    public void afterPropertiesSet() throws Exception {
        this.userName = "afterPropertiesSet";
        System.out.println("AccountInitializingBeanDao afterPropertiesSet init ");
    }

    public void  init(){
        this.userName = "initMethod";
        System.out.println("AccountInitializingBeanDao init Method ");
    }

    @PostConstruct
    public void  initDe(){
        this.userName = "PostConstruct";
        System.out.println("AccountInitializingBeanDao @PostConstruct ");
    }


    public void setBeanName(String name) {
        System.out.println("AccountInitializingBeanDao name = " + name);

    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("AccountInitializingBeanDao applicationContext = " + applicationContext);
        this.context = applicationContext;
    }

    public ApplicationContext getContext() {
        return context;
    }
}
