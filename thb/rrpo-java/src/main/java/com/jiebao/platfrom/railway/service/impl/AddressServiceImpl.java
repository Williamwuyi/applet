package com.jiebao.platfrom.railway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.common.authentication.JWTUtil;
import com.jiebao.platfrom.common.domain.JiebaoConstant;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.domain.Tree;
import com.jiebao.platfrom.common.utils.SortUtil;
import com.jiebao.platfrom.common.utils.TreeUtil;
import com.jiebao.platfrom.railway.dao.AddressMapper;
import com.jiebao.platfrom.railway.domain.Address;

import com.jiebao.platfrom.railway.domain.Inform;
import com.jiebao.platfrom.railway.service.AddressService;
import com.jiebao.platfrom.system.dao.DeptMapper;
import com.jiebao.platfrom.system.dao.DictMapper;
import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.domain.Dict;
import com.jiebao.platfrom.system.domain.User;
import com.jiebao.platfrom.system.service.DeptService;
import com.jiebao.platfrom.system.service.UserService;
import com.jiebao.platfrom.system.service.impl.DeptServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.*;


@Slf4j
@Service("AddressService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    @Autowired
    AddressMapper addressMapper;

    @Autowired
    DeptService deptService;

    @Autowired
    DeptMapper deptMapper;

    @Autowired
    AddressService addressService;

    @Autowired
    UserService userService;

    @Autowired
    DictMapper dictMapper;


    @Override
    public IPage<Address> getAddressList(QueryRequest request) {

        LambdaQueryWrapper<Address> lambdaQueryWrapper = new LambdaQueryWrapper();
        Page<Address> page = new Page<>(request.getPageNum(), request.getPageSize());
        lambdaQueryWrapper.orderByDesc(Address::getId);
        return this.baseMapper.selectPage(page, lambdaQueryWrapper);
    }

    @Override
    public Map<String, Object> getAddressLists(QueryRequest request, Dept dept) {

        Map<String, Object> result = new HashMap<>();
        try {
            List<Dept> depts = deptService.findDept(dept, request);
            List<Tree<Dept>> trees = new ArrayList<>();
            buildTrees(trees, depts);
            Tree<Dept> deptTree = TreeUtil.builds(trees);
            result.put("rows", deptTree);
            result.put("total", depts.size());
        } catch (Exception e) {
            log.error("获取部门列表失败", e);
            result.put("rows", null);
            result.put("total", 0);
        }
        return result;
    }


    @Override
    public List<Address> findAddresses(QueryRequest request, Address address) {

        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        SortUtil.handleWrapperSort(request, queryWrapper, "creat_time", JiebaoConstant.ORDER_DESC, true);
        List<Address> addresses = baseMapper.selectList(queryWrapper);
        for (Address s : addresses) {
            String deptId = s.getDeptId();
            Dept byId = deptService.getById(deptId);
            s.setDeptName(byId.getDeptName());
        }
        return addresses;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateByKey(Address address) {
        Address byId = addressService.getById(address.getId());
        Dept addressDept = deptService.getById(byId.getDeptId());
        address.setUnit(addressDept.getDeptName());
        this.addressMapper.updateById(address);
    }


    private void buildTrees(List<Tree<Dept>> trees, List<Dept> depts) {

        depts.forEach(dept -> {
            List<Address> address = addressMapper.getAddressList(dept.getDeptId());
            Tree<Dept> tree = new Tree<>();
            tree.setId(dept.getDeptId());
            tree.setKey(tree.getId());
            tree.setParentId(dept.getParentId());
            tree.setText(dept.getDeptName());
            tree.setCreateTime(dept.getCreateTime());
            tree.setModifyTime(dept.getModifyTime());
            tree.setOrder(dept.getOrderNum());
            tree.setTitle(tree.getText());
            tree.setValue(tree.getId());


            List<Tree<Dept>> childList = new ArrayList<>();
            for (int i = 0; i < address.size(); i++) {
                Tree<Dept> data = new Tree<>();
                Address info = address.get(i);
                data.setId(info.getId());
                data.setKey(info.getId());
                data.setWeiXin(info.getWeiXin());
                data.setUserName(info.getUserName());
                data.setDeptName(info.getDeptName());
                data.setCreateTime(info.getCreatTime());
                data.setPhone(info.getPhone());
                data.setTelPhone(info.getTelPhone());
                data.setEmail(info.getEmail());
                data.setDeptId(info.getDeptId());
                childList.add(data);
            }
            tree.setChildren(childList);
            trees.add(tree);
        });
    }


    @Override
    public boolean addAddressList(MultipartFile file, String deptId) throws Exception {
        boolean index = true;
        List<Address> addressList = new ArrayList<>();
        String filename = file.getOriginalFilename();
        String sub = filename.substring(filename.lastIndexOf(".") + 1);
        InputStream inputStream = file.getInputStream();
        Workbook wb = null;
        /**
         * HSSFWorkbook:是操作Excel2003以前（包括2003）的版本，扩展名是.xls
         * XSSFWorkbook:是操作Excel2007的版本，扩展名是.xlsx
         */
        if ("xlsx".equals(sub)) {
            wb = new XSSFWorkbook(inputStream);
        } else {
            wb = new HSSFWorkbook(inputStream);
        }
        Sheet sheet = wb.getSheetAt(0);
        if (sheet != null) {
            //getLastRowNum()返回最后一行的索引，即比行总数小1

            for (int line = 1; line <= sheet.getLastRowNum(); line++) {
                Address address = new Address();
                Row row = sheet.getRow(line);
                if (row == null) {
                    continue;
                }
                row.getCell(0).setCellType(CellType.STRING);
                if (CellType.STRING != row.getCell(0).getCellTypeEnum()) {
                    throw new Exception("单元格类型不是文本类型");
                }
                /**
                 * 获取第一个单元格的内容
                 */
                String userName = row.getCell(0).getStringCellValue();
                if (row.getCell(1) == null) {
                    row.createCell(1).setCellType(CellType.STRING);
                } else {
                    row.getCell(1).setCellType(CellType.STRING);
                }
                if (row.getCell(2) == null) {
                    row.createCell(2).setCellType(CellType.STRING);
                    ;
                } else {
                    row.getCell(2).setCellType(CellType.STRING);
                }
                if (row.getCell(3) == null) {
                    row.createCell(3).setCellType(CellType.STRING);
                    ;
                } else {
                    row.getCell(3).setCellType(CellType.STRING);
                }
                if (row.getCell(4) == null) {
                    row.createCell(4).setCellType(CellType.STRING);
                    ;
                } else {
                    row.getCell(4).setCellType(CellType.STRING);
                }
                if (row.getCell(5) == null) {
                    row.createCell(5).setCellType(CellType.STRING);
                    ;
                } else {
                    row.getCell(5).setCellType(CellType.STRING);
                }




                String position = row.getCell(1).getStringCellValue();
                String newPosition = row.getCell(2).getStringCellValue();
                String phone = row.getCell(3).getStringCellValue();
                String telPhone = row.getCell(4).getStringCellValue();
                String email  = row.getCell(5).getStringCellValue();


                // if (StringUtils.isNotBlank(deptId) && !"".equals(userName) && !"".equals(position) && !"".equals(unit) && !"".equals(weiXin) && !"".equals(telPhone) && !"".equals(email) && !"".equals(phone)) {
                address.setUserName(userName);
                address.setPhone(phone);
                Map<String, Object> map = new HashMap<>();
                map.put("tel_phone", telPhone);
                List<Address> addresses = addressMapper.selectByMap(map);
                address.setTelPhone(telPhone);
                address.setEmail(email);
                if (deptId !=null){
                    Dept byId = deptService.getById(deptId);
                    address.setUnit(byId.getDeptName());
                }
                List result = Arrays.asList(position.split(","));
                String dickName =",";
                for (int i = 0; i < result.size(); i++) {
                    Map<String, Object> hashMap = new HashMap<>();
                    hashMap.put("parent_id","2a6c549aa14e2f77837b1a11f6e7ad0f");
                    hashMap.put("field_name", result.get(i));
                    List<Dict> dicts = dictMapper.selectByMap(hashMap);
                    if (dicts!=null){
                        for (Dict d:dicts
                        ) {
                            dickName =dickName+d.getDictId()+",";
                        }
                    }
                }
                if (",".equals(dickName)){
                    address.setPosition(null);
                }
                else {
                    address.setPosition(dickName);
                }
                address.setNewPosition(newPosition);
                if (deptId == null) {
                    Dept dept = this.deptService.getDept();
                    address.setDeptId(dept.getDeptId());
                } else {
                    address.setDeptId(deptId);
                }
                address.setStatus(2);
                if (addresses.isEmpty()) {
                    addressList.add(address);
                } else {
                    index = false;
                }
            }
            for (Address address : addressList) {
                addressService.save(address);
            }
        }
        return index;
    }


    @Override
    public List<Address> addressList(Address address, QueryRequest request) {

        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(address.getDeptId())) {
            queryWrapper.lambda().eq(Address::getDeptId, address.getDeptId());
        }
        SortUtil.handleWrapperSort(request, queryWrapper, "creatTime", JiebaoConstant.ORDER_ASC, true);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<Address> addressListNew(QueryRequest request, Address address, String userName, String telPhone) {
        LambdaQueryWrapper<Address> lambdaQueryWrapper = new LambdaQueryWrapper();
        if (StringUtils.isNotBlank(address.getDeptId())) {
            lambdaQueryWrapper.eq(Address::getDeptId, address.getDeptId());
        }
        if (StringUtils.isNotBlank(userName)) {
            lambdaQueryWrapper.like(Address::getUserName, userName);
        }
        if (StringUtils.isNotBlank(telPhone)) {
            lambdaQueryWrapper.like(Address::getTelPhone, telPhone);
        }
        if (StringUtils.isNotBlank(address.getPosition())) {
            String position = "," + address.getPosition() + ",";
            lambdaQueryWrapper.like(Address::getPosition, position);
        }
        lambdaQueryWrapper.orderByDesc(Address::getCreatTime);
        return this.baseMapper.selectList(lambdaQueryWrapper);
    }


    @Override
    public IPage<Address> getByDept(QueryRequest request, Address address, String userName, String telPhone) {

        LambdaQueryWrapper<Address> lambdaQueryWrapper = new LambdaQueryWrapper();
        Dept dept = deptService.getDept();
     //   List<Dept> allId = deptService.getDepts(dept.getDeptId());
        if (address == null || "".equals(address)) {
            lambdaQueryWrapper.eq(Address::getDeptId,dept.getDeptId());
        }
        if (StringUtils.isNotBlank(address.getDeptId())) {
            lambdaQueryWrapper.eq(Address::getDeptId, address.getDeptId());
        }
        if (StringUtils.isNotBlank(userName)) {
         //   lambdaQueryWrapper.in(Address::getDeptId,allId);
            lambdaQueryWrapper.like(Address::getUserName, userName);
        }
        if (StringUtils.isNotBlank(telPhone)) {
      //      lambdaQueryWrapper.in(Address::getDeptId,allId);
            lambdaQueryWrapper.like(Address::getTelPhone, telPhone);
        }
        if (StringUtils.isNotBlank(address.getPosition())) {
        //    lambdaQueryWrapper.in(Address::getDeptId,allId);
            String position = "," + address.getPosition() + ",";
            lambdaQueryWrapper.like(Address::getPosition, position);
        }
        Page<Address> page = new Page<>(request.getPageNum(), request.getPageSize());
        lambdaQueryWrapper.orderByDesc(Address::getCreatTime);

        return this.baseMapper.selectPage(page, lambdaQueryWrapper);
    }

    @Override
    public List<Address> getBookList(String deptRoot) {
        LambdaQueryWrapper<Address> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Address::getDeptId, deptRoot);
        return this.baseMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<Address> getList(List<String> deptIdList) {
        LambdaQueryWrapper<Address> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Address::getDeptId, deptIdList);
        return this.baseMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public Address findByName(String username) {
        return baseMapper.selectOne(new LambdaQueryWrapper<Address>().eq(Address::getUserName, username));
    }
}
