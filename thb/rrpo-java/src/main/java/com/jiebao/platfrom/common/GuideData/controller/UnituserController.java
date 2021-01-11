package com.jiebao.platfrom.common.GuideData.controller;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.jiebao.platfrom.common.GuideData.dao.HlbInfoMapper;
import com.jiebao.platfrom.common.GuideData.dao.InfoCommentMapper;
import com.jiebao.platfrom.common.GuideData.dao.UnituserMapper;
import com.jiebao.platfrom.common.GuideData.domain.HlbInfo;
import com.jiebao.platfrom.common.GuideData.domain.InfoComment;
import com.jiebao.platfrom.common.GuideData.domain.Unituser;
import com.jiebao.platfrom.common.GuideData.service.HlbInfoService;
import com.jiebao.platfrom.common.GuideData.service.InfoCommentService;
import com.jiebao.platfrom.common.GuideData.service.UnitUserService;
import com.jiebao.platfrom.common.controller.BaseController;
import com.jiebao.platfrom.common.dataImport.domain.Info;
import com.jiebao.platfrom.common.dataImport.service.InfoService;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.exception.JiebaoException;
import com.jiebao.platfrom.common.utils.MD5Util;
import com.jiebao.platfrom.railway.dao.ExchangeMapper;
import com.jiebao.platfrom.railway.domain.Address;
import com.jiebao.platfrom.railway.domain.Exchange;
import com.jiebao.platfrom.railway.domain.ExchangeUser;
import com.jiebao.platfrom.railway.domain.PrizeLimit;
import com.jiebao.platfrom.railway.service.AddressService;
import com.jiebao.platfrom.railway.service.ExchangeService;
import com.jiebao.platfrom.railway.service.ExchangeUserService;
import com.jiebao.platfrom.system.dao.DeptMapper;
import com.jiebao.platfrom.system.dao.UserConfigMapper;
import com.jiebao.platfrom.system.dao.UserMapper;
import com.jiebao.platfrom.system.dao.UserRoleMapper;
import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.domain.User;
import com.jiebao.platfrom.system.domain.UserConfig;
import com.jiebao.platfrom.system.domain.UserRole;
import com.jiebao.platfrom.system.service.DeptService;
import com.jiebao.platfrom.system.service.UserConfigService;
import com.jiebao.platfrom.system.service.UserRoleService;
import com.jiebao.platfrom.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.jar.JarException;

@Slf4j
@Validated
@RestController
@RequestMapping("/Unituser")
@Api(tags = "railWay-导数据")   //swagger2 api文档说明示例
public class UnituserController extends BaseController {

    private String message;

    @Autowired
    private UnitUserService unituserService;

    @Autowired
    private UserService userService;

    @Autowired
    private InfoService infoService;

    @Autowired
    private ExchangeService exchangeService;

    @Resource
    private ExchangeMapper exchangeMapper;

    @Autowired
    private InfoCommentService infoCommentService;

    @Autowired
    private HlbInfoMapper hlbInfoMapper;

    @Autowired
    private HlbInfoService hlbInfoService;

    @Autowired
    private ExchangeUserService exchangeUserService;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Autowired
    private UserConfigService userConfigService;

    @Autowired
    private DeptService deptService;
    @Autowired
    private UnituserMapper unituserMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DeptMapper deptMapper;
    @Autowired
    private AddressService addressService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private UserConfigMapper userConfigMapper;

    @GetMapping
    public JiebaoResponse getList() {
        List<Unituser> list = unituserService.list();
        for (Unituser oldUser : list
        ) {
            User user = new User();
            user.setUsername(oldUser.getUnitUser());
            user.setMobile(oldUser.getMobileTelephone());
            user.setStatus("1");
            user.setCreateTime(new Date());
            user.setModifyTime(new Date());
            user.setDescription(oldUser.getHeadship());
            user.setAvatar("default.jpg");
            user.setType(0);
            user.setSsex("2");
            user.setPassword(MD5Util.encrypt(oldUser.getUnitUser(), Unituser.DEFAULT_PASSWORD));

            List<Dept> depts = deptService.list();
            for (Dept dept : depts) {
                if (oldUser.getUnit().equals(dept.getDeptName())) {
                    user.setDeptId(dept.getDeptId());
                }
            }
            userService.save(user);

            String roleId = "2";
            // 保存用户角色
            String[] roles = roleId.split(StringPool.COMMA);
            setUserRoles(user, roles);

            // 创建用户默认的个性化配置
            userConfigService.initDefaultUserConfig(String.valueOf(user.getUserId()));

        }
        return new JiebaoResponse().data(list);
    }

    private void setUserRoles(User user, String[] roles) {
        Arrays.stream(roles).forEach(roleId -> {
            UserRole ur = new UserRole();
            ur.setUserId(user.getUserId());
            ur.setRoleId(roleId);
            this.userRoleMapper.insert(ur);
        });
    }


    @GetMapping("/info")
    @ApiOperation(value = "导信息互递主表", notes = "导信息互递主表", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse Info() {
        List<Info> list = infoService.listForYear();
        for (Info info : list
        ) {
            Exchange exchange = new Exchange();
            exchange.setTitle(info.getTitle());
            exchange.setContent(info.getContent());
            exchange.setCreatTime(new Date());
            exchange.setCreatUser(info.getUpUser());
            exchange.setStatus("3");
            exchange.setTypeId("665d35b42bc67d838cb50f592a6bd7a4");
            exchange.setSendTouser(info.getSendTouser());
            exchange.setInfoSeq(info.getInfoSeq());
            exchange.setUpFile(info.getUpFile1() + "," + info.getUpFile2() + "," + info.getUpFile3() + "," + info.getUpFile4());
            exchangeService.save(exchange);
        }
        System.out.println(list);
        return new JiebaoResponse().data(list);

    }

    @GetMapping("/infoComment")
    @ApiOperation(value = "导信息互递接收人表", notes = "导信息互递接收人表", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse infoComment() throws ParseException {
        // List<InfoComment> list = infoCommentService.list();
        List<Exchange> list = exchangeService.list();
        for (Exchange e : list) {

            List<InfoComment> infoComment = infoCommentService.selectId(e.getInfoSeq(), e.getTitle());

            for (InfoComment info : infoComment
            ) {
                ExchangeUser exchangeUser = new ExchangeUser();
                exchangeUser.setExchangeId(e.getId());
                User byName = userService.findByName(info.getCommentUser());
                if (byName != null) {
                    exchangeUser.setSendUserId(byName.getUserId());
                }
                exchangeUser.setSendUserName(info.getCommentUser());
                exchangeUser.setType("1");
                exchangeUser.setCreatTime(new Date());
                if (info.getCommentContent() != null && info.getViewTime() != null) {
                    exchangeUser.setIsRead(2);
                } else if (info.getViewTime() != null) {
                    exchangeUser.setIsRead(1);
                } else {
                    exchangeUser.setIsRead(0);
                }
                if (info.getViewTime() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = sdf.parse(info.getViewTime());
                    exchangeUser.setReceiveTime(date);
                }
                exchangeUser.setOpinion(info.getCommentContent());
                exchangeUserService.save(exchangeUser);
            }

        }
        return new JiebaoResponse().okMessage("ok");
    }

    @GetMapping("/newUserDept")
    @ApiOperation(value = "导新的user表Dept", notes = "导新的user表Dept", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse newUserDept() {
        List<User> list = userService.nullList();
        // List<Unituser> users = unituserService.selectNew();
        // List<Unituser> arrayList = new ArrayList<>();
        List<Dept> depts = deptService.list();
        for (User user : list
        ) {
            Unituser unituser = unituserService.selectName(user.getUsername());
            if (unituser.getUnit().contains("所")) {
                String xian = unituser.getUnit().replace(unituser.getUnit().charAt(unituser.getUnit().length() - 2) + "", "市");
                String substring = xian.substring(0, xian.length() - 1);
                System.out.println("++++++++++++++++" + substring);
                //  String qu = unituser.getUnit().replace(unituser.getUnit().charAt(unituser.getUnit().length() - 1) + "", "市");
                //  String zhen = unituser.getUnit().replace(unituser.getUnit().charAt(unituser.getUnit().length() - 1) + "", "镇");
                Dept byNewName = deptMapper.getByNewName(substring);
                //  Dept byNewName1 = deptMapper.getByNewName(qu);
                //  Dept byNewName2 = deptMapper.getByNewName(zhen);
                if (byNewName != null) {
                    user.setDeptId(byNewName.getDeptId());
                    userMapper.updateById(user);
                }
              /*  if (byNewName1!=null){
                    user.setDeptId( byNewName1.getDeptId());
                    userMapper.updateById(user);
                }
                if (byNewName2!=null){
                    user.setDeptId( byNewName2.getDeptId());
                    userMapper.updateById(user);
                }*/
            }
        }
        return new JiebaoResponse().okMessage("ok");
    }


    @GetMapping("/setRole")
    @ApiOperation(value = "导入角色", notes = "导入角色", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse setRole() {
        //查询剩下还没有角色用户
        List<User> users = userMapper.remainList();
        for (User u : users
        ) {
            Unituser unituser = unituserService.selectName(u.getUsername());
            if (unituser !=null){
                if (unituser.getUnit().contains("冷水滩")
                ) {
                    String roleId = "7c8f277c3adb22397b8c38f15ca03ea8";
                    // 保存用户角色
                    String[] roles = roleId.split(StringPool.COMMA);
                    setUserRoles(u, roles);
                }
            }
        }
        return new JiebaoResponse().okMessage("ok");
    }

    @GetMapping("/setUserConfig")
    @ApiOperation(value = "个性化配置", notes = "个性化配置", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse setUserConfig() {
        List<User> list = userService.list();
        for (User u : list
        ) {
            userConfigService.initDefaultUserConfig(String.valueOf(u.getUserId()));
        }
        return new JiebaoResponse().okMessage("可以");
    }

    @GetMapping("/updateDept")
    @ApiOperation(value = "修改组织机构", notes = "修改组织机构", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse updateDept() {
        //查找所有长铁用户
        List<Unituser> list = unituserService.selectHHT();
        for (Unituser u : list
        ) {
            userMapper.updateByName(u.getUnitUser());
        }
        return new JiebaoResponse().okMessage("可以");
    }


    @GetMapping("/addAddress")
    @ApiOperation(value = "人员导入通讯录", notes = "人员导入通讯录", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse addAddress() {
        List<User> list = userService.list();
        for (User u : list
        ) {
            Address address = new Address();
            address.setDeptId(u.getDeptId());
            address.setPosition(u.getDescription());
            address.setEmail(u.getEmail());
            if (u.getDeptId() != null && !"".equals(u.getDeptId())) {
                Dept byId = deptService.getById(u.getDeptId());
                address.setUnit(byId.getDeptName());
            }
            address.setTelPhone(u.getMobile());
            address.setUserName(u.getUsername());
            address.setStatus(1);
            addressService.save(address);
        }
        return new JiebaoResponse().okMessage("可以");
    }


    @GetMapping("/updateDeptOne")
    @ApiOperation(value = "公安处人员重新更改组织机构和角色", notes = "公安处人员重新更改组织机构和角色", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse updateDeptOne() {
        //查找所有长铁用户
        List<Unituser> list = unituserService.list();
        List<User> users = userService.list();
        for (Unituser u : list
        ) {
            if (u.getUnit().contains("省政府办公厅")) {
                for (User user : users
                ) {
                    if (u.getUnitUser().equals(user.getUsername())) {
                        User user1 = new User();
                        user1.setUserId(user.getUserId());
                        user1.setDeptId("dd6b6b562884d4590c9c2a4b176a5580");
                        userService.updateById(user1);
                    }
                }
            }
        }

        return new JiebaoResponse().okMessage("可以");
    }


    @GetMapping("/ImportStreetUser")
    @ApiOperation(value = "新增单位用户", notes = "新增单位用户", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse ImportStreetUser() {
        Map<String, Object> map = new HashMap<>();
        map.put("rank", 3);
        List<Dept> depts = deptMapper.selectByMap(map);

        for (Dept dept : depts
        ) {
            List<Dept> deptByName = deptService.getDeptByName(dept.getDeptName());
            if (deptByName.size()>1){
                User user = new User();
                Dept byId = deptService.getById(dept.getParentId());
                user.setUsername(byId.getDeptName()+dept.getDeptName()+"护路办");
                //user.setMobile(oldUser.getMobileTelephone());
                user.setStatus("1");
                user.setCreateTime(new Date());
                user.setModifyTime(new Date());
                // user.setDescription(oldUser.getHeadship());
                user.setAvatar("default.jpg");
                user.setType(0);
                user.setSsex("2");
                user.setPassword(MD5Util.encrypt(byId.getDeptName()+dept.getDeptName()+"护路办", Unituser.DEFAULT_PASSWORD));
                user.setDeptId(dept.getDeptId());
           /* List<Dept> depts = deptService.list();
            for (Dept dept : depts) {
                if (oldUser.getUnit().equals(dept.getDeptName())) {
                    user.setDeptId(dept.getDeptId());
                }
            }*/
                userService.save(user);
                //派出所单位用户角色ID
                String roleId = "148d0cf3fedff6ef1ad018863e01fe45";
                // 保存用户角色
                String[] roles = roleId.split(StringPool.COMMA);
                setUserRoles(user, roles);

                // 创建用户默认的个性化配置
                userConfigService.initDefaultUserConfig(String.valueOf(user.getUserId()));
            }else {
                User user = new User();
                user.setUsername(dept.getDeptName()+"护路办");
                //user.setMobile(oldUser.getMobileTelephone());
                user.setStatus("1");
                user.setCreateTime(new Date());
                user.setModifyTime(new Date());
                // user.setDescription(oldUser.getHeadship());
                user.setAvatar("default.jpg");
                user.setType(0);
                user.setSsex("2");
                user.setPassword(MD5Util.encrypt(dept.getDeptName()+"护路办", Unituser.DEFAULT_PASSWORD));
                user.setDeptId(dept.getDeptId());
           /* List<Dept> depts = deptService.list();
            for (Dept dept : depts) {
                if (oldUser.getUnit().equals(dept.getDeptName())) {
                    user.setDeptId(dept.getDeptId());
                }
            }*/
                userService.save(user);
                //派出所单位用户角色ID
                String roleId = "148d0cf3fedff6ef1ad018863e01fe45";
                // 保存用户角色
                String[] roles = roleId.split(StringPool.COMMA);
                setUserRoles(user, roles);

                // 创建用户默认的个性化配置
                userConfigService.initDefaultUserConfig(String.valueOf(user.getUserId()));
            }
        }
        return new JiebaoResponse().data(depts);
    }


    @GetMapping("/deleteUser")
    @ApiOperation(value = "删除单位用户", notes = "删除单位用户", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse deleteUser() {
        Map<String, Object> map = new HashMap<>();
        map.put("rank", 3);
        List<Dept> depts = deptMapper.selectByMap(map);
        for (Dept dept : depts
        ) {
            User byName = userService.findByName(dept.getDeptName());
            if (byName!=null){
                userConfigMapper.delete(new UpdateWrapper<UserConfig>().eq("user_id",byName.getUserId()));
                userRoleMapper.delete(new QueryWrapper<UserRole>().eq("user_id",byName.getUserId()));
                userMapper.delete(new QueryWrapper<User>().eq("user_id",byName.getUserId()));
            }
        }
        return new JiebaoResponse().data(depts);
    }




    @GetMapping("/addTime")
    @ApiOperation(value = "信息互递增加时间", notes = "信息互递增加时间", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse addTime() throws ParseException {
        List<Exchange> list = exchangeService.list();
        for (Exchange e : list
        ) {
            if (e.getInfoSeq() != null) {
                List<HlbInfo> hlbInfo = hlbInfoService.selectOnes(e.getInfoSeq());
                /*SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String format = simpleDateFormat.format(hlbInfo.get(0).getUpTime());
                Date parse = simpleDateFormat.parse(format);
                System.out.println(parse);*/
                e.setReleaseTime(hlbInfo.get(0).getUpTime());
                exchangeService.updateById(e);
            }

        }
        return new JiebaoResponse().okMessage("ok");
    }

}

