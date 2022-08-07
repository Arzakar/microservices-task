package com.rntgroup.scheduler;

import com.rntgroup.client.SimpleOneClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SimpleScheduler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SimpleOneClient simpleOneClient;

    @Scheduled(initialDelay = 60000, fixedRate = 500)
    @Async
    public void requestFactorial() {
        int randomNumber = getRandomNumber();
        simpleOneClient.getFactorialBy(randomNumber);

        logger.info("Request /factorial?" + randomNumber);
    }

    @Scheduled(initialDelay = 60000, fixedRate = 500)
    @Async
    public void requestSimplyFactorial() {
        int randomNumber = getRandomNumber();
        simpleOneClient.getSimplyFactorialBy(randomNumber);

        logger.info("Request /factorial/simply?" + randomNumber);
    }

    private int getRandomNumber() {
        return (int)(Math.random() * 2600) + 1;
    }
}
