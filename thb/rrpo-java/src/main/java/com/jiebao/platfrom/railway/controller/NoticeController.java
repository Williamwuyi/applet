package com.jiebao.platfrom.railway.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.jiebao.platfrom.common.annotation.Log;
import com.jiebao.platfrom.common.authentication.JWTUtil;
import com.jiebao.platfrom.common.controller.BaseController;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.exception.JiebaoException;
import com.jiebao.platfrom.railway.dao.NoticeMapper;
import com.jiebao.platfrom.railway.domain.Notice;
import com.jiebao.platfrom.railway.service.NoticeService;
import com.jiebao.platfrom.system.dao.FileMapper;
import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.domain.User;
import com.jiebao.platfrom.system.service.DeptService;
import com.jiebao.platfrom.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
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
@RequestMapping(value = "/notice")
@Api(tags = "railWay-通知公告(改)")   //swagger2 api文档说明示例
public class NoticeController extends BaseController {

    private String message;
    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private FileMapper fileMapper;
    @Autowired
    DeptService deptService;
    @Autowired
    UserService userService;


    /**
     * 使用Mapper操作数据库
     *
     * @return JiebaoResponse 标准返回数据类型
     */
    @PostMapping(value = "/getNoticeListByMapper")
    @ApiOperation(value = "查询数据List", notes = "查询数据List列表", response = JiebaoResponse.class, httpMethod = "POST")
    public JiebaoResponse getNoticeListByMapper() {
        List<Notice> list = noticeService.list();
        for (Notice i : list
        ) {
            i.setKey(i.getId());
        }
        return new JiebaoResponse().data(list).message("查询成功").put("remake", "其他数据返回");
    }

    /**
     * 分页查询
     *
     * @param request - 分页参数
     * @return
     * @Parameters sortField  according to order by Field
     * @Parameters sortOrder  JiebaoConstant.ORDER_ASC or JiebaoConstant.ORDER_DESC
     */
    @GetMapping
    @ApiOperation(value = "分页查询(发件箱)", notes = "查询分页数据（发件箱）", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse getNoticeList(QueryRequest request, Notice notice, String startTime, String endTime) {
        IPage<Notice> noticeList = noticeService.getNoticeList(request, notice, startTime, endTime);
        List<Notice> records = noticeList.getRecords();
        for (Notice i : records
        ) {
            i.setKey(i.getId());
        }
        return new JiebaoResponse().data(this.getDataTable(noticeList));
    }


    @GetMapping("/inbox")
    @ApiOperation(value = "分页查询(收件箱)", notes = "查询分页数据（收件箱）", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse getNoticeInboxList(QueryRequest request, Notice notice, String startTime, String endTime) {
        Dept dept = deptService.getDept();
        if (dept.getRank() == 0){
            IPage<Notice> noticeList = noticeService.getNoticeInboxList(request, notice, startTime, endTime);
            List<Notice> records = noticeList.getRecords();
            for (Notice i : records
            ) {
                i.setKey(i.getId());
            }
            return new JiebaoResponse().data(this.getDataTable(noticeList));
        }
        else {
            IPage<Notice> noticeList = noticeService.getNoticeInboxListByParent(request, notice, startTime, endTime);
            List<Notice> records = noticeList.getRecords();
            for (Notice i : records
            ) {
                i.setKey(i.getId());
            }
            return new JiebaoResponse().data(this.getDataTable(noticeList));
        }

    }

    @DeleteMapping("/{ids}")
    @Log("删除通知公告")
    @ApiOperation(value = "批量删除", notes = "批量删除", response = JiebaoResponse.class, httpMethod = "DELETE")
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse delete(@PathVariable String[] ids) throws JiebaoException {
        try {
            Arrays.stream(ids).forEach(id -> {
                Notice byId = noticeService.getById(id);
                //未发布时才能删掉本体信息,否则只改状态为4
                if ("1".equals(byId.getStatus()) || "2".equals(byId.getStatus())) {
                    noticeService.removeById(id);
                } else {
                    noticeMapper.updateStatus(id);
                }
            });
        } catch (Exception e) {
            throw new JiebaoException("删除失败");
        }
        return new JiebaoResponse().message("删除成功");
    }

    @PostMapping
    @ApiOperation(value = "新增通知公告", notes = "新增通知公告", response = JiebaoResponse.class, httpMethod = "POST")
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse addNotice(@Valid Notice notice, String[] fileIds) {
        notice.setStatus("1");
        String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
        notice.setCreateUser(username);
        User byName = userService.findByName(username);
        notice.setUserId(byName.getUserId());
        notice.setDeptId(byName.getDeptId());
        noticeService.save(notice);
        if (fileIds != null) {
            Arrays.stream(fileIds).forEach(fileId -> {
                fileMapper.updateNoticeByFileId(fileId, notice.getId());
            });
        }
        return new JiebaoResponse().message("成功");
    }

    @PutMapping
    @Log("修改通知公告")
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(value = "修改", notes = "修改", response = JiebaoResponse.class, httpMethod = "PUT")
    public void updateNotice(@Valid Notice notice) throws JiebaoException {
        try {
            this.noticeService.updateByKey(notice);
        } catch (Exception e) {
            message = "修改通讯录失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }


    @GetMapping("/revoke/{noticeIds}")
    @Log("撤销通知公告")
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(value = "撤销通知公告", notes = "撤销通知公告", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse revoke(@PathVariable String[] noticeIds) throws JiebaoException {
        try {
            if (noticeIds != null) {
                Arrays.stream(noticeIds).forEach(noticeId -> {
                    //状态status改为2
                    noticeService.revokeNotice(noticeId);
                });
                return new JiebaoResponse().message("撤销通知公告成功");
            }
        } catch (Exception e) {
            message = "撤销通知公告失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
        return new JiebaoResponse().message("通知公告撤销失败");

    }


    @GetMapping("/release/{noticeIds}")
    @Log("发布通知公告")
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(value = "发布通知公告", notes = "发布通知公告", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse release(@PathVariable String[] noticeIds) throws JiebaoException {
        try {
            if (noticeIds != null) {
                Arrays.stream(noticeIds).forEach(noticeId -> {
                    //状态status改为3
                    noticeService.release(noticeId);
                    Notice byId = noticeService.getById(noticeId);
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
                            ArrayList<File> filess =new ArrayList<>();
                            Map<String, Object> map = new HashMap<>();
                            map.put("ref_type", 3);
                            map.put("file_type", 2);
                            map.put("ref_id",noticeId);
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
                                for (File file:filess) {
                                    multipartEntityBuilder.addBinaryBody("file",file);
                                }
                            }
                            Map<String, String> params = new HashMap<>();
                            params.put("id", byId.getTargetsId());
                            Dept dept = deptService.getDept();
                            String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
                            params.put("source", (dept.getDeptName()+"-"+username));
                            params.put("title", byId.getTitle());
                            //如果富编辑器里有图片，转换成base64替换img标签所有内容
                           /* Map<String, Object> mapF = new HashMap<>();
                            mapF.put("ref_type",8);
                            mapF.put("file_type",1);
                            List<com.jiebao.platfrom.system.domain.File> filesF = fileMapper.selectByMap(mapF);
                            if (filesF.size()>0){
                                Element doc = Jsoup.parseBodyFragment(byId.getContent()).body();
                                Elements jpg = doc.select("img[src]");
                                for (com.jiebao.platfrom.system.domain.File f: filesF
                                ) {
                                    String url = f.getFileUrl() + f.getOldName();
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
                            if (byId.getTime()!=null){
                                String relTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(byId.getTime());
                                params.put("time", relTime);
                            }
                            else {
                                String relTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
                                params.put("time", relTime);
                            }

                            params.put("user", username);
                            ContentType strContent=ContentType.create("text/plain", Charset.forName("UTF-8"));
                            if (!CollectionUtils.isEmpty(params)) {
                                params.forEach((key, value) -> {
                                    //此处的字符串参数会被设置到请求体Query String Parameters中
                                    multipartEntityBuilder.addTextBody(key, value,strContent);
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
                return new JiebaoResponse().message("发布通知公告成功");
            }
        } catch (Exception e) {
            message = "发布通知公告失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
        return new JiebaoResponse().message("发布通知公告失败");
    }

    @GetMapping(value = "/getList")
    @ApiOperation(value = "List", notes = "List列表", response = JiebaoResponse.class, httpMethod = "GET")
    public List<Notice> getList(Notice notice, QueryRequest request) {
        return noticeService.getNoticeLists(notice, request);
    }


   /* @DeleteMapping("/inbox/{ids}")
    @Log("删除通知公告(收件箱)")
    @ApiOperation(value = "批量删除(收件箱)", notes = "批量删除(收件箱)", response = JiebaoResponse.class, httpMethod = "DELETE")
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse deleteInbox(@PathVariable String[] ids) throws JiebaoException {
        try {
            String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
            User byName = userService.findByName(username);
            Arrays.stream(ids).forEach(id -> {
                Map<String, Object> map = new HashMap<>();
                map.put("")
                noticeService.removeByMap(map);
            });
        } catch (Exception e) {
            throw new JiebaoException("删除失败");
        }
        return new JiebaoResponse().message("删除成功");
    }*/

}
