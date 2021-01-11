package com.jiebao.platfrom.railway.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiebao.platfrom.railway.domain.PrizeType;
import com.jiebao.platfrom.railway.domain.PublicFile;
import com.jiebao.platfrom.system.domain.File;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author yf
 */
public interface PublicFileMapper extends BaseMapper<PublicFile> {

    /**
     * 根据publicId查对应的公共文件
     * @param refId
     * @return
     */
    @Select("SELECT * FROM `sys_files` WHERE ref_id = #{refId} and ref_type = 4 ORDER BY time ")
    List<File> selectFiles(String refId);

    @Select("SELECT * FROM `rail_public_file` WHERE parent_id =#{publicFileId} ORDER BY creat_time ")
    List<PublicFile> selectByParentId(String publicFileId);
}
