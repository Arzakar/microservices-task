package com.rntgroup.controller;

import com.netflix.servo.annotations.DataSourceType;
import com.netflix.servo.annotations.Monitor;
import com.netflix.servo.annotations.MonitorTags;
import com.netflix.servo.monitor.BasicTimer;
import com.netflix.servo.monitor.MonitorConfig;
import com.netflix.servo.monitor.Monitors;
import com.netflix.servo.monitor.Stopwatch;
import com.netflix.servo.tag.BasicTagList;
import com.netflix.servo.tag.TagList;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@RestController
public class SimpleOneController {

    @Monitor(name = "Empty request counter",
            type = DataSourceType.COUNTER,
            description = "Total number of visits to the start page")
    private final AtomicInteger emptyRequestCounter = new AtomicInteger(0);

    @Monitor(name = "Factorial request counter",
            type = DataSourceType.COUNTER,
            description = "Total number of visits to the start page")
    private final AtomicInteger factorialRequestCounter = new AtomicInteger(0);

    @MonitorTags
    private final TagList tags = BasicTagList.of("id", "controller", "class", this.getClass().getSimpleName());

    @PostConstruct
    public void init() {
        Monitors.registerObject("simpleOneController", this);
    }

    @GetMapping
    public String welcomePage() {
        emptyRequestCounter.incrementAndGet();
        return "Hello! This is service One";
    }

    @GetMapping("/factorial")
    public BigInteger getFactorialBy(@RequestParam("number") int number) {
        factorialRequestCounter.incrementAndGet();
        BasicTimer timer = new BasicTimer(MonitorConfig.builder("timer").build());
        Stopwatch stopwatch = timer.start();

        BigInteger result = IntStream.rangeClosed(1, number)
                .mapToObj(BigInteger::valueOf)
                .reduce(BigInteger.ONE, BigInteger::multiply);

        stopwatch.stop();
        System.out.println(timer.getValue());
        return result;
    }
}
