package com.xxl.job.core.biz;

import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.biz.model.RegistryParam;
import com.xxl.job.core.biz.model.ReturnT;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The interface Admin biz.
 *
 * @author xuxueli 2017-07-27 21:52:49
 */
public interface AdminBiz {
    Map<String, List<String>> RELATIONSHIP_MAP = new ConcurrentHashMap<>();

    /**
     * The constant MAPPING.
     */
    String MAPPING = "/api";

    /**
     * callback
     *
     * @param callbackParamList the callback param list
     * @return return t
     */
    ReturnT<String> callback(List<HandleCallbackParam> callbackParamList);

    /**
     * registry
     *
     * @param registryParam the registry param
     * @return return t
     */
    ReturnT<String> registry(RegistryParam registryParam);

    /**
     * registry remove
     *
     * @param registryParam the registry param
     * @return return t
     */
    ReturnT<String> registryRemove(RegistryParam registryParam);

}
