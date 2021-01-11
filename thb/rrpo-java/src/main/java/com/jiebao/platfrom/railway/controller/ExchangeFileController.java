package com.jiebao.platfrom.railway.controller;

import com.jiebao.platfrom.common.annotation.Log;
import com.jiebao.platfrom.common.controller.BaseController;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.exception.JiebaoException;
import com.jiebao.platfrom.railway.domain.Exchange;
import com.jiebao.platfrom.railway.domain.ExchangeFile;
import com.jiebao.platfrom.railway.service.ExchangeFileService;
import com.jiebao.platfrom.system.domain.User;
import com.jiebao.platfrom.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @author yf
 */
@Slf4j
@RestController
@RequestMapping(value = "/exchangeFile")
@Api(tags = "railWay-文件共享（暂时不用）")   //swagger2 api文档说明示例
public class ExchangeFileController extends BaseController {


    private String message;

    @Autowired
    private ExchangeFileService exchangeFileService;

    @Autowired
    private UserService userService;


    /**
     * 使用Mapper操作数据库
     *
     * @return JiebaoResponse 标准返回数据类型
     */
    @GetMapping
    @ApiOperation(value = "查询数据List", notes = "查询数据List列表", response = JiebaoResponse.class, httpMethod = "GET")
    public Map<String, Object> getFileList() {

        //根据userID查询相对应有哪些个人文件
        List<User> list = userService.list();
        for (User user : list
        ) {
            List<ExchangeFile> byUser = exchangeFileService.findByUser(user.getUserId());
        }
        return null;
    }

    /**
     * 分页查询
     *
     * @param - 分页参数
     * @return
     * @Parameters sortField  according to order by Field
     * @Parameters sortOrder  JiebaoConstant.ORDER_ASC or JiebaoConstant.ORDER_DESC
     */
    @DeleteMapping("/{ids}")
    @Log("删除文件")
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(value = "批量删除", notes = "批量删除", response = JiebaoResponse.class, httpMethod = "DELETE")
    public JiebaoResponse delete(@PathVariable String[] ids) throws JiebaoException {
        try {
            Arrays.stream(ids).forEach(id -> {
                ExchangeFile byId = exchangeFileService.getById(id);
                File file = new File(byId.getUrl());
                if (file.exists()) {
                    file.delete();
                }
                exchangeFileService.removeById(id);
            });
        } catch (Exception e) {
            throw new JiebaoException("删除失败");
        }
        return new JiebaoResponse().message("删除成功");
    }

    @PutMapping
    @Log("修改")
    @ApiOperation(value = "修改", notes = "修改", response = JiebaoResponse.class, httpMethod = "PUT")
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse updateFile(@Valid ExchangeFile files) throws JiebaoException {
        try {
            exchangeFileService.updateByKey(files);
            return new JiebaoResponse().message("成功");
        } catch (Exception e) {
            message = "修改通讯录失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }


    @PostMapping("/upload")
    @ApiOperation(value = "文件上传", notes = "文件上传", response = JiebaoResponse.class, httpMethod = "POST")
    public JiebaoResponse upload(@RequestParam(value = "file", required = false) MultipartFile file, ExchangeFile files) {
        String path = null;
        if (file != null) {
            path = "D:/rrpo/download/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "_" + file.getOriginalFilename();
        }
        files.setUrl(path);
        files.setTitle(file.getOriginalFilename());
        //files.setCreateUser();
        exchangeFileService.save(files);
        try {
            File fileSave = new File(path);
            if (!fileSave.getParentFile().exists()) {
                fileSave.getParentFile().mkdirs();
            }
            file.transferTo(fileSave);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JiebaoResponse().message("上传成功");
    }


    @GetMapping("/downLoad/{id}")
    @ApiOperation(value = "文件下载", notes = "文件下载", response = JiebaoResponse.class, httpMethod = "GET")
    public void downLoad(HttpServletResponse response, @PathVariable String id) {
        ExchangeFile byId = exchangeFileService.getById(id);
        String downloadFilePath = byId.getUrl();
        String fileName = byId.getTitle();
        File file = new File(downloadFilePath);
        if (file.exists()) {
            response.setContentType("application/force-download");// 设置强制下载不打开  
            response.setCharacterEncoding("utf-8");
            try {
                response.addHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileName, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream outputStream = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    outputStream.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
                return;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            return;
        }
        return;
    }

    @GetMapping("/{parentsId}")
    @Log("根据部门查看文件")
    @ApiOperation(value = "根据部门查看文件", notes = "根据部门查看文件", httpMethod = "GET")
    @Transactional(rollbackFor = Exception.class)
    public List<ExchangeFile> findById(@PathVariable String parentsId) {
        return exchangeFileService.getByParentsId(parentsId);
    }


}
