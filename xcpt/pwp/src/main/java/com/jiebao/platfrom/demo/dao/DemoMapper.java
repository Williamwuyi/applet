package com.jiebao.platfrom.demo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiebao.platfrom.demo.domain.Demo;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 演示代码 - Mapper
 * @Author Sinliz
 * @Date 2020/3/12 10:00
 */
public interface DemoMapper extends BaseMapper<Demo> {

    //Mapper查询示例一
    //TODO: 可使用注解/xml配置来编写SQL语句
    @Select("SELECT * FROM sys_demo")
    List<Demo> getDemoList();

}
