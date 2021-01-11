package com.jiebao.platfrom.railway.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.railway.domain.Notice;
import com.jiebao.platfrom.railway.domain.Notice;

import java.util.List;

/**
 * @author yf
 */
public interface NoticeService extends IService<Notice> {

    /**
     * 发件箱
     * @param request
     * @param notice
     * @param startTime
     * @param endTime
     * @return
     */
    IPage<Notice> getNoticeList(QueryRequest request, Notice notice, String startTime, String endTime);

    /**
     * 收件箱
     * @param request
     * @param notice
     * @param startTime
     * @param endTime
     * @return
     */
    IPage<Notice> getNoticeInboxList(QueryRequest request, Notice notice, String startTime, String endTime);


    /**
     * 只看父级发的通知公告
     * @param request
     * @param notice
     * @param startTime
     * @param endTime
     * @return
     */
    IPage<Notice> getNoticeInboxListByParent(QueryRequest request, Notice notice, String startTime, String endTime);

    void updateByKey(Notice notice);

    List<Notice> getNoticeLists(Notice notice, QueryRequest request);

    /**
     * 撤回
     * @param noticeId
     * @return
     */
    boolean revokeNotice(String noticeId);

    /**
     * 发布
     * @param noticeId
     * @return
     */
    boolean release(String noticeId);
}
