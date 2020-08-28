package org.dean.course.framework.container;

import org.springframework.context.SmartLifecycle;
import org.springframework.context.support.DefaultLifecycleProcessor;

public class AccountInitConfigDao implements SmartLifecycle {
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


    public void start() {
        System.out.println("AccountInitConfigDao start");
    }


    public void stop() {
        System.out.println("AccountInitConfigDao stop");
    }


    public boolean isRunning() {
        return false;
    }

    /**
     * Return the phase that this lifecycle object is supposed to run in.
     * <p>The default implementation returns {@link #DEFAULT_PHASE} in order to
     * let {@code stop()} callbacks execute after regular {@code Lifecycle}
     * implementations.
     *
     * @see #isAutoStartup()
     * @see #start()
     * @see #stop(Runnable)
     * @see DefaultLifecycleProcessor
     */
    public int getPhase() {
        return 0;
    }
}
