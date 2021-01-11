package com.jiebao.platfrom.railway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.railway.domain.ManageFile;

import java.util.List;
import java.util.Map;

/**
 * @author yf
 */
public interface ManageFileService extends IService<ManageFile> {

    /**
     * 分页查询
     *
     * @param request
     * @param manageFile
     * @return
     */
    Map<String, Object> findManageFileList(QueryRequest request, ManageFile manageFile);

    /**
     * 查询文件夹List
     *
     * @param manageFile
     * @param request
     * @return
     */
    List<ManageFile> findManageFiles(ManageFile manageFile, QueryRequest request);

    /**
     * 创建新文件夹
     *
     * @param manageFile json format context
     * @return Result<XxxxDO>
     */
    void createManageFile(ManageFile manageFile);

    /**
     * 查询该ID的子集List
     *
     * @param id
     * @return
     */
    List<ManageFile> findChilderManageFile(String id);

    /**
     * 查询该文件夹下一级的子文件夹和file
     *
     * @param manageFileId
     * @return
     */
    List<ManageFile> getManageFileListById(String manageFileId);

    /**
     * 把manageFileId绑定到file表的file_id
     *
     * @param fileId
     * @param manageFileId
     * @return
     */
    boolean bindFile(String fileId, String manageFileId);
}
