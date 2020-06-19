package com.zhaxd.common.kettle.environment;

import com.zhaxd.web.service.DataBaseRepositoryService;
import com.zhaxd.web.service.JobService;
import com.zhaxd.web.service.TransService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class StartInit implements InitializingBean {

    @Autowired
    private TransService transService;

    @Autowired
    private DataBaseRepositoryService dataBaseRepositoryService;

    @Autowired
    private JobService jobService;

    @Override
    public void afterPropertiesSet() throws Exception {
        //初始化环境***
//		com.zhaxd.common.kettle.environment.KettleInit.init();
        com.zhaxd.common.kettle.environment.KettleInit.environmentInit();
        org.pentaho.di.core.KettleEnvironment.init();
        jobService.scanJob();
        transService.scanJob();
//        dataBaseRepositoryService.scanKettle();
        dataBaseRepositoryService.scanRepositoryCache();
    }

}
