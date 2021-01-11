package com.jiebao.platfrom.system.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.domain.Tree;
import com.jiebao.platfrom.railway.domain.Address;
import com.jiebao.platfrom.system.domain.Dept;

import java.util.List;
import java.util.Map;

public interface DeptService extends IService<Dept> {

    Map<String, Object> findDepts(QueryRequest request, Dept dept);

    Map<String, Object> findCityDepts(QueryRequest request, Dept dept);

    List<Dept> findDeptForDept(Dept dept, QueryRequest request);

    List<Dept> findDepts(Dept dept, QueryRequest request);

    List<Dept> findDept(Dept dept, QueryRequest request);

    void createDept(Dept dept);

    void updateDept(Dept dept);

    void deleteDepts(String[] deptIds);

    List<Dept> findChilderDept(String id);

    /**
     * 查询部门树和user
     *
     * @param request
     * @param dept
     * @return
     */
    Tree<Dept> findDeptUser(QueryRequest request, Dept dept);

    List<Address> getAddress(String id);


    List<Dept> getList(String parentId);

    List<Dept> getChildrenList(String parentId);

    void getAllIds(List<String> prentIds, List<String> list); //子集极其附属子集

    Dept getDept();//获取当前登陆人 部门

    List<Dept> getDeptByName(String deptName);//通过名字查询对应的 部门

    String seekDeptId(String startDeptId, String endDeptId);// 散列  低位已知   高位已知  得高位到低位的直属下级

    boolean affiliate(String prentId, String chileId); //子类id是否附属 父类 是返回true


    List<Dept> queryDeptChile(String prentId); //获得本级一下 组织机构

    List<Dept> queryDeptChileNotshiro(String prentId); //无权限

     List<Dept> getDepts(String deptId);


}
