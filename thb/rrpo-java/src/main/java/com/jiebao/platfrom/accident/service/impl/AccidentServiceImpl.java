package com.jiebao.platfrom.accident.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.accident.dao.AccidentMapper;
import com.jiebao.platfrom.accident.dao.DeptSMapper;
import com.jiebao.platfrom.accident.daomain.Accident;
import com.jiebao.platfrom.accident.daomain.CompareTable;
import com.jiebao.platfrom.accident.service.IAccidentService;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.system.dao.DeptMapper;
import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qta
 * @since 2020-08-04
 */
@Service
public class AccidentServiceImpl extends ServiceImpl<AccidentMapper, Accident> implements IAccidentService {
    @Autowired
    DeptService deptService;
    @Autowired
    DeptSMapper deptSMapper;
    @Autowired
    DeptMapper deptMapper;

    @Override
    public JiebaoResponse list(QueryRequest queryRequest, String policeId, String cityLevelId, String quDeptId, String startDate, String endDate) {
        QueryWrapper<Accident> queryWrapper = new QueryWrapper<>();
        if (quDeptId != null) {
            queryWrapper.eq("city_qx_id", quDeptId);
        } else if (cityLevelId != null) {
            queryWrapper.eq("city_cs_id", cityLevelId);
        } else if (policeId != null) {
            queryWrapper.in("city_cs_id", deptSMapper.selectDeptIds(policeId));
        } else {  //
            Dept dept = deptService.getDept();
            if (!dept.getDeptId().equals("0"))
                queryWrapper.eq("city_cs_id", dept.getDeptId());
        }
        if (startDate != null) {
            queryWrapper.ge("month", startDate);    //不能小于此时间
        }
        if (endDate != null) {
            queryWrapper.le("month", endDate);//不能大于此时间
        }
        queryWrapper.orderByDesc("date");
        Page<Accident> page = new Page<>(queryRequest.getPageNum(), queryRequest.getPageSize());
        return new JiebaoResponse().data(this.baseMapper.ListPage(page, queryWrapper)).message("查询成功");
    }

    @Override
    public JiebaoResponse map(String policeId, String cityLevelId, String startDate, String endDate, String quDeptId) {  //视图接口
        QueryWrapper<Accident> queryWrapper = new QueryWrapper<>();
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        if (quDeptId != null) {
            queryWrapper.eq("city_qx_id", quDeptId);
        }
        if (cityLevelId != null) {
            queryWrapper.eq("city_cs_id", cityLevelId);
        } else if (policeId != null) {
            queryWrapper.in("city_cs_id", deptSMapper.selectDeptIds(policeId));
        }
        if (startDate != null) {
            queryWrapper.ge("month", startDate);    //不能小于此时间
        }
        if (endDate != null) {
            queryWrapper.le("month", endDate);//不能大于此时间
        }
        String[] stringDict = new String[]{"nature", "instation_section", "road", "age", "closed", "jzd", "distance", "identity", "conditions"};  //存储字典类
        for (String column : stringDict
        ) {
            QueryWrapper<Accident> clone = queryWrapper.clone();
            clone.groupBy(column);
            jiebaoResponse.put(column, this.baseMapper.listCg(clone, column));
        }

        return jiebaoResponse.okMessage("查询成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse lock(String[] accidentId, String month, Integer status) {//上锁
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        UpdateWrapper<Accident> qw = new UpdateWrapper<>();
        qw.set("statu", status);
        if (accidentId != null && accidentId.length != 0) {
            qw.in("accident_id", Arrays.asList(accidentId));
        } else {
            if (month == null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
                month = simpleDateFormat.format(new Date());
            }
            qw.likeRight("date", month);
        }
        jiebaoResponse = update(qw) ? jiebaoResponse.okMessage("操作成功") : jiebaoResponse.failMessage("操作失败");
        return jiebaoResponse;
    }

    @Override
    public String func(String nature, String instationSection, String road, String age, String closed, String jzd, String distance, String identity, String conditions) {
        String sql = "";
        if (nature != null && !nature.equals("A"))
            return "B";
        else {
            if (nature != null && instationSection != null && nature.equals("A") && instationSection.equals("A"))
                return "B";
            else {
                if (road == null || age == null || closed == null ||
                        jzd == null || distance == null || identity == null ||
                        conditions == null)
                    return "";
                sql = "select dnfxxs from zd_dnfxxs where ";
                sql += "  instr(zd3,'" + road + "')>0 ";
                sql += " and instr(zd4,'" + age + "')>0 ";
                sql += " and instr(zd5,'" + closed + "')>0 ";
                sql += " and instr(zd6,'" + jzd + "')>0 ";
                sql += " and instr(zd7,'" + distance + "')>0 ";
                sql += " and instr(zd8,'" + identity + "')>0 ";
                sql += " and instr(zd9,'" + conditions + "')>0 ";
                sql += " limit 1";
            }
        }
        return this.baseMapper.getXs(sql);
    }

    /**
     * //返回  本期  上期表
     *
     * @param startYear 开始年月
     * @param endYear   结束年月
     * @return
     */
    @Override
    public List<CompareTable> compareTable(String startYear, String endYear) {  // 代码过长======================>建议照着页面重写 速度更快  主要公式===?  (上期-下期)/上期  得到系数
        List<CompareTable> compareTables = new ArrayList<>();  //获得 市州 本期
        List<Dept> deptNameByAsc = deptMapper.getDeptNameByAsc();
        for (Dept dept : deptNameByAsc) {
            compareTables.add(this.baseMapper.shiTable(startYear, endYear, dept.getDeptId()).setName(dept.getDeptName()));
        }
        List<Dept> deptNameByAscGz = deptMapper.getDeptNameByAscGz();
        for (Dept dept : deptNameByAscGz) {
            compareTables.add(this.baseMapper.gzTable(startYear, endYear, dept.getDeptId()).setName(dept.getDeptName()));
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        Map<String, CompareTable> map = new HashMap<>();
        for (CompareTable compareTable : compareTables) {
            map.put(compareTable.getDeptName(), compareTable);
        }

        try {
            Date parse = simpleDateFormat.parse(startYear);//本期开始
            Date parse1 = simpleDateFormat.parse(endYear);//本期结束
            long l = parse1.getTime() - parse.getTime();
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            start.setTime(parse);
            end.setTime(parse1);
            int i1 = start.get(Calendar.MONTH) - end.get(Calendar.MONTH);
            int i2 = (start.get(Calendar.YEAR) - end.get(Calendar.YEAR)) * 12;
            int sum = Math.abs(i1 + i2);
            Date parse2 = null;
            Date parse3 = null;
            if (sum < 12) {   //相差 小于 12个小时 就是同比
                start.add(Calendar.YEAR, -1);
                end.add(Calendar.YEAR, -1);
                parse2 = start.getTime();
                parse3 = end.getTime();
            } else {  //达到一年 环比
                parse2 = new Date(parse.getTime() - l); //上期  开始
                parse3 = new Date(parse1.getTime() - l);//上期  结束
            }
            String upStartDate = simpleDateFormat.format(parse2);
            String upendDate = simpleDateFormat.format(parse3);
            List<CompareTable> list = new ArrayList<>();
            for (Dept dept : deptNameByAsc) {
                list.add(this.baseMapper.shiTableUP(upStartDate, upendDate, dept.getDeptId()).setName(dept.getDeptName()));
            }
            for (Dept dept : deptNameByAscGz) {
                list.add(this.baseMapper.gzTableUP(upStartDate, upendDate, dept.getDeptId()).setName(dept.getDeptName()));
            }
            CompareTable compareTableCount = new CompareTable("全省合计", 0,
                    0, 0.00,
                    0.00, 0.00,
                    0.00, 0.00,
                    0.00, 0.00,
                    0.00, 0.00,
                    0.00); //计算总的
            DecimalFormat decimalFormat = new DecimalFormat("0.0000");
            for (CompareTable compareTable : list) {
                CompareTable compareTable1 = map.get(compareTable.getDeptName());//已经保存的数值
                compareTable1.setUpNumber(compareTable.getUpNumber() == null ? 0 : compareTable.getUpNumber());
                compareTable1.setUpDnxs(compareTable.getUpDnxs() == null ? 0 : compareTable.getUpDnxs());
                compareTable1.setUpDnTjxs(compareTable.getUpDnTjxs() == null ? 0 : compareTable.getUpDnTjxs());
                compareTable1.setUpDeathToll(compareTable.getUpDeathToll() == null ? 0 : compareTable.getUpDeathToll());
                double i = (double) (compareTable1.getUpNumber() - compareTable1.getNumber()) / compareTable1.getUpNumber();
                try {
                    compareTable1.setBj1(getDouble(i));
                } catch (NumberFormatException e) {
                    compareTable1.setBj1(0.00);
                }
                try {
                    compareTable1.setBj2(getDouble((compareTable1.getUpDnxs() - compareTable1.getDnxs()) / compareTable1.getUpDnxs()));
                } catch (NumberFormatException e) {
                    compareTable1.setBj2(0.00);
                }
                try {
                    compareTable1.setBj3(getDouble((compareTable1.getUpDnTjxs() - compareTable1.getDntjxs()) / compareTable1.getUpDnTjxs()));
                } catch (NumberFormatException e) {
                    compareTable1.setBj3(0.00);
                }
                try {
                    compareTable1.setBj4(getDouble((compareTable1.getUpDeathToll() - compareTable1.getDeathToll()) / compareTable1.getUpDeathToll()));
                } catch (NumberFormatException e) {
                    compareTable1.setBj4(0.00);
                }
                if (compareTable1.getDeptName().contains("公安处")) {  //公安处级别

                    compareTableCount.setNumber(compareTableCount.getNumber() + compareTable1.getNumber());
                    compareTableCount.setUpNumber(compareTableCount.getUpNumber() + compareTable1.getUpNumber());

                    compareTableCount.setDnxs(compareTableCount.getDnxs() + compareTable1.getDnxs());
                    compareTableCount.setUpDnxs(compareTableCount.getUpDnxs() + compareTable1.getUpDnxs());


                    compareTableCount.setDntjxs(compareTableCount.getDntjxs() + compareTable1.getDntjxs());
                    compareTableCount.setUpDnTjxs(compareTableCount.getUpDnTjxs() + compareTable1.getUpDnTjxs());

                    compareTableCount.setDeathToll(getDouble(compareTableCount.getDeathToll() + compareTable1.getDeathToll()));
                    compareTableCount.setUpDeathToll(getDouble(compareTableCount.getUpDeathToll() + compareTable1.getUpDeathToll()));
                    try {
                        if (compareTableCount.getUpNumber() != 0)
                            compareTableCount.setBj1(getDouble((double) (compareTableCount.getUpNumber() - compareTableCount.getNumber()) / compareTableCount.getUpNumber()));
                    } catch (NumberFormatException e) {
                        compareTableCount.setBj1(0.00);
                    }
                    try {
                        if (compareTableCount.getUpDnxs() != 0)
                            compareTableCount.setBj2(getDouble((compareTableCount.getUpDnxs() - compareTableCount.getDnxs()) / compareTableCount.getUpDnxs()));
                    } catch (NumberFormatException e) {
                        compareTableCount.setBj2(0.00);
                    }
                    try {
                        if (compareTableCount.getUpDnTjxs() != 0)
                            compareTableCount.setBj3(getDouble((compareTableCount.getUpDnTjxs() - compareTableCount.getDntjxs()) / compareTableCount.getUpDnTjxs()));
                    } catch (NumberFormatException e) {
                        compareTableCount.setBj3(0.00);
                    }
                    try {
                        if (compareTableCount.getUpDeathToll() != 0)
                            compareTableCount.setBj4(getDouble(((compareTableCount.getUpDeathToll() - compareTable1.getDeathToll()) / compareTableCount.getUpDeathToll())));
                    } catch (NumberFormatException e) {
                        compareTableCount.setBj4(0.00);
                    }
                }
            }
            compareTables.add(compareTableCount);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (CompareTable compareTable : compareTables) {
            System.out.println(compareTable);
        }

        return compareTables;
    }

    private Double getDouble(Double math) {
        DecimalFormat decimalFormat = new DecimalFormat("0.0000");
        try {
            return Double.valueOf(decimalFormat.parse(decimalFormat.format(math)).toString());
        } catch (NumberFormatException e) {
            return 0.00;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0.00;
        }

    }


}
