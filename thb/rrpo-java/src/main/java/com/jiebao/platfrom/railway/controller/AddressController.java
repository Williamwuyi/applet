package com.jiebao.platfrom.railway.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiebao.platfrom.common.annotation.Log;
import com.jiebao.platfrom.common.authentication.JWTUtil;
import com.jiebao.platfrom.common.controller.BaseController;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.exception.JiebaoException;
import com.jiebao.platfrom.railway.dao.AddressMapper;
import com.jiebao.platfrom.railway.domain.Address;
import com.jiebao.platfrom.railway.service.AddressService;
import com.jiebao.platfrom.system.dao.DeptMapper;
import com.jiebao.platfrom.system.dao.DictMapper;
import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.domain.Dict;
import com.jiebao.platfrom.system.domain.File;
import com.jiebao.platfrom.system.domain.User;
import com.jiebao.platfrom.system.service.DeptService;
import com.jiebao.platfrom.system.service.FileService;
import com.jiebao.platfrom.system.service.UserService;
import com.wuwenze.poi.ExcelKit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author yf
 */
@Slf4j
@RestController
@RequestMapping(value = "/address")
@Api(tags = "railWay-通讯录")   //swagger2 api文档说明示例
public class AddressController extends BaseController {

    private String message;

    @Autowired
    private AddressService addressService;
    @Autowired
    private DeptMapper deptMapper;
    @Autowired
    private DeptService deptService;
    @Autowired
    private DictMapper dictMapper;
    @Autowired
    private FileService fileService;

    /**
     * 使用Mapper操作数据库
     *
     * @return JiebaoResponse 标准返回数据类型
     */
    @GetMapping
    @ApiOperation(value = "根据组织机构查询数据List", notes = "查询数据List列表", response = JiebaoResponse.class, httpMethod = "GET")
    public Map<String, Object> getAddressListByMapper(QueryRequest request, Dept dept) {
        return this.addressService.getAddressLists(request, dept);
    }


    /**
     * 分页查询
     *
     * @return
     * @Parameters sortField  according to order by Field
     * @Parameters sortOrder  JiebaoConstant.ORDER_ASC or JiebaoConstant.ORDER_DESC
     */
    @DeleteMapping("/{ids}")
    @Log("删除")
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(value = "批量删除", notes = "批量删除", httpMethod = "DELETE")
    public JiebaoResponse delete(@PathVariable String[] ids) throws JiebaoException {
        if (ids == null) {
            throw new JiebaoException("请传入参数");
        } else {
            Dept dept = deptService.getDept();
            boolean result = Arrays.stream(ids).allMatch(id -> {
                Address address = null;
                address = addressService.getById(id);
                Dept byId = deptService.getById(address.getDeptId());
                if (dept.getRank() != 0)
                    if (!dept.getDeptId().equals(address.getDeptId())) {//不相等则  进行查看
                        if (dept.getRank().equals(byId.getRank())) {
                            return false;
                        }
                        if (!deptService.affiliate(dept.getDeptId(), address.getDeptId())) {//不属于
                            return false;
                        }
                    }
                addressService.removeById(id);
                return true;
            });
            if (result) {
                return new JiebaoResponse().okMessage("删除成功");
            } else {
                return new JiebaoResponse().failMessage("由于权限原因，部分未能删除");
            }
        }
    }

    @PostMapping
    @Log("新增")
    @ApiOperation(value = "新增通讯录", notes = "新增通讯录", httpMethod = "POST")
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse addAddress(@Valid Address address) throws JiebaoException {
        try {
            Dept dept = this.deptService.getDept();
            Dept byId = deptService.getById(address.getDeptId());
            if (dept.getRank() != 0)
                if (!dept.getDeptId().equals(address.getDeptId())) {//不相等则  进行查看
                    if (dept.getRank().equals(byId.getRank())) {
                        return new JiebaoResponse().failMessage("无权限新增其他组织机构");
                    }
                    if (!deptService.affiliate(dept.getDeptId(), address.getDeptId())) { //不属于
                        return new JiebaoResponse().failMessage("当前无权限新增");
                    }
                }
            address.setStatus(1);
            if (address.getDeptId() != null) {
                Dept addressDept = deptService.getById(address.getDeptId());
                address.setUnit(addressDept.getDeptName());
            }
            this.addressService.saveOrUpdate(address);
            return new JiebaoResponse().okMessage("新增成功");
        } catch (Exception e) {
            message = "新增通讯录失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }


    @PutMapping
    @Log("修改通讯录")
    @ApiOperation(value = "修改通讯录", notes = "修改通讯录", httpMethod = "PUT")
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse updateAddress(@Valid Address address) throws JiebaoException {
        try {
            Dept dept = this.deptService.getDept();
            Dept byId = deptService.getById(address.getDeptId());
            if (dept.getRank() != 0)
                if (!dept.getDeptId().equals(address.getDeptId())) {//不相等则  进行查看
                    if (dept.getRank().equals(byId.getRank())) {
                        return new JiebaoResponse().failMessage("无权限修改其他组织机构");
                    }
                    if (!deptService.affiliate(dept.getDeptId(), address.getDeptId())) {//不属于
                        return new JiebaoResponse().failMessage("当前无权限修改");
                    }
                }
            this.addressService.updateByKey(address);
            return new JiebaoResponse().okMessage("修改成功");
        } catch (Exception e) {
            message = "修改通讯录失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }


    @PostMapping(value = "/importAddress")
    @ApiOperation(value = "导入通讯录", notes = "导入通讯录", httpMethod = "POST")
    public JiebaoResponse excelImport(@RequestParam(value = "file") MultipartFile file, String deptId) throws Exception {
        boolean result = addressService.addAddressList(file, deptId);
        if (result) {
            return new JiebaoResponse().okMessage("导入成功");
        } else {
            return new JiebaoResponse().failMessage("电话号码或用户名重复，部分或未导入成功");
        }
    }


    @PostMapping(value = "/excel")
    @ApiOperation(value = "导出", notes = "导出", httpMethod = "POST")
    public void export(HttpServletResponse response, QueryRequest request, Address address, String userName, String telPhone) throws JiebaoException {
        try {
           // List<Address> addresses = this.addressService.addressList(address, request);
            List<Address> addresses =  this.addressService.addressListNew(request,address,userName,telPhone);
            for (Address addr : addresses
            ) {
                /*if (addr.getDeptId() == null) {
                    addr.setDeptName("");
                } else {
                    Dept byId = deptMapper.getById(addr.getDeptId());
                    if (byId == null) {
                        addr.setDeptName("");
                    } else {
                        addr.setDeptName(byId.getDeptName());
                    }
                }*/
                if (addr.getPosition() != null) {
                    String s = addr.getPosition().replaceAll("^,*|,*$", "");
                    List result = Arrays.asList(s.split(","));
                    QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
                    queryWrapper.in("dict_id", result);
                    List<Dict> userInfoList = dictMapper.selectList(queryWrapper);
                    List<String> position = userInfoList.stream().map(p -> p.getFieldName()).collect(Collectors.toList());
                    String join = String.join(",", position);
                    addr.setPosition(join);
                }
            }
            ExcelKit.$Export(Address.class, response).downXlsx(addresses, false);
        } catch (Exception e) {
            message = "导出Excel失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }


    @GetMapping("/iPage")
    @Log("根据组织机构分页查看通讯录")
    @ApiOperation(value = "根据组织机构分页查看通讯录", notes = "根据组织机构分页查看通讯录", httpMethod = "GET")
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse findByDept(QueryRequest request, Address address, String userName, String telPhone) {
        IPage<Address> deptList = addressService.getByDept(request, address, userName, telPhone);
        List<Address> records = deptList.getRecords();
        for (Address a : records
        ) {
            Dept byId = deptService.getById(a.getDeptId());
            if (byId != null) {
                a.setDeptName(byId.getDeptName());
            }
            if (a.getPosition() != null) {
                //解析职位ID为name
                String s = a.getPosition().replaceAll("^,*|,*$", "");
                List result = Arrays.asList(s.split(","));
                QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
                queryWrapper.in("dict_id", result);
                List<Dict> userInfoList = dictMapper.selectList(queryWrapper);
                List<String> position = userInfoList.stream().map(p -> p.getFieldName()).collect(Collectors.toList());
                String join = String.join(",", position);
                a.setPosition(join);

                String positionId = String.join(",", result);
                a.setPositionId(positionId);

            }
        }
        return new JiebaoResponse().data(this.getDataTable(deptList));
    }



    /**
     * 文件下载
     *
     * @param
     */
    @ApiOperation("下载模板接口")
    @RequestMapping("/downloadFile")
    public void downloadFile( HttpServletResponse response) {

        File file = fileService.getById("f6dc2572334aee5a08ad845409c8d731");
        java.io.File downloadFile = new java.io.File(file.getFileUrl() + file.getNewName());
        if (downloadFile.exists()) {
            try {
                InputStream is = new BufferedInputStream(new FileInputStream(downloadFile));
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                is.close();
                response.addHeader("Content-Disposition", "attachment;filename=" + new String(file.getOldName().getBytes("UTF-8"), "ISO-8859-1"));
                OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
                response.setContentType("application/octet-stream");
                outputStream.write(buffer);
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                log.warn("File download Exception：" + e);
            }
        }
    }
}
