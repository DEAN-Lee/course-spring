package org.dean.course.framework.container;

public class UserServiceConstructorDIDemo {
    private UserInfoDao userInfoDao;
    private AccountDao accountDao;

    public UserServiceConstructorDIDemo(UserInfoDao userInfoDao, AccountDao accountDao) {
        this.userInfoDao = userInfoDao;
        this.accountDao = accountDao;
        System.out.println("UserServiceConstructorDIDemo init");
    }

    public UserInfoDao getUserInfoDao() {
        return userInfoDao;
    }

    public AccountDao getAccountDao() {
        return accountDao;
    }

    public UserServiceConstructorDIDemo() {
        System.out.println("UserServiceConstructorDIDemo init");
    }
}
