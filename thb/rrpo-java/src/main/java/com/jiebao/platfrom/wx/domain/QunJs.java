package com.jiebao.platfrom.wx.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.domain.Dict;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author qta
 * @since 2020-08-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wx_qun_js")
public class QunJs implements Serializable {  //群建设对象

    private static final long serialVersionUID = 1L;
    @TableId(value = "js_id", type = IdType.UUID)
    private String jsId;
    private String wxId;

    private String wxName;

    private String date;

    private String szCity; //市州

    @TableField(exist = false)
    private Dept szDept;

    private String qxCity;  //区县
    @TableField(exist = false)
    private Dept qxDept;

    private String jdCity;//街道
    @TableField(exist = false)
    private Dept jdDept;

    private String wxUserName; //群主名字

    private String wxUserZw;  //群主植物

    /**
     * 人数
     */
    private Integer wxNumber;

    /**
     * 线路
     */
    private String qunLine;
    @TableField(exist = false)
    private Dict dictLine;

    /**
     * 线路
     */
    private Integer qunKm;

    /**
     * 活跃度
     */
    private Double qunHyd;

    /**
     * 0未加入 1加入  县市区级联络员是否加入
     */
    private Integer xq;

    /**
     * 铁路公安是否加入
     */
    private Integer ga;

    /**
     * 市州级联络员是否加入
     */
    private Integer sz;

    /**
     * 铁路工务是否加入
     */
    private Integer gw;

    /**
     * 省考核乡镇专用微信是否加入
     */
    private Integer zy;

    /**
     * 铁路电务是否加入
     */
    private Integer dw;

    /**
     * 路地临时施工单位是否加入
     */
    private Integer sg;

    /**
     * 铁路车务（车站）是否加入
     */
    private Integer cw;

    /**
     * 铁路护路信息是否占90%以上
     */
    private Integer hlxx;

    /**
     * 执行湘护路护线组[2016]2号文件情况
     */
    private String file1;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * 解决具体问题个数及解决主要问题简要情况（可另附页）
     */
    private String file2;

    /**
     * 主要经验做法（可另附页）
     */
    private String file3;

    /**
     * 省办意见
     */
    private String sbyj;

    private String award;

    private String fillDeptId;//填报单位

    private String fillDate;//填报时间

}
