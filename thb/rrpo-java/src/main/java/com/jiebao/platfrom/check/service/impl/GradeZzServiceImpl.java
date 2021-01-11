package com.jiebao.platfrom.check.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.check.dao.GradeZzMapper;
import com.jiebao.platfrom.check.domain.GradeZz;
import com.jiebao.platfrom.check.service.IGradeService;
import com.jiebao.platfrom.check.service.IGradeZzService;
import com.jiebao.platfrom.railway.service.ExchangeService;
import com.jiebao.platfrom.railway.service.InformService;
import com.jiebao.platfrom.railway.service.PrizeService;
import com.jiebao.platfrom.railway.service.PublicFileService;
import com.jiebao.platfrom.system.dao.UserMapper;
import com.jiebao.platfrom.system.service.FileService;
import com.jiebao.platfrom.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qta
 * @since 2020-08-11
 */
@Service
public class GradeZzServiceImpl extends ServiceImpl<GradeZzMapper, GradeZz> implements IGradeZzService {
    @Autowired
    IGradeService gradeService;
    @Autowired
    ExchangeService exchangeService;
    @Autowired
    UserService userService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    PrizeService prizeService;
    @Autowired
    InformService informService;
    @Autowired
    PublicFileService publicFileService;
    @Autowired
    GradeZzMapper gradeZzMapper;
    @Autowired
    FileService fileService;

//    @Override
//    public JiebaoResponse deleteByGradeIdAndZzId(String[] list) {
//        return new JiebaoResponse().message(removeByIds(Arrays.asList(list)) ? "解除绑定成功" : "解除绑定失败");
//    }
//
//    @Override
//    public JiebaoResponse list(String gradeId, String yearId, String deptId, String menusId, Integer type, QueryRequest queryRequest) {
//        if (gradeId == null) {
//            if (yearId == null || deptId == null || menusId == null) {
//                return new JiebaoResponse().message("信息不能为空");
//            }
//            QueryWrapper<Grade> queryWrapper1 = new QueryWrapper<>();  //考核 关联部分
//            queryWrapper1.eq("year_id", yearId);
//            queryWrapper1.eq("dept_id", deptId);
//            queryWrapper1.eq("check_id", menusId);
//            Grade grade = gradeService.getOne(queryWrapper1);
//            if (grade == null) {
//                return new JiebaoResponse().message("无对象");
//            }
//            gradeId = grade.getGradeId();
//        }
//        List<String> zzId = gradeZzMapper.getZzId(type, gradeId); //佐证的id
//        if (zzId.size() == 0) {
//            return new JiebaoResponse().message("无对象");
//        }
//
//        if (type == 1) {
//            //信息互递
//            Page<Exchange> page = new Page<>(queryRequest.getPageNum(), queryRequest.getPageSize());
//            return new JiebaoResponse().data(gradeZzMapper.ListXXHD(gradeId, type, page)).message("操作成功");
//        }
//
//        if (type == 2) {
//            //一事一奖
//            Page<Prize> page = new Page<>(queryRequest.getPageNum(), queryRequest.getPageSize());
//            return new JiebaoResponse().data(gradeZzMapper.ListYSYJ(gradeId, type, page)).message("操作成功");
//        }
//
//        if (type == 3) {
//            //通知公告
//            Page<Inform> page = new Page<>(queryRequest.getPageNum(), queryRequest.getPageSize());
//            return new JiebaoResponse().data(gradeZzMapper.ListTZGG(gradeId, type, page)).message("操作成功");
//        }
//
//        if (type == 4) {
//            //公共信息
//            Page<File> page = new Page<>(queryRequest.getPageNum(), queryRequest.getPageSize());
//            return new JiebaoResponse().data(gradeZzMapper.ListGGXX(gradeId, type, page)).message("操作成功");
//        }
//
//        return null;
//    }
//
//    @Override
//    public JiebaoResponse getData(Integer type, Integer status, QueryRequest queryRequest) {
//        String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
//        String deptID = userMapper.getDeptID(username);  //得到此人存在的部门
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.in("dept_id", deptID);
//        List<String> userNameByDepts = userMapper.getUserNameByDepts(queryWrapper);//前市州相关人员  得到用户
//        List<String> userIdByDepts = userMapper.getUserNameByDepts(queryWrapper);//前市州相关人员  得到用户
//        if (type == 1)
//            return new JiebaoResponse().data(ExchangeList(userNameByDepts, status, queryRequest));
//        if (type == 2)
//            return new JiebaoResponse().data(PrizeList(userNameByDepts, status, queryRequest));
//        if (type == 3)
//            return new JiebaoResponse().data(InformList(userNameByDepts, status, queryRequest));
//        if (type == 4)
//            return new JiebaoResponse().data(PublicFileList(userIdByDepts, status, queryRequest));
//        return null;
//    }
//
//
//    private List<Exchange> ExchangeList(List<String> Username, Integer status, QueryRequest queryRequest) { //去信息互递拿值
//        QueryWrapper<Exchange> queryWrapper = new QueryWrapper<>();
//        queryWrapper.in("creat_user", Username);
//        queryWrapper.eq("is_check", status);
//        Page<Exchange> page = new Page<>(queryRequest.getPageNum(), queryRequest.getPageSize());
//        return exchangeService.list(queryWrapper);
//    }
//
//    private List<Prize> PrizeList(List<String> Username, Integer status, QueryRequest queryRequest) { //一事一奖
//        QueryWrapper<Prize> queryWrapper = new QueryWrapper<>();
//        queryWrapper.in("creat_user", Username);
//        queryWrapper.eq("is_check", status);
//        Page<Prize> page = new Page<>(queryRequest.getPageNum(), queryRequest.getPageSize());
//        return prizeService.list(queryWrapper);
//    }
//
//    private List<Inform> InformList(List<String> Username, Integer status, QueryRequest queryRequest) { //通知公告
//        QueryWrapper<Inform> queryWrapper = new QueryWrapper<>();
//        queryWrapper.in("create_user", Username);
//        queryWrapper.eq("is_check", status);
//        Page<Inform> page = new Page<>(queryRequest.getPageNum(), queryRequest.getPageSize());
//        return informService.list(queryWrapper);
//    }
//
//    private List<File> PublicFileList(List<String> UserId, Integer status, QueryRequest queryRequest) { //公共信息  //未写好
//        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
//        queryWrapper.in("user_id", UserId);
//        queryWrapper.eq("is_check", status);
//        Page<File> page = new Page<>(queryRequest.getPageNum(), queryRequest.getPageSize());
//        return fileService.list(queryWrapper);
//    }

}
