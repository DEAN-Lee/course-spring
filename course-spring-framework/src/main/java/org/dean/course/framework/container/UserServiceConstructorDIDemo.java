package org.dean.course.framework.container;

public class UserServiceConstructorDIDemo {
    private UserInfoDao userInfoDao;
    private AccountDao accountDao;

    public UserServiceConstructorDIDemo(UserInfoDao userInfoDao, AccountDao accountDao) {
        this.userInfoDao = userInfoDao;
        this.accountDao = accountDao;
    }

    public UserInfoDao getUserInfoDao() {
        return userInfoDao;
    }

    public AccountDao getAccountDao() {
        return accountDao;
    }

}
