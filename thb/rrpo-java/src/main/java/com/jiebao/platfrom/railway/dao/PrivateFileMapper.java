package com.jiebao.platfrom.railway.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiebao.platfrom.railway.domain.PrivateFile;
import com.jiebao.platfrom.railway.domain.PublicFile;
import com.jiebao.platfrom.system.domain.File;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author yf
 */
public interface PrivateFileMapper extends BaseMapper<PrivateFile> {

    /**
     * 根据publicId查对应的公共文件
     * @param refId
     * @return
     */
    @Select("SELECT * FROM `sys_files` WHERE ref_id = #{refId} and user_id =#{userId} and ref_type = 5 ORDER BY time ")
    List<File> selectFiles(String refId,String userId);


    @Select("SELECT * FROM `rail_private_file` WHERE parent_id =#{privateFileId} and user_id = #{userId} ORDER BY creat_time ")
    List<PrivateFile> selectByParentId(String privateFileId,String userId);
}
