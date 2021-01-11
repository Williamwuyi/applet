package com.jiebao.platfrom.check.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author qta
 * @since 2020-08-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("check_year")
public class Year implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 年度考核id
     */
    @TableId(value = "year_id", type = IdType.UUID)
    private String yearId;


    /**
     * 对应年份
     */
    private String yearDate;

    @TableField(exist = false)
    private List<YearSize> list;//基础数据对应多少个数据


}
