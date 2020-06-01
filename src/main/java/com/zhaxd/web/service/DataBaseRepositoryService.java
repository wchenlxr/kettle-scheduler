package com.zhaxd.web.service;

import com.zhaxd.common.kettle.repository.RepositoryUtil;
import com.zhaxd.common.toolkit.Constant;
import com.zhaxd.core.dto.BootTablePage;
import com.zhaxd.core.dto.kettle.RepositoryTree;
import com.zhaxd.core.mapper.KRepositoryDao;
import com.zhaxd.core.mapper.KRepositoryTypeDao;
import com.zhaxd.core.model.KRepository;
import com.zhaxd.core.model.KRepositoryType;
import com.zhaxd.web.quartz.KettleQuartz;
import com.zhaxd.web.quartz.QuartzManager;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class DataBaseRepositoryService {


    @Autowired
    private KRepositoryDao kRepositoryDao;

    @Autowired
    private KRepositoryTypeDao kRepositoryTypeDao;

    @Autowired
    private CacheManager cacheManager;

    @PostConstruct
    private void scanRepositoryCache() {
        new Thread(() -> {
            List<KRepository> kRepositories = kRepositoryDao.all();
            kRepositories.stream().map(kRepository -> kRepository.getRepositoryId()).forEach(s -> {
                Cache cache = cacheManager.getCache("sysCache");
                try {
                    List<RepositoryTree> val = getTreeList(s);
                    cache.put(new Element("repositoryId:" + s, val));
                    cache.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    scanRepositoryCache();
                }
            });
        }).start();
    }

    @PostConstruct
    private void scanKettle() {
        try {
            List<KRepository> kRepositories = kRepositoryDao.all();
            kRepositories.stream().forEach(
                    kRepository -> {
                        // 拼接Quartz的任务名称
                        StringBuilder jobName = new StringBuilder();
                        jobName.append(Constant.JOB_PREFIX).append(Constant.QUARTZ_SEPARATE)
                                .append(kRepository.getRepositoryId());
                        // 拼接Quartz的任务组名称
                        StringBuilder jobGroupName = new StringBuilder();
                        jobGroupName.append(Constant.JOB_GROUP_PREFIX).append(Constant.QUARTZ_SEPARATE)
                                .append(kRepository.getRepositoryId());
                        // 拼接Quartz的触发器名称
                        String triggerName = StringUtils.replace(jobName.toString(), Constant.JOB_PREFIX, Constant.TRIGGER_PREFIX);
                        // 拼接Quartz的触发器组名称
                        String triggerGroupName = StringUtils.replace(jobGroupName.toString(), Constant.JOB_GROUP_PREFIX, Constant.TRIGGER_GROUP_PREFIX);
                        Map<String, Object> parameter = new HashMap<>();
                        parameter.put(Constant.REPOSITORYOBJECT, kRepository);
                        //添加任务
                        QuartzManager.addJob(jobName.toString(), jobGroupName.toString(), triggerName, triggerGroupName, KettleQuartz.class, "0 0 0/1 * * ?", parameter);
                    });
        } catch (Exception e) {

        }
    }

    /**
     * @param repositoryId
     * @return List<RepositoryTree>
     * @throws KettleException
     * @Title getRepositoryTreeList
     * @Description 获取数据库资源库的树形菜单
     */
    @Cacheable(value = "sysCache", key = "'repositoryId:' + #repositoryId")
    public List<RepositoryTree> getTreeList(Integer repositoryId) throws KettleException {
        KettleDatabaseRepository kettleDatabaseRepository = null;
        List<RepositoryTree> allRepositoryTreeList = new ArrayList<RepositoryTree>();
        if (RepositoryUtil.KettleDatabaseRepositoryCatch.containsKey(repositoryId)) {
            kettleDatabaseRepository = RepositoryUtil.KettleDatabaseRepositoryCatch.get(repositoryId);
        } else {
            KRepository kRepository = kRepositoryDao.unique(repositoryId);
            kettleDatabaseRepository = RepositoryUtil.connectionRepository(kRepository);
        }
        if (null != kettleDatabaseRepository) {
            List<RepositoryTree> repositoryTreeList = new ArrayList<RepositoryTree>();
            allRepositoryTreeList = RepositoryUtil.getAllDirectoryTreeList(kettleDatabaseRepository, "/", repositoryTreeList);
        }
        return allRepositoryTreeList;
    }

    /**
     * @param kRepository
     * @return boolean
     * @throws KettleException
     * @Title ckeck
     * @Description 判断是否可以连接上资源库
     */
    public boolean ckeck(KRepository kRepository) throws KettleException {
        KettleDatabaseRepository kettleDatabaseRepository = RepositoryUtil.connectionRepository(kRepository);
        if (kettleDatabaseRepository != null) {
            if (kettleDatabaseRepository.isConnected()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * @param uId 用户ID
     * @return List<KRepository>
     * @throws KettleException
     * @Title getList
     * @Description 获取列表，不分页
     */
    public List<KRepository> getList(Integer uId) throws KettleException {
        KRepository kRepository = new KRepository();
        kRepository.setAddUser(uId);
        kRepository.setDelFlag(1);
        return kRepositoryDao.template(kRepository);
    }

    /**
     * @param start 其实行数
     * @param size  每页数据条数
     * @param uId   用户ID
     * @return BootTablePage
     * @Title getList
     * @Description 获取列表带分页
     */
    public BootTablePage getList(Integer start, Integer size, Integer uId) {
        KRepository kRepository = new KRepository();
        kRepository.setAddUser(uId);
        kRepository.setDelFlag(1);
        List<KRepository> kRepositoryList = kRepositoryDao.template(kRepository);
        long allCount = kRepositoryDao.templateCount(kRepository);
        BootTablePage bootTablePage = new BootTablePage();
        bootTablePage.setRows(kRepositoryList);
        bootTablePage.setTotal(allCount);
        return bootTablePage;
    }

    /**
     * @return List<KRepositoryType>
     * @Title getRepositoryTypeList
     * @Description 获取资源库类别列表
     */
    public List<KRepositoryType> getRepositoryTypeList() {
        return kRepositoryTypeDao.all();
    }

    /**
     * @param repositoryId 资源库ID
     * @return KRepository
     * @Title getKRepository
     * @Description 获取资源库对象
     */
    public KRepository getKRepository(Integer repositoryId) {
        //如果根据主键没有获取到对象，返回null
        return kRepositoryDao.single(repositoryId);
    }

    /**
     * @return String[]
     * @Title getAccess
     * @Description 获取资源库访问类型
     */
    public String[] getAccess() {
        return RepositoryUtil.getDataBaseAccess();
    }

    /**
     * @param kRepository 资源库对象
     * @param uId         用户ID
     * @return void
     * @Title insert
     * @Description 插入资源库
     */
    public void insert(KRepository kRepository, Integer uId) {
        kRepository.setAddTime(new Date());
        kRepository.setAddUser(uId);
        kRepository.setEditTime(new Date());
        kRepository.setEditUser(uId);
        kRepository.setDelFlag(1);
        kRepositoryDao.insert(kRepository);
    }

    /**
     * @param kRepository 资源库对象
     * @param uId         用户ID
     * @return void
     * @Title update
     * @Description 更新资源库
     */
    public void update(KRepository kRepository, Integer uId) {
        kRepository.setEditTime(new Date());
        kRepository.setEditUser(uId);
        //只有不为null的字段才参与更新
        kRepositoryDao.updateTemplateById(kRepository);
    }

    /**
     * @param repositoryId 资源库ID
     * @return void
     * @Title delete
     * @Description 删除资源库
     */
    public void delete(Integer repositoryId) {
        KRepository kRepository = kRepositoryDao.unique(repositoryId);
        kRepository.setDelFlag(0);
        kRepositoryDao.updateById(kRepository);
    }
}
