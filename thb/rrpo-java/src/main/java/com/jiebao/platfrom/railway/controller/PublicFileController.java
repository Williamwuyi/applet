
package com.jiebao.platfrom.railway.controller;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.jiebao.platfrom.common.annotation.Log;
import com.jiebao.platfrom.common.controller.BaseController;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.domain.Tree;
import com.jiebao.platfrom.common.exception.JiebaoException;
import com.jiebao.platfrom.railway.dao.PublicFileMapper;
import com.jiebao.platfrom.railway.domain.PublicFile;
import com.jiebao.platfrom.railway.service.PublicFileService;
import com.jiebao.platfrom.system.dao.FileMapper;
import com.jiebao.platfrom.system.domain.File;
import com.jiebao.platfrom.system.domain.User;
import com.jiebao.platfrom.system.service.FileService;
import com.jiebao.platfrom.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/publicFile")
@Api(tags = "railWay-公共信息公共文件")   //swagger2 api文档说明示例
public class PublicFileController extends BaseController {

    private String message;

    @Autowired
    private PublicFileService publicFileService;




    @GetMapping
    public JiebaoResponse publicFileList(QueryRequest request, PublicFile publicFile) {
        return new JiebaoResponse().data(this.publicFileService.findpublicFileList(request, publicFile)).okMessage("查询成功") ;
    }

    @Log("新增文件夹")
    @PostMapping
    public JiebaoResponse addPublicFile(@Valid PublicFile publicFile) throws JiebaoException {
        try {
            this.publicFileService.createPublicFile(publicFile);
           /* Arrays.stream(fileIds).forEach(fileId -> {
                fileMapper.updateInformByFileId(fileId, publicFile.getId());
            });*/
          return   new JiebaoResponse().okMessage("新增成功");
        } catch (Exception e) {
            message = "新增文件夹失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }

    @Log("删除文件夹")
    @DeleteMapping("/{publicFileIds}")
    public JiebaoResponse deletePublicFiles(@NotBlank(message = "{required}") @PathVariable String publicFileIds) throws JiebaoException {
        try {
            String[] ids = publicFileIds.split(StringPool.COMMA);
            Arrays.stream(ids).forEach(id -> {
                List<PublicFile> list = this.publicFileService.findChilderPublicFile(id);
                if (list.size() > 0) {
                    list.forEach(publicFile -> {
                        publicFileService.removeById(publicFile.getId());
                    });
                }
                publicFileService.removeById(id);
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
    public JiebaoResponse updatePublicFile(@Valid PublicFile publicFile) throws JiebaoException {
        try {
            this.publicFileService.updateById(publicFile);
            return new JiebaoResponse().okMessage("修改成功");
        } catch (Exception e) {
            message = "修改文件夹失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }

    @ApiOperation(value = "根据ID查子文件夹和文件", notes = "根据ID查子文件夹和文件", httpMethod = "GET")
    @GetMapping("/getByIdList")
    public JiebaoResponse publicFileList(String publicFileId) {
        return new JiebaoResponse().data(this.publicFileService.getPublicFileListById(publicFileId)).okMessage("查询成功");
    }

    @ApiOperation(value = "根据fileId查文件详情并绑定对应文件夹", notes = "根据fileId查文件详情并绑定对应文件夹", httpMethod = "GET")
    @GetMapping("/BindFile")
    public JiebaoResponse bindFile( String fileId,String publicFileId) throws JiebaoException {
        try {
            publicFileService.bindFile(fileId,publicFileId);
            return new JiebaoResponse().okMessage("绑定成功");
        } catch (Exception e) {
            message = "失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }


}

