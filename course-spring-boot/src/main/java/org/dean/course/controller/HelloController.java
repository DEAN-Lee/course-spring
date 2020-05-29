package org.dean.course.controller;


import org.dean.course.sevice.listener.custom.Publisher;
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
    private static final String template_two = "price, %s!";
    @Autowired
    private StrategyContext strategyContext;
    @Autowired
    private Publisher publisher;

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
            name = strategyContext.callStrategyMethod(1);
        } else {
            name = strategyContext.callStrategyMethod(2);
        }
        return String.format(template, name);
    }

    @GetMapping("/sendEvent")
    public String sendEvent(@RequestParam(value = "price", defaultValue = "1") Float price) {
        publisher.publishMyApplicationEvent(price);
        return String.format(template_two, price);
    }
}
