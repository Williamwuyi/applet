
package com.jiebao.platfrom.railway.controller;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.jiebao.platfrom.common.annotation.Log;
import com.jiebao.platfrom.common.authentication.JWTUtil;
import com.jiebao.platfrom.common.controller.BaseController;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.exception.JiebaoException;
import com.jiebao.platfrom.railway.domain.PrivateFile;
import com.jiebao.platfrom.railway.domain.PublicFile;
import com.jiebao.platfrom.railway.service.PrivateFileService;
import com.jiebao.platfrom.railway.service.PublicFileService;
import com.jiebao.platfrom.system.domain.User;
import com.jiebao.platfrom.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
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
@RequestMapping("/privateFile")
@Api(tags = "railWay-公共信息个人文件")   //swagger2 api文档说明示例
public class PrivateFileController extends BaseController {

    private String message;

    @Autowired
    private PrivateFileService privateFileService;



    @GetMapping
    public Map<String, Object> privateFileList(QueryRequest request, PrivateFile privateFile) {
        return new JiebaoResponse().data(this.privateFileService.findPrivateFileList(request, privateFile)).okMessage("查询成功") ;
    }

    @Log("新增文件夹")
    @PostMapping
    public JiebaoResponse addPrivateFile(@Valid PrivateFile privateFile) throws JiebaoException {
        try {
            privateFileService.createPrivateFile(privateFile);
            return new JiebaoResponse().okMessage("新增成功");
        } catch (Exception e) {
            message = "新增文件夹失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }

    @Log("删除文件夹")
    @DeleteMapping("/{privateFileIds}")
    public JiebaoResponse deletePrivateFiles(@NotBlank(message = "{required}") @PathVariable String privateFileIds) throws JiebaoException {
        try {
            String[] ids = privateFileIds.split(StringPool.COMMA);
            Arrays.stream(ids).forEach(id -> {
                List<PrivateFile> list = this.privateFileService.findChilderPrivateFile(id);
                if (list.size() > 0) {
                    list.forEach(privateFile -> {
                        privateFileService.removeById(privateFile.getId());
                    });
                }
                privateFileService.removeById(id);
            });
            return new JiebaoResponse().okMessage("删除成功");
        } catch (Exception e) {
            message = "删除文件夹失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }

    @Log("修改文件夹")
    @PutMapping
    public JiebaoResponse updatePrivateFile(@Valid PrivateFile privateFile) throws JiebaoException {
        try {
            this.privateFileService.updateById(privateFile);
            return new JiebaoResponse().okMessage("修改成功");
        } catch (Exception e) {
            message = "修改文件夹失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }


    @ApiOperation(value = "根据ID查子文件夹和文件", notes = "根据ID查子文件夹和文件", httpMethod = "GET")
    @GetMapping("/getByIdList")
    public JiebaoResponse privateFileList( String privateFileId) {
        return new JiebaoResponse().data(this.privateFileService.getPrivateFileListById(privateFileId)).okMessage("查询成功");
    }

}

