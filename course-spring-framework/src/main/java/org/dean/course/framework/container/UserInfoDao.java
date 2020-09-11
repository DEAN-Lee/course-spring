package org.dean.course.framework.container;

public class UserInfoDao {
    private String name;
    private String age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void printUserInfo(){
        System.out.println("UserInfoDao print List");
    }
}
