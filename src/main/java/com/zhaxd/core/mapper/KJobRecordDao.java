package com.zhaxd.core.mapper;

import org.beetl.sql.core.annotatoin.SqlStatement;
import org.beetl.sql.core.mapper.BaseMapper;

import com.zhaxd.core.model.*;

import java.util.List;


public interface KJobRecordDao extends BaseMapper<KJobRecord> {
    @SqlStatement(params = "recordJob")
    KJobRecord selectLastJob(Integer recordJob);
}