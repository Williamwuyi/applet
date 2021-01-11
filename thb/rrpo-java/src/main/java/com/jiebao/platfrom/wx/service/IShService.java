package com.jiebao.platfrom.wx.service;

import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.wx.domain.Sh;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author qta
 * @since 2020-08-20
 */
public interface IShService extends IService<Sh> {
    JiebaoResponse shWx(String qunId, Integer status, String massage);

    JiebaoResponse list(QueryRequest queryRequest,String qunid);


}
