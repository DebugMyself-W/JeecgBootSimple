package com.aoyang.modules.base.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "接口")
@RestController
@RequestMapping("/online/hospital")
@Slf4j
public class MyTestController {

    @AutoLog(value = "测试")
    @ApiOperation(value = "测试", notes = "测试")
    @GetMapping("/hello")
    public String hello(){
        return "HelloWorld";
    }

}
