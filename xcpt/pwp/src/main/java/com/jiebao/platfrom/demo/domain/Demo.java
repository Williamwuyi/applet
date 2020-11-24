package com.jiebao.platfrom.demo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * 演示代码 - 实体类
 * @Author Sinliz
 * @Date 2020/3/12 9:25
 */

@Data   //lombok支持
@TableName("sys_demo")
public class Demo extends Model<Demo> implements Serializable {
    //extends Model<?> 后, 实体类可以实现直接操作数据库，如：insert update等方法 (可选)
    //implements 必须实现序列化
    private static final long serialVersionUID = -5578039851919235887L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @TableField(value = "name")
    private String name;

}
