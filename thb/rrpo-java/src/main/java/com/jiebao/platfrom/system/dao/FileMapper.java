package com.jiebao.platfrom.system.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiebao.platfrom.system.domain.File;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface FileMapper extends BaseMapper<File> {

    @Update("UPDATE sys_files SET ref_id = #{exchangeId} WHERE file_id =#{fileId}")
    boolean updateByFileId(String fileId, String exchangeId);

    @Update("UPDATE sys_files SET ref_id = #{informId} WHERE file_id =#{fileId}")
    boolean updateInformByFileId(String fileId, String informId);

    @Update("UPDATE sys_files SET ref_id = #{publicFileId} WHERE file_id =#{fileId}")
    boolean updatePublicFileByFileId(String fileId, String publicFileId);

    @Delete("delete from sys_files ${ew.customSqlSegment}")
    Integer deleteByRefId(@Param("ew") QueryWrapper<File> queryWrapper);

    @Update("UPDATE sys_files SET ref_id = #{noticeId} WHERE file_id =#{fileId}")
    boolean updateNoticeByFileId(String fileId, String noticeId);

    @Select("select * from sys_files where ref_id=#{refId}")
    List<File> fileList(String refId);
}
