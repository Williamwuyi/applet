###Jiebao-demo-spring
#### 项目开发工具
```
Maven、Mysql、Redis、Git
```
#### java环境
```
JDK1.8
```

#### 项目依赖
~~~
SpringBoot、Mybatis-Plus、Freemarker、P6spy、MySql
Apache common、Apache shiro、Redis、Ip2region、JWT
ExcelKit等
~~~

#### 项目结构
```
├── java
    ├── com.jiebao.platfrom
       ├── common - 项目基础支持封装
       ├── system - 项目系统管理相关
           ├── controller - 控制器
           ├── dao - Mybatis-plus Mapper
           ├── domain - bean实体
           ├── manager - 用户操作封装
           ├── service - service数据操作
       ├── demo - 演示代码 
       ├── custom - 自定义业务模块
       ├── BasicSpringApplication - 项目启动类
├── resources
    ├── generator.templates - 代码生模板
    ├── ip2region - IP定位
    ├── mapper - Mybatis-Plus xml配置
    ├── application.yml - 项目配置
    ├── banner.txt - 启动banner图
    ├── logback-spring.xml - 日志配置
    ├── spy.properties - SQL打印配置
    ├── ValidationMessages.properties - 常用校验
```
#### 版本说明

#### 开发规范