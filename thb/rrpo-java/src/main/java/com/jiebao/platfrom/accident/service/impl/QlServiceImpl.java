package com.jiebao.platfrom.accident.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiebao.platfrom.accident.daomain.Jk;
import com.jiebao.platfrom.accident.daomain.Ql;
import com.jiebao.platfrom.accident.dao.QlMapper;
import com.jiebao.platfrom.accident.service.IQlService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.utils.ExportExcel;
import com.jiebao.platfrom.system.dao.FileMapper;
import com.jiebao.platfrom.system.domain.File;
import com.jiebao.platfrom.system.service.FileService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collection;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qta
 * @since 2020-11-07
 */
@Service
public class QlServiceImpl extends ServiceImpl<QlMapper, Ql> implements IQlService {
    @Autowired
    FileService fileService;

    @Override
    public IPage<Ql> listPage(QueryRequest queryRequest, String deptName, String policeName, String gwdName,String qxDeptName) {
        Page<Ql> page = new Page<>(queryRequest.getPageNum(), queryRequest.getPageSize());
        return this.baseMapper.pageList(page, queryWrapper(deptName, policeName, gwdName,qxDeptName));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean importExcel(HttpServletResponse response, String deptName, String policeName, String gwdName,String qxDeptName) {
        return ExportExcel.exportExcelList(list(queryWrapper(deptName, policeName, gwdName,qxDeptName)), Ql.class, response);
    }

    private LambdaQueryWrapper<Ql> queryWrapper(String deptName, String policeName, String gwdName,String qxDeptName) {
        LambdaQueryWrapper<Ql> queryWrapper = new LambdaQueryWrapper<>();
        if (deptName != null)
            queryWrapper.eq(Ql::getDzs, deptName);
        if (policeName != null)
            queryWrapper.eq(Ql::getGa, policeName);
        if (gwdName != null)
            queryWrapper.eq(Ql::getGwd, gwdName);
        if (qxDeptName != null) {
            queryWrapper.eq(Ql::getXsq, qxDeptName);
        }
        queryWrapper.eq(Ql::getDelflag, "0");
        queryWrapper.orderByDesc(Ql::getCreatTime);
        queryWrapper.orderByDesc(Ql::getDzs);
        queryWrapper.orderByDesc(Ql::getGa);
        queryWrapper.orderByDesc(Ql::getGwd);
        queryWrapper.orderByDesc(Ql::getXsq);
        queryWrapper.orderByDesc(Ql::getXz);
        return queryWrapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addOrUpdate(Ql ql, Collection<? extends Serializable> tp) {
        boolean flag = false;  //记录是新增 还是  删除
        if (ql.getId() == null) {
            ql.setCreatTime(LocalDateTime.now());
            ql.setDelflag("0");
            flag = true;
        }
        boolean b = saveOrUpdate(ql);
        if (tp != null && flag) {  //绑定文件
            LambdaUpdateWrapper<File> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(File::getRefId, ql.getId());
            updateWrapper.in(File::getFileId, tp);
            fileService.update(updateWrapper);
        }
        return b;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(String[] idList) {
        LambdaUpdateWrapper<Ql> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Ql::getDelflag, 1);
        updateWrapper.in(Ql::getId, Arrays.asList(idList));
        return update(updateWrapper);
    }


}


