package com.zhaxd.web.controller;

import com.zhaxd.common.toolkit.Constant;
import com.zhaxd.core.dto.BootTablePage;
import com.zhaxd.core.dto.ResultDto;
import com.zhaxd.core.model.KUser;
import com.zhaxd.web.service.JobMonitorService;
import com.zhaxd.web.service.JobRecordService;
import com.zhaxd.web.service.TransMonitorService;
import com.zhaxd.web.service.TransRecordService;
import com.zhaxd.web.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/main/")
public class MainController {

    @Autowired
    private TransMonitorService transMonitorService;

    @Autowired
    private JobMonitorService jobMonitorService;

    @Autowired
    private JobRecordService jobRecordService;

    @Autowired
    private TransRecordService transRecordService;

    /**
     * @param request
     * @return String
     * @Title allRuning
     * @Description
     */
    @RequestMapping("allRuning.shtml")
    public String allRuning(HttpServletRequest request) {
        KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
        Integer allMonitorTrans = transMonitorService.getAllMonitorTrans(kUser.getuId());
        Integer allMonitorJob = jobMonitorService.getAllMonitorJob(kUser.getuId());
        Integer allRuning = allMonitorTrans + allMonitorJob;
        return JsonUtils.objectToJson(allRuning);
    }

    /**
     * @param request
     * @return String
     * @Title getTransList
     * @Description 获取转换的Top5
     */
    @RequestMapping("getTransList.shtml")
    public String getTransList(HttpServletRequest request) {
        KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
        BootTablePage list = transMonitorService.getList(kUser.getuId());
        return JsonUtils.objectToJson(list);
    }

    /**
     * @param request
     * @return String
     * @Title getJobList
     * @Description 获取作业的Top5
     */
    @RequestMapping("getJobList.shtml")
    public String getJobList(HttpServletRequest request) {
        KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
        BootTablePage list = jobMonitorService.getList(kUser.getuId());
        return JsonUtils.objectToJson(list);
    }

    /**
     * @return String
     * @throws ParseException
     * @Title getKettleLine
     * @Description TODO
     */
    @RequestMapping("getKettleLine.shtml")
    public String getKettleLine(HttpServletRequest request) {
        KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<String> dateList = new ArrayList<String>();
        for (int i = -6; i <= 0; i++) {
            Calendar instance = Calendar.getInstance();
            instance.add(Calendar.DATE, i);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dateFormat = simpleDateFormat.format(instance.getTime());
            dateList.add(dateFormat);
        }
        resultMap.put("legend", dateList);
        Map<String, Object> transLine = transRecordService.getTransLine(dateList);
        resultMap.put("trans", transLine);
        Map<String, Object> jobLine = jobRecordService.getJobLine(dateList);
        resultMap.put("job", jobLine);
        return ResultDto.success(resultMap);
    }
}
