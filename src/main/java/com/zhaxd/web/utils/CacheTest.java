package com.zhaxd.web.utils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class CacheTest {
    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
        InputStream in = CacheTest.class.getResourceAsStream("./ehcache-setting.xml");
        File file = new File("src/main/java/com/zhaxd/web/utils/ehcache-setting.xml");
        BufferedInputStream input = null;
        try {
            input = new BufferedInputStream(new FileInputStream(file));
        } catch (Exception var11) {
            System.out.println(var11.getMessage());
        }
        CacheManager cacheManager = CacheManager.create(ClassLoader.getSystemResource("spring/ehcache-setting.xml"));
        // 2. 获取缓存对象
        Cache cache = cacheManager.getCache("sysCache");
        Element value1 = cache.get("key1");
        // 3. 创建元素
        Element element = new Element("key1", "value1");

        // 4. 将元素添加到缓存
        cache.put(element);

        // 5. 获取缓存
        Element value = cache.get("key1");
        System.out.println("value: " + value);
        System.out.println(value.getObjectValue());
        // 7. 刷新缓存
        cache.flush();

    }
}
