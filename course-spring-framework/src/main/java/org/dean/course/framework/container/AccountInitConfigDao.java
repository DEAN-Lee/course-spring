package org.dean.course.framework.container;

public class AccountInitConfigDao {
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public  void printUserList(){
        System.out.println("printUserList!!");
    }

    public AccountInitConfigDao() {
        System.out.println("AccountInitConfigDao init ");
    }

    public void  init(){
        System.out.println("AccountInitConfigDao init Method ");
    }
}
