package org.dean.course.framework.container;

public class AccountDao {
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public  void printAccountList(){
        System.out.println("printAccountList!!");
    }

}
