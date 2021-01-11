package com.jiebao.platfrom.wx.service;

import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.wx.domain.DeptLine;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author qta
 * @since 2020-10-21
 */
public interface IDeptLineService extends IService<DeptLine> {


    JiebaoResponse getLine(String deptId);

    List<DeptLine> childGetLine(String deptId);// 所在街道在当前市 可能存在的  线路

    JiebaoResponse add(DeptLine deptLine);

    JiebaoResponse delete(String[] ids);

}
