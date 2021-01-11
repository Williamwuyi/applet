package com.jiebao.platfrom.attendance.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.attendance.dao.RecordsMapper;
import com.jiebao.platfrom.attendance.daomain.Record;
import com.jiebao.platfrom.attendance.service.IRecordsService;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qta
 * @since 2020-08-07
 */
@Service
public class RecordsServiceImpl extends ServiceImpl<RecordsMapper, Record> implements IRecordsService {

    @Override
    public boolean saveOrUpdate(Record entity) {
        a(entity);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        entity.setDate(simpleDateFormat.format(new Date()));
        return super.saveOrUpdate(entity);
    }

    //白    //公   //差 //年   病   事  婚
    private void a(Record record) {
        Field[] fields = record.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            String fileName = fields[i].getName();//属性名
            if (fileName.substring(0, 3).equals("day")) { //如果是记录日期 公  差  班  则进入
                fileName = fileName.substring(0, 1).toUpperCase() + fileName.substring(1);//第一个大写
                String type = fields[i].getGenericType().toString();
                try {
                    Method m = record.getClass().getMethod("get" + fileName);
                    if (type.equals("class java.lang.String")) {   //如果type是类类型，则前面包含"class "，后面跟类名
                        String value = (String) m.invoke(record);
                        if (value != null) {
                            if (value.equals("白"))
                                record.setJiShi((record.getJiShi() == null ? 0 : record.getJiShi()) + 8);
//                        if (value.equals("公"))
//                            record.setJiShi(record.getJiShi() + 8);
                            if (value.equals("差"))
                                record.setChuChai((record.getChuChai() == null ? 0 : record.getChuChai()) + 8);
                            if (value.equals("年"))
                                record.setNianXiuJia((record.getNianXiuJia() == null ? 0 : record.getNianXiuJia()) + 8);
                            if (value.equals("病"))
                                record.setBinJia((record.getBinJia() == null ? 0 : record.getBinJia()) + 8);
                            if (value.equals("事"))
                                record.setShiJia((record.getShiJia() == null ? 0 : record.getShiJia()) + 8);
                            if (value.equals("婚"))
                                record.setHunJia((record.getHunJia() == null ? 0 : record.getHunJia()) + 8);
                        }
                    }
//            if (type.equals("class java.lang.Integer")) {
//                Integer value = (Integer) m.invoke(record);
//            }
//            if (type.equals("class java.lang.Short")) {
//                Short value = (Short) m.invoke(record);
//            }
//            if (type.equals("class java.lang.Double")) {
//                Double value = (Double) m.invoke(record);
//            }
//            if (type.equals("class java.lang.Boolean")) {
//                Boolean value = (Boolean) m.invoke(record);
//            }
//            if (type.equals("class java.util.Date")) {
//                Date value = (Date) m.invoke(record);
//            }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        record.setChuChai((record.getChuChai() == null ? 0 : record.getChuChai()));
        record.setNianXiuJia((record.getNianXiuJia() == null ? 0 : record.getNianXiuJia()));
        record.setBinJia((record.getBinJia() == null ? 0 : record.getBinJia()));
        record.setShiJia((record.getShiJia() == null ? 0 : record.getShiJia()));
        record.setHunJia((record.getHunJia() == null ? 0 : record.getHunJia()));
    }
}
