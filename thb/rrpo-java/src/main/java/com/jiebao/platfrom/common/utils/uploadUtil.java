package com.jiebao.platfrom.common.utils;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class uploadUtil {
    private static final String window = "D:/OA/img/";
    private static final String linux = "/OA/img/";

    public static List<String> upload(MultipartFile[] files, HttpServletRequest request) {
        String path = "D:/OA/img/";
        if (System.getProperty("os.name").indexOf("Windows") != -1) {
            path = window;
        } else {
            path = linux;
        }
        List<String> fileList = new ArrayList<>();   //存储    文件地址
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                if (!file.isEmpty()) {
                    try {
                        File filePath = new File(path);  //目录名
                        if (!filePath.exists())
                            filePath.mkdirs();
                        String filename = file.getOriginalFilename();
                        String houZui = filename.substring(filename.lastIndexOf("."), filename.length());//后缀
                        String name = filename.substring(0, filename.lastIndexOf(".")); //主体名
                        File file1 = new File(path + filename);  //最后存入
                        String url = filename;
                        if (file1.exists()) {   //已存在相同名字f
                            ;  //唯一数字类型  uuids
                            url = name + UUID.randomUUID().toString().hashCode() + houZui;
                            file1 = new File(path + url);
                        }
                        file.transferTo(file1);
                        fileList.add(url);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return fileList;
    }
}
