package com.jiebao.platfrom.common.utils;

import com.jiebao.platfrom.check.domain.Menus;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

public class ExportExcel {

    private static Object ExcelName;

    public static boolean exportExcelList(List<?> list, Class<?> cla, HttpServletResponse response) {  //纯列表导出
        HSSFWorkbook workbook = new HSSFWorkbook();
        String name = "未命名";
        ExcelName sheetName = cla.getAnnotation(ExcelName.class);
        if (sheetName != null)
            name = sheetName.name();
        HSSFSheet sheet = workbook.createSheet(name);
        HSSFRow row = sheet.createRow(0);

        int row1Cell = 0;//第一行的列
        Field[] declaredFields = cla.getDeclaredFields();
        for (Field field : declaredFields) {  //建立表头
            ExcelName annotation = field.getAnnotation(ExcelName.class);
            if (annotation == null)
                continue;
            HSSFCell cell = row.createCell(row1Cell++);
            cell.setCellValue(annotation.name());
        }
        /**
         * 下面是实体内容
         */
        int rowNumber = 1;
        for (Object entity : list
        ) {
            HSSFRow row1 = sheet.createRow(rowNumber++);
            Class<?> aClass = entity.getClass();
            Field[] declaredFields1 = aClass.getDeclaredFields();
            int row1Cells = 0;//第一行的列
            for (Field field : declaredFields1) {  //建立表头
                ExcelName annotation = field.getAnnotation(ExcelName.class);
                if (annotation == null)
                    continue;
                field.setAccessible(true);
                try {
                    Object o = field.get(entity);  //实体内容
                    HSSFCell cell = row1.createCell(row1Cells++);
                    if (o != null)
                        cell.setCellValue(o.toString());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            byte[] bytes = bos.toByteArray();
            InputStream is = new ByteArrayInputStream(bytes);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            response.setHeader("Content-Disposition", "attachment;filename=" + UUID.randomUUID() + ".xls");
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            outputStream.write(buffer);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean imTemplate(List<Menus> list, HttpServletResponse response) {  //纯列表导出
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("模板");
        HSSFRow row = sheet.createRow(0);

        int i = 0;
        for (Menus menus : list
        ) {
            HSSFCell cell = row.createCell(i++);
            cell.setCellValue(menus.getName());
            HSSFCell cell1 = row.createCell(i++);
            cell1.setCellValue("摘要");
            HSSFCell cell2 = row.createCell(i++);
            cell2.setCellValue("加分项/减分项");
        }
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            byte[] bytes = bos.toByteArray();
            InputStream is = new ByteArrayInputStream(bytes);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            response.setHeader("Content-Disposition", "attachment;filename=" + UUID.randomUUID() + ".xls");
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            outputStream.write(buffer);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}

