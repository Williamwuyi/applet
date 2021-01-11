package com.jiebao.platfrom.check.service;

import com.jiebao.platfrom.check.domain.Menus;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author qta
 * @since 2020-07-28
 */
public interface IMenusService extends IService<Menus> {
    JiebaoResponse addOrUpdate(Menus menus);

    JiebaoResponse deleteById(String menusId,Integer status);

    JiebaoResponse lists();   //树形列表

    JiebaoResponse fatherList();

    String selectByName(String name);

}
