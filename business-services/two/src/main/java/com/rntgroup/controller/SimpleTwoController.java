package com.rntgroup.controller;

import com.netflix.servo.annotations.DataSourceType;
import com.netflix.servo.annotations.Monitor;
import com.netflix.servo.annotations.MonitorTags;
import com.netflix.servo.monitor.Monitors;
import com.netflix.servo.tag.BasicTagList;
import com.netflix.servo.tag.TagList;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class SimpleTwoController {

    @Monitor(name = "Empty request counter",
            type = DataSourceType.COUNTER,
            description = "Total number of request simplify")
    private final AtomicInteger simplifyRequestCounter = new AtomicInteger(0);

    @MonitorTags
    private final TagList tags = BasicTagList.of("id", "controller", "class", this.getClass().getSimpleName());

    @PostConstruct
    public void init() {
        Monitors.registerObject("simpleTwoController", this);
    }

    @GetMapping(path = "/simplify")
    public String simplify(@RequestParam("number") BigInteger number) {
        simplifyRequestCounter.incrementAndGet();

        String numberAsString = number.toString();
        String result = numberAsString;

        if (numberAsString.length() > 6) {
            int degree = numberAsString.length() - 1;
            result = new StringBuilder(numberAsString.substring(0, 6))
                    .insert(1, ",")
                    .append("E+")
                    .append(degree)
                    .toString();
        }

        return result;
    }

}
