package com.jiebao.platfrom.common.utils;

import com.jiebao.platfrom.attendance.daomain.Record;
import com.jiebao.platfrom.check.dao.MenusYearMapper;
import com.jiebao.platfrom.check.dao.YearBindMenusMapper;
import com.jiebao.platfrom.check.domain.MenusYear;
import com.jiebao.platfrom.check.service.IMenusService;
import com.jiebao.platfrom.check.service.IMenusYearService;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.system.domain.User;
import com.jiebao.platfrom.system.service.UserService;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

public class CheckExcelUtil {
   @Transactional(rollbackFor = Exception.class)
    public static JiebaoResponse excel(String yearId, MultipartFile file, IMenusService menusService, IMenusYearService menusYearService, MenusYearMapper menusYearMapper, YearBindMenusMapper yearBindMenus, List<MenusYear> list) {  //年考核 题库导入
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        boolean b = false;
        try {

            Workbook workbook = null;
            try {
                InputStream inputStream = file.getInputStream();
                workbook = new HSSFWorkbook(inputStream);//2007
            } catch (OfficeXmlFileException e) {
                InputStream inputStream = file.getInputStream();
                workbook = new XSSFWorkbook(inputStream);//2007
            }
            Sheet sheetAt = workbook.getSheetAt(0);
            Row row = sheetAt.getRow(0);
            String[] arr = new String[row.getPhysicalNumberOfCells() ];
            for (int i = 0; i < row.getPhysicalNumberOfCells() ; i +=3) {
                Cell cell = row.getCell(i);
                if (cell == null) {
                    cell = row.createCell(i);
                }
                cell.setCellType(CellType.STRING);
                String stringCellValue = cell.getStringCellValue(); //表格内容
                if (yearBindMenus.exist(yearId, menusService.selectByName(stringCellValue)) == null) {
                    return jiebaoResponse.failMessage("本年考核规则不存在此模块" + stringCellValue);
                }
                arr[i] = menusService.selectByName(stringCellValue);
            }
            int sort = 0;   //排序用的
            for (int i = 1; i < sheetAt.getPhysicalNumberOfRows(); i++) {
                Row row1 = sheetAt.getRow(i); //对应行
                for (int j = 0; j < row.getPhysicalNumberOfCells(); j += 3) {
                    Cell cell = row1.getCell(j);
                    if (cell == null) {  //空返回
                        continue;
                    }
                    cell.setCellType(CellType.STRING);
                    if (menusYearMapper.exSit(cell.getStringCellValue(), yearId) != null) { //存在返回
                        continue;
                    }
                    Cell cell1 = row1.getCell(j + 1); //摘要
                    MenusYear menusYear = new MenusYear();
                    menusYear.setYearId(yearId);
                    if (cell1 != null) {//不等于null 就赋值
                        cell1.setCellType(CellType.STRING);
                        menusYear.setSummary(cell1.getStringCellValue());
                    }
                    Cell cell2 = row1.getCell(j + 2);
                    String saveOrDelete = "减分项";
                    if (cell2 != null) {
                        cell2.setCellType(CellType.STRING);
                        if (!cell2.getStringCellValue().equals("加分项") && !cell2.getStringCellValue().equals("减分项"))
                            return jiebaoResponse.failMessage("减分项加分项汉字不明确");
                        saveOrDelete = cell2.getStringCellValue();
                    }
                    menusYear.setSaveOrDelete(saveOrDelete);
                    menusYear.setParentId(arr[j]);
                    menusYear.setContent(cell.getStringCellValue());
                    menusYear.setDate(new Date());
                    menusYear.setSorts(sort++);
                    list.add(menusYear);
                }
            }
            b = menusYearService.saveBatch(list);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b ? jiebaoResponse.okMessage("操作成功") : jiebaoResponse.failMessage("操作失败");
    }


    private static void fz(HSSFCellStyle cellStyle, HSSFCell cell1, String name) {
        cell1.setCellValue(name);
        cell1.setCellStyle(cellStyle);
    }

    public static void exportExcel(List<Record> list, HttpServletResponse response, UserService userService, String dateMonth) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        //垂直居中
        cellStyle.setVerticalAlignment(cellStyle.getVerticalAlignmentEnum().CENTER);
        //水平居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        HSSFSheet sheet = workbook.createSheet("考勤表");
        for (int j = 0; j < 47; j++) {
            if (j < 6 || j >= 37) {
                sheet.setColumnWidth(j, 2000);
            } else if (j >= 6 && j < 37) {
                sheet.setColumnWidth(j, 700);
            }
        }
        sheet.setColumnWidth(47, 2500);


        int i = 0;  //标记行
        HSSFRow row1 = sheet.createRow(i++);
        int cells = 0;
        int day = 1;
        HSSFCell cell1 = row1.createCell(cells++);
        fz(cellStyle, cell1, "工号");
        HSSFCell cell2 = row1.createCell(cells++);
        fz(cellStyle, cell2, "姓名");
        HSSFCell cell3 = row1.createCell(cells++);
        fz(cellStyle, cell3, "职名");
        HSSFCell cell4 = row1.createCell(cells++);
        fz(cellStyle, cell4, "岗位挡序");
        HSSFCell cell5 = row1.createCell(cells++);
        fz(cellStyle, cell5, "岗位工资");
        HSSFCell cell6 = row1.createCell(cells++);
        fz(cellStyle, cell6, "技能工资");
        HSSFCell cell7 = row1.createCell(6);
        fz(cellStyle, cell7, "时间日期");
        CellRangeAddress cellRangeAddress7 = new CellRangeAddress(0, 0, 6, 36);
        sheet.addMergedRegion(cellRangeAddress7);
        HSSFCell cell37 = row1.createCell(37);
        fz(cellStyle, cell37, "小时总计数");
        CellRangeAddress cellRangeAddress37 = new CellRangeAddress(0, 0, 37, 45);
        sheet.addMergedRegion(cellRangeAddress37);
        HSSFCell cell46 = row1.createCell(46);
        fz(cellStyle, cell46, "签名");
        CellRangeAddress cellRangeAddress46 = new CellRangeAddress(0, 2, 46, 46);
        sheet.addMergedRegion(cellRangeAddress46);
        HSSFRow row2 = sheet.createRow(i++);  //第2行的
        for (int ii = 6; ii < 37; ii++) {
            HSSFCell cell = row2.createCell(ii);
            fz(cellStyle, cell, "" + (ii - 5));
            CellRangeAddress cellRangeAddress = new CellRangeAddress(1, 2, ii, ii);
            sheet.addMergedRegion(cellRangeAddress);
        }
        HSSFCell cell337 = row2.createCell(37);  // 2排  37行
        fz(cellStyle, cell337, "已工作时间");
        CellRangeAddress cellRangeAddress1 = new CellRangeAddress(1, 1, 37, 39);
        sheet.addMergedRegion(cellRangeAddress1);
        int cellNum = 40;
        CellRangeAddress cellRangeAddress9 = new CellRangeAddress(1, 2, cellNum, cellNum);
        sheet.addMergedRegion(cellRangeAddress9);
        HSSFCell cell9 = row2.createCell(cellNum++);
        fz(cellStyle, cell9, "出差");
        CellRangeAddress cellRangeAddress11 = new CellRangeAddress(1, 2, cellNum, cellNum);
        sheet.addMergedRegion(cellRangeAddress11);
        HSSFCell cell11 = row2.createCell(cellNum++);
        fz(cellStyle, cell11, "年休假");

        CellRangeAddress cellRangeAddress12 = new CellRangeAddress(1, 2, cellNum, cellNum);
        sheet.addMergedRegion(cellRangeAddress12);
        HSSFCell cell12 = row2.createCell(cellNum++);
        fz(cellStyle, cell12, "病假");
        CellRangeAddress cellRangeAddress13 = new CellRangeAddress(1, 2, cellNum, cellNum);
        sheet.addMergedRegion(cellRangeAddress13);
        HSSFCell cell13 = row2.createCell(cellNum++);
        fz(cellStyle, cell13, "事假");
        CellRangeAddress cellRangeAddress14 = new CellRangeAddress(1, 2, cellNum, cellNum);
        sheet.addMergedRegion(cellRangeAddress14);
        HSSFCell cell14 = row2.createCell(cellNum++);
        fz(cellStyle, cell14, "婚假");
        CellRangeAddress cellRangeAddress15 = new CellRangeAddress(1, 2, cellNum, cellNum);
        sheet.addMergedRegion(cellRangeAddress15);
        HSSFCell cell15 = row2.createCell(cellNum++);
        fz(cellStyle, cell15, "合计");


        HSSFRow row3 = sheet.createRow(i++); //第3行
        for (int ii = 0; ii < 3; ii++) {
            row3.createCell(ii + 37);
        }
        int cell3RowNum = 37;
        HSSFCell cel37 = row3.createCell(cell3RowNum++);
        fz(cellStyle, cel37, "计时");
        HSSFCell cell8 = row3.createCell(cell3RowNum++);
        fz(cellStyle, cell8, "计件");
        HSSFCell cell10 = row3.createCell(cell3RowNum++);
        fz(cellStyle, cell10, "加班");

        for (int ii = 0; ii < 6; ii++) {
            CellRangeAddress cellRangeAddress = new CellRangeAddress(0, 2, ii, ii);
            sheet.addMergedRegion(cellRangeAddress);
        }
        //内容区
        for (Record record : list
        ) {
            HSSFRow row = sheet.createRow(i);
            row.setHeight((short) (25 * 20));
            for (int j = 0; j < 47; j++) {
                HSSFCell cell = row.createCell(j);
                cell.setCellStyle(cellStyle);
                if (j == 0) {
                    cell.setCellValue(i + 1);
                }
                if (j == 1) {
                    User user = userService.getById(record.getUserId());
                    cell.setCellValue(user == null ? "无" : user.getUsername());
                }
                if (j == 2) {
                    cell.setCellValue(record.getPosition());
                }
                if (j == 3) {
                    cell.setCellValue(record.getWard());
                }
                if (j == 4) {
                    cell.setCellValue(record.getMoney());
                }
                if (j == 5) {
                    cell.setCellValue(record.getSkillMoney());
                }
                if (j == 6) {
                    cell.setCellValue(record.getDay1());
                }
                if (j == 7) {
                    cell.setCellValue(record.getDay2());
                }
                if (j == 8) {
                    cell.setCellValue(record.getDay3());
                }
                if (j == 9) {
                    cell.setCellValue(record.getDay4());
                }
                if (j == 10) {
                    cell.setCellValue(record.getDay5());
                }
                if (j == 11) {
                    cell.setCellValue(record.getDay6());
                }
                if (j == 12) {
                    cell.setCellValue(record.getDay7());
                }
                if (j == 13) {
                    cell.setCellValue(record.getDay8());
                }
                if (j == 14) {
                    cell.setCellValue(record.getDay9());
                }
                if (j == 15) {
                    cell.setCellValue(record.getDay10());
                }
                if (j == 16) {
                    cell.setCellValue(record.getDay11());
                }
                if (j == 17) {
                    cell.setCellValue(record.getDay12());
                }
                if (j == 18) {
                    cell.setCellValue(record.getDay13());
                }
                if (j == 19) {
                    cell.setCellValue(record.getDay14());
                }
                if (j == 20) {
                    cell.setCellValue(record.getDay15());
                }
                if (j == 21) {
                    cell.setCellValue(record.getDay16());
                }
                if (j == 22) {
                    cell.setCellValue(record.getDay17());
                }
                if (j == 23) {
                    cell.setCellValue(record.getDay18());
                }
                if (j == 24) {
                    cell.setCellValue(record.getDay19());
                }
                if (j == 25) {
                    cell.setCellValue(record.getDay20());
                }
                if (j == 26) {
                    cell.setCellValue(record.getDay21());
                }
                if (j == 27) {
                    cell.setCellValue(record.getDay22());
                }
                if (j == 28) {
                    cell.setCellValue(record.getDay23());
                }
                if (j == 29) {
                    cell.setCellValue(record.getDay24());
                }
                if (j == 30) {
                    cell.setCellValue(record.getDay25());
                }
                if (j == 31) {
                    cell.setCellValue(record.getDay26());
                }
                if (j == 32) {
                    cell.setCellValue(record.getDay27());
                }
                if (j == 33) {
                    cell.setCellValue(record.getDay28());
                }
                if (j == 34) {
                    cell.setCellValue(record.getDay29());
                }
                if (j == 35) {
                    cell.setCellValue(record.getDay30());
                }
                if (j == 36) {
                    cell.setCellValue(record.getDay31());
                }
                if (j == 37) {
                    cell.setCellValue(record.getJiShi());
                }
                if (j == 38) {
                    cell.setCellValue(record.getJiJian());
                }
                if (j == 39) {
                    cell.setCellValue(record.getJiaBan());
                }
                if (j == 40) {
                    cell.setCellValue(record.getChuChai());
                }
                if (j == 41) {
                    cell.setCellValue(record.getNianXiuJia());
                }
                if (j == 42) {
                    cell.setCellValue(record.getBinJia());
                }
                if (j == 43) {
                    cell.setCellValue(record.getShiJia());
                }
                if (j == 44) {
                    cell.setCellValue(record.getHunJia());
                }
                if (j == 45) {
                    cell.setCellValue(record.getHeJi());
                }
//                if (j == 46) {
//                    cell.setCellValue(record.getHeJi());
//                }
            }
            i++;
        }
        panMonth(dateMonth);
        response.setContentType("application/ms-excel;charset=utf-8");
//设置导出Excel的名称
        response.addHeader("Content-Disposition", "attachment;filename=导出.xls");
        //刷新缓冲
        try {
            response.flushBuffer();
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Integer panMonth(String month) {  //判断月份多少天  //参数 年份带月
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy");
        String format = simpleDateFormat.format(month);
        Integer yer = Integer.valueOf(simpleDateFormat1.format(month));
        switch (format) {
            case "01":
            case "03":
            case "05":
            case "07":
            case "08":
            case "10":
            case "12":
                return 31;
            case "04":
            case "06":
            case "09":
            case "11":
                return 30;
            case "02":
                return (yer % 400 == 0 || yer % 4 == 0 && yer % 100 != 0) ? 29 : 28;
            default:
                return null;
        }
    }

    public static void exportList(HttpServletResponse response, List<? extends Object> list, Class<?> cla, String workName, String titles) {  //大众通用类型 纯集合
        HSSFWorkbook workbook = new HSSFWorkbook();  //创建空间
        HSSFCellStyle cellStyle = workbook.createCellStyle();   //设定格式
        //垂直居中
        cellStyle.setVerticalAlignment(cellStyle.getVerticalAlignmentEnum().CENTER);
        //水平居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        if (workName == null) {//为空  去实体类拿去默认的参数
            Annotation[] annotations = cla.getAnnotations();
            ExcelName excelName = (ExcelName) annotations[0]; //表名
            workName = excelName.name();
        }
        HSSFSheet sheet = workbook.createSheet(workName);  //创建表
        Field[] declaredFields = cla.getDeclaredFields(); //所有属性
        HSSFRow row2 = sheet.createRow(0);
        HSSFCell cell1 = row2.createCell(0);
        cell1.setCellValue(titles);
        HSSFRow row = sheet.createRow(1);
        int cellNumber = 0;//记录列数
        for (Field filed : declaredFields
        ) {
            ExcelName annotation = filed.getAnnotation(ExcelName.class);
            if (annotation == null)
                continue;
            HSSFCell cell = row.createCell(cellNumber++);
            cell.setCellValue(annotation.name());

        }   //标题  以及表的 属性创建完毕
        int Rows = 2;
        for (Object o : list
        ) {
            HSSFRow row1 = sheet.createRow(Rows);
            Class<?> aClass = o.getClass();
            Field[] declaredFields1 = aClass.getDeclaredFields();
            int cellNumbers = 0;//记录列数
            for (int y = 0; y < declaredFields1.length; y++) {
                Field field = declaredFields1[y];
                ExcelName annotation = field.getAnnotation(ExcelName.class);
                if (annotation == null)
                    continue;
                HSSFCell cell = row1.createCell(cellNumbers++);
                field.setAccessible(true);
                try {
                    Object title = field.get(o);
                    cell.setCellValue(title == null ? "" : title.toString());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (y == declaredFields1.length - 1)
                    cellNumbers = 0;
            }
            Rows++;
        }
        allSheet(sheet);
        applyColor(response, workbook);
    }

    public static void exportMap(HttpServletResponse response, Map<String, List<? extends Object>> map, Class<?> cla, String workName) {  //涉及到分区导出分组
        HSSFWorkbook workbook = new HSSFWorkbook();  //创建空间
        HSSFCellStyle cellStyle = workbook.createCellStyle();   //设定格式
        //垂直居中
        cellStyle.setVerticalAlignment(cellStyle.getVerticalAlignmentEnum().CENTER);
        //水平居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        if (workName == null) {
            Annotation[] annotations = cla.getAnnotations();
            ExcelName excelName = (ExcelName) annotations[0]; //表名
            workName = excelName.name();
        }
        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();// 模块名  KEY
            HSSFSheet sheet = workbook.createSheet(next + workName);  //创建表
            Field[] declaredFields = cla.getDeclaredFields(); //所有属性
            HSSFRow row = sheet.createRow(0);
            int cellNumber = 0;//记录列数
            for (Field filed : declaredFields  //创建第一行  表格栏目
            ) {
                ExcelName annotation = filed.getAnnotation(ExcelName.class);
                HSSFCell cell = row.createCell(cellNumber++);
                cell.setCellValue(annotation.name());
            }   //标题  以及表的 属性创建完毕
            int Rows = 1;  //  下标1  相当于 第二行开始
            for (Object o : map.get(next)
            ) {
                HSSFRow row1 = sheet.createRow(Rows);
                System.out.println(o);
                Class<?> aClass = o.getClass();
                Field[] declaredFields1 = aClass.getDeclaredFields();
                for (int y = 0; y < declaredFields1.length; y++) {
                    HSSFCell cell = row1.createCell(y);
                    Field field = declaredFields1[y];
                    field.setAccessible(true);
                    try {
                        Object title = field.get(o);
                        cell.setCellValue(title == null ? "" : title.toString());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                Rows++;
            }
            allSheet(sheet);  //自适应 列宽
        }
        applyColor(response, workbook);  //响应s
    }

    private static void applyColor(HttpServletResponse response, HSSFWorkbook workbook) {// 响应到前端
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + UUID.randomUUID() + ".xls");
        try {
            response.flushBuffer();  //刷新缓冲
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void allSheet(HSSFSheet sheet) {  //遍历列表  获取  设置最大列宽
        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
            HSSFRow row = sheet.getRow(i);
//            for(int j=0;j<row.getPhysicalNumberOfCells();j++){
//                sheet.a
//            }
            sheet.autoSizeColumn(i);
        }
    }

}
