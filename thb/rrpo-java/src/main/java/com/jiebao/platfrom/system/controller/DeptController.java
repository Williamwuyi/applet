package com.jiebao.platfrom.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiebao.platfrom.common.annotation.Log;
import com.jiebao.platfrom.common.authentication.JWTUtil;
import com.jiebao.platfrom.common.controller.BaseController;
import com.jiebao.platfrom.common.domain.JiebaoConstant;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.domain.Tree;
import com.jiebao.platfrom.common.exception.JiebaoException;
import com.jiebao.platfrom.common.utils.SortUtil;
import com.jiebao.platfrom.railway.dao.AddressMapper;
import com.jiebao.platfrom.railway.domain.Address;
import com.jiebao.platfrom.railway.domain.Exchange;
import com.jiebao.platfrom.railway.domain.PrizeOpinion;
import com.jiebao.platfrom.system.dao.DeptMapper;
import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.domain.User;
import com.jiebao.platfrom.system.service.DeptService;
import com.jiebao.platfrom.system.service.UserService;
import com.wuwenze.poi.ExcelKit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Validated
@RestController
@RequestMapping("dept")
@Api(tags = "railWay-组织机构")   //swagger2 api文档说明示例
public class DeptController extends BaseController {

    private String message;

    @Autowired
    private DeptService deptService;

    @Autowired
    private UserService userService;

    @Autowired
    private DeptMapper deptMapper;


    @GetMapping("/queryDeptChileNotshiro")
    @ApiOperation("查找本级以及本级一下")
    public JiebaoResponse queryDeptChileNotshiro(String prentId) {
        return new JiebaoResponse().data(deptService.queryDeptChileNotshiro(prentId)).message("查询成功");
    }

    @GetMapping("/queryDeptChile")
    @ApiOperation("查找本级以及本级一下")
    public JiebaoResponse queryDeptChile(String prentId) {
        return new JiebaoResponse().data(deptService.queryDeptChile(prentId)).message("查询成功");
    }

    @GetMapping("/queryDeptChileByPrize")
    @ApiOperation("查找本级以及本级一下(一事一奖)")
    public JiebaoResponse queryDeptChileByPrize(String prentId) {
        Dept dept = deptService.getDept();
        Dept parentDept = deptService.getById(dept.getParentId());
        List<Dept> list = new ArrayList<>();
        if (dept.getRank() == 4 || parentDept.getRank() == 4) {
            if (prentId != null) {
                List<Dept> depts = deptService.queryDeptChileNotshiro(prentId);
                list = depts;
            } else {
                List<Dept> deptNameByAsc = deptMapper.getDeptNameByAsc();
                list = deptNameByAsc;
            }
        } else {
            List<Dept> depts = deptService.queryDeptChileNotshiro(prentId);
            list = depts;
        }
        return new JiebaoResponse().data(list).message("查询成功");
    }


    @GetMapping
    public Map<String, Object> deptList(QueryRequest request, Dept dept) {
        return this.deptService.findDepts(request, dept);
    }

    @GetMapping("/findAllCity")
    public Map<String, Object> findAllCity(QueryRequest request, Dept dept) {
        return this.deptService.findCityDepts(request, dept);

    }


    @GetMapping("/list")
    public List<Dept> List(QueryRequest request, Dept dept) {
        List<Dept> depts = deptService.findDepts(dept, request);
        for (Dept d : depts
        ) {
            String[] a = {};
            d.setChildren(a);
        }
        return depts;
    }


    @GetMapping("/ListGA")
    public List<Dept> ListGA(QueryRequest request, Dept dept) {
        List<Dept> depts = deptService.findDepts(dept, request);
        return depts;
    }

    @Log("新增组织机构")
    @PostMapping
    @RequiresPermissions("dept:add")
    public void addDept(@Valid Dept dept) throws JiebaoException {
        try {
            dept.setStatus(1);
            this.deptService.createDept(dept);
        } catch (Exception e) {
            message = "新增组织机构失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }

    @Log("删除组织机构")
    @DeleteMapping("/{deptIds}")
    @RequiresPermissions("dept:delete")
    public void deleteDepts(@NotBlank(message = "{required}") @PathVariable String deptIds) throws JiebaoException {
        try {
            String[] ids = deptIds.split(StringPool.COMMA);
            Arrays.stream(ids).forEach(id -> {
                List<Dept> list = this.deptService.findChilderDept(id);
                if (list.size() > 0) {
                    list.forEach(dept -> {
                        deptService.removeById(dept.getDeptId());
                    });
                }
                deptService.removeById(id);
            });
        } catch (Exception e) {
            message = "删除组织机构失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }

    @Log("修改组织机构")
    @PutMapping
    @RequiresPermissions("dept:update")
    public void updateDept(@Valid Dept dept) throws JiebaoException {
        try {
            this.deptService.updateDept(dept);
        } catch (Exception e) {
            message = "修改组织机构失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }

    @PostMapping("excel")
    @RequiresPermissions("dept:export")
    public void export(Dept dept, QueryRequest request, HttpServletResponse response) throws JiebaoException {
        try {
            List<Dept> depts = this.deptService.findDepts(dept, request);
            ExcelKit.$Export(Dept.class, response).downXlsx(depts, false);
        } catch (Exception e) {
            message = "导出Excel失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }


    @GetMapping("deptUser")
    public Tree<Dept> deptUserList(QueryRequest request, Dept dept) {
        return this.deptService.findDeptUser(request, dept);
    }

    @GetMapping("/getChildrenAddress")
    public List<Address> deptUserList(String id) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("start:" + simpleDateFormat.format(new Date()));
        List<Address> list = deptService.getAddress(id);
        System.out.println("end:" + simpleDateFormat.format(new Date()));
        return list;
    }


    @GetMapping("/findRank")
    @ApiOperation(value = "查询登录人的组织机构层级", notes = "查询登录人的组织机构层级", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse findRank() {
        String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
        User byName = userService.findByName(username);
        Dept byId = deptService.getById(byName.getDeptId());
        return new JiebaoResponse().data(byId).put("status", "200");
    }

    @GetMapping("/findRankFour")
    @ApiOperation(value = "查询所有公安处", notes = "查询所有公安处", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse findRankFour() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("rank", 4);
        List<Dept> depts = deptMapper.selectByMap(map);
        return new JiebaoResponse().data(depts).put("status", "200");
    }

    @GetMapping("/findCity")
    @ApiOperation(value = "查询所有市州", notes = "查询所有市州", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse findCity() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("rank", 3);
        List<Dept> depts = deptMapper.selectByMap(map);
        return new JiebaoResponse().data(depts).put("status", "200");
    }

    @GetMapping("/findProvince")
    @ApiOperation(value = "查询所有省级", notes = "查询所有省级", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse findProvince() {
        List<Dept> depts = deptMapper.selectRankZero();
        Dept dept = deptService.getDept();
        if (dept.getRank() == 0) {
            return new JiebaoResponse().data(depts).okMessage("查询成功");
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("dept_id", "0");
            List<Dept> list = deptMapper.selectByMap(map);
            return new JiebaoResponse().data(list).okMessage("查询成功");
        }
    }


    @GetMapping("/findSameDept")
    @ApiOperation(value = "查询同父级下所有组织机构", notes = "查询同父级下所有组织机构", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse findSameDept(String deptId) {
        Dept byId = deptService.getById(deptId);
        HashMap<String, Object> map = new HashMap<>();
        map.put("parent_id", byId.getParentId());
        List<Dept> depts = deptMapper.selectByMap(map);
        return new JiebaoResponse().data(depts).okMessage("查询成功");
    }


    @GetMapping("/findChildrenDept")
    @ApiOperation(value = "查询所有子节点", notes = "查询所有子节点", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse findChildrenDept(String deptId) {
        List<Dept> allId = deptService.getDepts(deptId);
        return new JiebaoResponse().data(allId).okMessage("查询成功");
    }


    @GetMapping("/findRankIfFour")
    @ApiOperation(value = "查询父级是否为公安处", notes = "查询父级是否为公安处", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse findRankIfFour() {
        Dept dept = deptService.getDept();
        Dept byId = deptService.getById(dept.getParentId());
        if (byId != null) {
            return new JiebaoResponse().data(byId.getRank()).okMessage("查询成功");
        } else {
            return new JiebaoResponse().failMessage("省级无上级机构");
        }
    }


    @GetMapping("/findRankOne")
    @ApiOperation(value = "查询所属市级", notes = "查询所属市级", response = JiebaoResponse.class, httpMethod = "GET")
    public Dept findRankOne(Dept dept) {
        if (dept.getRank() == 1 ) {
            return dept;
        }
        Dept tdept = depts().stream().filter(
                x -> Objects.equals(x.getDeptId(), dept.getParentId())
        ).findFirst().get();
        return findRankOne(tdept);
    }
    private List<Dept> depts() {
        return deptService.list();
    }
}
