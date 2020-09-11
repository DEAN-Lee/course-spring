package org.dean.course.framework.container;

import org.springframework.beans.factory.annotation.Autowired;

public class UserServiceAnnotationDemo {
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private AccountDao accountDao;

    public void printAccountList(){
        System.out.println("UserServiceAnnotationDemo print list");
        accountDao.printAccountList();
        userInfoDao.printUserInfo();
    }

}
