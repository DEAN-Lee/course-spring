package org.dean.course.framework.container;

import org.springframework.beans.factory.annotation.Required;

public class UserServiceSetDI {
    private UserInfoDao userInfoDao;
    private AccountDao accountDao;

    private AccountInitializingBeanDao initializingBeanDao;

    public UserInfoDao getUserInfoDao() {
        return userInfoDao;
    }

    public AccountDao getAccountDao() {
        return accountDao;
    }

    public void setUserInfoDao(UserInfoDao userInfoDao) {
        this.userInfoDao = userInfoDao;
    }


    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public UserServiceSetDI() {
        System.out.println("UserServiceSetDI init");
    }

    public void printAccountList(){
        System.out.println("UserServiceSetDI print List");
        initializingBeanDao.printUserList();
    }

    public AccountInitializingBeanDao getInitializingBeanDao() {
        return initializingBeanDao;
    }

    @Required
    public void setInitializingBeanDao(AccountInitializingBeanDao initializingBeanDao) {
        this.initializingBeanDao = initializingBeanDao;
    }
}
