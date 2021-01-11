package com.jiebao.platfrom.railway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.common.domain.JiebaoConstant;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.utils.SortUtil;
import com.jiebao.platfrom.railway.dao.PrizeLimitMapper;
import com.jiebao.platfrom.railway.dao.PrizeTypeMapper;
import com.jiebao.platfrom.railway.domain.Exchange;
import com.jiebao.platfrom.railway.domain.PrizeLimit;
import com.jiebao.platfrom.railway.domain.PrizeType;
import com.jiebao.platfrom.railway.service.PrizeLimitService;
import com.jiebao.platfrom.railway.service.PrizeTypeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author yf
 */
@Slf4j
@Service("PrizeLimitService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class PrizeLimitServiceImpl extends ServiceImpl<PrizeLimitMapper, PrizeLimit> implements PrizeLimitService {


    @Override
    public IPage<PrizeLimit> getPrizeLimitList(QueryRequest request, PrizeLimit prizeLimit) {
        QueryWrapper<PrizeLimit> queryWrapper = new QueryWrapper();
        if (StringUtils.isNotBlank(prizeLimit.getDeptName())) {
            queryWrapper.lambda().like(PrizeLimit::getDeptName, prizeLimit.getDeptName());
        }
        Page<PrizeLimit> page = new Page<>(request.getPageNum(), request.getPageSize());
        SortUtil.handleWrapperSort(request, queryWrapper, "creatTime", JiebaoConstant.ORDER_DESC, true);
        return this.baseMapper.selectPage(page, queryWrapper);
    }
}
