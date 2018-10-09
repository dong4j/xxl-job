package com.xxl.job.core.biz.model;

import com.xxl.job.core.handler.IJobHandler;

import java.io.*;
import java.util.Map;

import lombok.Data;

/**
 * Created by xuxueli on 2017-05-10 20:22:42
 */
@Data
public class RegistryParam implements Serializable {
    private static final long serialVersionUID = 42L;

    private String                   registGroup;
    private String                   registryKey;
    private String                   registryValue;
    /** 保存 jobhandler 名称与 class 对应关系 */
    private Map<String, IJobHandler> jobHandlerRepository;

    public RegistryParam() {}

    public RegistryParam(String registGroup, String registryKey, String registryValue) {
        this.registGroup = registGroup;
        this.registryKey = registryKey;
        this.registryValue = registryValue;
    }
}
