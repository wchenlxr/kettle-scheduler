package com.zhaxd.web.service;

import com.zhaxd.common.toolkit.Constant;
import com.zhaxd.core.dto.BootTablePage;
import com.zhaxd.core.mapper.KTransMonitorDao;
import com.zhaxd.core.model.KTransMonitor;
import com.zhaxd.web.utils.CommonUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TransMonitorService {

    @Autowired
    private KTransMonitorDao kTransMonitorDao;

    /**
     * @param start 起始行数
     * @param size  每页数据条数
     * @param uId   用户ID
     * @return BootTablePage
     * @Title getList
     * @Description 获取分页列表
     */
    public BootTablePage getList(Integer start, Integer size, Integer monitorStatus, Integer categoryId, String transName, Integer uId) {
        KTransMonitor template = new KTransMonitor();
        template.setAddUser(uId);
        template.setMonitorStatus(monitorStatus);
        if (StringUtils.isNotEmpty(transName)) {
            template.setTransName(transName);
        }
        List<KTransMonitor> kTransMonitorList = kTransMonitorDao.pageQuery(template, start, size, categoryId);
        Long allCount = kTransMonitorDao.allCount(template, categoryId);
        BootTablePage bootTablePage = new BootTablePage();
        bootTablePage.setRows(kTransMonitorList);
        bootTablePage.setTotal(allCount);
        return bootTablePage;
    }

    /**
     * @param start 起始行数
     * @param size  每页数据条数
     * @param uId   用户ID
     * @return BootTablePage
     * @Title getList
     * @Description 获取不分页列表
     */
    public BootTablePage getList(Integer uId) {
        KTransMonitor template = new KTransMonitor();
        template.setAddUser(uId);
        template.setMonitorStatus(1);
        List<KTransMonitor> kTransMonitorList = kTransMonitorDao.template(template);
        Collections.sort(kTransMonitorList);
        List<KTransMonitor> newKTransMonitorList = new ArrayList<KTransMonitor>();
        if (kTransMonitorList.size() >= 5) {
            newKTransMonitorList = kTransMonitorList.subList(0, 5);
        } else {
            newKTransMonitorList = kTransMonitorList;
        }
        BootTablePage bootTablePage = new BootTablePage();
        bootTablePage.setRows(newKTransMonitorList);
        bootTablePage.setTotal(5);
        return bootTablePage;
    }

    /**
     * @param uId 用户ID
     * @return Integer
     * @Title getAllMonitorTrans
     * @Description 获取全部被监控的转换
     */
    public Integer getAllMonitorTrans(Integer uId) {
        KTransMonitor template = new KTransMonitor();
        template.setAddUser(uId);
        template.setMonitorStatus(1);
        List<KTransMonitor> kTransMonitorList = kTransMonitorDao.template(template);
        return kTransMonitorList.size();
    }

    /**
     * @param uId 用户ID
     * @return Integer
     * @Title getAllSuccess
     * @Description 获取所有转换执行成功的次数的和
     */
    public Integer getAllSuccess(Integer uId) {
        KTransMonitor template = new KTransMonitor();
        template.setAddUser(uId);
        template.setMonitorStatus(1);
        List<KTransMonitor> kTransMonitorList = kTransMonitorDao.template(template);
        Integer allSuccess = 0;
        for (KTransMonitor KTransMonitor : kTransMonitorList) {
            allSuccess += KTransMonitor.getMonitorSuccess();
        }
        return allSuccess;
    }

    /**
     * @param uId 用户ID
     * @return Integer
     * @Title getAllFail
     * @Description 获取所有转换执行失败的次数的和
     */
    public Integer getAllFail(Integer uId) {
        KTransMonitor template = new KTransMonitor();
        template.setAddUser(uId);
        template.setMonitorStatus(1);
        List<KTransMonitor> kTransMonitorList = kTransMonitorDao.template(template);
        Integer allSuccess = 0;
        for (KTransMonitor KTransMonitor : kTransMonitorList) {
            allSuccess += KTransMonitor.getMonitorFail();
        }
        return allSuccess;
    }

}