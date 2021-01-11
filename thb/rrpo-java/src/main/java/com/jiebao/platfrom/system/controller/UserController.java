package com.jiebao.platfrom.system.controller;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.jiebao.platfrom.common.annotation.Log;
import com.jiebao.platfrom.common.authentication.JWTUtil;
import com.jiebao.platfrom.common.controller.BaseController;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.exception.JiebaoException;
import com.jiebao.platfrom.common.utils.MD5Util;
import com.jiebao.platfrom.railway.dao.BriefingUserMapper;
import com.jiebao.platfrom.railway.dao.ExchangeUserMapper;
import com.jiebao.platfrom.railway.domain.BriefingUser;
import com.jiebao.platfrom.railway.domain.ExchangeUser;
import com.jiebao.platfrom.railway.service.AddressService;
import com.jiebao.platfrom.railway.service.ExchangeUserService;
import com.jiebao.platfrom.system.dao.DeptMapper;
import com.jiebao.platfrom.system.dao.UserMapper;
import com.jiebao.platfrom.system.dao.UserRoleMapper;
import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.domain.User;
import com.jiebao.platfrom.system.domain.UserConfig;
import com.jiebao.platfrom.system.domain.UserRole;
import com.jiebao.platfrom.system.service.DeptService;
import com.jiebao.platfrom.system.service.UserConfigService;
import com.jiebao.platfrom.system.service.UserService;
import com.wuwenze.poi.ExcelKit;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.lang.reflect.Array;
import java.util.*;

@Slf4j
@Validated
@RestController
@RequestMapping("user")
public class UserController extends BaseController {

    private String message;

    @Autowired
    private UserService userService;
    @Autowired
    private UserConfigService userConfigService;
    @Autowired
    private ExchangeUserMapper exchangeUserMapper;
    @Autowired
    private BriefingUserMapper briefingUserMapper;
    @Autowired
    private DeptMapper deptMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DeptService deptService;
    @Autowired
    private UserRoleMapper userRoleMapper;


    @GetMapping("check/{username}")
    public boolean checkUserName(@NotBlank(message = "{required}") @PathVariable String username) {
        return this.userService.findByName(username) == null;
    }

    @GetMapping("/{username}")
    public User detail(@NotBlank(message = "{required}") @PathVariable String username) {
        return this.userService.findByName(username);
    }

    @GetMapping
    @RequiresPermissions("user:view")
    public Map<String, Object> userList(QueryRequest queryRequest, User user) {
        return getDataTable(userService.findUserDetail(user, queryRequest));
    }

    @Log("新增用户")
    @PostMapping
    @RequiresPermissions("user:add")
    public void addUser(User user) throws JiebaoException {
        try {
            String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
            if (username.equals(user.getUsername())) {
                message = "用户名重复";
                throw new JiebaoException(message);
            }
            this.userService.createUser(user);
        } catch (Exception e) {
            message = "新增用户失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }

    @Log("修改用户")
    @PutMapping
    @RequiresPermissions("user:update")
    public void updateUser(User user) throws JiebaoException {
        try {
            this.userService.updateUser(user);
        } catch (Exception e) {
            message = "修改用户失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }

    @Log("删除用户")
    @DeleteMapping("/{userIds}")
    @RequiresPermissions("user:delete")
    public void deleteUsers(@NotBlank(message = "{required}") @PathVariable String userIds) throws JiebaoException {
        try {
            String[] ids = userIds.split(StringPool.COMMA);
            this.userService.deleteUsers(ids);
        } catch (Exception e) {
            message = "删除用户失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }

    @PutMapping("profile")
    public void updateProfile(User user) throws JiebaoException {
        try {
            this.userService.updateProfile(user);
        } catch (Exception e) {
            message = "修改个人信息失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }

    @PutMapping("avatar")
    public void updateAvatar(
            @NotBlank(message = "{required}") String username,
            @NotBlank(message = "{required}") String avatar) throws JiebaoException {
        try {
            this.userService.updateAvatar(username, avatar);
        } catch (Exception e) {
            message = "修改头像失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }

    @PutMapping("userconfig")
    public void updateUserConfig(UserConfig userConfig) throws JiebaoException {
        try {
            this.userConfigService.update(userConfig);
        } catch (Exception e) {
            message = "修改个性化配置失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }

    @GetMapping("password/check")
    public boolean checkPassword(
            @NotBlank(message = "{required}") String username,
            @NotBlank(message = "{required}") String password) {
        String encryptPassword = MD5Util.encrypt(username, password);
        User user = userService.findByName(username);
        if (user != null)
            return StringUtils.equals(user.getPassword(), encryptPassword);
        else
            return false;
    }

    @PutMapping("password")
    public void updatePassword(
            @NotBlank(message = "{required}") String username,
            @NotBlank(message = "{required}") String password) throws JiebaoException {
        try {
            userService.updatePassword(username, password);
        } catch (Exception e) {
            message = "修改密码失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }

    @PutMapping("password/reset")
    @RequiresPermissions("user:reset")
    public void resetPassword(@NotBlank(message = "{required}") String usernames) throws JiebaoException {
        try {
            String[] usernameArr = usernames.split(StringPool.COMMA);
            this.userService.resetPassword(usernameArr);
        } catch (Exception e) {
            message = "重置用户密码失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }

    @PostMapping("excel")
    @RequiresPermissions("user:export")
    public void export(QueryRequest queryRequest, User user, HttpServletResponse response) throws JiebaoException {
        try {
            List<User> users = this.userService.findUserDetail(user, queryRequest).getRecords();
            ExcelKit.$Export(User.class, response).downXlsx(users, false);
        } catch (Exception e) {
            message = "导出Excel失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }

    @GetMapping("userList")
    public List<User> userList() {
        return userService.list();
    }


    @GetMapping("getByDept/{deptIds}")
    @ApiOperation(value = "根据部门查人员", notes = "根据部门查人员", response = JiebaoResponse.class, httpMethod = "GET")
    public List<User> getByDept(@PathVariable String[] deptIds) {
        List<User> users = new ArrayList();
        Arrays.stream(deptIds).forEach(deptId -> {
            List<User> byDept = userService.getByDepts(deptId);
            users.addAll(byDept);
        });
        return users;
    }

    @GetMapping("getId")
    @ApiOperation(value = "根据信息互递或者简报ID查接收人员", notes = "根据信息互递或者简报ID查接收人员", response = JiebaoResponse.class, httpMethod = "GET")
    public List<User> getId(String someId, Integer type) {
        List<User> users = new ArrayList();
        //1为信息互递ID
        if (type == 1) {
            Map<String, Object> exchangeMap = new HashMap<>();
            exchangeMap.put("exchange_id", someId);
            List<ExchangeUser> exchangeUsers = exchangeUserMapper.selectByMap(exchangeMap);
            for (ExchangeUser e : exchangeUsers
            ) {
                users.add(userService.getById(e.getSendUserId()));
            }
        }
        //2为简报
        else if (type == 2) {
            Map<String, Object> briefingMap = new HashMap<>();
            briefingMap.put("briefing_id", someId);
            List<BriefingUser> briefingUsers = briefingUserMapper.selectByMap(briefingMap);
            for (BriefingUser b : briefingUsers
            ) {
                users.add(userService.getById(b.getSendUserId()));
            }
        }
        return users;
    }

    @GetMapping("/getDeptAndUser")
    @ApiOperation(value = "根据部门id查子部门和该部门人员", notes = "根据部门id查子部门和该部门人员", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse getDeptAndUser(String[] deptIds) {
        List<User> users = new ArrayList();
        List<Dept> depts = new ArrayList<>();
    /*    List<String> deptIdes = Arrays.asList(deptIds);
        Collection<User> users1 = userService.listByIds(deptIdes);
        Map<String, Object> map = new HashMap<>();
        map.put("parent_id", deptIds);
        Collection<Dept> depts1 = deptService.listByMap(map);*/
        Arrays.stream(deptIds).forEach(deptId -> {
            List<User> byDept = userService.getByDepts(deptId);
            users.addAll(byDept);

            Map<String, Object> map = new HashMap<>();
            map.put("parent_id", deptId);
            List<Dept> dept = deptMapper.selectByMap(map);
            depts.addAll(dept);
        });
        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("dept", depts);
        map1.put("user", users);
        return new JiebaoResponse().data(map1).okMessage("查询成功");
    }


    @GetMapping("/getUserInfo")
    @ApiOperation(value = "根据userId获取人员信息", notes = "根据userId获取人员信息", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse getUserInfo(String[] userIds) {
        ArrayList<User> list = new ArrayList();
        Arrays.stream(userIds).forEach(userId -> {
            User byId = userService.getById(userId);
            list.add(byId);
        });
        return new JiebaoResponse().data(list);
    }

    @GetMapping("/getDept")
    @ApiOperation(value = "获取用户是否有组织机构ID", notes = "获取人员是否有组织机构ID", response = JiebaoResponse.class, httpMethod = "GET")
    public boolean getDept() {
        String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
        User byName = userService.findByName(username);
        if (byName.getDeptId() == null || byName.getDeptId() =="") {
            return false;
        } else {
            return true;
        }
    }

    @GetMapping("/bandingDept")
    @ApiOperation(value = "若无组织机构id，绑定组织机构id", notes = "绑定组织机构id", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse bandingDept(String deptId)  {
        String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
        User byName = userService.findByName(username);
        Dept byId = deptService.getById(deptId);
        if (StringUtils.isNotBlank(deptId)){
            userMapper.updateDept(deptId,byName.getUserId());
            UserRole ur = new UserRole();
            if (username.contains("所")){
                ur.setUserId(byName.getUserId());
                ur.setRoleId("a8f32da9a55f1f999a78dc926aecc009");
            }
            else {
                if (byId.getRank()==1){
                    ur.setUserId(byName.getUserId());
                    ur.setRoleId("1ef79e30d65cf948db46a7e408f46085");
                }
                if (byId.getRank() == 2){ //区县
                    ur.setUserId(byName.getUserId());
                    ur.setRoleId("7c8f277c3adb22397b8c38f15ca03ea8");
                }
               if(byId.getRank() == 3){
                    ur.setUserId(byName.getUserId());
                    ur.setRoleId("8c970a7008c0860ae295e8f0fec3280f");
                }
                if (byId.getRank() == 4){
                    ur.setUserId(byName.getUserId());
                    ur.setRoleId("4f98f01015aa8dbd853e7208c3b44568");
                }
                 if (byId.getRank() == 0){
                    ur.setUserId(byName.getUserId());
                    ur.setRoleId("9a6ac9c6c290261b2ed31b5c52c9d74b");
                }
                 else {
                     ur.setUserId(byName.getUserId());
                     ur.setRoleId("2");
                 }
            }
            userRoleMapper.insert(ur);
            return new JiebaoResponse().okMessage("绑定组织机构成功");
        }
        else {
            return new JiebaoResponse().okMessage("绑定组织机构失败");
        }
    }
}
