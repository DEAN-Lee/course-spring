package org.dean.course.sevice.observer;

import org.springframework.stereotype.Service;

import java.util.Observable;

/**
 * 被观察者-目标类
 */
@Service
public class GoldFutures extends Observable {
    private float price;

    public float getPrice() {
        return this.price;
    }

    public void setPrice(float price) {
        super.setChanged();  //设置内部标志位，注明数据发生变化
        super.notifyObservers(price);    //通知观察者价格改变了https://www.jianshu.com/p/5e72c6b76c72
        this.price = price;
    }
}
