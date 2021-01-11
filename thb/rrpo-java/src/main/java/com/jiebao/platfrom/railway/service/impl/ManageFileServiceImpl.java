package com.jiebao.platfrom.railway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.common.authentication.JWTUtil;
import com.jiebao.platfrom.common.domain.JiebaoConstant;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.domain.Tree;
import com.jiebao.platfrom.common.utils.SortUtil;
import com.jiebao.platfrom.common.utils.TreeUtil;
import com.jiebao.platfrom.railway.dao.ManageFileMapper;

import com.jiebao.platfrom.railway.domain.ManageFile;
import com.jiebao.platfrom.railway.service.ManageFileService;
import com.jiebao.platfrom.system.dao.FileMapper;
import com.jiebao.platfrom.system.domain.File;
import com.jiebao.platfrom.system.domain.User;
import com.jiebao.platfrom.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author yf
 */
@Slf4j
@Service("ManageFileService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ManageFileServiceImpl extends ServiceImpl<ManageFileMapper, ManageFile> implements ManageFileService {


    @Autowired
    ManageFileMapper manageFileMapper;

    @Autowired
    ManageFileService manageFileService;

    @Autowired
    UserService userService;

    @Autowired
    private FileMapper fileMapper;


    @Override
    public Map<String, Object> findManageFileList(QueryRequest request, ManageFile manageFile) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<ManageFile> manageFiles = findManageFiles(manageFile, request);
            List<Tree<ManageFile>> trees = new ArrayList<>();
            buildTrees(trees, manageFiles);
            Tree<ManageFile> manageFileTree = TreeUtil.build(trees);
            result.put("rows", manageFileTree);
            result.put("total", manageFiles.size());
        } catch (Exception e) {
            log.error("获取组织机构列表失败", e);
            result.put("rows", null);
            result.put("total", 0);
        }
        return result;
    }


    private void buildTrees(List<Tree<ManageFile>> trees, List<ManageFile> manageFiles) {
        manageFiles.forEach(manageFile -> {
            Tree<ManageFile> tree = new Tree<>();
            tree.setId(manageFile.getId());
            tree.setKey(tree.getId());
            tree.setParentId(manageFile.getParentId());
            tree.setName(manageFile.getName());
            tree.setMark(manageFile.getMark());
            tree.setCreatTime(manageFile.getCreatTime());
            trees.add(tree);
        });
    }


    @Override
    public List<ManageFile> findManageFiles(ManageFile manageFile, QueryRequest request) {
        QueryWrapper<ManageFile> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(manageFile.getName())) {
            queryWrapper.lambda().eq(ManageFile::getName, manageFile.getName());
        }
        SortUtil.handleWrapperSort(request, queryWrapper, "creatTime", JiebaoConstant.ORDER_ASC, true);
        return this.baseMapper.selectList(queryWrapper);
    }


    @Override
    @Transactional
    public void createManageFile(ManageFile manageFile) {
        String parentId = manageFile.getParentId();
        if (parentId == null) {
            manageFile.setParentId("0");
        }
        String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
        manageFile.setUserName(username);
        this.save(manageFile);
    }

    @Override
    public List<ManageFile> findChilderManageFile(String id) {
        LambdaQueryWrapper<ManageFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ManageFile::getParentId, id);
        return this.baseMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<ManageFile> getManageFileListById(String manageFileId) {
        List<ManageFile> manageFiles = manageFileMapper.selectByParentId(manageFileId);
        for (ManageFile p : manageFiles
        ) {
            List<ManageFile> manageFileP = manageFileMapper.selectByParentId(p.getId());

            HashMap<String, Object> map = new HashMap<>();
            map.put("ref_id", p.getId());
            List<File> files = fileMapper.selectByMap(map);
            if (manageFileP.size() > 0 || files.size() > 0) {
                p.setHasChildren(true);
            }
        }
        List<File> files = manageFileMapper.selectFiles(manageFileId);
        for (File f:files
        ) {
            User byId = userService.getById(f.getUserId());
            f.setUserName(byId.getUsername());
        }
        List list = new ArrayList<>();
        list.addAll(manageFiles);
        list.addAll(files);
        return list;
    }

    @Override
    public boolean bindFile(String fileId, String manageFileId) {
        return fileMapper.updatePublicFileByFileId(fileId, manageFileId);
    }
}
