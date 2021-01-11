package com.jiebao.platfrom.railway.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.railway.domain.AsYear;
import com.jiebao.platfrom.railway.domain.PrizeLimit;
import com.jiebao.platfrom.system.domain.File;

import java.util.List;

/**
 * @author yf
 */
public interface AsYearService extends IService<AsYear> {

       JiebaoResponse addAsYear(String gradeId, String[] ids);

       JiebaoResponse pagelist(QueryRequest queryRequest,String gradeId);

       List<File> fileList(QueryRequest queryRequest, String yearId, String deptId);


}
