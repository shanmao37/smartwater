package com.fourfaith.service;

import com.fourfaith.dal.mysql.FFswllpMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @version V1.0
 * @ClassName
 * @Description
 * @Author fourfaith_lwj1
 * @Date 2024-07-03 9:45
 */
@Service
@Slf4j
public class TestServiceImpl implements TestService{
    @Resource
    private FFswllpMapper ffswllpMapper;

    @Override
    public List<Map> queryData() {
        return ffswllpMapper.queryData();
    }
}
