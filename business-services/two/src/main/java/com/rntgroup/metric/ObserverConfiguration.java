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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ObserverConfiguration implements CommandLineRunner {

    @Value("${graphite.metric.prefix}")
    private String metricPrefix;

    @Value("${graphite.host}")
    private String graphiteHost;

    @Value("${graphite.port}")
    private String graphitePort;

    private final long delay = Pollers.getPollingIntervals().get(0) / 1000L;

    @Override
    public void run(String... args) {
        initMetricsPublishing();
    }

    private MetricObserver rateTransform(MetricObserver observer) {
        final long heartbeat = 2 * delay;
        return new CounterToRateMetricTransform(observer, heartbeat, TimeUnit.SECONDS);
    }

    private MetricObserver async(String name, MetricObserver observer) {
        final long expireTime = 2000 * delay;
        final int queueSize = 10;
        return new AsyncMetricObserver(name, observer, queueSize, expireTime);
    }

    private MetricObserver createGraphiteObserver() {
        final String metricPrefix = this.metricPrefix;
        final String graphiteServerAddress = graphiteHost + ":" + graphitePort;
        return rateTransform(async("graphite", new GraphiteMetricObserver(metricPrefix, graphiteServerAddress)));
    }

    private void schedule(MetricPoller poller, List<MetricObserver> observers) {
        final PollRunnable task = new PollRunnable(poller, BasicMetricFilter.MATCH_ALL,
                true, observers);
        PollScheduler.getInstance().addPoller(task, delay, TimeUnit.SECONDS);
    }

    private void initMetricsPublishing() {
        final List<MetricObserver> observers = List.of(createGraphiteObserver());

        PollScheduler.getInstance().start();
        schedule(new MonitorRegistryMetricPoller(), observers);
        schedule(new JvmMetricPoller(), observers);
    }
}
