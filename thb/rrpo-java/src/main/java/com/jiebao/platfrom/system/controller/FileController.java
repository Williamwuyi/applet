package com.jiebao.platfrom.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiebao.platfrom.common.authentication.JWTUtil;
import com.jiebao.platfrom.common.controller.BaseController;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.railway.domain.Prize;
import com.jiebao.platfrom.system.dao.FileMapper;
import com.jiebao.platfrom.system.domain.Basic;
import com.jiebao.platfrom.system.domain.File;
import com.jiebao.platfrom.system.domain.User;
import com.jiebao.platfrom.system.manager.UserManager;
import com.jiebao.platfrom.system.service.BasicService;
import com.jiebao.platfrom.system.service.FileService;
import com.jiebao.platfrom.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 文件附件处理
 * 有关联文件直接存储，无关联文件使用redis存储
 *
 * @author Sinliz
 */

@RestController
@RequestMapping(value = "/file")
@Slf4j
@Api(tags = "文件上传")   //swagger2 api文档说明示例
public class FileController extends BaseController {

    // 默认值定义
    private final String IMAGE_FILE_TYPE = "1";
    private final String APPENDIX_FILE_TYPE = "2";
    private final Integer IMAGE_DEFAULT_SIZE = 1;
    private final Integer FILE_DEFAULT_SIZE = 10;
    private final String IMAGE_DEFAULT_TYPE = "bmp,jpg,jepg,png,gif,webp";
    private final String FILE_DEFAULT_TYPE = "bmp,jpg,jepg,png,gif,webp,doc,docx,xls,xlsx,pdf,rar,zip,7z";
    private final String TEN = "10";

    // 文件存储路径、虚拟访问路径读取
    @Value("${jiebao.upload.imagePath}")
    private String imagePath;
    @Value("${jiebao.upload.filePath}")
    private String filePath;
    @Value("${jiebao.upload.image}")
    private String imageAccessPath;

    @Value("${jiebao.upload.file}")
    private String fileAccessPath;
    @Value("${jiebao.host}")
    private String host;

    @Autowired
    private BasicService basicService;
    @Autowired
    private UserManager userManager;
    @Autowired
    private FileService fileService;
    @Autowired
    private UserService userService;


    @GetMapping("selectById")
    @ApiOperation("指定文件查询信息")
    public JiebaoResponse selectById(String fileId) {
        return new JiebaoResponse().data(fileService.getById(fileId));
    }

    /**
     * 富文本编辑器图片上传
     * 仅用于上传编辑器图片
     *
     * @param file    文件对象
     * @param refType 文件关联类型：一事一奖/信息互递/其他
     * @param refId   文件关联ID
     * @return JiebaoResponse
     */
    @ApiOperation("富文本编辑器图片上传接口")
    @PostMapping("/uploadImage")
    public JiebaoResponse uploadImage(MultipartFile file, String refType, String refId, HttpServletRequest request) {
        return this.upload(file, this.IMAGE_FILE_TYPE, refType, refId, request);
    }

    /**
     * 附件上传
     *
     * @param file    文件对象
     * @param refType 文件关联类型：一事一奖/信息互递/其他
     * @param refId   文件关联ID
     * @return JiebaoResponse
     */
    @ApiOperation("文件上传接口")
    @PostMapping("/uploadFile")
    public JiebaoResponse uploadFile(MultipartFile file, String refType, String refId, HttpServletRequest request) {
        return this.upload(file, this.APPENDIX_FILE_TYPE, refType, refId, request);
    }


    /**
     * 读取附件
     *
     * @param refId 文件库文件的关联ID
     * @return
     */
    @ApiOperation("读取附件接口")
    @PostMapping("/getAppendixList")
    public JiebaoResponse getAppendixList(String refId) {
        if (StringUtils.isEmpty(refId)) {
            return new JiebaoResponse().put("status", false);
        } else {
            List<File> list = fileService.getAppendixList(refId);
            return new JiebaoResponse().put("status", true).put("fileList", list);
        }
    }

    /**
     * 读取附件
     *
     * @param refId 文件库文件的关联ID
     * @return
     */
    @ApiOperation("读取信息互递回复附件接口")
    @PostMapping("/getAppendixListForEX")
    public JiebaoResponse getAppendixListForEX(String refId) {
        if (StringUtils.isEmpty(refId)) {
            return new JiebaoResponse().put("status", false);
        } else {
            List<File> list = fileService.getAppendixListForEX(refId);
            return new JiebaoResponse().put("status", true).put("fileList", list);
        }
    }


    /**
     * 读取附件
     *
     * @param refId 文件库文件的关联ID
     * @return
     */
    @ApiOperation("读取信息互递发送附件接口")
    @PostMapping("/getAppendixListReply")
    public JiebaoResponse getAppendixListReply(String refId) {
        if (StringUtils.isEmpty(refId)) {
            return new JiebaoResponse().put("status", false);
        } else {
            List<File> list = fileService.getAppendixListReply(refId);
            return new JiebaoResponse().put("status", true).put("fileList", list);
        }
    }

    /**
     * 文件下载
     *
     * @param fileId 文件ID
     */
    @ApiOperation("文件下载接口")
    @RequestMapping("/downloadFile")
    public void downloadFile(String fileId, HttpServletResponse response) {

        File file = fileService.getById(fileId);
        java.io.File downloadFile = new java.io.File(file.getFileUrl() + file.getNewName());
        if (downloadFile.exists()) {
            try {
                InputStream is = new BufferedInputStream(new FileInputStream(downloadFile));
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                is.close();
                response.addHeader("Content-Disposition", "attachment;filename=" + new String(file.getOldName().getBytes("UTF-8"), "ISO-8859-1"));
                OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
                response.setContentType("application/octet-stream");
                outputStream.write(buffer);
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                log.warn("File download Exception：" + e);
            }
        }
    }

    /**
     * 文件删除
     *
     * @param fileId
     * @return
     */
    @ApiOperation("文件删除接口")
    @PostMapping("/deleteFile")
    public JiebaoResponse deleteFile(String fileId) {
        System.out.println(fileId + "-------------");
        File file = fileService.getById(fileId);
        if (null != file) {
            java.io.File deleteFile = new java.io.File(file.getFileUrl() + file.getNewName());
            if (deleteFile.delete() && file.deleteById(file.getFileId())) {
                return new JiebaoResponse().put("status", true).message("文件删除成功！");
            } else {
                return new JiebaoResponse().put("status", false).message("文件删除失败，请重试！");
            }
        } else {
            return new JiebaoResponse().put("status", false).message("此文件不存在！");
        }
    }

    /**
     * 文件上传处理
     *
     * @param file
     * @param type
     * @param refType
     * @param refId
     * @return
     */
    private JiebaoResponse upload(MultipartFile file, String type, String refType, String refId, HttpServletRequest request) {
        if (file.isEmpty()) {
            return new JiebaoResponse().message("文件对象为空，请选择上传！").put("status", false);
        }
        //读取数据库基础上传配置
        List<Basic> list = basicService.list();
        Basic basic = null;
        if (list.size() > 0) {
            basic = list.get(0);
        } else {
            basic = new Basic();
            basic.setFileSize(this.FILE_DEFAULT_SIZE);
            basic.setImageSize(this.IMAGE_DEFAULT_SIZE);
        }
        //检查数据库是否配置，无配置使用默认配置
        Integer maxSize = 0;
        String uploadType = null;
        if (type.equals(this.IMAGE_FILE_TYPE)) {
            if (basic.getImageSize() == null || basic.getImageSize() < 0) {
                maxSize = this.IMAGE_DEFAULT_SIZE;
            } else {
                maxSize = basic.getImageSize();
            }
            if (StringUtils.isEmpty(basic.getImageType())) {
                uploadType = this.IMAGE_DEFAULT_TYPE;
            } else {
                uploadType = basic.getImageType();
            }
        } else {
            if (basic.getFileSize() == null || basic.getFileSize() < 0) {
                maxSize = this.FILE_DEFAULT_SIZE;
            } else {
                maxSize = basic.getFileSize();
            }
            if (StringUtils.isEmpty(basic.getFileType())) {
                uploadType = this.FILE_DEFAULT_TYPE;
            } else {
                uploadType = basic.getFileType();
            }
        }
        //检查图片/文件类型与大小是否超过限制
        if (checkFileType(file, uploadType)) {
            if (checkFileSize(file, maxSize)) {
                String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
                User user = userManager.getUser(username);
                if (StringUtils.isEmpty(refId)) {   //文件无关联 保存文件 存储数据库
                    return this.saveFile(file, type, refType, null, user.getUserId(), request);
                } else {
                    return this.saveFile(file, type, refType, refId, user.getUserId(), request);
                }
            } else {
                if (type.equals(this.IMAGE_FILE_TYPE)) {
                    return new JiebaoResponse().message("图片文件超出最大上传限制：" + maxSize + "MB！").put("status", false);
                } else {
                    return new JiebaoResponse().message("文件超出最大上传限制：" + maxSize + "MB！").put("status", false);
                }
            }
        } else {
            if (type.equals(this.IMAGE_FILE_TYPE)) {
                return new JiebaoResponse().message("图片文件不符合可上传类型：" + uploadType).put("status", false);
            } else {
                return new JiebaoResponse().message("文件不符合可上传类型：" + uploadType).put("status", false);
            }
        }
    }

    private JiebaoResponse saveFile(MultipartFile multipartFile, String fileType, String refType, String refId, String userId, HttpServletRequest request) {
        String suffix = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));    //文件后缀名
        String fileNewName = UUID.randomUUID().toString();  //新文件名
        String path = "";   //上传地址
        String accessPath = ""; //文件访问虚拟地址
        if (fileType.equals(this.IMAGE_FILE_TYPE)) {
            path = this.imagePath;
            accessPath = this.imageAccessPath;
        } else {
            path = this.filePath;
            accessPath = this.fileAccessPath;
        }
        String currentTimeFolder = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "/";
        path = path + currentTimeFolder;
        accessPath = accessPath + currentTimeFolder;
        java.io.File currentFile = new java.io.File(path + fileNewName + suffix);
        java.io.File folder = new java.io.File(path);
        if (!folder.exists() && !folder.isDirectory()) {    //判断文件存储路径，不存在创建文件夹
            folder.mkdirs();
        }
        try {
            multipartFile.transferTo(currentFile);  //保存文件至上传地址
        } catch (IOException e) {
            log.warn("upload file Exception：", e);
        }
        accessPath = host + accessPath + fileNewName + suffix;   //上传后完整文件虚拟访问地址

        //创建文件信息对象保存至数据库
        File file = new File();
        file.setFileType(fileType);
        file.setOldName(multipartFile.getOriginalFilename());
        file.setNewName(fileNewName + suffix);
        file.setFileUrl(path);
        file.setAccessUrl(accessPath);
        file.setRefType(refType);
        file.setFileSuffix(suffix);
        if (StringUtils.isEmpty(refId)) {
            file.setStatus(false);
        } else {
            file.setStatus(true);
            file.setRefId(refId);
        }
        file.setTime(new Date());
        file.setUserId(userId);

        if (file.insert()) {
            return new JiebaoResponse().put("status", true).put("file", file);
        } else {
            currentFile.delete();
            return new JiebaoResponse().put("status", false).message("上传服务异常，上传失败，请重试！");
        }
    }

    /**
     * 文件类型判断
     *
     * @param file
     * @param uploadType 上传文件允许类型
     * @return boolean 上传文件是否属于允许上传文件类型
     */
    private boolean checkFileType(MultipartFile file, String uploadType) {
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        List<String> list = Stream.of(uploadType.split(",")).collect(Collectors.toList());
        return list.contains(suffix);
    }

    /**
     * 文件大小判断
     *
     * @param file
     * @param maxSize 图片/文件最大size
     * @return boolean 上传文件是否小于允许上传文件大小
     */
    private boolean checkFileSize(MultipartFile file, Integer maxSize) {
        long fileSize = file.getSize();
        if (fileSize > maxSize * 1024 * 1024L) {
            return false;
        }
        return true;
    }

    /**
     * 读取一事一奖word文档接口
     *
     * @return
     */
    @ApiOperation("读取附件接口")
    @GetMapping("/getPrizeList")
    public JiebaoResponse getPrizeList(QueryRequest request, File file, String startTime, String endTime) {
        IPage<File> fileList = fileService.getFileList(request, file, startTime, endTime);
        List<File> records = fileList.getRecords();
        for (File f : records
        ) {
            User byId = userService.getById(f.getUserId());
            f.setUserName(byId.getUsername());
        }
        return new JiebaoResponse().data(this.getDataTable(fileList));
    }
}
