package com.fourfaith.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version V1.0
 * @ClassName
 * @Description
 * @Author fourfaith_lwj1
 * @Date 2024-07-03 10:09
 */
@RestController
@RequestMapping("/aep")
public class AepController {

    @PostMapping("/subscribe")
    public void subscribe(@RequestBody String param){
        System.out.println(param);
    }
}
