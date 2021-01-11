package com.jiebao.platfrom.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.common.domain.JiebaoConstant;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.service.CacheService;
import com.jiebao.platfrom.common.utils.MD5Util;
import com.jiebao.platfrom.common.utils.SortUtil;
import com.jiebao.platfrom.railway.domain.Address;
import com.jiebao.platfrom.railway.service.AddressService;
import com.jiebao.platfrom.system.dao.UserMapper;
import com.jiebao.platfrom.system.dao.UserRoleMapper;
import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.domain.User;
import com.jiebao.platfrom.system.domain.UserRole;
import com.jiebao.platfrom.system.manager.UserManager;
import com.jiebao.platfrom.system.service.DeptService;
import com.jiebao.platfrom.system.service.UserConfigService;
import com.jiebao.platfrom.system.service.UserRoleService;
import com.jiebao.platfrom.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Service("userService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private UserConfigService userConfigService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private UserManager userManager;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DeptService deptService;
    @Autowired
    private AddressService addressService;


    @Override
    public User findByName(String username) {
        return baseMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username).eq(User::getType, 0));
    }

    @Override
    public User findWxByName(String username) {
        return baseMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    @Override
    public IPage<User> findUserDetail(User user, QueryRequest request) {
 //       String deptId = deptService.getDept().getDeptId();
//        if (StringUtils.isNotBlank(user.getUsername())) {//不等于 null  或者"  "    名字的模糊查询优先级最大      所以选了名字再选部门无效
//            List<String> list = new ArrayList<>();
//            List<String> prentIds = new ArrayList<>();
//            prentIds.add(deptId);
//            list.add(deptId);
//            deptService.getAllIds(prentIds, list);
//            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//            queryWrapper.like("username", user.getUsername());
//            queryWrapper.in("dept_id", list);
//            Page<User> page = new Page<>(request.getPageNum(), request.getPageSize());
//            return this.baseMapper.queryList(page, queryWrapper);
//        } else if (StringUtils.isBlank(user.getDeptId())) {
//            user.setDeptId(deptId);
//        }
        try {
            Page<User> page = new Page<>();
            SortUtil.handlePageSort(request, page, "ssex", JiebaoConstant.ORDER_ASC, false);
            return this.baseMapper.findUserDetail(page, user);
        } catch (Exception e) {
            log.error("查询用户异常", e);
            return null;
        }
    }

    @Override
    @Transactional
    public void updateLoginTime(String username) throws Exception {
        User user = new User();
        user.setLastLoginTime(new Date());

        this.baseMapper.update(user, new LambdaQueryWrapper<User>().eq(User::getUsername, username));

        // 重新将用户信息加载到 redis中
        cacheService.saveUser(username);
    }

    @Override
    @Transactional
    public void createUser(User user) throws Exception {
        // 创建用户
        user.setType(0);
        user.setCreateTime(new Date());
        user.setAvatar(User.DEFAULT_AVATAR);
        user.setPassword(MD5Util.encrypt(user.getUsername(), User.DEFAULT_PASSWORD));
        save(user);

        System.out.println(user.getRoleId() + "+++++++++++++++++++++++++++");
        // 保存用户角色
        String[] roles = user.getRoleId().split(StringPool.COMMA);
        setUserRoles(user, roles);

        // 创建用户默认的个性化配置
        userConfigService.initDefaultUserConfig(String.valueOf(user.getUserId()));

        // 将用户相关信息保存到 Redis中
        userManager.loadUserRedisCache(user);


        Address address = new Address();
        address.setDeptId(user.getDeptId());
        address.setPosition(user.getDescription());
        address.setEmail(user.getEmail());
        if (user.getDeptId() !=null && !"".equals(user.getDeptId())){
            Dept byId = deptService.getById(user.getDeptId());
            address.setUnit(byId.getDeptName());
        }
        address.setTelPhone(user.getMobile());
        address.setUserName(user.getUsername());
        address.setStatus(1);
        addressService.save(address);
    }

    @Override
    @Transactional
    public void updateUser(User user) throws Exception {
        Dept dept = deptService.getDept();
        String deptId = getById(user.getUserId()).getDeptId();  //此对象原先id
        if (dept.getRank() != 0)
            if (!dept.getDeptId().equals(deptId)) {//不相等则  进行查看
                if (!deptService.affiliate(dept.getDeptId(), deptId)) //不属于
                    return;
            }
        // 更新用户
        user.setPassword(null);
        user.setModifyTime(new Date());
        updateById(user);
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, user.getUserId()));

        String[] roles = user.getRoleId().split(StringPool.COMMA);
        setUserRoles(user, roles);

        // 重新将用户信息，用户角色信息，用户权限信息 加载到 redis中
        cacheService.saveUser(user.getUsername());
        cacheService.saveRoles(user.getUsername());
        cacheService.savePermissions(user.getUsername());




     /*   Address byName = addressService.findByName(user.getUsername());
        if (byName!=null){
            Address address = new Address();
            address.setDeptId(user.getDeptId());
            address.setPosition(user.getDescription());
            address.setEmail(user.getEmail());
            if (user.getDeptId() !=null && !"".equals(user.getDeptId())){
                Dept byId = deptService.getById(user.getDeptId());
                address.setUnit(byId.getDeptName());
            }
            address.setTelPhone(user.getMobile());
            address.setStatus(1);
            address.setId(byName.getId());
            addressService.updateById(address);
        }*/

    }

    @Override
    @Transactional
    public void deleteUsers(String[] userIds) throws Exception {
        Dept dept = deptService.getDept();
        String deptId = getById(userIds[0]).getDeptId();  //此对象原先id
        if (dept.getRank() != 0)
            if (!dept.getDeptId().equals(deptId)) {//不相等则  进行查看
                if (!deptService.affiliate(dept.getDeptId(), deptId)) //不属于
                    return;
            }
        // 先删除相应的缓存
        this.userManager.deleteUserRedisCache(userIds);

        List<String> list = Arrays.asList(userIds);

        removeByIds(list);

        // 删除用户角色
        this.userRoleService.deleteUserRolesByUserId(userIds);
        // 删除用户个性化配置
        this.userConfigService.deleteByUserId(userIds);
    }

    @Override
    @Transactional
    public void updateProfile(User user) throws Exception {
        updateById(user);
        // 重新缓存用户信息
        cacheService.saveUser(user.getUsername());
    }

    @Override
    @Transactional
    public void updateAvatar(String username, String avatar) throws Exception {
        User user = new User();
        user.setAvatar(avatar);

        this.baseMapper.update(user, new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        // 重新缓存用户信息
        cacheService.saveUser(username);
    }

    @Override
    @Transactional
    public void updatePassword(String username, String password) throws Exception {
        User user = new User();
        user.setPassword(MD5Util.encrypt(username, password));

        this.baseMapper.update(user, new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        // 重新缓存用户信息
        cacheService.saveUser(username);
    }

    @Override
    @Transactional
    public void regist(String username, String password) throws Exception {
        User user = new User();
        user.setPassword(MD5Util.encrypt(username, password));
        user.setUsername(username);
        user.setCreateTime(new Date());
        user.setStatus(User.STATUS_VALID);
        user.setSsex(User.SEX_UNKNOW);
        user.setAvatar(User.DEFAULT_AVATAR);
        user.setDescription("注册用户");
        this.save(user);

        UserRole ur = new UserRole();
        ur.setUserId(user.getUserId());
        ur.setRoleId(""); // 注册用户角色 ID
        this.userRoleMapper.insert(ur);

        // 创建用户默认的个性化配置
        userConfigService.initDefaultUserConfig(String.valueOf(user.getUserId()));
        // 将用户相关信息保存到 Redis中
        userManager.loadUserRedisCache(user);

    }

    @Override
    @Transactional
    public void resetPassword(String[] usernames) throws Exception {
        for (String username : usernames) {

            User user = new User();
            user.setPassword(MD5Util.encrypt(username, User.DEFAULT_PASSWORD));

            this.baseMapper.update(user, new LambdaQueryWrapper<User>().eq(User::getUsername, username));
            // 重新将用户信息加载到 redis中
            cacheService.saveUser(username);
        }

    }


    private void setUserRoles(User user, String[] roles) {
        Arrays.stream(roles).forEach(roleId -> {
            UserRole ur = new UserRole();
            ur.setUserId(user.getUserId());
            ur.setRoleId(roleId);
            this.userRoleMapper.insert(ur);
        });
    }


    @Override
    public List<User> getByDepts(String deptId) {
        return userMapper.getByDepts(deptId);
    }

    @Override
    public User getByOpenid(String openid) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getOpenid, openid);
        lambdaQueryWrapper.eq(User::getType, 1);
        return this.baseMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public List<User> nullList() {
        return userMapper.nullList();
    }
}
