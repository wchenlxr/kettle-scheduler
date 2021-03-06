package com.zhaxd.core.mapper;

import org.beetl.sql.core.annotatoin.SqlStatement;
import org.beetl.sql.core.mapper.BaseMapper;

import com.zhaxd.core.model.*;


public interface KTransRecordDao extends BaseMapper<KTransRecord> {
    @SqlStatement(params = "recordTrans")
    KTransRecord selectLastTrans(Integer recordTrans);
}