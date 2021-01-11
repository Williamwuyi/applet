package com.jiebao.platfrom.accident.daomain;

import com.jiebao.platfrom.common.utils.ExcelName;
import lombok.Data;

@Data
public class CompareTable {
    /**
     * bj   代表 比较    up 代表 上期指数
     */
    @ExcelName(name = "单位")
    private String deptName;

    @ExcelName(name = "事故上期死亡人数")
    private Integer upNumber;
    @ExcelName(name = "事故本期死亡人数")
    private Integer number;
    @ExcelName(name = "事故同期比较")
    private Double bj1;

    @ExcelName(name = "电脑分析上期死亡人数")
    private Double upDnxs;
    @ExcelName(name = "电脑分析本期死亡人数")
    private Double dnxs;
    @ExcelName(name = "电脑分析同期比较")
    private Double bj2;


    @ExcelName(name = "电脑调节上期死亡人数")
    private Double upDnTjxs;
    @ExcelName(name = "电脑调节本期死亡人数")
    private Double dntjxs;
    @ExcelName(name = "电脑调节同期比较")
    private Double bj3;

    @ExcelName(name = "属地责任上期死亡人数")
    private Double upDeathToll;
    @ExcelName(name = "属地责任本期死亡人数")
    private Double deathToll;
    @ExcelName(name = "属地责任同期比较")
    private Double bj4;

    public CompareTable setName(String name) {
        this.deptName = name;
        return this;
    }

    public CompareTable() {
        this.upNumber = 0;
        this.number = 0;
        this.bj1 = 0.00;
        this.upDnxs = 0.00;
        this.dnxs = 0.00;
        this.bj2 = 0.00;
        this.upDnTjxs = 0.00;
        this.dntjxs = 0.00;
        this.bj3 = 0.00;
        this.upDeathToll = 0.00;
        this.deathToll = 0.00;
        this.bj4 = 0.00;
    }

    public CompareTable(String deptName, Integer upNumber, Integer number, Double bj1, Double upDnxs, Double dnxs, Double bj2, Double upDnTjxs, Double dntjxs, Double bj3, Double upDeathToll, Double deathToll, Double bj4) {
        this.deptName = deptName;
        this.upNumber = upNumber;
        this.number = number;
        this.bj1 = bj1;
        this.upDnxs = upDnxs;
        this.dnxs = dnxs;
        this.bj2 = bj2;
        this.upDnTjxs = upDnTjxs;
        this.dntjxs = dntjxs;
        this.bj3 = bj3;
        this.upDeathToll = upDeathToll;
        this.deathToll = deathToll;
        this.bj4 = bj4;
    }

}
