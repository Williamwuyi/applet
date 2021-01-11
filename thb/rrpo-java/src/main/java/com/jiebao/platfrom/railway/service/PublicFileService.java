package com.jiebao.platfrom.railway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.railway.domain.PrizeType;
import com.jiebao.platfrom.railway.domain.PublicFile;
import com.jiebao.platfrom.system.domain.Dept;

import java.util.List;
import java.util.Map;

/**
 * @author yf
 */
public interface PublicFileService extends IService<PublicFile> {

    /**
     * 分页查询
     *
     * @param request
     * @param publicFile
     * @return
     */
    Map<String, Object> findpublicFileList(QueryRequest request, PublicFile publicFile);

    /**
     * 查询文件夹List
     *
     * @param publicFile
     * @param request
     * @return
     */
    List<PublicFile> findPublicFiles(PublicFile publicFile, QueryRequest request);

    /**
     * 创建新文件夹
     *
     * @param publicFile
     */
    void createPublicFile(PublicFile publicFile);

    /**
     * 查询该ID的子集List
     *
     * @param id
     * @return
     */
    List<PublicFile> findChilderPublicFile(String id);

    /**
     * 查询该文件夹下一级的子文件夹和file
     *
     * @param publicFileId
     * @return
     */
    List<PublicFile> getPublicFileListById(String publicFileId);

    /**
     * 把publicFileId绑定到file表的file_id
     *
     * @param fileId
     * @param publicFileId
     * @return
     */
    boolean bindFile(String fileId, String publicFileId);
}
