package com.jiebao.platfrom.accident.controller;


import com.jiebao.platfrom.accident.daomain.Accident;
import com.jiebao.platfrom.accident.daomain.CompareTable;
import com.jiebao.platfrom.accident.service.IAccidentService;
import com.jiebao.platfrom.accident.service.IDeptService;
import com.jiebao.platfrom.common.annotation.Log;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.utils.ExportExcel;
import com.jiebao.platfrom.system.dao.DeptMapper;
import com.jiebao.platfrom.system.dao.DictMapper;
import com.jiebao.platfrom.system.service.DeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qta
 * @since 2020-08-04
 */
@RestController
@RequestMapping("/accident/accident")
@Api(tags = "accident-事故信息总表")
public class AccidentController {
    @Autowired
    IAccidentService accidentService;
    @Autowired
    IDeptService deptService;
    @Autowired
    DeptService deptServices;
    @Autowired
    DeptMapper deptMapper;
    @Autowired
    DictMapper dictMapper;


    @PostMapping("saveOrUpdate")
    @ApiOperation("添加修改 事故信息")
    @Log("添加修改 事故信息")
    public JiebaoResponse saveOrUpdate(Accident accident) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();

        if (accident.getPoliceId() != null) {
            if (deptServices.getById(accident.getPoliceId()).getRank() == 4) {
                return jiebaoResponse.failMessage("请选中对应的派出所");
            }
            accident.setPoliceFather(deptServices.getById(accident.getPoliceId()).getParentId());
        }
        if (accident.getAccidentId() != null) {
            Accident accident1 = accidentService.getById(accident.getAccidentId()); //数据库已存在德
            if (accident1.getStatu() != null && accident1.getStatu() == 1)
                return jiebaoResponse.failMessage("已锁定不可操作");
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
            try {
                accident.setMonth(simpleDateFormat.format(simpleDateFormat.parse(accident.getDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        jiebaoResponse = accidentService.saveOrUpdate(accident) ? jiebaoResponse.okMessage("操作成功") : jiebaoResponse.failMessage("操作失败");
        return jiebaoResponse;
    }

    @DeleteMapping("deleteByLists/{lists}")
    @ApiOperation("集合删除事故信息")
    @Log("集合删除事故信息")
    public JiebaoResponse deleteByLists(@PathVariable String[] lists) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        jiebaoResponse = accidentService.removeByIds(Arrays.asList(lists)) ? jiebaoResponse.okMessage("删除成功") : jiebaoResponse.failMessage("删除失败");
        return jiebaoResponse;
    }

    @GetMapping("listPage")
    @ApiOperation("条件查询分页  事故信息")
    @Log("条件查询分页  事故信息")
    public JiebaoResponse list(QueryRequest queryRequest, String policeId, String cityLevelId, String quDeptId, String startDate, String endDate) {
        return accidentService.list(queryRequest, policeId, cityLevelId, quDeptId, startDate, endDate);
    }

    @GetMapping("map")
    @ApiOperation("获取地图展示数据")
    @Log("获取地图展示数据")
    public JiebaoResponse map(String policeId, String cityLevelId, String startDate, String endDate, String quDeptId) {
        return accidentService.map(policeId, cityLevelId, startDate, endDate, quDeptId);
    }


    @PostMapping("lock")
    @ApiOperation("是否锁定")
    public JiebaoResponse lock(String[] accidentId, String month, Integer status) {
        return accidentService.lock(accidentId, month, status);
    }

    @PostMapping("func")
    @ApiOperation("系数求出")
    public JiebaoResponse func(String nature, String instationSection, String road, String age, String closed, String jzd, String distance, String identity, String conditions) {
        return new JiebaoResponse().data(accidentService.func(nature, instationSection, road, age, closed, jzd, distance, identity, conditions)).okMessage("操作成功");
    }

    @GetMapping("compareTable")
    @ApiOperation("同期比较")
    public JiebaoResponse compareTable(String startYear, String endYear) {
        return new JiebaoResponse().okMessage("操作成功").data(accidentService.compareTable(startYear, endYear));
    }

    @GetMapping("compareTableExcel")
    @ApiOperation("同期比较导出")
    public void compareTableImpot(String startYear, String endYear, HttpServletResponse response) {
        ExportExcel.exportExcelList(accidentService.compareTable(startYear, endYear), CompareTable.class, response);
    }


//    @GetMapping("sort")
//    @ApiOperation("涉铁案导入")
//    public void sort() {
//        List<Zaajqk> list = zaajqkService.list();
//        List<Case> arrayList = new ArrayList<>();
//        for (Zaajqk lwswfx : list) {
//            Case aCase = new Case();
//            try {
//                aCase.setCityCsId(deptMapper.s1(lwswfx.getFsdsz().substring(0,2)));
//                aCase.setCityQxId(deptMapper.s2(lwswfx.getFsxsdq().substring(0,2)));
//                aCase.setCityXc(deptMapper.s3(lwswfx.getFsxsdq().substring(0,2)));
//                aCase.setPoliceId(deptMapper.s4(lwswfx.getFsxsdq().substring(0,2)));
//                aCase.setLineId(dictMapper.s1(lwswfx.getXl().substring(0,2)));
//            }catch (Exception e){
//
//            }
//            aCase.setMonth(lwswfx.getTjyf().replace(".","-"));
//            try {
//                aCase.setDate((lwswfx.getTjyf()+lwswfx.getFssj().substring(2,11)+":00").replace(".","-"));
//            }catch (Exception e){
//
//            }
//            aCase.setAddress(lwswfx.getFsdd());
//            aCase.setStatu(1);
//            String na = null;
//            String ajmc = lwswfx.getAjmc();
//            if (ajmc != null) {
//                if (ajmc.equals("击打列车")) {
//                    na = "0";
//                } else if (ajmc.equals("摆放障碍")) {
//                    na = "1";
//                } else if (ajmc.equals("提车钩")) {
//                    na = "2";
//                } else if (ajmc.equals("关塞门")) {
//                    na = "3";
//                } else if (ajmc.equals("拆盗掀盗")) {
//                    na = "4";
//                } else if (ajmc.equals("耕牛肇事")) {
//                    na = "5";
//                } else if (ajmc.equals("拦车断刀")) {
//                    na = "6";
//                } else if (ajmc.equals("爆炸破坏")) {
//                    na = "7";
//                } else if (ajmc.equals("机动车肇事")) {
//                    na = "8";
//                } else if (ajmc.equals("高铁护网进入")) {
//                    na = "9";
//                } else if (ajmc.equals("高铁非正常停车")) {
//                    na = "10";
//                } else {
//                    na = "11";
//                }
//            }
//            aCase.setNature(na);
//            if(lwswfx.getTcsj()!=null){
//                try {
//                    aCase.setDateLength(Double.valueOf(lwswfx.getTcsj()));
//                }catch (Exception e){
//
//                }
//            }
//
//            String stata = null;
//            String ajzt = lwswfx.getAjzt();
//            if (ajzt != null) {
//                if (ajzt.equals("未破案")) {
//                    stata = "0";
//                }
//                if (ajzt.equals("已破案")) {
//                    stata = "1";
//                }
//            }
//            aCase.setStatus(stata);
//            String fb = null;
//            String fbzt = lwswfx.getFbzt();
//            if (fbzt != null) {
//                if (fbzt.equals("全封闭")) {
//                    fb = "0";
//                }
//                if (fbzt.equals("因社会管理原因造栅栏开口")) {
//                    fb = "1";
//                }
//                if (fbzt.equals("因铁路原因造成栅栏开口")) {
//                    fb = "2";
//                }
//                if (fbzt.equals("未封闭")) {
//                    fb = "3";
//                }
//            }
//            aCase.setFbStatus(fb);
//            aCase.setMs(lwswfx.getAjqk());
//         arrayList.add(aCase);
//        }
//        for (Object s : arrayList) {
//            System.out.println(s);
//        }
//        caseService.saveBatch(arrayList);
//    }
}
