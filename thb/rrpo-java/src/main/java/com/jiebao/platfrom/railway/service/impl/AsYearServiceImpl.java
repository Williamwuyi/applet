package com.jiebao.platfrom.railway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.common.domain.JiebaoConstant;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.utils.DateUtil;
import com.jiebao.platfrom.common.utils.SortUtil;
import com.jiebao.platfrom.railway.dao.AsYearMapper;
import com.jiebao.platfrom.railway.dao.PrizeLimitMapper;
import com.jiebao.platfrom.railway.domain.AsYear;
import com.jiebao.platfrom.railway.domain.PrizeLimit;
import com.jiebao.platfrom.railway.service.AsYearService;
import com.jiebao.platfrom.railway.service.PrizeLimitService;
import com.jiebao.platfrom.system.domain.File;
import com.jiebao.platfrom.system.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author yf
 */
@Slf4j
@Service("AsYearService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class AsYearServiceImpl extends ServiceImpl<AsYearMapper, AsYear> implements AsYearService {
    @Autowired
    FileService fileService;

    @Override
    public JiebaoResponse addAsYear(String gradeId, String[] ids) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        Date date = new Date();
        List<AsYear> list = new ArrayList<>();
        for (String id : ids
        ) {
            if (this.baseMapper.exist(gradeId, id) != null) {
                continue;
            }
            AsYear asYear = new AsYear();
            asYear.setType(2);
            asYear.setCreatTime(date);
            asYear.setGradeId(gradeId);
            asYear.setFileId(id);
            list.add(asYear);
        }
        return saveBatch(list) ? jiebaoResponse.okMessage("操作成功") : jiebaoResponse.failMessage("操作失败");
    }

    @Override
    public JiebaoResponse pagelist(QueryRequest queryRequest, String gradeId) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        return jiebaoResponse.okMessage("查询成功").data(this.baseMapper.getFiles(gradeId));
    }

    @Override
    public List<File> fileList(QueryRequest queryRequest, String yearId, String deptId) {
        List<String> fileIds = this.baseMapper.getFileSByYear(yearId, deptId);//
        if (fileIds.size() == 0)
            return null;
        return fileService.getBaseMapper().selectBatchIds(fileIds);
    }
}
