package org.dean.course.sevice.strategy;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dean github:https://github.com/DEAN-Lee
 * @Title: StrategyContext
 * @Description: 策略控制类
 * @date 2020/5/21 22:02
 */
@Service
public class StrategyContext {
    private final Map<Integer, Strategy> strategyMap = new HashMap<Integer, Strategy>();

    public StrategyContext(List<Strategy> strategyList) {
        strategyMap.clear();
        for (Strategy strategy : strategyList) {
            strategyMap.put(strategy.strategyType(), strategy);
        }
    }

    public String callStrategyMethod(Integer strategyType) {
        return strategyMap.get(strategyType).strategyMethod();
    }
}
