package com.zhaxd.web.service;

import com.zhaxd.common.toolkit.Constant;
import com.zhaxd.core.dto.BootTablePage;
import com.zhaxd.core.mapper.KJobRecordDao;
import com.zhaxd.core.model.KJobRecord;
import org.apache.commons.io.FileUtils;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JobRecordService {

    @Autowired
    private KJobRecordDao kJobRecordDao;

    /**
     * @param start 起始行数
     * @param size  每页行数
     * @param uId   用户ID
     * @param jobId 作业ID
     * @return BootTablePage
     * @Title getList
     * @Description 获取带分页的列表
     */
    public BootTablePage getList(Integer start, Integer size, Integer uId, Integer jobId) {
        KJobRecord template = new KJobRecord();
        template.setAddUser(uId);
        if (jobId != null) {
            template.setRecordJob(jobId);
        }
        List<KJobRecord> kJobRecordList = kJobRecordDao.template(template, start, size);
        long totalCount = kJobRecordDao.templateCount(template);
        BootTablePage bootTablePage = new BootTablePage();
        bootTablePage.setRows(kJobRecordList);
        bootTablePage.setTotal(totalCount);
        return bootTablePage;
    }

    /**
     * @return String
     * @throws IOException
     * @Title getLogContent
     * @Description 转换日志内容
     */
    public String getLogContent(Integer jobId) throws IOException {
        KJobRecord kJobRecord = kJobRecordDao.unique(jobId);
        String logFilePath = kJobRecord.getLogFilePath();
        if (null == logFilePath) return "";
        return FileUtils.readFileToString(new File(logFilePath), Constant.DEFAULT_ENCODING);
    }

    /**
     * @return Map<String,Object>
     * @Title getTransLine
     * @Description 获取7天内作业的折线图
     */
    public Map<String, Object> getJobLine(List<String> dateList) {
        SQLManager sqlManager = kJobRecordDao.getSQLManager();
        List<Integer> mapList = sqlManager.execute(new SQLReady("select IFNULL(b.count,0) count from (SELECT DATE_FORMAT( date_add(concat(\'" + dateList.get(0) + "\'), interval(help_topic_id) DAY),'%Y-%m-%d') DT FROM mysql.help_topic\n" +
                " WHERE help_topic_id  <=  timestampdiff(DAY,concat(\'" + dateList.get(0) + "\'),concat(\'" + dateList.get(dateList.size() - 1) + "\')))a left join (SELECT DATE_FORMAT(start_time,'%Y-%m-%d') date1 ,count(distinct record_job) count FROM `k_job_record` where start_time between \'" + dateList.get(0) + "\' and \'" + dateList.get(dateList.size() - 1) + " 23:59:59\'\n" +
                " group by DATE_FORMAT(start_time,'%Y-%m-%d'))b on a.dt = b.date1"), Integer.class);
        HashMap<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("name", "作业");
        resultMap.put("data", mapList);
        return resultMap;
    }
}