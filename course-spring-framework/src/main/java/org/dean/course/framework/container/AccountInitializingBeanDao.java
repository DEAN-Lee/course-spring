package org.dean.course.framework.container;

import org.springframework.beans.factory.InitializingBean;

import javax.annotation.PostConstruct;

public class AccountInitializingBeanDao implements InitializingBean {
    private String userName;

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
}
