package com.jiebao.platfrom.wx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jiebao.platfrom.common.authentication.JWTUtil;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.system.dao.UserMapper;
import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.service.DeptService;
import com.jiebao.platfrom.wx.domain.QunJs;
import com.jiebao.platfrom.wx.dao.QunJsMapper;
import com.jiebao.platfrom.wx.service.IQunJsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qta
 * @since 2020-08-31
 */
@Service
public class QunJsServiceImpl extends ServiceImpl<QunJsMapper, QunJs> implements IQunJsService {
    @Autowired
    DeptService deptService;

    @Autowired
    UserMapper userMapper;

    @Override
    public JiebaoResponse selectById(String qunId) {
        QueryWrapper<QunJs> queryWrapper = new QueryWrapper<>();
        if (qunId != null) {
            queryWrapper.eq("wx_id", qunId);
        }
        queryWrapper.orderByAsc("date");
        return new JiebaoResponse().data(this.baseMapper.getQunJs(queryWrapper)).message("查询成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse addOrUpdate(QunJs entity) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        if (entity.getJsId() == null) {
            if (!jude(entity.getWxId())) {
                return jiebaoResponse.failMessage("请勿重复绑定");
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            entity.setFillDate(simpleDateFormat.format(new Date()));
            String username = JWTUtil.getUsername(SecurityUtils.getSubject().getPrincipal().toString());
            Dept dept = deptService.getById(userMapper.getDeptID(username));  //当前登陆人的部门
            fz(entity, dept);
        } else {  //此操作不太重要
            QunJs qunJs = getById(entity.getJsId());
            if (!qunJs.getWxId().equals(entity.getWxId())) {
                if (!jude(entity.getWxId())) {
                    return jiebaoResponse.failMessage("请勿重复绑定");
                }
            }
        }
        jiebaoResponse = super.saveOrUpdate(entity) ? jiebaoResponse.okMessage("操作成功") : jiebaoResponse.failMessage("操作失败");
        return jiebaoResponse;
    }

    @Override
    public boolean updateById(String qunId,String massage) {
        UpdateWrapper<QunJs> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("wx_id", qunId);
         updateWrapper.set("sbyj",massage);
        return update(updateWrapper);
    }

    private void fz(QunJs qunJs, Dept dept) {  //判断当前 登陆人的组织级别  主级封装
        if (dept.getParentId().equals("0")) {
            qunJs.setSzCity(dept.getDeptId());
        }
        Dept dept1 = deptService.getById(dept.getParentId());//第二次查询
        if (dept1.getParentId().equals("0")) { //区县级别的
            qunJs.setSzCity(dept1.getDeptId());
            qunJs.setQxCity(dept.getDeptId());
        }
        Dept dept2 = deptService.getById(deptService.getById(dept.getParentId()).getParentId());
        if (dept2.getParentId().equals("0")) { //乡镇街道级别的
            qunJs.setJdCity(dept.getDeptId());
            qunJs.setQxCity(dept1.getDeptId());
            qunJs.setSzCity(dept2.getDeptId());
        }
    }

    private boolean jude(String wxId) {
        return this.baseMapper.judge(wxId) == null ? true : false;
    }


}
