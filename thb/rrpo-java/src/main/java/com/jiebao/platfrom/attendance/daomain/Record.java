package com.jiebao.platfrom.attendance.daomain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author qta
 * @since 2020-08-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("attendance_record")
public class Record implements Serializable {
    //白    //公   //差 //年   病   事  婚
    private static final long serialVersionUID = 1L;

    @TableId(value = "record_id", type = IdType.UUID)
    private String recordId;

    /**
     * 人员对象
     */
    private String userId;  //用户
//    private String deptId; //部门
    private String position;//职位
    private Integer ward;//挡位排序
    private Double money;//g工资
    private Double skillMoney;//技能工资
    private Double jiShi;  //计时
    private Double jiJian;//记件
    private Double jiaBan;//加班
    private Double chuChai;//出差
    private Double nianXiuJia;//年休假
    private Double binJia;//病假
    private Double shiJia;//事假
    private Double hunJia;//婚嫁
    private Double heJi;//合计

    private String day1;
    private String day2;
    private String day3;
    private String day4;
    private String day5;
    private String day6;
    private String day7;
    private String day8;
    private String day9;
    private String day10;
    private String day11;
    private String day12;
    private String day13;
    private String day14;
    private String day15;
    private String day16;
    private String day17;
    private String day18;
    private String day19;
    private String day20;
    private String day21;
    private String day22;
    private String day23;
    private String day24;
    private String day25;
    private String day26;
    private String day27;
    private String day28;
    private String day29;
    private String day30;
    private String day31;

    private String date;
}



