package com.jiebao.platfrom.railway.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiebao.platfrom.railway.domain.ManageFile;
import com.jiebao.platfrom.railway.domain.PublicFile;
import com.jiebao.platfrom.system.domain.File;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author yf
 */
public interface ManageFileMapper extends BaseMapper<ManageFile> {

    /**
     * 根据publicId查对应的公共文件
     * @param refId
     * @return
     */
    @Select("SELECT * FROM `sys_files` WHERE ref_id = #{refId} and ref_type = 6 ORDER BY time ")
    List<File> selectFiles(String refId);

    @Select("SELECT * FROM `rail_manage_file` WHERE parent_id =#{manageFileId} ORDER BY creat_time ")
    List<ManageFile> selectByParentId(String manageFileId);
}
