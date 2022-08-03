package com.rntgroup.controller;

import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringListProperty;
import com.netflix.config.DynamicStringProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/configs")
public class ConfigPropertiesController {

    @GetMapping
    public String getProperties(@RequestParam("property-name") String propertyName) {
        DynamicStringProperty property = DynamicPropertyFactory.getInstance().getStringProperty(propertyName, "Not Found!");
        return propertyName + ": " + property.getValue();
    }

}
