package com.xxl.job.admin.core.util;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * local cache tool
 *
 * @author xuxueli 2018-01-22 21:37:34
 */
public class LocalCacheUtil {
    /** 类型建议用抽象父类，兼容性更好 */
    private static ConcurrentMap<String, LocalCacheData> cacheRepository = new ConcurrentHashMap<>();

    private static class LocalCacheData {
        private String key;
        private Object val;
        private long   timeoutTime;

        /**
         * Instantiates a new Local cache data.
         */
        public LocalCacheData() {
        }

        /**
         * Instantiates a new Local cache data.
         *
         * @param key         the key
         * @param val         the val
         * @param timeoutTime the timeout time
         */
        public LocalCacheData(String key, Object val, long timeoutTime) {
            this.key = key;
            this.val = val;
            this.timeoutTime = timeoutTime;
        }

        /**
         * Gets key.
         *
         * @return the key
         */
        public String getKey() {
            return key;
        }

        /**
         * Sets key.
         *
         * @param key the key
         */
        public void setKey(String key) {
            this.key = key;
        }

        /**
         * Gets val.
         *
         * @return the val
         */
        public Object getVal() {
            return val;
        }

        /**
         * Sets val.
         *
         * @param val the val
         */
        public void setVal(Object val) {
            this.val = val;
        }

        /**
         * Gets timeout time.
         *
         * @return the timeout time
         */
        public long getTimeoutTime() {
            return timeoutTime;
        }

        /**
         * Sets timeout time.
         *
         * @param timeoutTime the timeout time
         */
        public void setTimeoutTime(long timeoutTime) {
            this.timeoutTime = timeoutTime;
        }
    }


    /**
     * set cache
     *
     * @param key       the key
     * @param val       the val
     * @param cacheTime the cache time
     * @return boolean
     */
    public static boolean set(String key, Object val, long cacheTime) {

        // clean timeout cache, before set new cache (avoid cache too much)
        cleanTimeutCache();

        // set new cache
        if (StringUtils.isBlank(key)) {
            return false;
        }
        if (val == null) {
            remove(key);
        }
        if (cacheTime <= 0) {
            remove(key);
        }
        long           timeoutTime    = System.currentTimeMillis() + cacheTime;
        LocalCacheData localCacheData = new LocalCacheData(key, val, timeoutTime);
        cacheRepository.put(localCacheData.getKey(), localCacheData);
        return true;
    }

    /**
     * remove cache
     *
     * @param key the key
     * @return boolean
     */
    public static boolean remove(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        cacheRepository.remove(key);
        return true;
    }

    /**
     * get cache
     *
     * @param key the key
     * @return object
     */
    public static Object get(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        LocalCacheData localCacheData = cacheRepository.get(key);
        if (localCacheData != null && System.currentTimeMillis() < localCacheData.getTimeoutTime()) {
            return localCacheData.getVal();
        } else {
            remove(key);
            return null;
        }
    }

    /**
     * clean timeout cache
     *
     * @return boolean
     */
    public static boolean cleanTimeutCache() {
        if (!cacheRepository.keySet().isEmpty()) {
            for (String key : cacheRepository.keySet()) {
                LocalCacheData localCacheData = cacheRepository.get(key);
                if (localCacheData != null && System.currentTimeMillis() >= localCacheData.getTimeoutTime()) {
                    cacheRepository.remove(key);
                }
            }
        }
        return true;
    }

}
