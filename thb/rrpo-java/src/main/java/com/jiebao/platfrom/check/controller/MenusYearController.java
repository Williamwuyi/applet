package com.jiebao.platfrom.check.controller;


import com.jiebao.platfrom.check.domain.MenusYear;
import com.jiebao.platfrom.check.domain.Year;
import com.jiebao.platfrom.check.service.IMenusYearService;
import com.jiebao.platfrom.common.annotation.Log;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.utils.CheckExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qta
 * @since 2020-08-05
 */
@RestController
@RequestMapping("/check/menus-year")
@Api(tags = "check_年份考核与考核项绑定")
public class MenusYearController {

    @Autowired
    IMenusYearService menusYearService;

    @PostMapping("add")
    @ApiOperation("添加  考核年份规则生成")
    @Log("添加  修改考核年份规则生成")
    public JiebaoResponse add(MenusYear menusYear) {
        return menusYearService.addOrUpdate(menusYear);
    }

    @GetMapping("list")
    @ApiOperation("集合")
    @Log("查询年份考核试题")
    public JiebaoResponse list(String yearId) {
        return menusYearService.List(yearId);
    }

    @DeleteMapping("deleteByLists")
    @ApiOperation("集合删除")
    @Log("年份考核项集合删除")
    public JiebaoResponse deleteByLists(String[] listS) {
        return menusYearService.deleteByListAndYearDate(Arrays.asList(listS)) ? new JiebaoResponse().okMessage("操作成功") : new JiebaoResponse().failMessage("操作失败");
    }

    @PostMapping("excel")
    @ApiOperation("excel上传绑定  参数 第二个 年份考核id")
    public JiebaoResponse excel(MultipartFile multipartFile, String yearId) {
        try {
            return menusYearService.excel(multipartFile, yearId);
        } catch (Exception e) {
            return new JiebaoResponse().failMessage("导入失败");
        }

    }

    @PostMapping("importTemplate")
    @ApiOperation("导出模板")
    public JiebaoResponse importTemplate(String yearId, HttpServletResponse response) {
        boolean b = menusYearService.importTemplate(yearId, response);
        return b ? new JiebaoResponse().okMessage("操作成功") : new JiebaoResponse().failMessage("操作失败");

    }

}
