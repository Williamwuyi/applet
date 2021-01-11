package com.jiebao.platfrom.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiebao.platfrom.system.domain.Dept;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DeptMapper extends BaseMapper<Dept> {

	/**
	 * 递归删除部门
	 *
	 * @param deptId deptId
	 */
	void deleteDepts(String deptId);

	@Select("SELECT * FROM `sys_dept` d where d.dept_id = #{deptId}")
	Dept getById(String deptId);
	@Select("SELECT dept_id FROM `sys_dept` d where d.dept_id = #{deptId}")
	String getIdById(String deptId);

	@Select("SELECT dept_id FROM `sys_dept` d where d.dept_name = #{deptName}")
	String getIdByName(String deptName);


	@Select("SELECT dept_name FROM `sys_dept` d where d.dept_id = #{deptId}")
	String getByName(String deptId);

	@Select("SELECT * FROM `sys_dept` d where d.dept_name = #{deptName}")
	Dept getByNewName(String deptName);

//	@Select("SELECT * FROM `sys_dept` d where d.dept_name = #{deptName}  ")
//	Dept getByNewNames(String deptName);

//	@Select("select dept_id from sys_dept where dept_name=#{name}")
//	String queryIdByName(String name);
//
//	@Select("select dept_id from sys_dept where dept_name=#{name} and parent_id=#{id}")
//	String queryIdByName1(String name,String id);

	@Select("SELECT * FROM `sys_dept`  where rank = 0 order by order_num ")
	List<Dept> selectRankZero();

	@Select("select * from sys_dept s where s.rank=1 and (dept_name like '%市%' or dept_name like '%州%')  order by s.order_num asc")
	List<Dept> getDeptNameByAsc();//市州


	@Select("select * from sys_dept s where s.rank=4   order by s.order_num asc ")
	List<Dept> getDeptNameByAscGz();//升序去查 公安处

	@Select("SELECT * FROM `sys_dept` WHERE parent_id = #{deptId} ORDER BY order_num ")
	List<Dept> findChildDept(String deptId);

	@Select("SELECT dept_id FROM `sys_dept` WHERE dept_name like '${name}%' and rank=1  limit 1 ")
	String s1(@Param("name") String name);

	@Select("SELECT dept_id FROM `sys_dept` WHERE dept_name like '${name}%' and rank=2  limit 1 ")
	String s2(@Param("name")String name);

	@Select("SELECT dept_id FROM `sys_dept` WHERE dept_name like '${name}%' and rank=3  limit 1 ")
	String s3(@Param("name")String name);

	@Select("SELECT dept_id FROM `sys_dept` WHERE dept_name like '${name}%' and rank=2  limit 1 ")
	String s4(@Param("name")String name);
}

