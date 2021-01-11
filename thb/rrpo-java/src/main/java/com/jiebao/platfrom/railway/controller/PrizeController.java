package com.jiebao.platfrom.railway.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.jiebao.platfrom.common.authentication.JWTUtil;
import com.jiebao.platfrom.common.controller.BaseController;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.exception.JiebaoException;
import com.jiebao.platfrom.common.utils.EqualsMonth;
import com.jiebao.platfrom.demo.test.WorderToNewWordUtils;
import com.jiebao.platfrom.railway.dao.*;
import com.jiebao.platfrom.railway.domain.*;
import com.jiebao.platfrom.railway.service.*;
import com.jiebao.platfrom.system.dao.*;
import com.jiebao.platfrom.system.domain.*;
import com.jiebao.platfrom.system.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.ir.ContinueNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import org.hibernate.validator.internal.util.privilegedactions.GetResource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 一事一奖内容表
 * </p>
 *
 * @author yf
 * @since 2020-07-24
 */
@Slf4j
@RestController
@RequestMapping("/prize")
@Api(tags = "railWay-一事一奖")   //swagger2 api文档说明示例
public class PrizeController extends BaseController {


    //private final String HOST = "http://192.168.10.104:19527";
    private final String HOST = "http://114.115.147.159:19527";


    private String message;

    @Autowired
    private PrizeUserService prizeUserService;

    @Autowired
    private PrizeService prizeService;

    @Autowired
    private PrizeMapper prizeMapper;

    @Autowired
    private PrizeOrderService prizeOrderService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PrizeLimitMapper prizeLimitMapper;

    @Autowired
    private PrizeUserMapper prizeUserMapper;

    @Autowired
    private DeptService deptService;

    @Autowired
    private DeptMapper deptMapper;

    @Autowired
    private PrizeOpinionMapper prizeOpinionMapper;

    @Autowired
    private PrizeOpinionService prizeOpinionService;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private PrizeRejectOpinionService prizeRejectOpinionService;

    @Autowired
    private PrizeRejectOpinionMapper prizeRejectOpinionMapper;

    @Autowired
    private DictService dictService;


    /**
     * 创建一条一事一奖
     */


    @PostMapping("/save")
    @ApiOperation(value = "创建一条一事一奖", notes = "创建一条一事一奖", response = JiebaoResponse.class, httpMethod = "POST")
    public JiebaoResponse creatPrize(@Valid Prize prize, String[] fileIds, String cityId) throws JiebaoException {
        try {
            String newNumber = null;
            String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
            User byName = userService.findByName(username);
            prize.setCreatUser(username);
            //获得number最大值
            //生成编号
            Integer maxNumber = prizeMapper.findMaxNumber();
            if (maxNumber == null) {
                maxNumber = 0;
            }
            String maxNumberString = (maxNumber + 1) + "";
            for (int i = 0; i < 4; i++) {
                if (maxNumberString.length() == 4) {
                    break;
                } else {
                    maxNumberString = "0" + maxNumberString;
                }
            }
            String place = prize.getPlace();
            if (place.contains("衡阳市")) {
                newNumber = "01" + maxNumberString;
            } else if (place.contains("岳阳市")) {
                newNumber = "02" + maxNumberString;
            } else if (place.contains("怀化市")) {
                newNumber = "03" + maxNumberString;
            } else if (place.contains("娄底市")) {
                newNumber = "04" + maxNumberString;
            } else if (place.contains("郴州市")) {
                newNumber = "05" + maxNumberString;
            } else if (place.contains("湘西州")) {
                newNumber = "06" + maxNumberString;
            } else if (place.contains("株洲市")) {
                newNumber = "07" + maxNumberString;
            } else if (place.contains("永州市")) {
                newNumber = "08" + maxNumberString;
            } else if (place.contains("张家界市")) {
                newNumber = "09" + maxNumberString;
            } else if (place.contains("湘潭市")) {
                newNumber = "10" + maxNumberString;
            } else if (place.contains("邵阳市")) {
                newNumber = "11" + maxNumberString;
            } else if (place.contains("益阳市")) {
                newNumber = "12" + maxNumberString;
            } else if (place.contains("长沙市")) {
                newNumber = "13" + maxNumberString;
            } else if (place.contains("常德市")) {
                newNumber = "14" + maxNumberString;
            } else if (place.contains("长沙铁路公安处")) {
                newNumber = "15" + maxNumberString;
            } else if (place.contains("怀化铁路公安处")) {
                newNumber = "16" + maxNumberString;
            } else if (place.contains("衡阳铁路公安处")) {
                newNumber = "17" + maxNumberString;
            }
            Dept dept = deptService.getById(byName.getDeptId());
            Dept parentDept = deptService.getById(dept.getParentId());
            prize.setNewNumber(newNumber);
            prize.setUserId(byName.getUserId());

            if ( dept.getRank() == 4 || parentDept.getRank() ==4) {
                prize.setType(0);
            }
            else {
                prize.setType(1);
            }
            boolean save = prizeService.save(prize);
            Arrays.stream(fileIds).forEach(fileId -> {
                fileMapper.updateByFileId(fileId, prize.getId());
            });



            if (dept.getRank() == 2 || dept.getRank() == 3) {
                //获取该区县或派出所或街道的组织机构，则getParentId就是获取它上级组织机构ID
                Dept byId = deptService.getById(byName.getDeptId());
                //把要发送的deptId保存到数据库
                prizeUserService.saveByDept(prize.getId(), byId.getParentId());
                //市级发送自身组织机构id
            } else if (dept.getRank() == 1) {
                Dept byId = deptService.getById(byName.getDeptId());
                //把要发送的deptId保存到数据库
                prizeUserService.saveByDept(prize.getId(), byId.getDeptId());
                //公安处创建发送给自己
            } else if (dept.getRank() == 4) {
                Dept byId = deptService.getById(byName.getDeptId());
                //把要发送的deptId保存到数据库
                prizeUserService.saveByDept(prize.getId(), byId.getDeptId());
            } else {
                return new JiebaoResponse().message("无权限");
            }
            return new JiebaoResponse().message("创建一条一事一奖成功");

        } catch (Exception e) {
            message = "创建一事一奖失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }


    /**
     * 批量发布一事一奖
     */
    @GetMapping("/{prizeIds}")
    @ApiOperation(value = "批量发布一事一奖", notes = "批量发布一事一奖", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse creatPrize(@PathVariable String[] prizeIds) throws JiebaoException {
        try {
            String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
            User byName = userService.findByName(username);
            PrizeLimit prizeLimit = prizeLimitMapper.selectOne(new LambdaQueryWrapper<PrizeLimit>().eq(PrizeLimit::getDeptId, byName.getDeptId()));
            Integer limitNumber;
            if (prizeLimit == null) {
                //如果无记录，则设置默认限制上报次数如果为10,则要10-1即应为9
                limitNumber = 219;
            } else {
                limitNumber = prizeLimit.getLimitNumber();
            }
            //获取当前年月，该组织机构已经发布条数 ，然后+1，set进releaseNumber
            Integer countByDept = prizeOrderService.getCountByDept(byName.getDeptId());
            if (countByDept <= limitNumber) {
                Arrays.stream(prizeIds).forEach(prizeId -> {
                    Prize prize = prizeService.getById(prizeId);
                    if (prize.getStatus() == 1) {
                        PrizeOrder prizeOrderSet = new PrizeOrder();
                        prizeOrderSet.setDeptId(byName.getDeptId());
                        prizeOrderSet.setCreatTime(prize.getReleaseTime());
                        prizeOrderSet.setPrizeId(prizeId);
                        prizeOrderSet.setTitle(prize.getContent());
                        prizeOrderSet.setReleaseNumber(countByDept + 1);
                        prizeOrderService.save(prizeOrderSet);
                        //把status改为3,并创建发布时间
                        prizeMapper.release(prizeId);
                        prizeUserMapper.setCreatTime(prizeId);
                    }
                });
            } else {
                return new JiebaoResponse().message("超出发布次数限制，本月限制发布条数为" + (limitNumber + 1) + "，已超出！");
            }
            return new JiebaoResponse().message("发布一事一奖成功");
        } catch (Exception e) {
            throw new JiebaoException("发布一事一奖失败");
        }
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "prizeIds", value = "一事一奖的id")
    })
    @DeleteMapping("/{prizeIds}")
    @ApiOperation(value = "删除一事一奖（包括未发布的，已发布的，已发布的并不会真正删除，但是查询会显示不出）", notes = "删除一事一奖（包括未发布的，已发布的，已发布的并不会真正删除，但是查询会显示不出）", response = JiebaoResponse.class, httpMethod = "DELETE")
    public JiebaoResponse deletePrize(@PathVariable String[] prizeIds) throws JiebaoException {

        try {

            System.out.println(prizeIds);
            Arrays.stream(prizeIds).forEach(prizeId -> {
                //不能直接删掉文件（暂时未做文件），不能删除接收人，不能删除该信息本体，直接改状态 status为4
                Prize byId = prizeService.getById(prizeId);
                //1为未发送状态
                if (byId.getStatus() == 1) {
                    //删除接收的组织机构
                    prizeUserService.deleteByPrizeId(prizeId);
                    //删除内容本体（文件还没加哦）
                    prizeService.removeById(prizeId);
                    //3为已发送状态，只需改状态为4
                    message = "删除成功";
                } else if (byId.getStatus() == 3) {
                    message = "删除失败";
                } else if (byId.getStatus() == 2) {
                    //prizeUserService.ByPrizeId(prizeId);
                    message = "删除失败";
                }
            });
            return new JiebaoResponse().message(message);
        } catch (Exception e) {
            message = "删除一事一奖失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }


    @PutMapping("/update")
    @ApiOperation(value = "修改未发布的一事一奖（已发布的不能修改）", notes = "修改发布的一事一奖（已发布的不能修改）", httpMethod = "PUT")
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse updatePrize(@Valid Prize prize) throws JiebaoException {
        try {
            this.prizeService.updateById(prize);
            return new JiebaoResponse().message("修改未发送的一事一奖成功");
        } catch (Exception e) {
            message = "修改一事一奖失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }

    @PutMapping("/updateCityStatus")
    @ApiOperation(value = "修改cityStatus", notes = "修改cityStatus", httpMethod = "PUT")
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse updateCityStatus( String [] prizeIds,String value) throws JiebaoException {
        try {
            System.out.println(prizeIds+"---------------");
            Arrays.stream(prizeIds).forEach(prizeId -> {
                this.prizeMapper.updateCityStatus(prizeId,value);
            });
            return new JiebaoResponse().message("修改未发送的一事一奖成功");
        } catch (Exception e) {
            message = "修改一事一奖失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }


    @PutMapping("/updateGongStatus")
    @ApiOperation(value = "修改gongStatus", notes = "修改gongStatus", httpMethod = "PUT")
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse updateGongStatus( String [] prizeIds,String value) throws JiebaoException {
        try {
            System.out.println(prizeIds+"---------------");
            Arrays.stream(prizeIds).forEach(prizeId -> {
                this.prizeMapper.updateGongStatus(prizeId,value);
            });
            return new JiebaoResponse().message("修改未发送的一事一奖成功");
        } catch (Exception e) {
            message = "修改一事一奖失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }


    @GetMapping
    @ApiOperation(value = "分页查询发件箱（未发送的和已发送的）", notes = "分页查询发件箱（未发送的和已发送的）", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse getPrizeList(QueryRequest request, Prize prize, String startTime, String endTime) {
        IPage<Prize> prizeList = prizeService.getPrizeList(request, prize, startTime, endTime);
        List<Prize> records = prizeList.getRecords();
        for (Prize r :records
        ) {
            Dict byId = dictService.getById(r.getTypes());
            r.setTypeObject(byId);
            Dict byIdTwo = dictService.getById(r.getIdentity());
            r.setIdentityObject(byIdTwo);
        }
        return new JiebaoResponse().data(this.getDataTable(prizeList));
    }


    @GetMapping("/inbox")
    @ApiOperation(value = "分页查询收件箱", notes = "分页查询收件箱", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse getPrizeInboxList(QueryRequest request, Prize prize, String startTime, String endTime,String [] statuses) {
        IPage<Prize> prizeList = prizeService.getPrizeInboxList(request, prize, startTime, endTime, statuses);
        List<Prize> records = prizeList.getRecords();
        for (Prize r :records
                ) {
            Dict byId = dictService.getById(r.getTypes());
            r.setTypeObject(byId);
            Dict byIdTwo = dictService.getById(r.getIdentity());
            r.setIdentityObject(byIdTwo);
        }
        return new JiebaoResponse().data(this.getDataTable(prizeList));
    }

    @PostMapping("/info")
    @ApiOperation(value = "详情", notes = "详情", response = JiebaoResponse.class, httpMethod = "POST")
    public Prize getPrizeInfo(String prizeId) {
        Prize byId = prizeService.getById(prizeId);
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("prize_id", prizeId);
        List<PrizeOpinion> prizeOpinions = prizeOpinionMapper.selectByMap(hashMap);
        byId.setOpinions(prizeOpinions);
        return byId;
    }

    @PostMapping("/report")
    @ApiOperation(value = "审批并上报", notes = "审批并上报", response = JiebaoResponse.class, httpMethod = "POST")
    public JiebaoResponse PrizeReport(String auditOpinion, String moneys, String[] prizeIds, String sendDeptId, String sendCityId) throws JiebaoException {
        try {
            String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
            User byName = userService.findByName(username);
            Dept byId = deptService.getById(byName.getDeptId());
           /* List<Dept> depts = new ArrayList<>();
            if (byId.getRank() == 1) {
                Map<String, Object> columnMap = new HashMap<>();
                columnMap.put("dept_id", sendDeptId);
                depts = deptMapper.selectByMap(columnMap);
            } else if (byId.getRank() == 0) {
                depts = null;
            } else {
                Map<String, Object> columnMap = new HashMap<>();
                columnMap.put("dept_id", byId.getParentId());
                depts = deptMapper.selectByMap(columnMap);
            }
            List<Dept> finalDepts = depts;*/
            Arrays.stream(prizeIds).forEach(prizeId -> {
                //查询该级组织机构是否有审批内容
                Integer result = prizeOpinionMapper.selectOpinion(byId.getRank(), prizeId);
                if (result == 0) {

                    Prize prize = prizeService.getById(prizeId);
                    User user = userMapper.getByUserId(prize.getUserId());
                    //该篇一事一奖创建人组织机构
                    Dept dept = deptService.getById(user.getDeptId());
                    System.out.println(dept + "该篇一事一奖创建人组织机构");
                    Dept byIdFather = deptService.getById(dept.getParentId());
                    //如果当前审核上报的是市级，将上报到公安处，而由公安处或派出所提出来的不上报给公安处
                    String finalDept = null;
                    if (byId.getRank() == 1 && dept.getRank() != 4 && byIdFather.getRank() != 4) {
                        finalDept = sendDeptId;
                        prizeUserService.saveByDept(prizeId, byId.getParentId());
                    } else if (byId.getRank() == 4 && dept.getRank() == 4) {
                        finalDept = null;

                    } else if (byId.getRank() == 4 && byIdFather.getRank() == 4) {
                        finalDept = sendCityId;
                    } else {
                        finalDept = byId.getParentId();
                    }
                    if (byId.getRank() != 0 && finalDept !=null){
                        //上报
                        prizeUserService.saveByDept(prizeId, finalDept);
                    }

                    // 0是省 Status=8
                    if (byId.getRank() == 0) {
                        prizeMapper.updateStatusForPro(prizeId);
                    }
                    //4为公安处 Status=7
                    if (byId.getRank() == 4) {
                        prizeMapper.updateStatusForIron(prizeId);
                    }
                    //1为市 Status=6
                    if (byId.getRank() == 1) {
                        prizeMapper.updateStatusForCity(prizeId);
                    }
                    //2为区 Status=5
                    if (byId.getRank() == 2){
                        prizeMapper.updateStatusForQu(prizeId);
                    }


                    PrizeOpinion prizeOpinion = new PrizeOpinion();
                    prizeOpinion.setRank(byId.getRank());
                    //id必须传来
                    prizeOpinion.setPrizeId(prizeId);
                    prizeOpinion.setAuditOpinion(auditOpinion);
                    // prizeOpinion.setMoney(prizeOpinion.getMoney());
                    prizeOpinionService.saveOrUpdate(prizeOpinion);
                }
            });
            if(byId.getRank() != 4 ){
                //解析json数组
                JSONArray jsonArray = JSON.parseArray(moneys);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    String prizeId = jsonObject.getString("prizeId");
                    String opinionMoney = jsonObject.getString("opinionMoney");
                    System.out.println(jsonObject.getString("prizeId") + ":" + jsonObject.getInteger("opinionMoney"));
                    prizeOpinionService.saveByPrizeId(prizeId, opinionMoney, byId.getRank());
                }
            }
            return new JiebaoResponse().message("审批或上报成功").put("status", "200");
        } catch (Exception e) {
            message = "审批或上报失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }

    @GetMapping("/reject/list")
    @ApiOperation(value = "驳回", notes = "驳回", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse prizeReject(String[] prizeIds, String rejectAuditOpinion) throws JiebaoException {
        try {
            String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
            User byName = userService.findByName(username);
            Arrays.stream(prizeIds).forEach(prizeId -> {
                Prize byId = prizeService.getById(prizeId);
                if (byId.getStatus() != 8) {
                    this.prizeMapper.reject(prizeId);
                    PrizeRejectOpinion prj = new PrizeRejectOpinion();
                    prj.setAuditOpinion(rejectAuditOpinion);
                    prj.setId(prizeId);
                    prj.setUserId(byName.getUserId());
                    prizeRejectOpinionService.saveOrUpdate(prj);
                }
            });
            return new JiebaoResponse().message("驳回成功").put("status", "200");
        } catch (Exception e) {
            message = "驳回失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }

    }


    @GetMapping("/findRejectOpinion")
    @ApiOperation(value = "驳回意见查询", notes = "驳回意见查询", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse findRejectOpinion(String prizeId) {
        PrizeRejectOpinion prizeRejectOpinion = prizeRejectOpinionMapper.selectOne(new LambdaQueryWrapper<PrizeRejectOpinion>().eq(PrizeRejectOpinion::getId, prizeId));
        if (prizeRejectOpinion != null) {
            User byId = userService.getById(prizeRejectOpinion.getUserId());
            Dept dept = deptService.getById(byId.getDeptId());
            prizeRejectOpinion.setRank(dept.getRank());
            prizeRejectOpinion.setUserName(byId.getUsername());
            return new JiebaoResponse().data(prizeRejectOpinion).put("status", "200");
        } else {
            return new JiebaoResponse().data("").put("status", "200");
        }
    }


    @GetMapping("/findOpinion")
    @ApiOperation(value = "意见查询", notes = "意见查询", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse findOpinion(String prizeId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("prize_id", prizeId);
        List<PrizeOpinion> prizeOpinions = prizeOpinionMapper.selectByMap(map);
        return new JiebaoResponse().data(prizeOpinions).put("status", "200");
    }

   /* @GetMapping("/briefing")
    @ApiOperation(value = "简报", notes = "简报", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse briefing(String startTime, String endTime) throws JiebaoException {
        try {
            List<Prize> prizes = prizeMapper.selectByBriefing(startTime, endTime);
            for (Prize p : prizes
            ) {
                PrizeOpinion prizeOpinion = prizeOpinionMapper.selectOne(new LambdaQueryWrapper<PrizeOpinion>().eq(PrizeOpinion::getPrizeId, p.getId()).eq(PrizeOpinion::getRank, 0));
                if (prizeOpinion != null) {
                    String money = prizeOpinion.getMoney();
                    p.setBriefingMoney(money);
                }
            }
            return new JiebaoResponse().data(prizes).put("status", "200");
        } catch (Exception e) {
            message = "生成简报失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }*/

    @GetMapping("/briefing")
    @ApiOperation(value = "简报info", notes = "简报info", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse briefing(QueryRequest request, Prize prize, String startTime, String endTime) {
        IPage<Prize> prizeList = prizeService.getBriefing(request, prize, startTime, endTime);
        List<Prize> records = prizeList.getRecords();
        for (Prize p : records
        ) {
            PrizeOpinion prizeOpinion = prizeOpinionMapper.selectOne(new LambdaQueryWrapper<PrizeOpinion>().eq(PrizeOpinion::getPrizeId, p.getId()).eq(PrizeOpinion::getRank, 0));
            if (prizeOpinion != null) {
                String money = prizeOpinion.getMoney();
                p.setBriefingMoney(money);
            }
        }
        return new JiebaoResponse().data(this.getDataTable(prizeList));
    }


    @PostMapping("/briefingWord")
    @ApiOperation(value = "生成简报（不带金额和带金额）", notes = "生成简报（不带金额和带金额）", response = JiebaoResponse.class, httpMethod = "POST")
    public void briefingWord( QueryRequest request, Prize prize, String startTime, String endTime, String period, String year, String month, String day) throws FileNotFoundException {

        IPage<Prize> prizeList = prizeService.getBriefing(request, prize, startTime, endTime);
        List<Prize> records = prizeList.getRecords();

        Map<String, String> map = new HashMap<>();
        System.out.println(period + year + month + day + "-------------------------------");
        map.put("period", period);
        map.put("year", year);
        map.put("month", month);
        map.put("day", day);


        List<String[]> testList = new ArrayList<>();

            for (Prize p :
                    records) {
                String[] split = p.getPlace().split(",");
                System.out.println("++++++++++++++++" + split + "+++++++++++++++++++++++");
                testList.add(new String[]{p.getNumber(), split[0], p.getContent()});
            }
            //模板文件地址
            //String inputUrl = GetResource.class.getClassLoader().getResource("tempDoc.docx").getPath();
            String inputUrl = "/usr/local/rrpo/word/tempDoc.docx";


            System.out.println("-------------" + inputUrl + "---------------------");
            Date date = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            String oldName = "湖南铁路护路联防简报" + year + "年第" + period + "期" + "(" + df.format(date) + ")" + ".docx";
            //新生产的模板文件
            String newName = UUID.randomUUID().toString();


            //String outputUrl = "D:/upload/words/" + newName;
            String outputUrl = "/usr/local/rrpo/upload/" + newName;
            String outPath = outputUrl + ".docx";
            WorderToNewWordUtils.changWord(inputUrl, outPath, map, testList);
            String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
            User byName = userService.findByName(username);
            this.saveFile("2", "10", byName.getUserId(), oldName, newName, true, "0");
            //附带金额

            for (Prize p :
                    records) {
                PrizeOpinion prizeOpinion = prizeOpinionMapper.selectOne(new LambdaQueryWrapper<PrizeOpinion>().eq(PrizeOpinion::getPrizeId, p.getId()).eq(PrizeOpinion::getRank, 0));
                if (prizeOpinion != null) {
                    String[] split = p.getPlace().split(",");
                    testList.add(new String[]{p.getNumber(), split[0], p.getContent(), (prizeOpinion.getMoney() + "元")});
                } else {
                    String[] split = p.getPlace().split(",");
                    testList.add(new String[]{p.getNumber(), split[0], p.getContent(), " "});
                }

            }
            //模板文件地址
            //String inputUrl = GetResource.class.getClassLoader().getResource("tempDoc_amount.docx").getPath();
            String inputUrlTwo = "/usr/local/rrpo/word/tempDoc_amount.docx";


            System.out.println("-------------" + inputUrlTwo + "---------------------");
            Date dateTwo = new Date();
            SimpleDateFormat dfTwo = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            String oldNameTwo = "湖南铁路护路联防简报" + year + "年第" + period + "期-附带金额" + "(" + dfTwo.format(dateTwo) + ")" + ".docx";
            //新生产的模板文件
            String newNameTwo = UUID.randomUUID().toString();


            //String outputUrl = "D:/upload/words/" + newName;
            String outputUrlTwo = "/usr/local/rrpo/upload/" + newNameTwo;
            String outPathTwo = outputUrlTwo + ".docx";
            WorderToNewWordUtils.changWord(inputUrl, outPathTwo, map, testList);
            String usernameTwo = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
            User byNameTwo = userService.findByName(usernameTwo);
            this.saveFile("2", "10", byNameTwo.getUserId(), oldNameTwo, newName, true, "1");

            System.out.println("-------------------------");
            //已完成的全部修改为已导出
           prizeMapper.updateAll();
    }


    private JiebaoResponse saveFile(String fileType, String refType, String userId, String oldName, String newName, boolean status, String ifHaveMoney) {
        String path = "";   //上传地址
        String accessPath = ""; //文件访问虚拟地址


        //path = "D:/upload/words/";
        path = "/usr/local/rrpo/upload/";
        accessPath = "/jbx/cdn/file/";
        String currentTimeFolder = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "/";
        java.io.File currentFile = new java.io.File(path + newName + ".docx");
        accessPath = HOST + accessPath + newName + ".docx";   //上传后完整文件虚拟访问地址
        //创建文件信息对象保存至数据库
        com.jiebao.platfrom.system.domain.File file = new com.jiebao.platfrom.system.domain.File();
        file.setFileType(fileType);
        file.setOldName(oldName);
        file.setNewName(newName + ".docx");
        file.setFileUrl(path);
        file.setAccessUrl(accessPath);
        file.setRefType(refType);
        file.setFileSuffix(".docx");
        file.setStatus(status);
        file.setTime(new Date());
        file.setUserId(userId);
        file.setIfHaveMoney(ifHaveMoney);
        if (file.insert()) {
            return new JiebaoResponse().put("status", true).put("file", file);
        } else {
            currentFile.delete();
            return new JiebaoResponse().put("status", false).message("上传服务异常，上传失败，请重试！");
        }
    }


    /**
     * 文件记录删除
     *
     * @param fileId
     * @return
     */
    @ApiOperation("文件记录删除")
    @PostMapping("/deleteFile")
    public JiebaoResponse deleteFile(String fileId) {
        int i = fileMapper.deleteById(fileId);
        return new JiebaoResponse().data(i);
    }


    @ApiOperation("统计十四市州发布情况")
    @GetMapping("/countRelease")
    public JiebaoResponse countRelease(String startTime, String endTime, Integer status) {
        Dept dept = deptService.getDept();
        Dept byId = deptService.getById(dept.getParentId());
        if (dept.getRank()==0){
            List changsha = new ArrayList();
            List changde = new ArrayList();
            List hengyang = new ArrayList();
            List shaoyang = new ArrayList();
            List zhuzhou = new ArrayList();
            List xiangtan = new ArrayList();
            List yueyang = new ArrayList();
            List zhangjiajie = new ArrayList();
            List yiyang = new ArrayList();
            List chenzhou = new ArrayList();
            List yongzhou = new ArrayList();
            List huaihua = new ArrayList();
            List loudi = new ArrayList();
            List xiangxi = new ArrayList();

            List changshaOne = new ArrayList();
            List changdeOne = new ArrayList();
            List hengyangOne = new ArrayList();
            List shaoyangOne = new ArrayList();
            List zhuzhouOne = new ArrayList();
            List xiangtanOne = new ArrayList();
            List yueyangOne = new ArrayList();
            List zhangjiajieOne = new ArrayList();
            List yiyangOne = new ArrayList();
            List chenzhouOne = new ArrayList();
            List yongzhouOne = new ArrayList();
            List huaihuaOne = new ArrayList();
            List loudiOne = new ArrayList();
            List xiangxiOne = new ArrayList();

            List<Map<String, Object>> list = prizeService.countRelease(startTime, endTime, status);
            List<Map<String, Object>> listOne = prizeService.countRelease(startTime, endTime, 8);

            for (Map<String, Object> item : list) {
                changsha.add(item.get("changsha"));
                changde.add(item.get("changde"));
                hengyang.add(item.get("hengyang"));
                shaoyang.add(item.get("shaoyang"));
                zhuzhou.add(item.get("zhuzhou"));
                xiangtan.add(item.get("xiangtan"));
                yueyang.add(item.get("yueyang"));
                zhangjiajie.add(item.get("zhangjiajie"));
                yiyang.add(item.get("yiyang"));
                chenzhou.add(item.get("chenzhou"));
                yongzhou.add(item.get("yongzhou"));
                huaihua.add(item.get("huaihua"));
                loudi.add(item.get("loudi"));
                xiangxi.add(item.get("xiangxi"));
            }

            for (Map<String, Object> item : listOne) {
                changshaOne.add(item.get("changsha"));
                changdeOne.add(item.get("changde"));
                hengyangOne.add(item.get("hengyang"));
                shaoyangOne.add(item.get("shaoyang"));
                zhuzhouOne.add(item.get("zhuzhou"));
                xiangtanOne.add(item.get("xiangtan"));
                yueyangOne.add(item.get("yueyang"));
                zhangjiajieOne.add(item.get("zhangjiajie"));
                yiyangOne.add(item.get("yiyang"));
                chenzhouOne.add(item.get("chenzhou"));
                yongzhouOne.add(item.get("yongzhou"));
                huaihuaOne.add(item.get("huaihua"));
                loudiOne.add(item.get("loudi"));
                xiangxiOne.add(item.get("xiangxi"));
            }

            String changshaData = JSON.toJSONString(changsha);
            String changdeData = JSON.toJSONString(changde);
            String hengyangData = JSON.toJSONString(hengyang);
            String shaoyangData = JSON.toJSONString(shaoyang);
            String zhuzhouData = JSON.toJSONString(zhuzhou);
            String xiangtanData = JSON.toJSONString(xiangtan);
            String yueyangData = JSON.toJSONString(yueyang);
            String zhangjiajieData = JSON.toJSONString(zhangjiajie);
            String yiyangData = JSON.toJSONString(yiyang);
            String chenzhouData = JSON.toJSONString(chenzhou);
            String yongzhouData = JSON.toJSONString(yongzhou);
            String huaihuaData = JSON.toJSONString(huaihua);
            String loudiData = JSON.toJSONString(loudi);
            String xiangxiData = JSON.toJSONString(xiangxi);

            String changshaDataOne = JSON.toJSONString(changshaOne);
            String changdeDataOne = JSON.toJSONString(changdeOne);
            String hengyangDataOne = JSON.toJSONString(hengyangOne);
            String shaoyangDataOne = JSON.toJSONString(shaoyangOne);
            String zhuzhouDataOne = JSON.toJSONString(zhuzhouOne);
            String xiangtanDataOne = JSON.toJSONString(xiangtanOne);
            String yueyangDataOne = JSON.toJSONString(yueyangOne);
            String zhangjiajieDataOne = JSON.toJSONString(zhangjiajieOne);
            String yiyangDataOne = JSON.toJSONString(yiyangOne);
            String chenzhouDataOne = JSON.toJSONString(chenzhouOne);
            String yongzhouDataOne = JSON.toJSONString(yongzhouOne);
            String huaihuaDataOne = JSON.toJSONString(huaihuaOne);
            String loudiDataOne = JSON.toJSONString(loudiOne);
            String xiangxiDataOne = JSON.toJSONString(xiangxiOne);


            JSONObject jsonObject = new JSONObject();
            JSONObject changshaObject = new JSONObject();
            changshaObject.put("name", "长沙市");
            changshaObject.put("not", changshaData);
            changshaObject.put("pass", changshaDataOne);
            jsonObject.put("changsha", changshaObject);

            JSONObject changdeObject = new JSONObject();
            changdeObject.put("name", "常德市");
            changdeObject.put("not", changdeData);
            changdeObject.put("pass", changdeDataOne);
            jsonObject.put("changde", changdeObject);

            JSONObject hengyangObject = new JSONObject();
            hengyangObject.put("name", "衡阳市");
            hengyangObject.put("not", hengyangData);
            hengyangObject.put("pass", hengyangDataOne);
            jsonObject.put("hengyang", hengyangObject);

            JSONObject shaoyangObject = new JSONObject();
            shaoyangObject.put("name", "邵阳市");
            shaoyangObject.put("not", shaoyangData);
            shaoyangObject.put("pass", shaoyangDataOne);
            jsonObject.put("shaoyang", shaoyangObject);


            JSONObject zhuzhouObject = new JSONObject();
            zhuzhouObject.put("name", "株洲市");
            zhuzhouObject.put("not", zhuzhouData);
            zhuzhouObject.put("pass", zhuzhouDataOne);
            jsonObject.put("zhuzhou", zhuzhouObject);

            JSONObject xiangtanObject = new JSONObject();
            xiangtanObject.put("name", "湘潭市");
            xiangtanObject.put("not", xiangtanData);
            xiangtanObject.put("pass", xiangtanDataOne);
            jsonObject.put("xiangtan", xiangtanObject);

            JSONObject yueyangObject = new JSONObject();
            yueyangObject.put("name", "岳阳市");
            yueyangObject.put("not", yueyangData);
            yueyangObject.put("pass", yueyangDataOne);
            jsonObject.put("yueyang", yueyangObject);

            JSONObject zhangjiajieObject = new JSONObject();
            zhangjiajieObject.put("name", "张家界市");
            zhangjiajieObject.put("not", zhangjiajieData);
            zhangjiajieObject.put("pass", zhangjiajieDataOne);
            jsonObject.put("zhangjiajie", zhangjiajieObject);

            JSONObject yiyangObject = new JSONObject();
            yiyangObject.put("name", "益阳市");
            yiyangObject.put("not", yiyangData);
            yiyangObject.put("pass", yiyangDataOne);
            jsonObject.put("yiyang", yiyangObject);

            JSONObject chenzhouObject = new JSONObject();
            chenzhouObject.put("name", "郴州市");
            chenzhouObject.put("not", chenzhouData);
            chenzhouObject.put("pass", chenzhouDataOne);
            jsonObject.put("chenzhou", chenzhouObject);

            JSONObject yongzhouObject = new JSONObject();
            yongzhouObject.put("name", "永州市");
            yongzhouObject.put("not", yongzhouData);
            yongzhouObject.put("pass", yongzhouDataOne);
            jsonObject.put("yongzhou", yongzhouObject);

            JSONObject huaihuaObject = new JSONObject();
            huaihuaObject.put("name", "怀化市");
            huaihuaObject.put("not", huaihuaData);
            huaihuaObject.put("pass", huaihuaDataOne);
            jsonObject.put("huaihua", huaihuaObject);

            JSONObject loudiObject = new JSONObject();
            loudiObject.put("name", "娄底市");
            loudiObject.put("not", loudiData);
            loudiObject.put("pass", loudiDataOne);
            jsonObject.put("loudi", loudiObject);

            JSONObject xiangxiObject = new JSONObject();
            xiangxiObject.put("name", "湘西州");
            xiangxiObject.put("not", xiangxiData);
            xiangxiObject.put("pass", xiangxiDataOne);
            jsonObject.put("xiangxi", xiangxiObject);

            String result = JSON.toJSONString(jsonObject);

            return new JiebaoResponse().data(result);
        }
        if (dept.getRank()==1){
            List<Map<String, Object>> list = prizeMapper.countPrizeForCity(startTime, endTime,dept.getDeptId());
            List<Map<String, Object>> maps = prizeMapper.countPrizeForCityDone(startTime, endTime, dept.getDeptId());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("all",list);
            jsonObject.put("done",maps);
            return new JiebaoResponse().data(jsonObject).okMessage("成功");
        }
        else if(dept.getRank()==2 && byId.getRank() != 4){
            List<Map<String, Object>> list = prizeMapper.countPrizeForArea(startTime, endTime,dept.getDeptId());
            List<Map<String, Object>> maps = prizeMapper.countPrizeForAreaDone(startTime, endTime, dept.getDeptId());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("all",list);
            jsonObject.put("done",maps);
            return new JiebaoResponse().data(jsonObject).okMessage("成功");
        }
        else {
            return new JiebaoResponse().failMessage("无权查看");
        }

    }

    @ApiOperation("统计十四市州金额")
    @GetMapping("/countMoney")
    public JiebaoResponse countMoney(String startTime, String endTime) {

        Dept dept = deptService.getDept();
        Dept byId = deptService.getById(dept.getParentId());
        if (dept.getRank()==0){
            List<Map<String, Object>> list = prizeService.countMoney(startTime, endTime);
            return new JiebaoResponse().data(list).okMessage("成功");
        }
        if (dept.getRank()==1){
            List<Map<String, Object>> list = prizeMapper.countMoneyForCity(startTime, endTime,dept.getDeptId());
            return new JiebaoResponse().data(list).okMessage("成功");
        }
        else if(dept.getRank()==2 && byId.getRank() != 4){
            List<Map<String, Object>> list = prizeMapper.countMoneyForArea(startTime, endTime,dept.getDeptId());
            return new JiebaoResponse().data(list).okMessage("成功");
        }
        else {
            return new JiebaoResponse().failMessage("无权查看");
        }


    }


    @ApiOperation("统计发布情况")
    @GetMapping("/countTypes")
    public JiebaoResponse countTypes(String startTime, String endTime) {
        Map<String, Object> map = prizeService.countTypes(startTime, endTime);
        JSONObject jsonObject = new JSONObject();
        String bohuiData = JSON.toJSONString(map.get("bohui"));
        String tongguoData = JSON.toJSONString(map.get("tongguo"));
        String zongshuData = JSON.toJSONString(map.get("zongshu"));

        JSONObject bohuiObject = new JSONObject();
        bohuiObject.put("name", "驳回");
        bohuiObject.put("Date", bohuiData);
        jsonObject.put("bohui", bohuiObject);

        JSONObject tongguoObject = new JSONObject();
        tongguoObject.put("name", "通过");
        tongguoObject.put("Date", tongguoData);
        jsonObject.put("tongguo", tongguoObject);


        JSONObject zongshuObject = new JSONObject();
        zongshuObject.put("name", "总数");
        zongshuObject.put("Date", zongshuData);
        jsonObject.put("zongshu", zongshuObject);
        String result = JSON.toJSONString(jsonObject);
        return new JiebaoResponse().data(result);
    }


    public String NullOrZero(Object obj) {
        return obj == null ? "0" : obj.toString();

    }











    /**
     * 同步到门户网
     *
     * @return
     */
    @ApiOperation("同步到门户网")
    @PostMapping("/menHu")
    public JiebaoResponse menHu(String prizeId, Date time, String targetsId,String title) {

        Prize byId = prizeService.getById(prizeId);
        if (byId.getSynchronizeWeb() == 0) {
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
                map.put("ref_id", prizeId);
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
                System.out.println(targetsId);
                Map<String, String> params = new HashMap<>();
                params.put("id", targetsId);
                Dept dept = deptService.getDept();
                String username = JWTUtil.getUsername(SecurityUtils.getSubject().getPrincipal().toString());
                params.put("source", (dept.getDeptName() + "-" + username));
                params.put("title", title);
                //如果富编辑器里有图片，转换成base64替换img标签所有内容
               /* Map<String, Object> mapF = new HashMap<>();
                map.put("ref_type",8);
                map.put("file_type",1);
                List<com.jiebao.platfrom.system.domain.File> filesF = fileMapper.selectByMap(mapF);
                if (filesF.size()>0){
                    Element doc = Jsoup.parseBodyFragment(byId.getContent()).body();
                    Elements jpg = doc.select("img[src]");

                    for (com.jiebao.platfrom.system.domain.File f: filesF
                    ) {
                        String url = f.getFileUrl() + f.getNewName();
                        //转换为base64
                        BASE64Encoder encoder = new BASE64Encoder();
                        InputStream   in = new FileInputStream(url);
                        byte[] data = new byte[in.available()];
                        String encode = encoder.encode(data);
                        ListIterator<Element> item = jpg.listIterator();
                     //   item..toString().replace(next.toString(),encode);

                    }

                }else {
                    params.put("html", byId.getContent());
                }*/
                params.put("html", byId.getContent());
                if (time != null) {
                    String relTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(time);
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
        return new JiebaoResponse().okMessage("同步成功");
    }

    @ApiOperation("统计事件类型")
    @GetMapping("/countType")
    public JiebaoResponse countType(String startTime, String endTime) {

        Dept dept = deptService.getDept();
        Dept byId = deptService.getById(dept.getParentId());
        if (dept.getRank()==0){
            List<Map<String, Object>> map = prizeMapper.countType(startTime, endTime);
            return new JiebaoResponse().data(map).okMessage("查询成功");
        }
        if (dept.getRank()==1){
            List<Map<String, Object>> list = prizeMapper.countTypeForCity(startTime, endTime,dept.getDeptId());
            return new JiebaoResponse().data(list).okMessage("成功");
        }
        else if(dept.getRank()==2 && byId.getRank() != 4){
            List<Map<String, Object>> list = prizeMapper.countTypeForArea(startTime, endTime,dept.getDeptId());
            return new JiebaoResponse().data(list).okMessage("成功");
        }
        else {
            return new JiebaoResponse().failMessage("无权查看");
        }
    }





    public static void main(String[] args) {
        String newsBody = "<img alt=\"\" src=\"/xiaoxiao1/uploads/artImage/image/20130711/20130711182603_176.png\" > 452112<img alt=\"\" src=\"/xiaoxiao2/uploads/artImage/image/20130711/20130711182603_176.png\" >";
        String HTTPHOST = "http://58.192.23.75:8080";
        Document doc = Jsoup.parse(newsBody);
        Elements pngs = doc.select("img[src]");
        System.out.println(pngs);
        ListIterator<Element> elementListIterator = pngs.listIterator();
        while (elementListIterator.hasNext()) {
            Element next = elementListIterator.next();
            System.out.println(next + "--------");
        }


        String replace = pngs.toString().replace(pngs.toString(), HTTPHOST);
        System.out.println(replace);

      /*  for (Element element : pngs) {
            String imgUrl = element.attr("src");
            if (imgUrl.trim().startsWith("/")) {
                imgUrl =HTTPHOST + imgUrl;
                element.attr("src", imgUrl);
            }
        }*/
        newsBody = doc.toString();
        // System.out.println(newsBody);
    }
}
