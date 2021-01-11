package com.jiebao.platfrom.railway.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiebao.platfrom.railway.domain.Address;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author yf
 */
public interface AddressMapper extends BaseMapper<Address> {

    //TODO: 可使用注解/xml配置来编写SQL语句

    /**
     * 查询根据组织机构ID所在的通讯录
     *
     * @param
     * @return List
     */
    //@Select("select * from rail_address r  join sys_dept d  on r.parents_id  = d.dept_id where r.parents_id = #{parentsId}")
    List<Address> getAddressList(@Param("deptId") String deptId);

    @Select("select * from rail_address r  join sys_dept d  on r.dept_id  = d.dept_id where r.dept_id = #{deptId}")
    List<Address> getAddressListByDept(@Param("deptId") String deptId);


    int selectUser(String username);

    @Select("SELECT * FROM `rail_address` r where r.dept_id =#{deptId}")
    List<Address> getByDeptId(@Param("deptId") String deptId);


    List<Address> getAddressByCondition(String userName, String phone, String deptId);

    @Select("SELECT * FROM `rail_address` WHERE dept_id = #{deptId} ORDER BY position DESC")
     List<Address>  findUserOwn(String deptId);

}
