package com.jiebao.platfrom.wx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.utils.CheckExcelUtil;
import com.jiebao.platfrom.common.utils.IDCard;
import com.jiebao.platfrom.system.dao.DeptMapper;
import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.service.DeptService;
import com.jiebao.platfrom.wx.dao.PeopleMapper;
import com.jiebao.platfrom.wx.domain.People;
import com.jiebao.platfrom.wx.domain.UserI;
import com.jiebao.platfrom.wx.service.IPeopleService;
import com.jiebao.platfrom.wx.service.IUserIService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qta
 * @since 2020-10-16
 */
@Service
public class PeopleServiceImpl extends ServiceImpl<PeopleMapper, People> implements IPeopleService {

    @Autowired
    DeptMapper deptMapper;
    @Autowired
    DeptService deptService;
    //    private static int lock = 1;//默认上锁
    @Autowired
    IUserIService userIService;

    @Override
    public JiebaoResponse listPage(QueryRequest queryRequest, String deptId, String LineId, Integer status, String km) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        Page<People> page = new Page<>(queryRequest.getPageNum(), queryRequest.getPageSize());
        return jiebaoResponse.data(this.baseMapper.listPage(page, packQueryWra(deptId, LineId, status, km))).message("查询成功");
    }

    @Override
    public JiebaoResponse listExcel(HttpServletResponse response, String deptId, String lineId, Integer status, String title, String km) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        List<People> lists = this.baseMapper.lists(packQueryWra(deptId, lineId, status, km));
        CheckExcelUtil.exportList(response, lists, People.class, null, title);
        return jiebaoResponse.message("查询成功");
    }

    private QueryWrapper<People> packQueryWra(String deptId, String lineId, Integer status, String km) {
        QueryWrapper<People> queryWrapper = new QueryWrapper<>();
        Dept dept = null;
        if (deptId == null) {
            dept = deptService.getDept();
            deptId = dept.getDeptId();
        } else {
            dept = deptService.getById(deptId);
        }
        Integer rank = dept.getRank();
        String column = "";
        if (rank == 1)
            column = "shi";
        if (rank == 2)
            column = "qu_xian";
        if (rank == 3)
            column = "xiang";
        if (rank != 0)
            queryWrapper.eq(column, deptId);
        if (StringUtils.isNotBlank(lineId))
            queryWrapper.eq("line", lineId);
        if (StringUtils.isNoneBlank(km))
            queryWrapper.eq("km", km);
        queryWrapper.eq("status", status);
        queryWrapper.orderByAsc("lu_number");
        return queryWrapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse saveOrUpdateChile(People people, String qunId) {
        packEntity(people);
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        if (qunId == null) {
            return jiebaoResponse.failMessage("未建立群");
        } else {
            UserI userI = new UserI(people.getName(), people.getIdCard(), people.getPhone(), people.getProper(), qunId, people.getXiang(), people.getQuXian(), people.getShi());
            userI.setDate(new Date());
            userIService.save(userI);
        }
        if (deptService.getById(people.getDeptId()).getRank() != 3)  //必须精确到乡镇
            return jiebaoResponse.failMessage("请选择到乡镇街道");
        if (people.getHlId() == null) {
            people.setCreatTime(new Date());
            people.setStatus(1);
        } else {   //修改   如果 改身份证号码  或者名字 等同 删除  在新增一个
            People peopleOld = getById(people.getHlId());  //数据库的对象
            if ((!people.getName().equals(peopleOld.getName())) || (!people.getIdCard().equals(peopleOld.getIdCard()))) { //有一个修改则视为  新增删减人数
                peopleOld.setStatus(3);
                updateById(peopleOld);  //老数据定义为将要删除的
                people.setHlId(null);
                people.setStatus(1); //新数据 定义为 添加的
                save(people);
            } else
                updateById(people.setStatus(2));//定义为 修改的
        }
        jiebaoResponse = super.saveOrUpdate(people) ? jiebaoResponse.okMessage("操作成功").data(people) : jiebaoResponse.failMessage("操作失败").data(people);
        return jiebaoResponse;
    }


    private void packEntity(People people) { //封装
        try {
            Map<String, Object> map = IDCard.identityCard18(people.getIdCard());
            people.setAge((Integer) map.get("age"));
            people.setSex((String) map.get("sex"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Dept dept = deptService.getById(people.getDeptId());
        if (dept.getRank() == 3) {
            people.setXiang(dept.getDeptId());
            Dept quXian = deptService.getById(dept.getParentId()); //区县级别
            people.setQuXian(quXian.getDeptId());
            Dept shiZhou = deptService.getById(quXian.getParentId());  //市州级别
            people.setShi(shiZhou.getDeptId());
        }
        if (dept.getRank() == 2) {
            people.setQuXian(dept.getDeptId());
            Dept shiZhou = deptService.getById(dept.getParentId()); //区县级别
            people.setShi(shiZhou.getDeptId());
        }
        if (dept.getRank() == 1) {
            people.setShi(dept.getDeptId());
        }
        if (people.getLu1() != null)
            people.setLuDuan(people.getLu1() + people.getLu2() + "+" + people.getLu3() + "-" + people.getLu1() + people.getLu4() + "+" + people.getLu5());
        people.setLuNumber(people.getLu2());
    }

//    @Override
//    public JiebaoResponse lock(Integer status) {
//        lock = status;
//        return new JiebaoResponse().okMessage("操作成功");
//    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean removeByIds(Collection<? extends Serializable> idList) {
//        if (isNotLock())
//            return false;
//        UpdateWrapper<People> updateWrapper = new UpdateWrapper<>();
//        updateWrapper.in("hl_id", idList);
//        updateWrapper.set("status", 3);//提示将要删除的
//        return update(updateWrapper);
//    }

//    @Override
//    public JiebaoResponse checkLock() {
//        JiebaoResponse jiebaoResponse = new JiebaoResponse();
//        return isNotLock() ? jiebaoResponse.okMessage("状态锁定") : jiebaoResponse.failMessage("锁定解除");
//    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse confirm(String[] ids, Integer status) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        UpdateWrapper<People> updateWrapper = new UpdateWrapper<>();
        if (status == 3)
            updateWrapper.lambda().set(People::getStatus, 4);
        else
            updateWrapper.lambda().set(People::getStatus, 5);
        updateWrapper.lambda().in(People::getHlId, Arrays.asList(ids));
        return update(updateWrapper) ? jiebaoResponse.okMessage("操作成功") : jiebaoResponse.failMessage("操作失败");
    }

//    private boolean isNotLock() { //判断当前是否上锁
//        return lock == 1 ? true : false;
//    }

    @Override
    public boolean excelImPort(MultipartFile file) {
        List<People> list = new ArrayList<>();
        try {
            InputStream inputStream = file.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            for (int i = 0; i < workbook.getNumberOfSheets(); i++
            ) {
                XSSFSheet sheetAt = workbook.getSheetAt(i);//每个工作区
                for (int j = 1; j < sheetAt.getPhysicalNumberOfRows(); j++
                ) {
                    People people = new People();
                    people.setShi(sheetAt.getSheetName());
                    XSSFRow row = sheetAt.getRow(j);//每一行
                    for (int k = 0; k < row.getPhysicalNumberOfCells(); k++
                    ) {
                        XSSFCell cell = row.getCell(k);
                        if (cell == null)
                            continue;
                        cell.setCellType(CellType.STRING);
                        System.out.println(k);
//                        cell.setCellType(CellType.STRING);
                        String value = "";
                        try {
                            value = cell.getStringCellValue();
                        } catch (NullPointerException e) {
                            continue;
                        }
                        if (k == 0)
                            people.setQuXian(value);
                        if (k == 1)
                            people.setXiang(value);
                        if (k == 2)
                            people.setProper(value);
                        if (k == 3)
                            people.setLine(value);
                        if (k == 4)
                            people.setName(value);
                        if (k == 5)
                            people.setSex(value);
                        if (k == 6)
                            try {
                                people.setAge(Integer.parseInt(value));
                            } catch (NumberFormatException e) {

                            }
                        if (k == 7)
                            people.setFace(value);
                        if (k == 8)
                            people.setAddress(value);
                        if (k == 9)
                            people.setIdCard(value);
                        if (k == 10)
                            people.setLuDuan(value);
                        if (k == 11)
                            people.setPhone(value);
                        if (k == 12)
                            people.setIsWx(value);
                        if (k == 13)
                            people.setIsQun(value);
                        if (k == 14)
                            people.setPolice(value);
                    }
                    list.add(people);
                }
            }
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return saveBatch(list);
    }

}
