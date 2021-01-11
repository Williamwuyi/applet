package com.jiebao.platfrom.wx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.common.authentication.JWTUtil;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.demo.test.WorderToNewWordUtils;
import com.jiebao.platfrom.system.dao.UserMapper;
import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.domain.File;
import com.jiebao.platfrom.system.service.DeptService;
import com.jiebao.platfrom.system.service.FileService;
import com.jiebao.platfrom.wx.dao.MonthMapper;
import com.jiebao.platfrom.wx.domain.Month;
import com.jiebao.platfrom.wx.domain.MonthMap;
import com.jiebao.platfrom.wx.service.IMonthService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qta
 * @since 2020-08-22
 */
@Service
@Component
public class MonthServiceImpl extends ServiceImpl<MonthMapper, Month> implements IMonthService {
    @Autowired
    DeptService deptService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    FileService fileService;
    private final String word1Url = "/usr/local/demo/month.docx";  //月度评选初选  地址
    private final String word2Url = "/usr/local/demo/month2.docx";  //月度评选  终选地址

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdate(Month entity) {
        if (entity.getWxMonthId() == null) {
            String username = JWTUtil.getUsername(SecurityUtils.getSubject().getPrincipal().toString());
            Dept dept = deptService.getById(userMapper.getDeptID(username));  //当前登陆人的部门
            entity.setJcDeptId(dept.getDeptId());
            entity.setShDeptId(dept.getDeptId());
            entity.setDate(new Date());
        }
        boolean b = super.saveOrUpdate(entity);
        if (b && entity.getFileIds() != null && entity.getFileIds().length != 0) {
            UpdateWrapper<File> updateWrapper = new UpdateWrapper<>();
            updateWrapper.in("file_id", Arrays.asList(entity.getFileIds()));
            updateWrapper.set("ref_id", entity.getWxMonthId());
            fileService.update(updateWrapper);
        }
        return b;
    }

    @Override
    public JiebaoResponse pageList(QueryRequest queryRequest, String month, Integer look, Integer status, String dptId, String year, String deptId) {  //status 2需要自己操作的   1正在走流程的   3  已经成功的
        QueryWrapper<Month> queryWrapper = new QueryWrapper<>();
        String username = JWTUtil.getUsername(SecurityUtils.getSubject().getPrincipal().toString());
        Dept dept = deptService.getById(userMapper.getDeptID(username));  //当前登陆人的部门

        List<String> list = new ArrayList<>();  //储存id
        List<String> listPrentId = new ArrayList<>();  //储存id
        if (deptId == null)
            deptId = dept.getDeptId();
        listPrentId.add(deptId);
        list.add(deptId);
        deptService.getAllIds(listPrentId, list);
        queryWrapper.in("jc_dept_id", list);
        if (month != null) {
            queryWrapper.eq("month", month);
        }
        if (status != null) {
            if (status == 1)
                queryWrapper.eq("status", 2);
            if (status == 2) {  //未审核需要审核的
                queryWrapper.eq("status", 2);
                queryWrapper.eq("sh_dept_id", dept.getDeptId());
            }
            if (status == 3)  //已通过
                queryWrapper.eq("status", 1);
            if (status == 4) {  //未上报
                queryWrapper.isNull("status");
                queryWrapper.eq("jc_dept_id", dept.getDeptId());
                queryWrapper.eq("sh_dept_id", dept.getDeptId());
            }
        }
        if (look != null) {
            queryWrapper.eq("look", look);
        }
        queryWrapper.orderByDesc("date");
        Page<Month> page = new Page<>(queryRequest.getPageNum(), queryRequest.getPageSize());
        return new JiebaoResponse().data(this.baseMapper.list(page, queryWrapper)).message("查询成功");
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse appear(String monthId, Integer status) {
        String username = JWTUtil.getUsername(SecurityUtils.getSubject().getPrincipal().toString());
        Dept dept = deptService.getById(userMapper.getDeptID(username));  //当前登陆人的部门
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        Month month = getById(monthId);
        if (!month.getShDeptId().equals(dept.getDeptId())) {
            return jiebaoResponse.failMessage("无权上报");
        }
        if (dept.getParentId().equals("0")) { //此为市级
            month.setSzDeptName(dept.getDeptName());//保存市级单位   做报表要用
        }
        if (status == 1) {  //赞成  //
            if (dept.getRank() == 0) {//省级
                month.setStatus(1);
            } else {  //非省级 继续上报
                month.setStatus(2);
                month.setShDeptId(dept.getParentId());
            }
        } else {
            month.setStatus(0);
        }
        updateById(month);
        return jiebaoResponse.okMessage("操作成功");
    }

    @Override
    public JiebaoResponse koran(String MonthId, Integer status) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        UpdateWrapper<Month> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("pre_status", status);
        updateWrapper.eq("wx_month_id", MonthId);
        jiebaoResponse = update(updateWrapper) ? jiebaoResponse.okMessage("操作成功") : jiebaoResponse.failMessage("操作失败");
        return jiebaoResponse;
    }

    @Override
    public JiebaoResponse year(Integer year) {
        QueryWrapper<Month> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("month", (year - 1) + "-11");
        queryWrapper.le("month", (year) + "-10");
        queryWrapper.eq("status", 1);
        queryWrapper.isNotNull("sz_dept_name");
        queryWrapper.groupBy("sz_dept_name");
        List<MonthMap> monthMaps = this.baseMapper.selectMap(queryWrapper);
        return new JiebaoResponse().data(monthMaps).okMessage("查询成功");
    }

    //区间查询
    @Override
    public List<MonthMap> yearSN(Date startDate, Date endDate) {
        QueryWrapper<Month> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("date", startDate);
        queryWrapper.le("date", endDate);
        queryWrapper.eq("status", 1);
        queryWrapper.isNotNull("sz_dept_name");
        queryWrapper.groupBy("sz_dept_name");
        return this.baseMapper.selectMap(queryWrapper);
    }


    @Override
    public JiebaoResponse monthDocx(HttpServletResponse response, String month) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
//        String inputUrl = GetResource.class.getClassLoader().getResource("month.docx").getPath();//模板位置
        Map<String, String> map = new HashMap<>();
        String forMat = forMat(month);
        map.put("month", forMat + "份全省乡镇街微信护路推荐汇总表");
        ArrayList<String[]> list = new ArrayList<>();
        List<Month> months = listByMonth(month, null);
//        if (months.size() == 0) {
//            return jiebaoResponse.failMessage("无数据可导出");
//        }
        for (Month Month : months
        ) {
            if (Month.getPreStatus() == null) {
                Month.setPreStatus(0);
            }
            list.add(new String[]{Month.getSerial().toString(), Month.getSzDeptName(), Month.getDeptJc().getDeptName(), Month.getFuContent(), Month.getPreStatus() == 1 ? "可入" : "不可入"});
        }
        return WorderToNewWordUtils.changWordMonth(response, word1Url, month, map, list) ? jiebaoResponse.okMessage("导出成功") : jiebaoResponse.failMessage("导出失败");
    }

    private List<Month> listByMonth(String month, Integer status) {
        QueryWrapper<Month> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("month", month);
        if (status != null)
            queryWrapper.eq("status", status);
        else
            queryWrapper.eq("sh_dept_id", "0");
        return this.baseMapper.listWord(queryWrapper);
    }

    @Override
    public JiebaoResponse monthDocxText(QueryRequest queryRequest, String month) {  //月报  直观图  非导出状态
        System.out.println(month);
        QueryWrapper<Month> queryWrapper = new QueryWrapper<>();
        if (month != null) {
            queryWrapper.eq("month", month);
        }
        queryWrapper.eq("status", 1);//已通过数据才有意义
        Page<Month> page = new Page<>(queryRequest.getPageNum(), queryRequest.getPageSize());
        return new JiebaoResponse().data(this.baseMapper.list(page, queryWrapper)).message("查询成功");
    }

    @Override
    public JiebaoResponse downDocxGood(HttpServletResponse response, String month, String number, String content) {  //优秀记录导出
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
//        String inputUrl =null;
//        try {
//            inputUrl= ResourceUtils.getFile("classpath:month2.docx").getPath();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        String inputUrl = GetResource.class.getClassLoader().getResource("month2.docx").getPath();//模板位置
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        String forMat = forMat(month);
        Map<String, String> map = new HashMap<>();  //参数
        map.put("number", number);
        map.put("content", content);
        map.put("date", simpleDateFormat.format(new Date()));
        map.put("monthText1", forMat + "份乡镇街优秀护路微信记录评选");
        map.put("monthText2", forMat + "份全省乡镇街铁路护路微信优秀记录");
        List<Month> months = listByMonth(month, 1);
//        if (months.size() == 0) {
//            return jiebaoResponse.failMessage("无数据可导出");
//        }
        List<String[]> list = new ArrayList<>();
        for (Month Month : months
        ) {
            System.out.println(Month);
            list.add(new String[]{Month.getSerial().toString(), Month.getSzDeptName(), Month.getDeptJc().getDeptName(), Month.getFuContent()});
        }
        return WorderToNewWordUtils.changWordMonth(response, word2Url, month, map, list) ? jiebaoResponse.okMessage("导出成功") : jiebaoResponse.failMessage("导出失败");
    }

    private String forMat(String month) {
        String format = month.substring(month.length() - 2);  //截取末尾
        switch (format) {
            case "01":
                format = "一月";
                break;
            case "02":
                format = "二月";
                break;
            case "03":
                format = "三月";
                break;
            case "04":
                format = "四月";
                break;
            case "05":
                format = "五月";
                break;
            case "06":
                format = "六月";
                break;
            case "07":
                format = "七月";
                break;
            case "08":
                format = "八月";
                break;
            case "09":
                format = "九月";
                break;
            case "10":
                format = "十月";
                break;
            case "11":
                format = "十一月";
                break;
            case "12":
                format = "十二月";
                break;
        }
        return format;
    }


    @Override
    public Month getById(Serializable id) {
        Month month = super.getById(id);
        month.setLook(1);
        updateById(month);
        month.setFileList(fileService.getAppendixList(month.getWxMonthId()));
        return month;
    }
}
