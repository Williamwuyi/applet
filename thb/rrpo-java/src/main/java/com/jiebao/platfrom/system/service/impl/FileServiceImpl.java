package com.jiebao.platfrom.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.common.domain.JiebaoConstant;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.utils.SortUtil;
import com.jiebao.platfrom.railway.domain.Prize;
import com.jiebao.platfrom.system.dao.FileMapper;
import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.domain.File;
import com.jiebao.platfrom.system.service.DeptService;
import com.jiebao.platfrom.system.service.FileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("fileService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

    @Autowired
    private DeptService deptService;

    @Override
    public List<File> getAppendixList(String refId) {
        LambdaQueryWrapper<File> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(File::getRefId, refId);
        lambdaQueryWrapper.eq(File::getFileType, "2");
        lambdaQueryWrapper.orderByDesc(File::getTime);
        return this.baseMapper.selectList(lambdaQueryWrapper);
    }


    @Override
    public List<File> getAppendixListReply(String refId) {
        LambdaQueryWrapper<File> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(File::getRefId, refId);
        lambdaQueryWrapper.eq(File::getFileType, "2");
        lambdaQueryWrapper.eq(File::getRefType,"1");
        lambdaQueryWrapper.orderByDesc(File::getTime);
        return this.baseMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<File> getAppendixListForEX(String refId) {
        LambdaQueryWrapper<File> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(File::getRefId, refId);
        lambdaQueryWrapper.eq(File::getFileType, "2");
        lambdaQueryWrapper.eq(File::getRefType,"7");
        lambdaQueryWrapper.orderByDesc(File::getTime);
        return this.baseMapper.selectList(lambdaQueryWrapper);
    }




    @Override

    public IPage<File> getFileList(QueryRequest request, File file, String startTime, String endTime) {
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        Dept dept = deptService.getDept();
        if ("0".equals(dept.getDeptId())){
            queryWrapper.lambda().eq(File::getRefType, "10");
            if (StringUtils.isNotBlank(file.getOldName())) {
                queryWrapper.lambda().like(File::getOldName, file.getOldName());
            }
            if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
                queryWrapper.lambda().ge(File::getTime, startTime).le(File::getTime, endTime);
            }
            Page<File> page = new Page<>(request.getPageNum(), request.getPageSize());
            SortUtil.handleWrapperSort(request, queryWrapper, "time", JiebaoConstant.ORDER_DESC, true);
            return this.baseMapper.selectPage(page, queryWrapper);
        }
        else {
            queryWrapper.lambda().eq(File::getRefType, "10");
            queryWrapper.lambda().eq(File::getIfHaveMoney,"0");
            if (StringUtils.isNotBlank(file.getOldName())) {
                queryWrapper.lambda().like(File::getOldName, file.getOldName());
            }
            if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
                queryWrapper.lambda().ge(File::getTime, startTime).le(File::getTime, endTime);
            }
            Page<File> page = new Page<>(request.getPageNum(), request.getPageSize());
            SortUtil.handleWrapperSort(request, queryWrapper, "time", JiebaoConstant.ORDER_DESC, true);
            return this.baseMapper.selectPage(page, queryWrapper);
        }

    }

    @Override
    public Integer deleteByRefIds(List<String> refIds) {
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("ref_id", refIds);
        return this.baseMapper.deleteByRefId(queryWrapper);
    }

}
