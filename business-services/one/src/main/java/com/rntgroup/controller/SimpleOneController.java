package com.rntgroup.controller;

import com.netflix.servo.DefaultMonitorRegistry;
import com.netflix.servo.annotations.DataSourceType;
import com.netflix.servo.annotations.Monitor;
import com.netflix.servo.annotations.MonitorTags;
import com.netflix.servo.monitor.BasicTimer;
import com.netflix.servo.monitor.MonitorConfig;
import com.netflix.servo.monitor.Monitors;
import com.netflix.servo.monitor.Stopwatch;
import com.netflix.servo.tag.BasicTagList;
import com.netflix.servo.tag.TagList;
import com.rntgroup.client.SimpleTwoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@RestController
public class SimpleOneController {

    @Autowired
    private SimpleTwoClient simpleTwoClient;

    @Monitor(name = "Empty request counter",
            type = DataSourceType.COUNTER,
            description = "Total number of visits to the start page")
    private final AtomicInteger homePageRequestCounter = new AtomicInteger(0);

    @Monitor(name = "Factorial request counter",
            type = DataSourceType.COUNTER,
            description = "Total number of calculate factorial")
    private final AtomicInteger factorialRequestCounter = new AtomicInteger(0);

    private final BasicTimer factorialDelayTimer = new BasicTimer(MonitorConfig.builder("factorialDelayTimer").build());

    @MonitorTags
    private final TagList tags = BasicTagList.of("id", "controller", "class", this.getClass().getSimpleName());

    @PostConstruct
    public void init() {
        Monitors.registerObject("simpleOneController", this);
        DefaultMonitorRegistry.getInstance().register(factorialDelayTimer);
    }

    @GetMapping
    public String homePage() {
        homePageRequestCounter.incrementAndGet();
        return "Hello! This is service One";
    }

    @GetMapping("/factorial")
    public BigInteger getFactorialBy(@RequestParam("number") int number) {
        factorialRequestCounter.incrementAndGet();
        Stopwatch stopwatch = factorialDelayTimer.start();

        BigInteger result = IntStream.rangeClosed(1, number)
                .mapToObj(BigInteger::valueOf)
                .reduce(BigInteger.ONE, BigInteger::multiply);

        stopwatch.stop();
        factorialDelayTimer.record(stopwatch.getDuration(), TimeUnit.MILLISECONDS);
        return result;
    }

    @GetMapping("/factorial/simply")
    public String getSimplyFactorialBy(@RequestParam("number") int number) {
        factorialRequestCounter.incrementAndGet();
        Stopwatch stopwatch = factorialDelayTimer.start();

        BigInteger result = IntStream.rangeClosed(1, number)
                .mapToObj(BigInteger::valueOf)
                .reduce(BigInteger.ONE, BigInteger::multiply);

        String resultAsString = simpleTwoClient.simplify(result);

        stopwatch.stop();
        factorialDelayTimer.record(stopwatch.getDuration(), TimeUnit.MILLISECONDS);
        return resultAsString;
    }
}
