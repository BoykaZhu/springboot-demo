package com.example.controller;

import com.example.properties.MailProperties;
import com.example.properties.UserProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/properties")
@Api(description = "properties config files")
public class PropertiesApi {
    @Autowired
    private MailProperties mailProperties;

    @Autowired
    private UserProperties userProperties;

    @RequestMapping(value = "/mail", method = RequestMethod.GET)
    @ApiOperation(value = "get mail setting")
    public MailProperties getMailSetting() {
        return mailProperties;
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    @ApiOperation(value = "get user setting")
    public UserProperties getUserProperties() {
        return userProperties;
    }
}
