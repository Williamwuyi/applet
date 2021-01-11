package com.jiebao.platfrom.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.common.authentication.JWTUtil;
import com.jiebao.platfrom.common.domain.JiebaoConstant;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.domain.Tree;
import com.jiebao.platfrom.common.utils.SortUtil;
import com.jiebao.platfrom.common.utils.TreeUtil;
import com.jiebao.platfrom.railway.dao.AddressMapper;
import com.jiebao.platfrom.railway.domain.Address;
import com.jiebao.platfrom.system.dao.DeptMapper;
import com.jiebao.platfrom.system.dao.UserMapper;
import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.domain.User;
import com.jiebao.platfrom.system.service.DeptService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Array;
import java.util.*;

@Slf4j
@Service("deptService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {


    @Autowired
    UserMapper userMapper;

    @Autowired
    DeptMapper deptMapper;

    @Autowired
    AddressMapper addressMapper;

    @Override
    public Map<String, Object> findDepts(QueryRequest request, Dept dept) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Dept> depts = findDepts(dept, request);
            List<Tree<Dept>> trees = new ArrayList<>();
            buildTrees(trees, depts);
            Tree<Dept> deptTree = TreeUtil.build(trees);

            result.put("rows", deptTree);
            result.put("total", depts.size());
        } catch (Exception e) {
            log.error("获取组织机构列表失败", e);
            result.put("rows", null);
            result.put("total", 0);
        }
        return result;
    }



    @Override
    public List<Dept> findDepts(Dept dept, QueryRequest request) {
        QueryWrapper<Dept> queryWrapper = new QueryWrapper<>();

        if (StringUtils.isNotBlank(dept.getDeptId())) {
            queryWrapper.lambda().eq(Dept::getParentId, dept.getDeptId());
        }
        if (StringUtils.isNotBlank(dept.getDeptName())) {
            queryWrapper.lambda().eq(Dept::getDeptName, dept.getDeptName());
        }
        if (dept.getRank() !=null ){
            queryWrapper.lambda().eq(Dept::getRank, dept.getRank());
        }
        if (dept.getStatus()!=null && !"".equals(dept.getStatus())){
            queryWrapper.lambda().eq(Dept::getStatus,dept.getStatus());
        }

        if (StringUtils.isNotBlank(dept.getCreateTimeFrom()) && StringUtils.isNotBlank(dept.getCreateTimeTo()))
            queryWrapper.lambda()
                    .ge(Dept::getCreateTime, dept.getCreateTimeFrom())
                    .le(Dept::getCreateTime, dept.getCreateTimeTo());
        SortUtil.handleWrapperSort(request, queryWrapper, "orderNum", JiebaoConstant.ORDER_ASC, true);
        return this.baseMapper.selectList(queryWrapper);
    }


    @Override
    public Map<String, Object> findCityDepts(QueryRequest request, Dept dept) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Dept> depts = findCityDepts(dept, request);
            List<Tree<Dept>> trees = new ArrayList<>();
            buildTrees(trees, depts);
            Tree<Dept> deptTree = TreeUtil.build(trees);

            result.put("rows", deptTree);
            result.put("total", depts.size());
        } catch (Exception e) {
            log.error("获取组织机构列表失败", e);
            result.put("rows", null);
            result.put("total", 0);
        }
        return result;
    }



    public List<Dept> findCityDepts(Dept dept, QueryRequest request) {
        QueryWrapper<Dept> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Dept::getStatus, 1);
        if (StringUtils.isNotBlank(dept.getDeptId())) {
            queryWrapper.lambda().eq(Dept::getParentId, dept.getDeptId());
        }
        if (StringUtils.isNotBlank(dept.getDeptName())) {
            queryWrapper.lambda().eq(Dept::getDeptName, dept.getDeptName());
        }
        if (dept.getRank() !=null ){
            queryWrapper.lambda().eq(Dept::getRank, dept.getRank());
        }

        if (StringUtils.isNotBlank(dept.getCreateTimeFrom()) && StringUtils.isNotBlank(dept.getCreateTimeTo()))
            queryWrapper.lambda()
                    .ge(Dept::getCreateTime, dept.getCreateTimeFrom())
                    .le(Dept::getCreateTime, dept.getCreateTimeTo());
        SortUtil.handleWrapperSort(request, queryWrapper, "orderNum", JiebaoConstant.ORDER_ASC, true);
        return this.baseMapper.selectList(queryWrapper);
    }





    @Override
    public List<Dept> findDeptForDept(Dept dept, QueryRequest request) {
        QueryWrapper<Dept> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Dept::getStatus, 1);
        if (StringUtils.isNotBlank(dept.getDeptId())) {
            queryWrapper.lambda().eq(Dept::getParentId, dept.getDeptId());
        }
        if (StringUtils.isNotBlank(dept.getDeptName())) {
            queryWrapper.lambda().eq(Dept::getDeptName, dept.getDeptName());
        }
        if (StringUtils.isNotBlank(dept.getCreateTimeFrom()) && StringUtils.isNotBlank(dept.getCreateTimeTo())) {
            queryWrapper.lambda()
                    .ge(Dept::getCreateTime, dept.getCreateTimeFrom())
                    .le(Dept::getCreateTime, dept.getCreateTimeTo());
        }
        if (dept.getDeptId() == null || dept.getDeptId() == "" || dept.getDeptName() == null || dept.getDeptName() == "" || dept.getCreateTimeFrom() == null || dept.getCreateTimeFrom() == "" || dept.getCreateTimeTo() == null || dept.getCreateTimeTo() == "") {
            queryWrapper.lambda().ne(Dept::getRank, 2);
            queryWrapper.lambda().ne(Dept::getRank, 3);
        }
        SortUtil.handleWrapperSort(request, queryWrapper, "orderNum", JiebaoConstant.ORDER_ASC, true);
        return this.baseMapper.selectList(queryWrapper);
    }


    @Override
    public List<Dept> findDept(Dept dept, QueryRequest request) {
        QueryWrapper<Dept> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(dept.getDeptName()))
            queryWrapper.lambda().eq(Dept::getDeptName, dept.getDeptName());
        if (StringUtils.isNotBlank(dept.getCreateTimeFrom()) && StringUtils.isNotBlank(dept.getCreateTimeTo()))
            queryWrapper.lambda()
                    .ge(Dept::getCreateTime, dept.getCreateTimeFrom())
                    .le(Dept::getCreateTime, dept.getCreateTimeTo());
        SortUtil.handleWrapperSort(request, queryWrapper, "orderNum", JiebaoConstant.ORDER_ASC, true);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public void createDept(Dept dept) {
        String parentId = dept.getParentId();
        // if (parentId == null)
        if (StringUtils.isEmpty(parentId))
            dept.setParentId("0");
        dept.setCreateTime(new Date());
        this.save(dept);
    }

    @Override
    @Transactional
    public void updateDept(Dept dept) {
        dept.setModifyTime(new Date());
        this.baseMapper.updateById(dept);
    }

    @Override
    @Transactional
    public void deleteDepts(String[] deptIds) {
        Arrays.stream(deptIds).forEach(deptId -> this.baseMapper.deleteDepts(deptId));
    }

    @Override
    public List<Dept> findChilderDept(String id) {
        LambdaQueryWrapper<Dept> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Dept::getParentId, id);
        return this.baseMapper.selectList(lambdaQueryWrapper);
    }

    private void buildTrees(List<Tree<Dept>> trees, List<Dept> depts) {
        depts.forEach(dept -> {
            Tree<Dept> tree = new Tree<>();
            tree.setId(dept.getDeptId());
            tree.setKey(tree.getId());
            tree.setParentId(dept.getParentId());
            tree.setText(dept.getDeptName());
            tree.setCreateTime(dept.getCreateTime());
            tree.setModifyTime(dept.getModifyTime());
            tree.setOrder(dept.getOrderNum());
            tree.setTitle(tree.getText());
            tree.setValue(tree.getId());
            trees.add(tree);
        });
    }

    @Override
    public Tree<Dept> findDeptUser(QueryRequest request, Dept dept) {
        List<Dept> depts = findDepts(dept, request);
        List<Tree<Dept>> trees = new ArrayList<>();
        buildDeptUserTrees(trees, depts);
        Tree<Dept> build = TreeUtil.build(trees);
        return build;
    }

    private void buildDeptUserTrees(List<Tree<Dept>> trees, List<Dept> depts) {
        depts.forEach(dept -> {
            Tree<Dept> tree = new Tree<>();
            tree.setId(dept.getDeptId());
            tree.setKey(tree.getId());
            tree.setParentId(dept.getParentId());
            tree.setText(dept.getDeptName());
            tree.setCreateTime(dept.getCreateTime());
            tree.setModifyTime(dept.getModifyTime());
            tree.setOrder(dept.getOrderNum());
            tree.setTitle(tree.getText());
            tree.setValue(tree.getId());

            List<User> user = userMapper.getUserList(dept.getDeptId());
            List<Tree<Dept>> childList = new ArrayList<>();
            for (int i = 0; i < user.size(); i++) {
                Tree<Dept> data = new Tree<>();
                User info = user.get(i);
                data.setId(info.getUserId());
                data.setTitle(info.getUsername());
                childList.add(data);
            }
            tree.setChildren(childList);
            trees.add(tree);
        });
    }


    private List getAllId(String id, List list) {
        //根据父ID查底下的子节点
        Map<String, Object> map = new HashMap<>();
        map.put("parent_id", id);
        List<Dept> depts = deptMapper.selectByMap(map);
        if (null != depts) {
            //如果底下有子节点-将子节点的ID加入list中
            for (int i = 0; i < depts.size(); i++) {
                list.add(depts.get(i).getDeptId());
                //重新调用自身  -根据子节点的id和list
                list = getAllId(depts.get(i).getDeptId(), list);
            }
        }
        //返回list
        return list;
    }


    @Override
    public List<Address> getAddress(String id) {
        List<String> allId = getDeptIds(id);
        List<Address> addresses = new ArrayList<>();
        for (String li : allId
        ) {
            Map<String, Object> map = new HashMap<>();
            map.put("dept_id", li);
            addresses.addAll(addressMapper.selectByMap(map));
        }
        return addresses;
    }

    @Override
    public List<Dept> getList(String parentId) {
        LambdaQueryWrapper<Dept> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Dept::getParentId, parentId);
        lambdaQueryWrapper.eq(Dept::getRank, 1);
        lambdaQueryWrapper.orderByAsc(Dept::getOrderNum);
        return this.baseMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<Dept> getChildrenList(String parentId) {
        LambdaQueryWrapper<Dept> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Dept::getParentId, parentId);
        lambdaQueryWrapper.orderByAsc(Dept::getOrderNum);
        return this.baseMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public void getAllIds(List<String> prentIds, List<String> list) {
        //根据父ID查底下的子节点
        QueryWrapper<Dept> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("parent_id", prentIds);
        List<Dept> list1 = list(queryWrapper);
        prentIds.clear();//清空
//        List<String> list2 = new ArrayList<>();  //存储每一级的  dept
        if (null != list1 && list1.size() > 0) {
            //如果底下有子节点-将子节点的ID加入list中
            for (Dept dept : list1
            ) {
                String deptId = dept.getDeptId();
                list.add(deptId);
                prentIds.add(deptId);
            }
            getAllIds(prentIds, list);
        }
        return;
    }

    @Override
    public Dept getDept() {
        String username = JWTUtil.getUsername(SecurityUtils.getSubject().getPrincipal().toString());
        return getById(userMapper.getDeptID(username));
    }

    @Override
    public List<Dept> getDeptByName(String deptName) {
        QueryWrapper<Dept> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dept_name", deptName);
        return list(queryWrapper);
    }

    @Override
    public String seekDeptId(String startDeptId, String endDeptId) {
        Dept dept = getById(startDeptId);
        String parentId = dept.getParentId();
        if (parentId.equals(endDeptId)) {
            return startDeptId;
        } else {
            return seekDeptId(parentId, endDeptId);
        }
    }

    @Override
    public boolean affiliate(String prentId, String chileId) {
        Dept dept = getById(chileId);
        if (dept == null) {
            return true;
        }
        if (dept.getParentId().equals(prentId)) {  //属于子类
            return true;
        } else if (dept.getDeptId().equals("0")) { //直接到最高级  则 没有
            return false;
        } else {

            return affiliate(prentId, dept.getParentId());  //找到最优答案
        }
    }


    @Override
    public List<Dept> queryDeptChile(String prentId) {
        Dept dept = getDept();
        if (dept.getParentId() != null) {
            Dept byId = deptMapper.getById(dept.getParentId());
            if (byId != null) {
                dept.setParentDeptName(byId.getDeptName());
            }
        }
        if (!dept.getDeptId().equals(prentId) && prentId != null) {//不相等则  进行查看
            if (!affiliate(dept.getDeptId(), prentId)) //不属于
                return null;
        }
        List<Dept> childrenList = new ArrayList<>();
        if (prentId == null && dept.getDeptId().equals("0"))
            childrenList = getChildrenList("-1");
        else if (prentId == null)
            childrenList.add(dept);
        else if (prentId != null) {
            List<Dept> childrenList1 = getChildrenList(prentId);
            if (childrenList1 != null)
                childrenList.addAll(childrenList1);
        }
        return childrenList;
    }

    @Override
    public List<Dept> queryDeptChileNotshiro(String prentId) {
        if (prentId == null) {
            prentId = "-1";
        }
        return getChildrenList(prentId);
    }


    public List getDeptIds(String id) {
        List list = new ArrayList();
        list.add(id); // 加上本身
        list = getAllId(id, list);
        return list;
    }




    public List<Dept> getChildren(String id, List<Dept> list) {
        //根据父ID查底下的子节点
        Map<String, Object> map = new HashMap<>();
        map.put("parent_id", id);
        List<Dept> depts = deptMapper.selectByMap(map);
        if (null != depts) {
            //如果底下有子节点-将子节点的ID加入list中
            for (int i = 0; i < depts.size(); i++) {
                list.add(depts.get(i));
                //重新调用自身  -根据子节点的id和list
                list = getChildren(depts.get(i).getDeptId(), list);
            }
        }
        //返回list
        return list;
    }


    @Override
    public List<Dept> getDepts(String deptId) {
        List<Dept> list = new ArrayList();
        Dept byId = deptMapper.getById(deptId);
        list.add(byId); // 加上本身
        list = getChildren(deptId, list);
        return list;
    }

   /* private List getId(String id,List list){
        List<Dept> forList = deptMapper.getChildId(id);
        for(Dept sysDept : forList){
            list.add(sysDept.getDeptId());
            list = getId(sysDept.getDeptId(),list);
        }
        return list;
    }
*/

}
