package com.jiebao.platfrom;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@MapperScan({"com.jiebao.platfrom.common.GuideData.dao","com.jiebao.platfrom.common.dataImport.dao"})
public class BasicSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(BasicSpringApplication.class, args);
    }

}
