package com.jiebao.platfrom.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.jiebao.platfrom.common.annotation.Log;
import com.jiebao.platfrom.common.controller.BaseController;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.exception.JiebaoException;
import com.jiebao.platfrom.system.dao.DictMapper;
import com.jiebao.platfrom.system.domain.Dict;
import com.jiebao.platfrom.system.service.DictService;
import com.wuwenze.poi.ExcelKit;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("dict")
public class DictController extends BaseController {

    private String message;
    private static final String zero ="0";

    @Autowired
    private DictService dictService;

    @Autowired
    private DictMapper dictMapper;

  /*  @GetMapping
//    @RequiresPermissions("dict:view")
    public Map<String, Object> DictList(QueryRequest request, Dict dict) {
        return getDataTable(this.dictService.findDicts(request, dict));
    }

    @Log("新增字典")
    @PostMapping
    @RequiresPermissions("dict:add")
    public void addDict(@Valid Dict dict) throws JiebaoException {
        try {
            this.dictService.createDict(dict);
        } catch (Exception e) {
            message = "新增字典成功";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }

    @Log("删除字典")
    @DeleteMapping("/{dictIds}")
    @RequiresPermissions("dict:delete")
    public void deleteDicts(@NotBlank(message = "{required}") @PathVariable String dictIds) throws JiebaoException {
        try {
            String[] ids = dictIds.split(StringPool.COMMA);
            this.dictService.deleteDicts(ids);
        } catch (Exception e) {
            message = "删除字典成功";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }

    @Log("修改字典")
    @PutMapping
    @RequiresPermissions("dict:update")
    public void updateDict(@Valid Dict dict) throws JiebaoException {
        try {
            this.dictService.updateDict(dict);
        } catch (Exception e) {
            message = "修改字典成功";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }

    @PostMapping("excel")
    @RequiresPermissions("dict:export")
    public void export(QueryRequest request, Dict dict, HttpServletResponse response) throws JiebaoException {
        try {
            List<Dict> dicts = this.dictService.findDicts(request, dict).getRecords();
            ExcelKit.$Export(Dict.class, response).downXlsx(dicts, false);
        } catch (Exception e) {
            message = "导出Excel失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }*/

    @PostMapping("addTable")
   // @RequiresPermissions("dict:addTable")
    @ApiOperation(value = "新增", notes = "新增", response = JiebaoResponse.class, httpMethod = "POST")
    public JiebaoResponse addTable( Dict dict)  {
        return this.dictService.createNewDict(dict);
    }

    @Log("删除字典")
    @PostMapping("deleteTable")
   // @RequiresPermissions("dict:deleteTable")
    @ApiOperation(value = "删除", notes = "删除", response = JiebaoResponse.class, httpMethod = "POST")
    public JiebaoResponse deleteTable( String [] dictIds) throws JiebaoException {
        if (dictIds == null) {
            return new JiebaoResponse().failMessage("请传入参数");
        }
        else {
            //String[] ids = dictIds.split(StringPool.COMMA);
            // dictService.removeByIds(dictIds);
            boolean result = Arrays.stream(dictIds).allMatch(id -> {
                Dict byId = dictService.getById(id);
                if (zero.equals(byId.getParentId())) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("parent_id", id);
                    List<Dict> dicts = dictMapper.selectByMap(map);
                    if (dicts.size() > 0) {
                        return false;
                    }
                }
                dictService.removeById(id);
                return true;
            });
            if (result){
                return new JiebaoResponse().okMessage("删除成功");
            }
            else {
                return new JiebaoResponse().failMessage("字典中有字典项，该字典不能删除");
            }
        }
    }

    @PostMapping("updateTable")
  //  @RequiresPermissions("dict:updateTable")
    @ApiOperation(value = "修改", notes = "修改", response = JiebaoResponse.class, httpMethod = "POST")
    public JiebaoResponse updateTable( Dict dict)  {
        boolean newDict = this.dictService.updateNewDict(dict);
        if (newDict){
            return new JiebaoResponse().okMessage("成功");
        }
        else {
            return new JiebaoResponse().failMessage("失败");
        }
    }

    @GetMapping("getListTable")
  //  @RequiresPermissions("dict:getListTable")
    @ApiOperation(value = "查询", notes = "查询", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse getListTable(QueryRequest queryRequest, Dict dict){
        IPage<Dict> listTable = this.dictService.getListTable(queryRequest, dict);
        return new JiebaoResponse().data(listTable).okMessage("查询成功");
    }


    @GetMapping("getListTableTwo")
    //  @RequiresPermissions("dict:getListTable")
    @ApiOperation(value = "查询", notes = "查询", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse getListTableTwo(QueryRequest queryRequest, Dict dict){
        IPage<Dict> listTable = this.dictService.getListTableTwo(queryRequest, dict);
        return new JiebaoResponse().data(listTable).okMessage("查询成功");
    }



    @GetMapping("getTargets")
    @ApiOperation(value = "获取推送栏目", notes = "获取推送栏目", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse getTargets() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://114.116.174.5:888/jws/push/targets");
        CloseableHttpResponse response = null;
        response = httpClient.execute(httpGet);
        HttpEntity httpEntity  = response.getEntity();
        String result = EntityUtils.toString(httpEntity, "UTF-8");
        JSONObject jsonObject = JSONObject.parseObject(result);
        String code = jsonObject.getString("code");
        System.out.println(code+"---------------");
        if ( "0".equals(code)){
            return new JiebaoResponse().data(jsonObject).okMessage("查询成功");
        }
        else {
            return  new JiebaoResponse().failMessage("查询失败");
        }
    }



    @GetMapping("disable")
    //  @RequiresPermissions("dict:getListTable")
    @ApiOperation(value = "禁用", notes = "禁用", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse getListTable( Dict dict){
        dict.setIfDisable(0);
       dictService.updateById(dict);
        return new JiebaoResponse().okMessage("禁用成功");
    }

    @GetMapping("noDisable")
    //  @RequiresPermissions("dict:getListTable")
    @ApiOperation(value = "解禁", notes = "解禁", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse noDisable( Dict dict){
        dict.setIfDisable(1);
        dictService.updateById(dict);
        return new JiebaoResponse().okMessage("解禁成功");
    }

}
