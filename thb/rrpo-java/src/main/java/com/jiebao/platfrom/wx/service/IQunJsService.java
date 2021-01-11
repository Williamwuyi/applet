package com.jiebao.platfrom.wx.service;

import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.wx.domain.Qun;
import com.jiebao.platfrom.wx.domain.QunJs;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author qta
 * @since 2020-08-31
 */
public interface IQunJsService extends IService<QunJs> {
    JiebaoResponse selectById(String qunId);

    JiebaoResponse addOrUpdate(QunJs entity);

    boolean updateById(String qunId,String massage);
}
