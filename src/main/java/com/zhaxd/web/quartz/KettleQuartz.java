//package com.zhaxd.web.quartz;
//
//import com.zhaxd.common.kettle.repository.RepositoryUtil;
//import com.zhaxd.common.toolkit.Constant;
//import com.zhaxd.core.model.KRepository;
//import org.apache.log4j.Logger;
//import org.pentaho.di.core.exception.KettleException;
//import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
//import org.quartz.*;
//
//public class KettleQuartz implements InterruptableJob {
//
//    private static Logger logger = Logger.getLogger(KettleQuartz.class);
//
//    @Override
//    public void interrupt() throws UnableToInterruptJobException {
//
//    }
//
//    @Override
//    public void execute(JobExecutionContext context) throws JobExecutionException {
//        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
//        Object KRepositoryObject = jobDataMap.get(Constant.REPOSITORYOBJECT);
//        KRepository kRepository = (KRepository) KRepositoryObject;
//        Integer repositoryId = kRepository.getRepositoryId();
//        KettleDatabaseRepository kettleDatabaseRepository = RepositoryUtil.KettleDatabaseRepositoryCatch.get(repositoryId);
//        if (null == kettleDatabaseRepository ) {
//            try {
//                RepositoryUtil.connectionRepository(kRepository);
//            } catch (KettleException e) {
//                logger.error("资源库创建失败");
//            }
//        } else if (false == kettleDatabaseRepository.test()) {
//            logger.error("资源库已断开连接");
//            try {
//                RepositoryUtil.connectionRepository(kRepository);
//                System.out.println("资源库重新创建正常");
//            } catch (KettleException e) {
//                e.printStackTrace();
//                logger.error("资源库重新创建失败");
//            }
//        }else{
//            System.out.println("资源库连接正常");
//        }
//    }
//}
