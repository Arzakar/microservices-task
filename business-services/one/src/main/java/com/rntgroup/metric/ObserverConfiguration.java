package com.rntgroup.metric;

import com.netflix.servo.monitor.Pollers;
import com.netflix.servo.publish.AsyncMetricObserver;
import com.netflix.servo.publish.BasicMetricFilter;
import com.netflix.servo.publish.CounterToRateMetricTransform;
import com.netflix.servo.publish.JvmMetricPoller;
import com.netflix.servo.publish.MetricObserver;
import com.netflix.servo.publish.MetricPoller;
import com.netflix.servo.publish.MonitorRegistryMetricPoller;
import com.netflix.servo.publish.PollRunnable;
import com.netflix.servo.publish.PollScheduler;
import com.netflix.servo.publish.graphite.GraphiteMetricObserver;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ObserverConfiguration implements CommandLineRunner {

    @Override
    public void run(String... args) {
        initMetricsPublishing();
    }

    private static MetricObserver rateTransform(MetricObserver observer) {
        final long heartbeat = 2 * Pollers.getPollingIntervals().get(0) / 1000L;
        return new CounterToRateMetricTransform(observer, heartbeat, TimeUnit.SECONDS);
    }

    private static MetricObserver async(String name, MetricObserver observer) {
        final long expireTime = 2000 * Pollers.getPollingIntervals().get(0) / 1000L;
        final int queueSize = 10;
        return new AsyncMetricObserver(name, observer, queueSize, expireTime);
    }

    private static MetricObserver createGraphiteObserver() {
        final String metricPrefix = "servo";
        final String graphiteServerAddress = "localhost:2003";
        return rateTransform(async("graphite", new GraphiteMetricObserver(metricPrefix, graphiteServerAddress)));
    }

    private static void schedule(MetricPoller poller, List<MetricObserver> observers) {
        final PollRunnable task = new PollRunnable(poller, BasicMetricFilter.MATCH_ALL,
                true, observers);
        PollScheduler.getInstance().addPoller(task, Pollers.getPollingIntervals().get(0) / 1000L, TimeUnit.SECONDS);
    }

    private static void initMetricsPublishing() {
        final List<MetricObserver> observers = List.of(createGraphiteObserver());

        PollScheduler.getInstance().start();
        schedule(new MonitorRegistryMetricPoller(), observers);
        schedule(new JvmMetricPoller(), observers);

    }
}
