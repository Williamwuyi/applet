package com.jiebao.platfrom.railway.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.jiebao.platfrom.check.dao.YearMapper;
import com.jiebao.platfrom.common.annotation.Log;
import com.jiebao.platfrom.common.authentication.JWTUtil;
import com.jiebao.platfrom.common.controller.BaseController;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.exception.JiebaoException;
import com.jiebao.platfrom.railway.dao.ExchangeMapper;
import com.jiebao.platfrom.railway.dao.ExchangeUserMapper;
import com.jiebao.platfrom.railway.domain.*;
import com.jiebao.platfrom.railway.service.AsYearService;
import com.jiebao.platfrom.railway.service.ExchangeFileService;
import com.jiebao.platfrom.railway.service.ExchangeService;
import com.jiebao.platfrom.railway.service.ExchangeUserService;
import com.jiebao.platfrom.system.dao.FileMapper;
import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.domain.User;
import com.jiebao.platfrom.system.service.DeptService;
import com.jiebao.platfrom.system.service.UserService;
import com.wuwenze.poi.ExcelKit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.shiro.SecurityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author yf
 */
@Slf4j
@RestController
@RequestMapping(value = "/exchange")
@Api(tags = "railWay-信息互递")   //swagger2 api文档说明示例
public class ExchangeController extends BaseController {


    private String message;

    @Autowired
    private ExchangeService exchangeService;

    @Autowired
    private ExchangeUserService exchangeUserService;

    @Resource
    private ExchangeMapper exchangeMapper;

    @Autowired
    private UserService userService;

    @Resource
    private ExchangeUserMapper exchangeUserMapper;

    @Resource
    private FileMapper fileMapper;

    @Autowired
    private DeptService deptService;

    @Autowired
    private AsYearService asYearService;

    @Resource
    private YearMapper yearMapper;

    /**
     * 创建一条信息互递
     */
    @PostMapping("/creat")
    @ApiOperation(value = "创建一条信息互递或创建并发送(修改)", notes = "创建一条信息互递或创建并发送(修改)", response = JiebaoResponse.class, httpMethod = "POST")
    public JiebaoResponse creatExchange(@Valid Exchange exchange, String[] sendUserIds, String[] fileIds, String[] menusYearIds,String yearTime) throws JiebaoException {
        try {
            String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
            if (username != null) {
                exchange.setCreatUser(username);
            }
            User byName = userService.findByName(username);
            Dept deptOwn = deptService.getById(byName.getDeptId());


            //修改时间
            if (exchange.getClaimTime() != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(exchange.getClaimTime());
                // 将时分秒,毫秒域清零
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                exchange.setClaimTime(cal.getTime());
            }
            if ("1".equals(exchange.getStatus())) {
                boolean save = exchangeService.saveOrUpdate(exchange);
                Arrays.stream(fileIds).forEach(fileId -> {
                    fileMapper.updateByFileId(fileId, exchange.getId());
                    if (menusYearIds != null) {
                        //关联年度考核项
                        Arrays.stream(menusYearIds).forEach(menusYearId -> {
                            AsYear asYear = new AsYear();
                            asYear.setFileId(fileId);
                            asYear.setGradeId(menusYearId);
                            asYear.setType(2);
                            asYear.setDeptId(this.getParentDept(deptOwn).getDeptId());
                            asYear.setYearId(yearTime);
                            asYearService.save(asYear);
                        });
                    }
                });

                exchangeUserService.deleteByExchangeId(exchange.getId());
                if (save) {
                    Arrays.stream(sendUserIds).forEach(sendUserId -> {
                        //把要发送的用户保存到数据库
                        User byId = userService.getById(sendUserId);
                        exchangeUserService.saveByUserId(exchange.getId(), sendUserId, byId.getUsername());
                    });
                }
                return new JiebaoResponse().message("创建一条信息互递成功");
            } else if ("3".equals(exchange.getStatus())) {
                if (exchange.getTime() == null) {
                    exchange.setTime(new Date());
                }
                boolean save = exchangeService.saveOrUpdate(exchange);
                Arrays.stream(fileIds).forEach(fileId -> {
                    fileMapper.updateByFileId(fileId, exchange.getId());
                    if (menusYearIds != null) {
                        //关联年度考核项
                        Arrays.stream(menusYearIds).forEach(menusYearId -> {
                            AsYear asYear = new AsYear();
                            asYear.setFileId(fileId);
                            asYear.setGradeId(menusYearId);
                            asYear.setType(2);
                            asYear.setDeptId(this.getParentDept(deptOwn).getDeptId());
                            asYear.setYearId(yearTime);
                            asYearService.save(asYear);
                        });
                    }
                });
                if (save) {
                    Arrays.stream(sendUserIds).forEach(sendUserId -> {
                        //把要发送的用户保存到数据库
                        User byId = userService.getById(sendUserId);
                        exchangeUserService.saveByUserId(exchange.getId(), sendUserId, byId.getUsername());
                    });
                }
                exchangeMapper.releaseSave(exchange.getId());
                exchangeUserMapper.setCreatTime(exchange.getId());
                if (exchange.getSynchronizeWeb() == 1) {
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

                          /*
                            //获取文件url
                            String fileUrl ="D:\\tempDoc.docx";
                            String fileu = "D:\\newDoc.docx";
                            File is = new File(fileUrl);
                            File fil =new File(fileu);
                           *//* Map<String, File> files = new HashMap<>();
                             files.put("file",is);
                            files.put("file",fil);*//*
                            ArrayList<File> filess =new ArrayList<>();
                            filess.add(is);
                            filess.add(fil);*/

                        //获取附件
                        ArrayList<File> filess = new ArrayList<>();
                        Map<String, Object> map = new HashMap<>();
                        map.put("ref_type", 8);
                        map.put("file_type", 2);
                        map.put("ref_id", exchange.getId());
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
                        System.out.println(exchange.getTargetsId() + "----------------");
                        Map<String, String> params = new HashMap<>();
                        params.put("id", exchange.getTargetsId());
                        Dept dept = deptService.getDept();
                        params.put("source", (dept.getDeptName() + "-" + username));
                        params.put("title", exchange.getTitle());
                        //如果富编辑器里有图片，转换成base64替换img标签所有内容
                       /* Map<String, Object> mapF = new HashMap<>();
                        map.put("ref_type", 8);
                        map.put("file_type", 1);
                        map.put("ref_id",exchange.getId());
                        List<com.jiebao.platfrom.system.domain.File> filesF = fileMapper.selectByMap(mapF);
                        if (filesF.size() > 0) {
                            Element doc = Jsoup.parseBodyFragment(exchange.getContent()).body();
                            Elements jpg = doc.select("img[src]");
                            for (com.jiebao.platfrom.system.domain.File f : files
                            ) {
                                String url = f.getFileUrl() + f.getNewName();
                                //转换为base64
                                BASE64Encoder encoder = new BASE64Encoder();
                                InputStream in = new FileInputStream(url);
                                byte[] data = new byte[in.available()];
                                String encode = encoder.encode(data);
                            }

                        } else {
                            params.put("html", exchange.getContent());
                        }*/
                        params.put("html", exchange.getContent());
                        if (exchange.getTime() != null) {
                            String relTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(exchange.getTime());
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
                return new JiebaoResponse().message("创建并发布一条信息互递成功");
            }
            return new JiebaoResponse().message("系统错误");
        } catch (Exception e) {
            message = "创建信息互递失败";
            log.error(message, e);
            throw new JiebaoException("创建一条信息互递失败");
        }

    }

    //查询父级，直到rank==1，为市级
    public List<Dept> depts() {
        return deptService.list();
    }

    public Dept getParentDept(Dept dept) {
        if (dept.getRank() == 1) {
            return dept;
        }
        Dept dept1 = depts().stream().filter(x -> Objects.equals(x.getDeptId(), dept.getParentId())
        ).findFirst().get();
        return getParentDept(dept1);
    }

    @GetMapping("/release/{exchangeIds}")
    @Log("批量发布信息互递")
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(value = "批量发布信息互递", notes = "批量发布信息互递", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse release(@PathVariable String[] exchangeIds) throws JiebaoException {
        try {
            if (exchangeIds != null) {
                Arrays.stream(exchangeIds).forEach(exchangeId -> {
                    //状态status改为3
                    exchangeMapper.release(exchangeId);
                    exchangeUserMapper.setCreatTime(exchangeId);
                    Exchange byId = exchangeService.getById(exchangeId);
                    if (byId.getSynchronizeWeb() == 1) {
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
                            map.put("ref_id", exchangeId);
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
                         /*   Map<String, Object> mapF = new HashMap<>();
                            map.put("ref_type", 8);
                            map.put("file_type", 1);
                            List<com.jiebao.platfrom.system.domain.File> filesF = fileMapper.selectByMap(mapF);
                            if (filesF.size() > 0) {
                                Element doc = Jsoup.parseBodyFragment(byId.getContent()).body();
                                Elements jpg = doc.select("img[src]");
                                for (com.jiebao.platfrom.system.domain.File f : files
                                ) {
                                    String url = f.getFileUrl() + f.getNewName();
                                    //转换为base64
                                    BASE64Encoder encoder = new BASE64Encoder();
                                    InputStream in = new FileInputStream(url);
                                    byte[] data = new byte[in.available()];
                                    String encode = encoder.encode(data);
                                }

                            } else {
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
                return new JiebaoResponse().message("发布信息互递成功");
            }
        } catch (Exception e) {
            message = "发布信息互递失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
        return new JiebaoResponse().message("发布信息互递失败");
    }


    @DeleteMapping("/{exchangeIds}")
    @ApiOperation(value = "批量删除信息（完全删除未发送的，假删除已发送的）(发件箱)", notes = "批量删除信息（完全删除未发送的，假删除已发送的）(发件箱)", response = JiebaoResponse.class, httpMethod = "DELETE")
    public JiebaoResponse deleteExchange(@PathVariable String[] exchangeIds) throws JiebaoException {
        try {
            Arrays.stream(exchangeIds).forEach(exchangeId -> {
                Map<String, Object> map = new HashMap<>();
                map.put("ref_type", 1);
                map.put("ref_id", exchangeId);
                List<com.jiebao.platfrom.system.domain.File> files = fileMapper.selectByMap(map);
                Exchange byId = exchangeService.getById(exchangeId);
                //未发送状态，删掉文件，删除接收人，删除该信息本体
                if ("1".equals(byId.getStatus())) {
                    if (files.size() > 0) {
                        for (com.jiebao.platfrom.system.domain.File file : files
                        ) {
                            File fil = new File((file.getFileUrl() + file.getOldName()));
                            if (fil.exists()) {
                                fil.delete();
                            }
                        }
                    }
                    fileMapper.deleteByMap(map);
                    exchangeUserService.deleteByExchangeId(exchangeId);
                    exchangeService.removeById(exchangeId);
                    //已发布状态，只把状态改为4即可，没有2撤回
                } else if ("3".equals(byId.getStatus())) {
                    exchangeMapper.updateStatus(exchangeId);
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
    @ApiOperation(value = "修改未发送的信息互递", notes = "修改未发送的信息互递", httpMethod = "PUT")
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse updateExchange(@Valid Exchange exchange, @PathVariable String[] sendUserIds) throws JiebaoException {
        try {
            exchangeService.updateById(exchange);
            //先删除掉原有发送人
            boolean b = exchangeUserService.deleteByExchangeId(exchange.getId());
            //重新添加
            if (b) {
                Arrays.stream(sendUserIds).forEach(sendUserId -> {
                    //把要发送的用户保存到数据库
                    User byId = userService.getById(sendUserId);
                    exchangeUserService.saveByUserId(exchange.getId(), sendUserId, byId.getUsername());
                });
                return new JiebaoResponse().message("修改未发送的信息互递成功");
            } else {
                return new JiebaoResponse().message("修改未发送的信息互递失败");
            }
        } catch (Exception e) {
            message = "修改信息互递失败";
            log.error(message, e);
            throw new JiebaoException("修改失败");
        }
    }

    @GetMapping
    @ApiOperation(value = "分页查询（查询未发送和已发送的）", notes = "查询分页数据（查询未发送和已发送的）", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse getExchangeList(QueryRequest request, Exchange exchange, String startTime, String endTime) {
        IPage<Exchange> exchangeList = exchangeService.getExchangeList(request, exchange, startTime, endTime);
        return new JiebaoResponse().data(this.getDataTable(exchangeList));
    }

    @GetMapping("/forCheck")
    @ApiOperation(value = "分页查询（查询未发送和已发送的给年度考核）", notes = "查询分页数据（查询未发送和已发送的给年度考核）", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse getExchangeListForCheck(QueryRequest request, Exchange exchange, String id, String startTime, String endTime) {
        IPage<Exchange> exchangeList = exchangeService.getExchangeListForCheck(request, exchange, id, startTime, endTime);
        return new JiebaoResponse().data(this.getDataTable(exchangeList));
    }


    @GetMapping("/inbox")
    @ApiOperation(value = "分页查询（查询收件箱）", notes = "查询分页数据（查询收件箱）", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse getExchangeInboxList(QueryRequest request, Exchange exchange, String startTime, String endTime) {
        IPage<Exchange> exchangeList = exchangeService.getExchangeInboxList(request, exchange, startTime, endTime);
        List<Exchange> records = exchangeList.getRecords();
        for (Exchange e : records
        ) {
            String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
            User byName = userService.findByName(username);
            ExchangeUser exchangeUser = exchangeUserMapper.getIsRead(byName.getUserId(), e.getId());
            if (exchangeUser != null) {
                e.setIsRead(exchangeUser.getIsRead());
            }
        }
        return new JiebaoResponse().data(this.getDataTable(exchangeList));
    }


    @DeleteMapping("/inbox/{exchangeIds}")
    @ApiOperation(value = "批量删除信息（收件箱）", notes = "批量删除信息（收件箱）", response = JiebaoResponse.class, httpMethod = "DELETE")
    public JiebaoResponse deleteInboxExchange(@PathVariable String[] exchangeIds) throws JiebaoException {
        try {
            String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
            User byName = userService.findByName(username);
            Arrays.stream(exchangeIds).forEach(exchangeId -> {
                exchangeUserService.removeBySendUserId(byName.getUserId(), exchangeId);
            });
            return new JiebaoResponse().message("批量删除信息成功");
        } catch (Exception e) {
            log.error(message, e);
            throw new JiebaoException("批量删除信息失败");
        }
    }

  /*  @DeleteMapping("/outbox/{exchangeIds}")
    @ApiOperation(value = "批量删除信息（收件箱）", notes = "批量删除信息（收件箱）", response = JiebaoResponse.class, httpMethod = "DELETE")
    public JiebaoResponse updateInboxExchange(@PathVariable String[] exchangeIds) throws JiebaoException {
        try {
            String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
            User byName = userService.findByName(username);
            Arrays.stream(exchangeIds).forEach(exchangeId -> {
                exchangeUserService.upDateBySendUserId(byName.getUserId(),exchangeId);
            });
            return new JiebaoResponse().message("批量删除信息成功");
        } catch (Exception e) {
            log.error(message, e);
            throw new JiebaoException("批量删除信息失败");
        }
    }*/


    @GetMapping(value = "/getInfoById/{exchangeId}")
    @ApiOperation(value = "根据ID查信息info", notes = "根据ID查信息info", response = JiebaoResponse.class, httpMethod = "GET")
    public Exchange getInfoById(@PathVariable String exchangeId) {
        Exchange byId = exchangeService.getById(exchangeId);
        Map<String, Object> columnMap = new HashMap<>();
        //列exchange_id为数据库中的列名，不是实体类中的属性名
        columnMap.put("exchange_id", exchangeId);
        List listId = new ArrayList();
        List listName = new ArrayList();
        List<ExchangeUser> exchangeUsers = exchangeUserMapper.selectByMap(columnMap);
        for (ExchangeUser eu : exchangeUsers
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
        map.put("ref_id", exchangeId);
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
    public JiebaoResponse getUserInfo(QueryRequest request, ExchangeUser exchangeUser) {
        IPage<ExchangeUser> exchangeUserList = exchangeUserService.getExchangeUserList(request, exchangeUser);
        List<ExchangeUser> records = exchangeUserList.getRecords();
        for (ExchangeUser e : records
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
        int zero = exchangeUserMapper.countByIsReadZero(exchangeUser.getExchangeId());
        int one = exchangeUserMapper.countByIsReadOne(exchangeUser.getExchangeId());
        int two = exchangeUserMapper.countByIsReadTwo(exchangeUser.getExchangeId());
        map.put("zero", zero);
        map.put("one", one);
        map.put("two", two);
        Map<String, Object> rspData = new HashMap<>();
        rspData.put("rows", exchangeUserList.getRecords());
        rspData.put("isRead", map);
        rspData.put("total", exchangeUserList.getTotal());
        return new JiebaoResponse().data(rspData);
    }


    @PutMapping("/receive")
    @ApiOperation(value = "意见回复或修改", notes = "意见回复或修改", httpMethod = "PUT")
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse receive(@Valid ExchangeUser exchangeUser, String[] fileIds) throws JiebaoException {
        try {
            String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
            User byName = userService.findByName(username);
            Arrays.stream(fileIds).forEach(fileId -> {
                fileMapper.updateByFileId(fileId, exchangeUser.getExchangeId());
            });
            this.exchangeUserService.updateByExchangeId(byName.getUserId(), exchangeUser.getExchangeId(), exchangeUser.getOpinion());
            return new JiebaoResponse().message("意见回复或修改成功").put("status", "200");
        } catch (Exception e) {
            message = "意见回复或修改失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }


    @GetMapping("/getReceive")
    @ApiOperation(value = "查看回复", notes = "查看回复", httpMethod = "GET")
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse getReceive(String exchangeId) {
        String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
        User byName = userService.findByName(username);
        ExchangeUser byNameAndId = exchangeUserMapper.findByNameAndId(exchangeId, byName.getUserId());
        if (byNameAndId.getIsRead() == 0) {
            exchangeUserMapper.updateIsRead(exchangeId, byName.getUserId());
        }
        return new JiebaoResponse().data(byNameAndId).message("查看成功").put("status", "200");
    }

    @PostMapping("/excel")
    @ApiOperation(value = "导出", notes = "导出", httpMethod = "POST")
    public void export(String[] exchangeIds, HttpServletResponse response) throws JiebaoException {
        try {
            List<ExchangeUser> list = new ArrayList<>();
            Arrays.stream(exchangeIds).forEach(exchangeId -> {
                ExchangeUser byId = exchangeUserService.getById(exchangeId);
                User user = userService.getById(byId.getSendUserId());
                Dept dept = deptService.getById(user.getDeptId());
                byId.setDeptName(dept.getDeptName());
                list.add(byId);
            });
            for (ExchangeUser u : list
            ) {
                System.out.println(u.toString());
            }
            ExcelKit.$Export(ExchangeUser.class, response).downXlsx(list, false);
        } catch (Exception e) {
            message = "导出Excel失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }


    @GetMapping("/getNotReceive")
    @ApiOperation(value = "查看要求回复却未回复信息互递", notes = "查看要求回复却未回复信息互递", httpMethod = "GET")
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse getNotReceive() {
        String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
        User byName = userService.findByName(username);
        List<Exchange> notReceive = exchangeMapper.getNotReceive(byName.getUserId());
        if (notReceive.size() > 0) {
            return new JiebaoResponse().data(notReceive).okMessage("查询成功");
        } else {
            return new JiebaoResponse().failMessage("无数据");
        }
    }
}
