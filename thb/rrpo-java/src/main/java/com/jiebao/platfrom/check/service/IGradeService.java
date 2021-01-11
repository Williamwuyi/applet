package com.jiebao.platfrom.check.service;

import com.jiebao.platfrom.check.domain.Grade;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import io.swagger.models.auth.In;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author qta
 * @since 2020-07-28
 */
public interface IGradeService extends IService<Grade> {
    JiebaoResponse addGrade(String gradeId, Double number, String message, Integer type);    //自评第一次


    JiebaoResponse commit(String yearDate, String deptId, Integer status);//最后提交  分数统计生成表

    JiebaoResponse selectByUserIdOrDateYear(String dateYear, String DeptId);  //查询对应考试情况

    JiebaoResponse putZz(String gradeId, String[] ids);//上传佐证操作

    JiebaoResponse checkStatus(String gradeId, Integer status);//审核 考核项是否存在问题

    JiebaoResponse checkStatusZZ(String zzId, Integer status);//审核 考核项是否存在问题


}
