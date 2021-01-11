package com.jiebao.platfrom.railway.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiebao.platfrom.railway.domain.ExchangeFile;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author yf
 */
public interface ExchangeFileMapper extends BaseMapper<ExchangeFile> {
    //Mapper查询示例一
    //TODO: 可使用注解/xml配置来编写SQL语句

    @Select("SELECT * FROM rail_area")
    List<ExchangeFile> getFileList();


    /**
     * 根据部门ID查询文件
     *
     * @param parentsId 部门id
     * @return
     */
    @Select("SELECT * FROM `rail_file` r where r.pid =#{parentsId}")
    List<ExchangeFile> getByParentsId(String parentsId);

    /**
     * 根据userID查询文件信息
     *
     * @param userId userId
     * @return
     */
    @Select("SELECT * FROM `rail_file` r ,rail_file_user u WHERE r.id = u.file_id AND u.user_id = #{userId}")
    List<ExchangeFile> findByUser(String userId);

    @Select("SELECT * FROM `rail_exchange_file` r  WHERE r.exchange_id = #{exchangeId}")
    List<ExchangeFile> getByExchangeId(String exchangeId);
}
