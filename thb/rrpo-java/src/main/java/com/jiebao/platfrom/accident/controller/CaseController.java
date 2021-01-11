package com.jiebao.platfrom.accident.controller;


import com.jiebao.platfrom.accident.daomain.Case;
import com.jiebao.platfrom.accident.daomain.Lwswfx;
import com.jiebao.platfrom.accident.service.ICaseService;
import com.jiebao.platfrom.accident.service.ILwswfxService;
import com.jiebao.platfrom.common.annotation.Log;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.system.service.DeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qta
 * @since 2020-08-05
 */
@RestController
@RequestMapping("/accident/case")
@Api(tags = "accident-设铁案")
public class CaseController {
    @Autowired
    ICaseService caseService;
    @Autowired
    DeptService deptServices;
    @Autowired
    ILwswfxService lwswfxService;

    @PostMapping("saveOrUpdate")
    @ApiOperation("添加修改 事故信息")
    @Log("添加修改 事故信息")
    public JiebaoResponse saveOrUpdate(Case ca) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        if (ca.getPoliceId() != null) {
            if (deptServices.getById(ca.getPoliceId()).getRank() == 4) {
                return jiebaoResponse.failMessage("请选中对应的派出所");
            }
            //   accident.setPoliceFather(deptServices.getById(accident.getPoliceId()).getParentId());
        }
        if (ca.getCaseId() != null) {
            Case aCase = caseService.getById(ca.getCaseId());
            if (aCase.getStatu() != null && aCase.getStatu() == 1)
                return jiebaoResponse.failMessage("已锁定不可更改");
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
            try {
                ca.setMonth(simpleDateFormat.format(simpleDateFormat.parse(ca.getDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        jiebaoResponse = caseService.saveOrUpdate(ca) ? jiebaoResponse.okMessage("操作成功") : jiebaoResponse.failMessage("操作失败");
        return jiebaoResponse;
    }

    @DeleteMapping("deleteByLists/{lists}")
    @ApiOperation("集合删除事故信息")
    @Log("集合删除事故信息")
    public JiebaoResponse deleteByLists(@PathVariable String[] lists) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        jiebaoResponse = caseService.removeByIds(Arrays.asList(lists)) ? jiebaoResponse.okMessage("删除成功") : jiebaoResponse.failMessage("删除失败");
        return jiebaoResponse;
    }

    @GetMapping("listPage")
    @ApiOperation("条件查询分页  事故信息")
    @Log("条件查询分页  事故信息")
    public JiebaoResponse list(QueryRequest queryRequest, String policeId, String cityLevelId, String quDeptId, String lineId, String nature, String startDate, String endDate) {
        return caseService.list(queryRequest, policeId, cityLevelId, quDeptId, lineId, nature, startDate, endDate);
    }

    @GetMapping("map")
    @ApiOperation("获取地图展示数据")
    @Log("获取地图展示数据")
    public JiebaoResponse map(String startDate, String endDate, Integer status) {
        return caseService.map(startDate, endDate, status);
    }

    @PostMapping("lock")
    @ApiOperation("是否锁定")
    public JiebaoResponse lock(String[] caseId, String month, Integer status) {
        return caseService.lock(caseId, month, status);
    }

    @PostMapping("sort")
    @ApiOperation("是否锁定")
    public JiebaoResponse sort(String[] caseId, String month, Integer status) {
        List<Case> list = new ArrayList<>();
        for (int i = 0; i < 168; i++) {
            Case aCase = new Case();
            if (i / 2 == 0) {
           aCase.setDate("2017-09-15 16:27:54");
           aCase.setCityCsId("1");
           aCase.setCityQxId("17");
           aCase.setCityXc("18");
           aCase.setLineId("e25de73dc523affa40b2723a80533a90");
           aCase.setNature("5");
           aCase.setDateLength(78.0);
           aCase.setStatu(1);
           aCase.setFbStatus("2");
           aCase.setContent("2");
           aCase.setRemark("2");
           aCase.setMs("2");
           aCase.setStatu(0);
           aCase.setMonth("2019-11");
            } else if (i / 3 == 0) {
                aCase.setDate("2017-09-15 16:27:54");
                aCase.setCityCsId("182");
                aCase.setCityQxId("205");
                aCase.setCityXc("207");
                aCase.setLineId("727273572a87fd89df80f377a15f6761");
                aCase.setNature("5");
                aCase.setDateLength(78.0);
                aCase.setStatu(1);
                aCase.setFbStatus("2");
                aCase.setContent("2");
                aCase.setRemark("2");
                aCase.setMs("2");
                aCase.setStatu(0);
                aCase.setMonth("2019-11");
            } else if (i/5==0){
                aCase.setDate("2017-09-15 16:27:54");
                aCase.setCityCsId("380");
                aCase.setCityQxId("402");
                aCase.setCityXc("404");
                aCase.setLineId("bbdaa864906c8c0ac0b18d502f559e7e");
                aCase.setNature("5");
                aCase.setDateLength(78.0);
                aCase.setStatu(1);
                aCase.setFbStatus("2");
                aCase.setContent("2");
                aCase.setRemark("2");
                aCase.setMs("2");
                aCase.setStatu(0);
                aCase.setMonth("2019-11");
            }else {
                aCase.setDate("2017-09-15 16:27:54");
                aCase.setCityCsId("1712");
                aCase.setCityQxId("1784");
                aCase.setCityXc("1791");
                aCase.setLineId("727273572a87fd89df80f377a15f6761");
                aCase.setNature("5");
                aCase.setDateLength(78.0);
                aCase.setStatu(1);
                aCase.setFbStatus("2");
                aCase.setContent("2");
                aCase.setRemark("2");
                aCase.setMs("2");
                aCase.setStatu(0);
                aCase.setMonth("2019-11");
            }
            list.add(aCase);
        }
        caseService.saveBatch(list);
        return null;
    }

    @GetMapping("sorts")
    @ApiOperation("监控")
    public JiebaoResponse sorts(){
        List<Lwswfx> list = lwswfxService.list();
       List<Case> caseArrayList = new ArrayList<>();
        for (Lwswfx lwswfx : list) {
            Case aCase = new Case();
            
        }
        return null;
    }
}
