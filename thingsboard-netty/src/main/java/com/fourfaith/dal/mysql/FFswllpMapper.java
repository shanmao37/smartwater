package com.fourfaith.dal.mysql;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @version V1.0
 * @ClassName
 * @Description
 * @Author fourfaith_lwj1
 * @Date 2024-07-03 9:37
 */
@Mapper
public interface FFswllpMapper {

    List<Map> queryData();
}
