package org.dean.course.sevice.listener.custom;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 具体监听者。
 */
@Component
public class Bull implements ApplicationListener<MyGoldFuturesEvent> {
    @Override
    public void onApplicationEvent(MyGoldFuturesEvent event) {

        Float price = ((Float) event.getSource()).floatValue();
        if (price > 0) {
            System.out.println("上涨" + price + "元，多方高兴了！");
        } else {
            System.out.println("下跌" + (-price) + "元，多方伤心了！");
        }
    }
}
