package com.jiebao.platfrom.accident.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.accident.daomain.Dept;
import com.jiebao.platfrom.common.domain.JiebaoResponse;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author qta
 * @since 2020-09-29
 */
public interface IDeptService extends IService<Dept> {
    JiebaoResponse addOrSave(Dept dept);

    JiebaoResponse list(String prentId);

    JiebaoResponse bind(String[] ids, String prentId);
}
