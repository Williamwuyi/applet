package com.jiebao.platfrom.accident.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.accident.dao.DeptSMapper;
import com.jiebao.platfrom.accident.dao.JkMapper;
import com.jiebao.platfrom.accident.daomain.CountTable;
import com.jiebao.platfrom.accident.daomain.Jk;
import com.jiebao.platfrom.accident.service.IJkService;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.utils.ExportExcel;
import com.jiebao.platfrom.system.dao.DeptMapper;
import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.domain.File;
import com.jiebao.platfrom.system.service.DeptService;
import com.jiebao.platfrom.system.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qta
 * @since 2020-11-07
 */
@Service
public class JkServiceImpl extends ServiceImpl<JkMapper, Jk> implements IJkService {
    @Autowired
    FileService fileService;
    @Autowired
    DeptMapper deptMapper;
    @Autowired
    DeptService deptService;

    @Autowired
    DeptSMapper deptSMapper;

    @Override
    public IPage<Jk> listPage(QueryRequest queryRequest, String gac, String dzs, String xsq, String lineName, String year) {
        Page<Jk> page = new Page<>(queryRequest.getPageNum(), queryRequest.getPageSize());
        return this.baseMapper.pageList(page, queryWrapper(gac, dzs, xsq, lineName, year));
    }

    @Override
    public boolean importExcel(HttpServletResponse response, String gac, String dzs, String xsq, String lineName, String year) {
        return ExportExcel.exportExcelList(list(queryWrapper(gac, dzs, xsq, lineName, year)), Jk.class, response);
    }

    private LambdaQueryWrapper<Jk> queryWrapper(String gac, String dzs, String xsq, String lineName, String year) {
        LambdaQueryWrapper<Jk> queryWrapper = new LambdaQueryWrapper<>();
        if (gac != null)
            queryWrapper.eq(Jk::getGac, gac);
        if (dzs != null)
            queryWrapper.eq(Jk::getDzs, dzs);
        if (xsq != null) {
            queryWrapper.eq(Jk::getXsq, xsq);
        }
        if (lineName != null) {
            queryWrapper.eq(Jk::getXlmc, lineName);
        }
        if (year != null) {
            queryWrapper.eq(Jk::getAzsjn, year);
        }
        queryWrapper.eq(Jk::getDelflag, "0");
        queryWrapper.orderByDesc(Jk::getCreatTime);
        queryWrapper.orderByDesc(Jk::getGac);
        queryWrapper.orderByDesc(Jk::getXsq);
        queryWrapper.orderByDesc(Jk::getDzs);
        queryWrapper.orderByDesc(Jk::getXzjd);
        return queryWrapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addOrUpdate(Jk jk, Collection<? extends Serializable> tp) {
        boolean flag = false;  //记录是新增 还是  删除
        if (jk.getId() == null) {
            jk.setCreatTime(LocalDateTime.now());
            jk.setDelflag("0");
            flag = true;
        }
        boolean b = saveOrUpdate(jk);
        if (tp != null && flag) {  //绑定文件
            LambdaUpdateWrapper<File> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(File::getRefId, jk.getId());
            updateWrapper.in(File::getFileId, tp);
            fileService.update(updateWrapper);
        }
        return b;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(String[] idList) {
        LambdaUpdateWrapper<Jk> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Jk::getDelflag, 1);
        updateWrapper.in(Jk::getId, Arrays.asList(idList));
        return update(updateWrapper);
    }

    @Override
    public List<CountTable> countTable(String year, String xlmc) {
        List<CountTable> list = new ArrayList<>();
        Dept deptUser = deptService.getDept(); //登陆人所属部门
        if (deptUser.getRank()==0) {//市级  rank 混乱
            List<Dept> deptNameByAsc = deptMapper.getDeptNameByAsc();
            for (Dept dept : deptNameByAsc) {
                CountTable countTable = this.baseMapper.ByDeptNameDzs(dept.getDeptName(), year, xlmc);
                countTable.setName(dept.getDeptName());
                list.add(countTable);
            }
            List<Dept> deptNameByAscGz = deptMapper.getDeptNameByAscGz();
            for (Dept dept : deptNameByAscGz) {
                CountTable countTable = this.baseMapper.ByDeptNameGa(dept.getDeptName(), year, xlmc);
                countTable.setName(dept.getDeptName());
                list.add(countTable);
            }
        } else if (deptUser.getRank()==1) {
            List<Dept> childrenList = deptService.getChildrenList(deptUser.getDeptId());
            for (Dept dept : childrenList) {
                CountTable countTable = this.baseMapper.ByDeptNameXq(dept.getDeptName(), year, xlmc);
                countTable.setName(dept.getDeptName());
                list.add(countTable);
            }
        } else if (deptUser.getRank()==4) {
            Collection<Dept> childrenList = deptService.listByIds( deptSMapper.selectDeptIds(deptSMapper.selectIdByDeptId(deptUser.getDeptId())));
            for (Dept dept : childrenList) {
                CountTable countTable = this.baseMapper.ByDeptNameDzs(dept.getDeptName(), year, xlmc);
                countTable.setName(dept.getDeptName());
                list.add(countTable);
            }
        }
        return list;
    }
}
