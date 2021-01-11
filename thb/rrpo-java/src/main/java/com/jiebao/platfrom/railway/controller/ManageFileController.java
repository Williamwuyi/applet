
package com.jiebao.platfrom.railway.controller;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.jiebao.platfrom.common.annotation.Log;
import com.jiebao.platfrom.common.controller.BaseController;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.exception.JiebaoException;
import com.jiebao.platfrom.railway.domain.ManageFile;
import com.jiebao.platfrom.railway.service.ManageFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/manageFile")
@Api(tags = "railWay-上级文件")   //swagger2 api文档说明示例
public class ManageFileController extends BaseController {

    private String message;

    @Autowired
    private ManageFileService manageFileService;


    @GetMapping
    public JiebaoResponse manageFileList(QueryRequest request, ManageFile manageFile) {
        return  new JiebaoResponse().data(this.manageFileService.findManageFileList(request, manageFile)).okMessage("查询成功");
    }

    @Log("新增文件夹")
    @PostMapping
    public JiebaoResponse addManageFile(@Valid ManageFile manageFile) throws JiebaoException {
        try {
            this.manageFileService.createManageFile(manageFile);
          return   new JiebaoResponse().okMessage("新增成功");
        } catch (Exception e) {
            message = "新增文件夹失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }

    @Log("删除文件夹")
    @DeleteMapping("/{manageFileIds}")
    public JiebaoResponse deleteManageFiles(@NotBlank(message = "{required}") @PathVariable String manageFileIds) throws JiebaoException {
        try {
            String[] ids = manageFileIds.split(StringPool.COMMA);
            Arrays.stream(ids).forEach(id -> {
                List<ManageFile> list = this.manageFileService.findChilderManageFile(id);
                if (list.size() > 0) {
                    list.forEach(manageFile -> {
                        manageFileService.removeById(manageFile.getId());
                    });
                }
                manageFileService.removeById(id);
            });
            return  new JiebaoResponse().okMessage("删除成功");
        } catch (Exception e) {
            message = "删除文件夹失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }

    @Log("修改文件夹")
    @PutMapping
    public JiebaoResponse updateManageFile(@Valid ManageFile manageFile) throws JiebaoException {
        try {
            this.manageFileService.updateById(manageFile);
            return new JiebaoResponse().okMessage("修改成功");
        } catch (Exception e) {
            message = "修改文件夹失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }

    @ApiOperation(value = "根据ID查子文件夹和文件", notes = "根据ID查子文件夹和文件", httpMethod = "GET")
    @GetMapping("/getByIdList")
    public JiebaoResponse manageFileList(String manageFileId) {
        return new JiebaoResponse().data(this.manageFileService.getManageFileListById(manageFileId)).okMessage("查询成功");
    }

    @ApiOperation(value = "根据fileId查文件详情并绑定对应文件夹", notes = "根据fileId查文件详情并绑定对应文件夹", httpMethod = "GET")
    @GetMapping("/BindFile")
    public JiebaoResponse bindFile( String fileId,String manageFileId) throws JiebaoException {
        try {
            manageFileService.bindFile(fileId,manageFileId);
            return new JiebaoResponse().okMessage("绑定成功");
        } catch (Exception e) {
            message = "失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }
}

