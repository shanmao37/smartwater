package com.fourfaith.controller;

import com.fourfaith.service.TestService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @version V1.0
 * @ClassName
 * @Description
 * @Author fourfaith_lwj1
 * @Date 2024-07-03 9:42
 */
@RestController
@RequestMapping("/test")
public class TestController {
    @Resource
    private TestService testService;


    @PostMapping("/queryData")
    public List<Map> queryData() {
        return testService.queryData();
    }
}
