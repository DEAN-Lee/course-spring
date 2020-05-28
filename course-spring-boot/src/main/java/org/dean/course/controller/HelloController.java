package org.dean.course.controller;


import org.dean.course.sevice.strategy.StrategyContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * mvc
 */
@RestController
public class HelloController {
    private static final String template = "Hello, %s!";
    @Autowired
    private StrategyContext strategyContext;

    @GetMapping("/getWord")
    public String getWord(@RequestParam(value = "name", defaultValue = "word") String name) {
        if (name.equalsIgnoreCase("hello")) {
            strategyContext.callStrategyMethod(1);
        } else {
            strategyContext.callStrategyMethod(2);
        }
        return String.format(template, name);
    }

    @GetMapping("/getStrategy")
    public String getStrategy(@RequestParam(value = "type", defaultValue = "1") String type) {
        String name = "";
        if (type.equalsIgnoreCase("hello")) {
            name= strategyContext.callStrategyMethod(1);
        } else {
            name= strategyContext.callStrategyMethod(2);
        }
        return String.format(template, name);
    }
}
