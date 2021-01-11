package com.jiebao.platfrom.railway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.railway.domain.PrivateFile;

import java.util.List;
import java.util.Map;

/**
 * @author yf
 */
public interface PrivateFileService extends IService<PrivateFile> {

    /**
     * 分页查询
     *
     * @param request
     * @param privateFile
     * @return
     */
    Map<String, Object> findPrivateFileList(QueryRequest request, PrivateFile privateFile);

    /**
     * 查询文件夹List
     *
     * @param privateFile
     * @param request
     * @return
     */
    List<PrivateFile> findPrivateFiles(PrivateFile privateFile, QueryRequest request);

    /**
     * 创建新文件夹
     *
     * @param privateFile
     */
    void createPrivateFile(PrivateFile privateFile);

    /**
     * 查询该ID的子集List
     *
     * @param id
     * @return
     */

    List<PrivateFile> findChilderPrivateFile(String id);

    /**
     * 查询该文件夹下一级的子文件夹和file集合
     *
     * @param privateFileId
     * @return
     */

    List<PrivateFile> getPrivateFileListById(String privateFileId);
}
