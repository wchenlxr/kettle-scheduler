package com.zhaxd.web.service;

import com.zhaxd.common.toolkit.Constant;
import com.zhaxd.core.dto.BootTablePage;
import com.zhaxd.core.mapper.KTransRecordDao;
import com.zhaxd.core.model.KTransRecord;
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
public class TransRecordService {

    @Autowired
    private KTransRecordDao kTransRecordDao;

    /**
     * @param start   其实行数
     * @param size    结束行数
     * @param uId     用户ID
     * @param transId 转换ID
     * @return BootTablePage
     * @Title getList
     * @Description 获取列表
     */
    public BootTablePage getList(Integer start, Integer size, Integer uId, Integer transId) {
        KTransRecord template = new KTransRecord();
        template.setAddUser(uId);
        if (transId != null) {
            template.setRecordTrans(transId);
        }
        List<KTransRecord> kTransRecordList = kTransRecordDao.template(template, start, size);
        long totalCount = kTransRecordDao.templateCount(template);
        BootTablePage bootTablePage = new BootTablePage();
        bootTablePage.setRows(kTransRecordList);
        bootTablePage.setTotal(totalCount);
        return bootTablePage;
    }

    /**
     * @param recordId 转换记录ID
     * @return String
     * @throws IOException
     * @Title getLogContent
     * @Description 转换日志内容
     */
    public String getLogContent(Integer recordId) throws IOException {
        KTransRecord kTransRecord = kTransRecordDao.unique(recordId);
        String logFilePath = kTransRecord.getLogFilePath();
        if (null == logFilePath) return "";
        return FileUtils.readFileToString(new File(logFilePath), Constant.DEFAULT_ENCODING);
    }


    /**
     * @return Map<String   ,   Object>
     * @Title getTransLine
     * @Description 获取7天内转换的折线图
     */
    public Map<String, Object> getTransLine(List<String> dateList) {
        SQLManager sqlManager = kTransRecordDao.getSQLManager();
        List<Integer> mapList = sqlManager.execute(new SQLReady("select IFNULL(b.count,0) count from (SELECT DATE_FORMAT( date_add(concat(\'" + dateList.get(0) + "\'), interval(help_topic_id) DAY),'%Y-%m-%d') DT FROM mysql.help_topic\n" +
                " WHERE help_topic_id  <=  timestampdiff(DAY,concat(\'" + dateList.get(0) + "\'),concat(\'" + dateList.get(dateList.size() - 1) + "\')))a left join (SELECT DATE_FORMAT(start_time,'%Y-%m-%d') date1 ,count(distinct record_trans) count FROM `k_trans_record` where start_time between \'" + dateList.get(0) + "\' and \'" + dateList.get(dateList.size() - 1) + " 23:59:59\'\n" +
                " group by DATE_FORMAT(start_time,'%Y-%m-%d'))b on a.dt = b.date1"), Integer.class);
        HashMap<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("name", "转换");
        resultMap.put("data", mapList);
        return resultMap;
    }
}