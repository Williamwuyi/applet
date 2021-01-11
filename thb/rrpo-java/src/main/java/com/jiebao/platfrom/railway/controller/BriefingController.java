package com.jiebao.platfrom.railway.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiebao.platfrom.common.annotation.Log;
import com.jiebao.platfrom.common.authentication.JWTUtil;
import com.jiebao.platfrom.common.controller.BaseController;
import com.jiebao.platfrom.common.domain.JiebaoConstant;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.exception.JiebaoException;
import com.jiebao.platfrom.common.utils.SortUtil;
import com.jiebao.platfrom.railway.dao.BriefingCountMapper;
import com.jiebao.platfrom.railway.dao.BriefingMapper;
import com.jiebao.platfrom.railway.dao.BriefingUserMapper;
import com.jiebao.platfrom.railway.domain.Briefing;
import com.jiebao.platfrom.railway.domain.BriefingCount;
import com.jiebao.platfrom.railway.domain.BriefingUser;
import com.jiebao.platfrom.railway.domain.ExchangeUser;
import com.jiebao.platfrom.railway.service.BriefingCountService;
import com.jiebao.platfrom.railway.service.BriefingService;
import com.jiebao.platfrom.railway.service.BriefingUserService;
import com.jiebao.platfrom.system.dao.FileMapper;
import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.domain.User;
import com.jiebao.platfrom.system.service.DeptService;
import com.jiebao.platfrom.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.annotations.Select;
import org.apache.shiro.SecurityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author yf
 */
@Slf4j
@RestController
@RequestMapping(value = "/briefing")
@Api(tags = "railWay-护路简报")   //swagger2 api文档说明示例
public class BriefingController extends BaseController {


    private String message;

    @Autowired
    private BriefingService briefingService;

    @Autowired
    private BriefingUserService briefingUserService;


    @Resource
    private BriefingMapper briefingMapper;

    @Autowired
    private UserService userService;

    @Resource
    private BriefingUserMapper briefingUserMapper;

    @Resource
    private FileMapper fileMapper;

    @Autowired
    private DeptService deptService;

    @Autowired
    private BriefingCountService briefingCountService;

    @Resource
    private BriefingCountMapper briefingCountMapper;


    /**
     * 创建一条护路简报
     */
    @PostMapping("/creat")
    @ApiOperation(value = "创建一条护路简报或创建并发送(修改)", notes = "创建一条护路简报或创建并发送(修改)", response = JiebaoResponse.class, httpMethod = "POST")
    public JiebaoResponse creatBriefing(@Valid Briefing briefing, String[] sendUserIds, String[] fileIds, String briefingCounts) throws JiebaoException {
        try {
            String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
            if (username != null) {
                briefing.setCreatUser(username);
            }
            if (briefing.getStatus() == 1) {
                boolean save = briefingService.saveOrUpdate(briefing);
                Arrays.stream(fileIds).forEach(fileId -> {
                    fileMapper.updateByFileId(fileId, briefing.getId());
                });
                briefingUserService.deleteByBriefingId(briefing.getId());
                if (save) {
                    Arrays.stream(sendUserIds).forEach(sendUserId -> {
                        //把要发送的用户保存到数据库
                        User byId = userService.getById(sendUserId);
                        briefingUserService.saveByUserId(briefing.getId(), sendUserId, byId.getUsername());
                    });
                }
                //解析json数组

                if (briefingCounts != null && !"".equals(briefingCounts)) {
                    JSONArray jsonArray = JSON.parseArray(briefingCounts);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        System.out.println(jsonObject.getString("deptId") + ":" + jsonObject.getInteger("count"));
                        BriefingCount briefingCount = new BriefingCount();
                        briefingCount.setBriefingId(briefing.getId());
                        briefingCount.setDeptId(jsonObject.getString("deptId"));
                        briefingCount.setCount(jsonObject.getInteger("count"));
                        Map<String, Object> map = new HashMap<>();
                        map.put("briefing_id",briefing.getId());
                        map.put("dept_id",jsonObject.getString("deptId"));
                        List<BriefingCount> briefingCountList = briefingCountMapper.selectByMap(map);
                        if (briefingCountList.size() == 0){
                            briefingCountService.save(briefingCount);
                        }
                    }
                }


                return new JiebaoResponse().message("创建一条护路简报成功");
            } else if (briefing.getStatus() == 3) {
                boolean save = briefingService.saveOrUpdate(briefing);
                Arrays.stream(fileIds).forEach(fileId -> {
                    fileMapper.updateByFileId(fileId, briefing.getId());
                });
                if (save) {
                    Arrays.stream(sendUserIds).forEach(sendUserId -> {
                        //把要发送的用户保存到数据库
                        User byId = userService.getById(sendUserId);
                        briefingUserService.saveByUserId(briefing.getId(), sendUserId, byId.getUsername());
                    });
                }
                briefingMapper.releaseSave(briefing.getId());
                briefingUserMapper.setCreatTime(briefing.getId());
                if (briefing.getSynchronizeWeb() == 1) {
                    //HttpPost请求实体
                    HttpPost httpPost = new HttpPost("http://114.116.174.5:888/jws/push");
                    //使用工具类创建 httpClient
                    CloseableHttpClient client = HttpClients.createDefault();
                    CloseableHttpResponse resp = null;
                    String respondBody = null;
                    try {
                        //设置请求超时时间和 sockect 超时时间
                           /* RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(20000).setSocketTimeout(20000000).build();
                            httpPost.setConfig(requestConfig);*/
                        //附件参数需要用到的请求参数实体构造器
                        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

                        //获取附件
                        ArrayList<File> filess = new ArrayList<>();
                        Map<String, Object> map = new HashMap<>();
                        map.put("ref_type", 8);
                        map.put("file_type", 2);
                        map.put("ref_id", briefing.getId());
                        List<com.jiebao.platfrom.system.domain.File> files = fileMapper.selectByMap(map);
                        if (files.size() > 0) {
                            for (com.jiebao.platfrom.system.domain.File f : files
                            ) {
                                String url = f.getFileUrl() + f.getNewName();
                                File is = new File(url);
                                filess.add(is);
                            }
                        }
                        if (!CollectionUtils.isEmpty(filess)) {
                            for (File file : filess) {
                                multipartEntityBuilder.addBinaryBody("file", file);
                            }
                        }
                        System.out.println(briefing.getTargetsId() + "----------------");
                        Map<String, String> params = new HashMap<>();
                        params.put("id", briefing.getTargetsId());
                        Dept dept = deptService.getDept();
                        params.put("source", dept.getDeptName() + "-" + username);
                        params.put("title", briefing.getTitle());
                        //如果富编辑器里有图片，转换成base64替换img标签所有内容
                     /*   Map<String, Object> mapF = new HashMap<>();
                        map.put("ref_type",8);
                        map.put("file_type",1);
                        List<com.jiebao.platfrom.system.domain.File> filesF = fileMapper.selectByMap(mapF);
                        if (filesF.size()>0){
                            Element doc = Jsoup.parseBodyFragment(briefing.getContent()).body();
                            Elements jpg = doc.select("img[src]");
                            for (com.jiebao.platfrom.system.domain.File f: files
                            ) {
                                String url = f.getFileUrl() + f.getNewName();
                                //转换为base64
                                BASE64Encoder encoder = new BASE64Encoder();
                                InputStream   in = new FileInputStream(url);
                                byte[] data = new byte[in.available()];
                                String encode = encoder.encode(data);
                            }

                        }else {
                            params.put("html", briefing.getContent());
                        }*/
                        params.put("html", briefing.getContent());
                        if (briefing.getTime() != null) {
                            String relTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(briefing.getTime());
                            params.put("time", relTime);
                        } else {
                            String relTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
                            params.put("time", relTime);
                        }

                        params.put("user", username);

                        ContentType strContent = ContentType.create("text/plain", Charset.forName("UTF-8"));
                        if (!CollectionUtils.isEmpty(params)) {
                            params.forEach((key, value) -> {
                                //此处的字符串参数会被设置到请求体Query String Parameters中

                                multipartEntityBuilder.addTextBody(key, value, strContent);
                            });
                        }
                        HttpEntity httpEntity = multipartEntityBuilder.build();
                        //将请求参数放入 HttpPost 请求体中
                        //使用 httpEntity 后 Content-Type会自动被设置成 multipart/form-data
                        httpPost.setEntity(httpEntity);
                        //执行发送post请求
                        resp = client.execute(httpPost);
                        //将返回结果转成String
                        respondBody = EntityUtils.toString(resp.getEntity());
                        System.out.println("++++++++++++++" + respondBody);
                    } catch (IOException e) {
                        //日志信息及异常处理

                    } finally {
                        if (resp != null) {
                            try {
                                //关闭请求
                                resp.close();
                            } catch (IOException e) {

                            }
                        }
                    }
                }
                return new JiebaoResponse().message("创建并发布一条护路简报成功");
            }
            return new JiebaoResponse().message("系统错误");
        } catch (Exception e) {
            message = "创建护路简报失败";
            log.error(message, e);
            throw new JiebaoException("创建一条护路简报失败");
        }
    }

    @GetMapping("/release/{briefingIds}")
    @Log("批量发布护路简报")
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(value = "批量发布护路简报", notes = "批量发布护路简报", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse release(@PathVariable String[] briefingIds) throws JiebaoException {
        try {
            if (briefingIds != null) {
                Arrays.stream(briefingIds).forEach(briefingId -> {
                    //状态status改为3
                    briefingMapper.release(briefingId);
                    briefingUserMapper.setCreatTime(briefingId);
                    Briefing byId = briefingService.getById(briefingId);
                    if (byId.getSynchronizeWeb() == 1) {

                        //HttpPost请求实体
                        HttpPost httpPost = new HttpPost("http://114.116.174.5:888/jws/push");
                        //使用工具类创建 httpClient
                        CloseableHttpClient client = HttpClients.createDefault();
                        CloseableHttpResponse resp = null;
                        String respondBody = null;
                        try {
                            //附件参数需要用到的请求参数实体构造器
                            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
                            //获取附件
                            ArrayList<File> filess = new ArrayList<>();
                            Map<String, Object> map = new HashMap<>();
                            map.put("ref_type", 8);
                            map.put("file_type", 2);
                            map.put("ref_id", briefingId);
                            List<com.jiebao.platfrom.system.domain.File> files = fileMapper.selectByMap(map);
                            if (files.size() > 0) {
                                for (com.jiebao.platfrom.system.domain.File f : files
                                ) {
                                    String url = f.getFileUrl() + f.getNewName();
                                    File is = new File(url);
                                    filess.add(is);
                                }
                            }
                            if (!CollectionUtils.isEmpty(filess)) {
                                for (File file : filess) {
                                    multipartEntityBuilder.addBinaryBody("file", file);
                                }
                            }
                            System.out.println(byId.getTargetsId() + "----------------");
                            Map<String, String> params = new HashMap<>();
                            params.put("id", byId.getTargetsId());
                            Dept dept = deptService.getDept();
                            String username = JWTUtil.getUsername(SecurityUtils.getSubject().getPrincipal().toString());
                            params.put("source", (dept.getDeptName() + "-" + username));
                            params.put("title", byId.getTitle());
                            //如果富编辑器里有图片，转换成base64替换img标签所有内容
                           /* Map<String, Object> mapF = new HashMap<>();
                            map.put("ref_type",8);
                            map.put("file_type",1);
                            map.put("ref_id",briefingId);
                            List<com.jiebao.platfrom.system.domain.File> filesF = fileMapper.selectByMap(mapF);
                            if (filesF.size()>0){
                                Element doc = Jsoup.parseBodyFragment(byId.getContent()).body();
                                Elements jpg = doc.select("img[src]");
                                for (com.jiebao.platfrom.system.domain.File f: files
                                ) {
                                    String url = f.getFileUrl() + f.getNewName();
                                    //转换为base64
                                    BASE64Encoder encoder = new BASE64Encoder();
                                    InputStream   in = new FileInputStream(url);
                                    byte[] data = new byte[in.available()];
                                    String encode = encoder.encode(data);
                                }

                            }else {
                                params.put("html", byId.getContent());
                            }*/
                            params.put("html", byId.getContent());
                            if (byId.getTime() != null) {
                                String relTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(byId.getTime());
                                params.put("time", relTime);
                            } else {
                                String relTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
                                params.put("time", relTime);
                            }
                            params.put("user", username);

                            ContentType strContent = ContentType.create("text/plain", Charset.forName("UTF-8"));
                            if (!CollectionUtils.isEmpty(params)) {
                                params.forEach((key, value) -> {
                                    //此处的字符串参数会被设置到请求体Query String Parameters中
                                    multipartEntityBuilder.addTextBody(key, value, strContent);
                                });
                            }
                            HttpEntity httpEntity = multipartEntityBuilder.build();
                            //将请求参数放入 HttpPost 请求体中
                            //使用 httpEntity 后 Content-Type会自动被设置成 multipart/form-data
                            httpPost.setEntity(httpEntity);
                            //执行发送post请求
                            resp = client.execute(httpPost);
                            //将返回结果转成String
                            respondBody = EntityUtils.toString(resp.getEntity());
                            System.out.println("++++++++++++++" + respondBody);
                        } catch (IOException e) {
                            //日志信息及异常处理
                        } finally {
                            if (resp != null) {
                                try {
                                    //关闭请求
                                    resp.close();
                                } catch (IOException e) {
                                }
                            }
                        }
                    }
                });
                return new JiebaoResponse().okMessage("发布护路简报成功");
            }
        } catch (Exception e) {
            log.error(message, e);
            throw new JiebaoException(message);
        }
        return new JiebaoResponse().failMessage("发布护路简报失败");
    }


    @DeleteMapping("/{briefingIds}")
    @ApiOperation(value = "批量删除信息（完全删除未发送的，假删除已发送的）(发件箱)", notes = "批量删除信息（完全删除未发送的，假删除已发送的）(发件箱)", response = JiebaoResponse.class, httpMethod = "DELETE")
    public JiebaoResponse deleteBriefing(@PathVariable String[] briefingIds) throws JiebaoException {
        try {
            Arrays.stream(briefingIds).forEach(briefingId -> {
                Briefing byId = briefingService.getById(briefingId);
                //未发送状态，删掉文件，删除接收人，删除该信息本体
                if (byId.getStatus() == 1) {
                    briefingUserService.deleteByBriefingId(briefingId);
                    briefingService.removeById(briefingId);
                    //已发布状态，只把状态改为4即可，没有2撤回
                } else if (byId.getStatus() == 3) {
                    briefingMapper.updateStatus(briefingId);
                }
            });
            return new JiebaoResponse().message("批量删除信息成功");
        } catch (Exception e) {
            message = "删除发件箱失败";
            log.error(message, e);
            throw new JiebaoException("批量删除信息失败");
        }
    }

    @PutMapping("{sendUserIds}")
    @ApiOperation(value = "修改未发送的护路简报", notes = "修改未发送的护路简报", httpMethod = "PUT")
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse updateBriefing(@Valid Briefing briefing, @PathVariable String[] sendUserIds) throws JiebaoException {
        try {
            briefingService.updateById(briefing);
            //先删除掉原有发送人
            boolean b = briefingUserService.deleteByBriefingId(briefing.getId());
            //重新添加
            if (b) {
                Arrays.stream(sendUserIds).forEach(sendUserId -> {
                    //把要发送的用户保存到数据库
                    User byId = userService.getById(sendUserId);
                    briefingUserService.saveByUserId(briefing.getId(), sendUserId, byId.getUsername());
                });
                return new JiebaoResponse().message("修改未发送的护路简报成功");
            } else {
                return new JiebaoResponse().message("修改未发送的护路简报失败");
            }
        } catch (Exception e) {
            message = "修改护路简报失败";
            log.error(message, e);
            throw new JiebaoException("修改失败");
        }
    }

    @GetMapping
    @ApiOperation(value = "分页查询（查询未发送和已发送的）", notes = "查询分页数据（查询未发送和已发送的）", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse getBriefingList(QueryRequest request, Briefing briefing, String startTime, String endTime) {
        IPage<Briefing> briefingList = briefingService.getBriefingList(request, briefing, startTime, endTime);
        return new JiebaoResponse().data(this.getDataTable(briefingList));
    }

    @GetMapping("/forCheck")
    @ApiOperation(value = "分页查询（查询未发送和已发送的给年度考核）", notes = "查询分页数据（查询未发送和已发送的给年度考核）", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse getBriefingListForCheck(QueryRequest request, Briefing briefing, String id, String startTime, String endTime) {
        IPage<Briefing> briefingList = briefingService.getBriefingListForCheck(request, briefing, id, startTime, endTime);
        return new JiebaoResponse().data(this.getDataTable(briefingList));
    }


    @GetMapping("/inbox")
    @ApiOperation(value = "分页查询（查询收件箱）", notes = "查询分页数据（查询收件箱）", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse getBriefingInboxList(QueryRequest request, Briefing briefing, String startTime, String endTime) {
        IPage<Briefing> briefingList = briefingService.getBriefingInboxList(request, briefing, startTime, endTime);
        List<Briefing> records = briefingList.getRecords();
        for (Briefing e : records
        ) {
            String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
            User byName = userService.findByName(username);
            BriefingUser briefingUser = briefingUserMapper.getIsRead(byName.getUserId(), e.getId());
            if (briefingUser != null) {
                e.setIsRead(briefingUser.getIsRead());
            }
        }
        return new JiebaoResponse().data(this.getDataTable(briefingList));
    }


    @DeleteMapping("/inbox/{briefingIds}")
    @ApiOperation(value = "批量删除信息（收件箱）", notes = "批量删除信息（收件箱）", response = JiebaoResponse.class, httpMethod = "DELETE")
    public JiebaoResponse deleteInboxBriefing(@PathVariable String[] briefingIds) throws JiebaoException {
        try {
            String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
            User byName = userService.findByName(username);
            Arrays.stream(briefingIds).forEach(briefingId -> {
                briefingUserService.removeBySendUserId(byName.getUserId(), briefingId);
            });
            return new JiebaoResponse().message("批量删除信息成功");
        } catch (Exception e) {
            log.error(message, e);
            throw new JiebaoException("批量删除信息失败");
        }
    }


    @GetMapping(value = "/getInfoById")
    @ApiOperation(value = "根据ID查信息info", notes = "根据ID查信息info", response = JiebaoResponse.class, httpMethod = "GET")
    public Briefing getInfoById(String briefingId) {
        Briefing byId = briefingService.getById(briefingId);
        Map<String, Object> columnMap = new HashMap<>();
        //列briefing_id为数据库中的列名，不是实体类中的属性名
        columnMap.put("briefing_id", briefingId);
        List listId = new ArrayList();
        List listName = new ArrayList();
        //    List listDeptName = new ArrayList();
        List<BriefingUser> briefingUsers = briefingUserMapper.selectByMap(columnMap);
        for (BriefingUser eu : briefingUsers
        ) {
            listId.add(eu.getSendUserId());
            User user = userService.getById(eu.getSendUserId());
            listName.add(user.getUsername());
        }
        String[] array = (String[]) listId.toArray(new String[0]);
        String[] arrayName = (String[]) listName.toArray(new String[0]);
        byId.setUserId(array);
        byId.setUserName(arrayName);
        Map<String, Object> map = new HashMap<>();
        map.put("ref_id", briefingId);
        List<com.jiebao.platfrom.system.domain.File> files = fileMapper.selectByMap(map);
        ArrayList<String> refIds = new ArrayList<>();
        for (com.jiebao.platfrom.system.domain.File f : files
        ) {
            refIds.add(f.getRefId());
        }
        byId.setRefIds(refIds);
        return byId;
    }


    @GetMapping(value = "/getUserInfo")
    @ApiOperation(value = "根据ID查接收info（发件箱）", notes = "根据ID查接收info（发件箱）", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse getUserInfo(QueryRequest request, BriefingUser briefingUser) {
        IPage<BriefingUser> briefingUserList = briefingUserService.getBriefingUserList(request, briefingUser);
        List<BriefingUser> records = briefingUserList.getRecords();
        for (BriefingUser e : records
        ) {
            User byId = userService.getById(e.getSendUserId());
            Dept byDept = deptService.getById(byId.getDeptId());
            if (byDept.getRank() == 1) {
                e.setDeptName(byDept.getDeptName());
            } else if (byDept.getRank() == 2) {
                Dept byParentDept = deptService.getById(byDept.getParentId());
                e.setDeptName(byParentDept.getDeptName() + " " + byDept.getDeptName());
            } else if (byDept.getRank() == 3) {
                Dept byParentDept = deptService.getById(byDept.getParentId());
                Dept byCityDept = deptService.getById(byParentDept.getParentId());
                e.setDeptName(byCityDept.getDeptName() + " " + byParentDept.getDeptName() + " " + byDept.getDeptName());
            } else {
                e.setDeptName(byDept.getDeptName());
            }
        }
        HashMap<String, Object> map = new HashMap<>();
        int zero = briefingUserMapper.countByIsReadZero(briefingUser.getBriefingId());
        int one = briefingUserMapper.countByIsReadOne(briefingUser.getBriefingId());
        map.put("zero", zero);
        map.put("one", one);
        Map<String, Object> rspData = new HashMap<>();
        rspData.put("rows", briefingUserList.getRecords());
        rspData.put("isRead", map);
        rspData.put("total", briefingUserList.getTotal());
        return new JiebaoResponse().data(rspData);
    }


    @GetMapping("/getView")
    @ApiOperation(value = "查看(收件箱)", notes = "查看(收件箱)", httpMethod = "GET")
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse getView(String briefingId) {
        String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
        User byName = userService.findByName(username);
        BriefingUser byNameAndId = briefingUserMapper.findByNameAndId(briefingId, byName.getUserId());
        if (byNameAndId.getIsRead() == 0) {
            briefingUserMapper.updateIsRead(briefingId, byName.getUserId());
        }
        return new JiebaoResponse().data(byNameAndId).message("查看成功").put("status", "200");
    }


    @GetMapping("/countCity")
    @ApiOperation(value = "统计每个市州被填报的数量", notes = "统计每个市州被填报的数量", httpMethod = "GET")
    @Transactional(rollbackFor = Exception.class)
    public List<Map<String, Object>> countCity(String startTime, String endTime) {
        Dept dept = deptService.getDept();
        String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
        if (dept.getRank() == 0) {
            return briefingCountMapper.countCity(startTime, endTime);
        } else {
            return briefingCountMapper.countCityByI(startTime, endTime, username);
        }

    }


    @GetMapping("/countList")
    @ApiOperation(value = "分页查询统计", notes = "查询分页数据统计", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse countList(QueryRequest request, Briefing briefing, String startTime, String endTime) {
        IPage<Briefing> briefingList = briefingService.countList(request, briefing, startTime, endTime);
        return new JiebaoResponse().data(this.getDataTable(briefingList));
    }


    @GetMapping("/countCityById")
    @ApiOperation(value = "根据ID查询十四个市州条数", notes = "根据ID查询十四个市州条数", response = JiebaoResponse.class, httpMethod = "GET")
    public List<Map<String, Object>> countCityById(String briefingId) {
        List<Map<String, Object>> maps = briefingMapper.countCityById(briefingId);
        return maps;
    }


    @GetMapping("/countListByCity")
    @ApiOperation(value = "根据十四个市州查询对应简报", notes = "根据十四个市州查询对应简报", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse countListByCity(@RequestParam(defaultValue = "1") Integer pageNum,@RequestParam(defaultValue = "1") Integer pageSize,String startTime,String endTime,String deptName,String title) {
        IPage<Briefing> briefingIPage = briefingService.countListByCity(pageNum, pageSize, startTime, endTime,deptName, title);
        return new JiebaoResponse().data(briefingIPage).okMessage("查询成功");
    }
}
