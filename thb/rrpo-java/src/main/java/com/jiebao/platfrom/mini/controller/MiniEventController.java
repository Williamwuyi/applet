package com.jiebao.platfrom.mini.controller;

import com.jiebao.platfrom.common.authentication.JWTUtil;
import com.jiebao.platfrom.common.domain.JiebaoConstant;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.mini.domian.Event;
import com.jiebao.platfrom.mini.service.EventService;
import com.jiebao.platfrom.system.domain.File;
import com.jiebao.platfrom.system.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Date;

@RestController
@RequestMapping(value = "/mini/event")
@Api(tags = "小程序-事件上报")
public class MiniEventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private FileService fileService;

    @ApiOperation("事件新增")
    @PostMapping("/insert")
    public JiebaoResponse insert (@Valid Event event, String [] fileIds) {
        if (event == null) {
            return new JiebaoResponse().message("事件对象为空！").put("status", JiebaoConstant.STATUS_CODE_ERROR);
        } else {
            String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
            event.setCreatTime(new Date());
            event.setCreatUser(username);
            eventService.save(event);
            if (!StringUtils.isEmpty(fileIds)) {
                Arrays.stream(fileIds).forEach(item -> {
                    File file = fileService.getById(item);
                    file.setRefId(event.getId());
                    file.insertOrUpdate();
                });
            }
            return new JiebaoResponse().message("保存成功！").put("status", JiebaoConstant.STATUS_CODE_SUCCESS);
        }
    }

}
