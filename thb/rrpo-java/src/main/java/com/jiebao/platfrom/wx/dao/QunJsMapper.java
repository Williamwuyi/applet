package com.jiebao.platfrom.wx.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiebao.platfrom.wx.domain.QunJs;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author qta
 * @since 2020-08-31
 */
public interface QunJsMapper extends BaseMapper<QunJs> {
    @Select("select 1 from wx_qun_js where wx_id=#{wxId} limit 1")
    Integer judge(String wxId);

    @Select("select * from wx_qun_js  ${ew.customSqlSegment}")
    @Results({
            @Result(property = "szDept", column = "sz_city", one = @One(select = "com.jiebao.platfrom.system.dao.DeptMapper.selectById")),
            @Result(property = "qxDept", column = "qx_city", one = @One(select = "com.jiebao.platfrom.system.dao.DeptMapper.selectById")),
            @Result(property = "jdDept", column = "jd_city", one = @One(select = "com.jiebao.platfrom.system.dao.DeptMapper.selectById")),
            @Result(property = "dictLine", column = "qun_line", one = @One(select = "com.jiebao.platfrom.system.dao.DictMapper.selectById")),
            @Result(property = "wxNumber", column = "wx_id", one = @One(select = "com.jiebao.platfrom.wx.dao.UserIMapper.countByWxId"))
    })
    QunJs getQunJs(@Param("ew") QueryWrapper<QunJs> ew);

}
