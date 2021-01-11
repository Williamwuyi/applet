package com.jiebao.platfrom.accident.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiebao.platfrom.accident.daomain.Ql;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author qta
 * @since 2020-11-07
 */
public interface QlMapper extends BaseMapper<Ql> {
    @Select(" select ID as id,ID as idF,bm,dzs,xsq,xz,zl,ga,pcs,jwq,gwd,gls,fw,cd,jtsgqk,yy,kzqk,delflag,creat_time from accident_ql ${ew.customSqlSegment}")
    @Results({
            @Result(property = "tpFile", column = "idF", one = @One(select = "com.jiebao.platfrom.system.dao.FileMapper.fileList"))
    })
    IPage<Ql> pageList(Page<Ql> page, @Param("ew") LambdaQueryWrapper<Ql> ew);

}
