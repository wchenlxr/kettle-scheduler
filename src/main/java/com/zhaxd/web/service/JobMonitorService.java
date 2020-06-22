package com.zhaxd.web.service;

import com.zhaxd.core.dto.BootTablePage;
import com.zhaxd.core.mapper.KJobMonitorDao;
import com.zhaxd.core.model.KJobMonitor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JobMonitorService {

    @Autowired
    private KJobMonitorDao kJobMonitorDao;

    /**
     * @param start 起始行数
     * @param size  每页数据条数
     * @param uId   用户ID
     * @return BootTablePage
     * @Title getList
     * @Description 获取作业监控分页列表
     */
    public BootTablePage getList(Integer start, Integer size, Integer monitorStatus, Integer categoryId, String jobName, Integer uId) {
        KJobMonitor template = new KJobMonitor();
        template.setAddUser(uId);
        template.setMonitorStatus(monitorStatus);
        if (StringUtils.isNotEmpty(jobName)) {
            template.setJobName(jobName);
        }
        List<KJobMonitor> kJobMonitorList = kJobMonitorDao.pageQuery(template, start, size, categoryId);
        Long allCount = kJobMonitorDao.allCount(template, categoryId);
        BootTablePage bootTablePage = new BootTablePage();
        bootTablePage.setRows(kJobMonitorList);
        bootTablePage.setTotal(allCount);
        return bootTablePage;
    }

    /**
     * @param uId 用户ID
     * @return BootTablePage
     * @Title getList
     * @Description 获取作业监控不分页列表
     */
    public BootTablePage getList(Integer uId) {
        KJobMonitor template = new KJobMonitor();
        template.setAddUser(uId);
        template.setMonitorStatus(1);
        List<KJobMonitor> kJobMonitorList = kJobMonitorDao.template(template);
        Collections.sort(kJobMonitorList);
        List<KJobMonitor> newKJobMonitorList = new ArrayList<KJobMonitor>();
        if (kJobMonitorList.size() >= 5) {
            newKJobMonitorList = kJobMonitorList.subList(0, 5);
        } else {
            newKJobMonitorList = kJobMonitorList;
        }
        BootTablePage bootTablePage = new BootTablePage();
        bootTablePage.setRows(newKJobMonitorList);
        bootTablePage.setTotal(5);
        return bootTablePage;
    }

    /**
     * @param uId 用户ID
     * @return Integer
     * @Title getAllMonitorJob
     * @Description 获取所有的监控作业
     */
    public Integer getAllMonitorJob(Integer uId) {
        KJobMonitor template = new KJobMonitor();
        template.setAddUser(uId);
        template.setMonitorStatus(1);
        List<KJobMonitor> kJobMonitorList = kJobMonitorDao.template(template);
        return kJobMonitorList.size();
    }

    /**
     * @param uId 用户ID
     * @return Integer
     * @Title getAllSuccess
     * @Description 获取执行成功的数
     */
    public Integer getAllSuccess(Integer uId) {
        KJobMonitor template = new KJobMonitor();
        template.setAddUser(uId);
        template.setMonitorStatus(1);
        List<KJobMonitor> kJobMonitorList = kJobMonitorDao.template(template);
        Integer allSuccess = 0;
        for (KJobMonitor KJobMonitor : kJobMonitorList) {
            allSuccess += KJobMonitor.getMonitorSuccess();
        }
        return allSuccess;
    }

    /**
     * @param uId 用户ID
     * @return Integer
     * @Title getAllFail
     * @Description 获取执行失败的数
     */
    public Integer getAllFail(Integer uId) {
        KJobMonitor template = new KJobMonitor();
        template.setAddUser(uId);
        template.setMonitorStatus(1);
        List<KJobMonitor> kJobMonitorList = kJobMonitorDao.template(template);
        Integer allSuccess = 0;
        for (KJobMonitor KJobMonitor : kJobMonitorList) {
            allSuccess += KJobMonitor.getMonitorFail();
        }
        return allSuccess;
    }

}