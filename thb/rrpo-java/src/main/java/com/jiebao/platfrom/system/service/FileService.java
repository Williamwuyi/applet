package com.jiebao.platfrom.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.system.domain.File;

import java.util.List;

public interface FileService extends IService<File> {

    List<File> getAppendixList(String refId);

    List<File> getAppendixListReply(String refId);

    IPage<File> getFileList(QueryRequest request, File file, String startTime, String endTime);

    Integer deleteByRefIds(List<String> refIds);

    List<File> getAppendixListForEX(String refId);
}
