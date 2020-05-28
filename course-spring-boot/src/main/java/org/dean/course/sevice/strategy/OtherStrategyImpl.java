package org.dean.course.sevice.strategy;

import org.springframework.stereotype.Service;

/**
 * @author Dean github:https://github.com/DEAN-Lee
 * @Title: OtherStrategyImpl
 * @Description:
 * @date 2020/5/21 22:01
 */
@Service
public class OtherStrategyImpl implements Strategy {
    @Override
    public String strategyMethod() {
        System.out.println("the Strategy two ");
        return "this is strategy two";
    }

    @Override
    public Integer strategyType() {
        return 2;
    }
}
