package org.dean.course.sevice.strategy;

import org.springframework.stereotype.Service;

/**
 * @author Dean github:https://github.com/DEAN-Lee
 * @Title: TheStrategyImpl
 * @Description:
 * @date 2020/5/21 22:00
 */
@Service
public class TheStrategyImpl implements Strategy {
    @Override
    public String strategyMethod() {
        System.out.println("the Strategy one ");
        return "this is strategy one";
    }

    @Override
    public Integer strategyType() {
        return 1;
    }
}
