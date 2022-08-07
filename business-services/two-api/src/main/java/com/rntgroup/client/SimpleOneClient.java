package com.rntgroup.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "api-gateway-service")
public interface SimpleOneClient {

    String ONE_SERVICE = "/one-service";

    @GetMapping(ONE_SERVICE + "/factorial")
    String getFactorialBy(@RequestParam("number") int number);

    @GetMapping(ONE_SERVICE + "/factorial/simply")
    String getSimplyFactorialBy(@RequestParam("number") int number);

}
