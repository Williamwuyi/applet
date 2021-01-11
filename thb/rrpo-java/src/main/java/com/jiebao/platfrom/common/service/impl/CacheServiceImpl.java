package com.jiebao.platfrom.common.service.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiebao.platfrom.common.domain.JiebaoConstant;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.service.CacheService;
import com.jiebao.platfrom.common.service.RedisService;
import com.jiebao.platfrom.system.dao.DeptMapper;
import com.jiebao.platfrom.system.dao.UserMapper;
import com.jiebao.platfrom.system.domain.*;
import com.jiebao.platfrom.system.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("cacheService")
public class CacheServiceImpl implements CacheService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private UserService userService;

    @Autowired
    private DeptService deptService;

    @Autowired
    private UserConfigService userConfigService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DeptMapper deptMapper;

    @Autowired
    private ObjectMapper mapper;

    @Override
    public void testConnect() throws Exception {
        this.redisService.exists("test");
    }

    @Override
    public User getUser(String username) throws Exception {
        String userString = this.redisService.get(JiebaoConstant.USER_CACHE_PREFIX + username);
        if (StringUtils.isBlank(userString)) {
            return null;
        } else {
            return this.mapper.readValue(userString, User.class);
        }
    }

    @Override
    public List<Role> getRoles(String username) throws Exception {
        String roleListString = this.redisService.get(JiebaoConstant.USER_ROLE_CACHE_PREFIX + username);
        if (StringUtils.isBlank(roleListString)) {
            return null;
        } else {
            JavaType type = mapper.getTypeFactory().constructParametricType(List.class, Role.class);
            return this.mapper.readValue(roleListString, type);
        }
    }

    @Override
    public List<Menu> getPermissions(String username) throws Exception {
        String permissionListString = this.redisService.get(JiebaoConstant.USER_PERMISSION_CACHE_PREFIX + username);
        if (StringUtils.isBlank(permissionListString)) {
            return null;
        } else {
            JavaType type = mapper.getTypeFactory().constructParametricType(List.class, Menu.class);
            return this.mapper.readValue(permissionListString, type);
        }
    }

    @Override
    public UserConfig getUserConfig(String userId) throws Exception {
        String userConfigString = this.redisService.get(JiebaoConstant.USER_CONFIG_CACHE_PREFIX + userId);
        if (StringUtils.isBlank(userConfigString)) {
            return null;
        } else {
            return this.mapper.readValue(userConfigString, UserConfig.class);
        }
    }

    @Override
    public void saveUser(User user) throws Exception {
        String username = user.getUsername();
        this.deleteUser(username);
        redisService.set(JiebaoConstant.USER_CACHE_PREFIX + username, mapper.writeValueAsString(user));
    }

    @Override
    public void saveUser(String username) throws Exception {
        User user = userMapper.findDetail(username);
        this.deleteUser(username);
        redisService.set(JiebaoConstant.USER_CACHE_PREFIX + username, mapper.writeValueAsString(user));
    }

    @Override
    public void saveRoles(String username) throws Exception {
        List<Role> roleList = this.roleService.findUserRole(username);
        if (!roleList.isEmpty()) {
            this.deleteRoles(username);
            redisService.set(JiebaoConstant.USER_ROLE_CACHE_PREFIX + username, mapper.writeValueAsString(roleList));
        }

    }

    @Override
    public void savePermissions(String username) throws Exception {
        List<Menu> permissionList = this.menuService.findUserPermissions(username);
        if (!permissionList.isEmpty()) {
            this.deletePermissions(username);
            redisService.set(JiebaoConstant.USER_PERMISSION_CACHE_PREFIX + username, mapper.writeValueAsString(permissionList));
        }
    }

    @Override
    public void saveUserConfigs(String userId) throws Exception {
        UserConfig userConfig = this.userConfigService.findByUserId(userId);
        if (userConfig != null) {
            this.deleteUserConfigs(userId);
            redisService.set(JiebaoConstant.USER_CONFIG_CACHE_PREFIX + userId, mapper.writeValueAsString(userConfig));
        }
    }

    @Override
    public void deleteUser(String username) throws Exception {
        username = username.toLowerCase();
        redisService.del(JiebaoConstant.USER_CACHE_PREFIX + username);
    }

    @Override
    public void deleteRoles(String username) throws Exception {
        username = username.toLowerCase();
        redisService.del(JiebaoConstant.USER_ROLE_CACHE_PREFIX + username);
    }

    @Override
    public void deletePermissions(String username) throws Exception {
        username = username.toLowerCase();
        redisService.del(JiebaoConstant.USER_PERMISSION_CACHE_PREFIX + username);
    }

    @Override
    public void deleteUserConfigs(String userId) throws Exception {
        redisService.del(JiebaoConstant.USER_CONFIG_CACHE_PREFIX + userId);
    }

    @Override
    public void saveDept() throws Exception {
        Map<String, Object> map = deptService.findDepts(new QueryRequest(), new Dept());
        redisService.set(JiebaoConstant.DEPT_CACHE, mapper.writeValueAsString(map));
    }


    @Override
    public Map<String, Object> getDept() throws Exception {
        String deptString = this.redisService.get(JiebaoConstant.DEPT_CACHE);
        if (StringUtils.isBlank(deptString)) {
            throw new Exception();
        } else {
            JavaType type = mapper.getTypeFactory().constructParametricType(Map.class, String.class, Object.class);
            return this.mapper.readValue(deptString, type);
        }
    }


    @Override
    public void saveAllChildrenDept() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("parent_id", 0);
        List<Dept> depts = deptMapper.selectByMap(map);
        for (Dept dept : depts
        ) {
            List<Dept> deptList = deptService.getDepts(dept.getDeptId());
            redisService.set(JiebaoConstant.DEPT_CACHE + dept.getDeptId(), mapper.writeValueAsString(deptList));
        }
    }


    @Override
    public List<Dept> getAllChildrenDept(String deptId) throws Exception {
        String ChildrenDept = this.redisService.get(JiebaoConstant.DEPT_CACHE + deptId);
        if (StringUtils.isBlank(ChildrenDept)) {
            return  null;
        } else {
            JavaType type = mapper.getTypeFactory().constructParametricType(List.class, Dept.class);
            return this.mapper.readValue(ChildrenDept,type);
        }
    }
}
