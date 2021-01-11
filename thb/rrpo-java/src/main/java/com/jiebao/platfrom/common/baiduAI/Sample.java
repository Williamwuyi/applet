package com.jiebao.platfrom.common.baiduAI;

import com.baidu.aip.imagesearch.AipImageSearch;
import org.json.JSONObject;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Sample {
    public static final String APP_ID = "21958040";
    public static final String API_KEY = "6TCwO8xXZ4xeWXCtOFnT0gY6";
    public static final String SECRET_KEY = "hncRwME2xsxgqMDjgdwspUsxDV9iXmaG";


    public static void main(String[] args) throws IOException {
      /*  String image ="D:\\upload\\images\\2020-08-10\\11.jpg";
        String brief ="周杰伦";
        String url ="http://192.168.10.104:9527/jbx/cdn/image/2020-08-10/38f9be69-194f-4c78-8295-f29ff0df64d3.png";
       // Sample.ware(image,brief,url);
        Sample.sample(image);*/

     /* String a= "12，2,3";
        String[] split = a.split(",");
        System.out.println(split[0]);*/

        String s = ",a,b,c,";
        String s1 = s.replaceAll("^,*|,*$", "");
        List result = Arrays.asList(s1.split(","));
        for (int i = 0; i < result.size(); i++) {
            System.out.println(result.get(i));

        }
        System.out.println(result);
    }

    /**
     * 入库，相同图片则返回false
     * @param image  本地图片路径或者图片二进制数据
     * @param brief  检索时原样带回,最长256B。样例：{"name":"周杰伦", "id":"666"} 。请注意，检索接口不返回原图，仅返回入库时填写的brief信息，所以调用该入库接口时，brief信息请尽量填写可关联至本地图库的图片id或者图片url、图片名称等信息
     * @param url 图片完整URL，URL长度不超过1024字节，URL对应的图片base64编码后大小不超过4M，最短边至少50px，最长边最大4096px,支持jpg/png/bmp格式，当image字段存在时url字段失效。
     * 由于图床的差异性，抓图服务无法适配所有的图床，部分URL可能抓不到图，或者图片下载超时，遇到上述情况时请更换图片URL、或者将图片下载到本地转码后上传
     * @param tags  1 - 65535范围内的整数，tag间以逗号分隔，最多2个tag，无层级关系。样例："100,11" ；检索时可圈定分类维度进行检索
     * @throws IOException
     */
    public static void ware(String image, String brief) throws IOException {
        AipImageSearch client = new AipImageSearch(APP_ID, API_KEY, SECRET_KEY);
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<>();
        options.put("tags", "100,11");

        // 参数为本地路径
        //  String image = "test.jpg";
        JSONObject res = client.similarAdd(image, brief, options);
        System.out.println(res.toString(2));

        // 参数为二进制数组
      /*  byte[] file = readFile(image);
        res = client.similarAdd(file, brief, options);
        System.out.println(res.toString(2));*/

        // 相似图检索—入库, 图片参数为远程url图片
       /* JSONObject jsonObject = client.similarAddUrl(url, brief, options);
        System.out.println(jsonObject.toString(2));*/
    }


    public static byte[] readFile(String url) throws IOException {
        byte[] content = null;
        BufferedInputStream bis = null;
        ByteArrayOutputStream out = null;
        try {
            FileInputStream input = new FileInputStream(url);
            bis = new BufferedInputStream(input, 1024);
            byte[] bytes = new byte[1024];
            int len;
            out = new ByteArrayOutputStream();
            while ((len = bis.read(bytes)) > 0) {
                out.write(bytes, 0, len);
            }
            bis.close();
            content = out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                bis.close();
            }
            if (out != null) {
                out.close();
            }
        }
        return content;
    }

    /**
     * 相似度查询
     * @param image
     */
    public static void sample(String image) {
        AipImageSearch client = new AipImageSearch(APP_ID, API_KEY, SECRET_KEY);
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<>();
        options.put("tags", "100,11");
        options.put("tag_logic", "0");
      //  options.put("pn", "1");
        options.put("rn", "200");
        JSONObject res = client.similarSearch(image, options);
        System.out.println(res.toString(2));
    }
}