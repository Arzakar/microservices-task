package com.rntgroup.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigInteger;

@FeignClient(value = "api-gateway-service")
public interface SimpleTwoClient {

    String TWO_SERVICE = "/two-service";

    @GetMapping(path = TWO_SERVICE + "/simplify")
    String simplify(@RequestParam("number") BigInteger number);

}
