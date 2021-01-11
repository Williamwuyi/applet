package com.jiebao.platfrom.check.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author qta
 * @since 2020-09-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("check_year_bind_menus")
public class YearBindMenus implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 考勤规则绑定考核类大模块
     */
    @TableId(value = "bind_id", type = IdType.UUID)
    private String bindId;

    private String yearId;

    private String menusId;


}
