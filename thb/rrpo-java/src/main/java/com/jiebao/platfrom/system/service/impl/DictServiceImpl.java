package com.jiebao.platfrom.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.common.domain.JiebaoConstant;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.utils.SortUtil;
import com.jiebao.platfrom.railway.domain.Briefing;
import com.jiebao.platfrom.system.dao.DictMapper;
import com.jiebao.platfrom.system.domain.Dict;
import com.jiebao.platfrom.system.service.DictService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("dictService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {


    @Autowired
    private DictService dictService;

    @Autowired
    private DictMapper dictMapper;
   /* @Override
    public IPage<Dict> findDicts(QueryRequest request, Dict dict) {
        try {
            LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();

            if (StringUtils.isNotBlank(dict.getKeyy())) {
                queryWrapper.eq(Dict::getKeyy, dict.getKeyy());
            }
            if (StringUtils.isNotBlank(dict.getValuee())) {
                queryWrapper.eq(Dict::getValuee, dict.getValuee());
            }
            if (StringUtils.isNotBlank(dict.getTableName())) {
                queryWrapper.eq(Dict::getTableName, dict.getTableName());
            }
            if (StringUtils.isNotBlank(dict.getFieldName())) {
                queryWrapper.eq(Dict::getFieldName, dict.getFieldName());
            }

            Page<Dict> page = new Page<>();
            SortUtil.handlePageSort(request, page, true);
            return this.page(page, queryWrapper);
        } catch (Exception e) {
            log.error("获取字典信息失败", e);
            return null;
        }
    }

    @Override
    @Transactional
    public void createDict(Dict dict) {
        this.save(dict);
    }

    @Override
    @Transactional
    public void updateDict(Dict dict) {
        this.baseMapper.updateById(dict);
    }

    @Override
    @Transactional
    public void deleteDicts(String[] dictIds) {
        List<String> list = Arrays.asList(dictIds);
        this.baseMapper.deleteBatchIds(list);
    }*/

    @Override
    public JiebaoResponse createNewDict(Dict dict) {
        String parentId = dict.getParentId();
        Map<String, Object> map = new HashMap<>();
        map.put("table_name",dict.getTableName());
        map.put("parent_id",parentId);
        List<Dict> dicts = dictMapper.selectByMap(map);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("field_name",dict.getFieldName());
        map2.put("parent_id",parentId);
        List<Dict> dictes = dictMapper.selectByMap(map2);
        if (dicts.size()>0 || dictes.size()>0){
           return new JiebaoResponse().failMessage("字段名重复");
        }
        else {
            if (StringUtils.isNotBlank(parentId)) {
                dict.setTableName("无");
                this.save(dict);
            }
            else {
                dict.setFieldName("无");
                dict.setParentId("0");
                this.save(dict);
            }
            return new JiebaoResponse().okMessage("新增成功");
        }
    }

    @Override
    public boolean updateNewDict(Dict dict) {
        int i = baseMapper.updateById(dict);
        if (i > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public IPage<Dict> getListTable(QueryRequest request, Dict dict) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Dict::getIfDisable,1);
        if (StringUtils.isNotBlank(dict.getTableName())){
            queryWrapper.lambda().like(Dict::getTableName,dict.getTableName());
        }
        if (StringUtils.isNotBlank(dict.getFieldName())){
            queryWrapper.lambda().like(Dict::getFieldName,dict.getFieldName());
        }
        if (StringUtils.isNotBlank(dict.getParentId())){
            queryWrapper.lambda().eq(Dict::getParentId,dict.getParentId());
        }
        if (StringUtils.isNotBlank(dict.getDictId())){
            queryWrapper.lambda().eq(Dict::getDictId,dict.getDictId());
        }
        Page<Dict> page = new Page<>(request.getPageNum(), request.getPageSize());
        SortUtil.handleWrapperSort(request, queryWrapper, "creatTime", JiebaoConstant.ORDER_DESC, true);
        return this.baseMapper.selectPage(page, queryWrapper);
    }



    @Override
    public IPage<Dict> getListTableTwo(QueryRequest request, Dict dict) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(dict.getTableName())){
            queryWrapper.lambda().like(Dict::getTableName,dict.getTableName());
        }
        if (StringUtils.isNotBlank(dict.getFieldName())){
            queryWrapper.lambda().like(Dict::getFieldName,dict.getFieldName());
        }
        if (StringUtils.isNotBlank(dict.getParentId())){
            queryWrapper.lambda().eq(Dict::getParentId,dict.getParentId());
        }
        if (StringUtils.isNotBlank(dict.getDictId())){
            queryWrapper.lambda().eq(Dict::getDictId,dict.getDictId());
        }
        Page<Dict> page = new Page<>(request.getPageNum(), request.getPageSize());
        SortUtil.handleWrapperSort(request, queryWrapper, "creatTime", JiebaoConstant.ORDER_DESC, true);
        return this.baseMapper.selectPage(page, queryWrapper);
    }
}
