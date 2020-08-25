package org.dean.course.framework.container;

public class UserServiceSetDI {
    private UserInfoDao userInfoDao;
    private AccountDao accountDao;

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
}
