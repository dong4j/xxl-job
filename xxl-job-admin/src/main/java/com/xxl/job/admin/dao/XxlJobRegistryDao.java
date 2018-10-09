package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobRegistry;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by xuxueli on 16/9/30.
 */
public interface XxlJobRegistryDao {

    /**
     * Remove dead int.
     *
     * @param timeout the timeout
     * @return the int
     */
    int removeDead(@Param("timeout") int timeout);

    /**
     * Find all list.
     *
     * @param timeout the timeout
     * @return the list
     */
    List<XxlJobRegistry> findAll(@Param("timeout") int timeout);

    /**
     * Registry update int.
     *
     * @param registryGroup the registry group
     * @param registryKey   the registry key
     * @param registryValue the registry value
     * @return the int
     */
    int registryUpdate(@Param("registryGroup") String registryGroup,
                       @Param("registryKey") String registryKey,
                       @Param("registryValue") String registryValue);

    /**
     * Registry save int.
     *
     * @param registryGroup the registry group
     * @param registryKey   the registry key
     * @param registryValue the registry value
     * @return the int
     */
    int registrySave(@Param("registryGroup") String registryGroup,
                     @Param("registryKey") String registryKey,
                     @Param("registryValue") String registryValue);

    /**
     * Registry delete int.
     *
     * @param registGroup   the regist group
     * @param registryKey   the registry key
     * @param registryValue the registry value
     * @return the int
     */
    int registryDelete(@Param("registryGroup") String registGroup,
                       @Param("registryKey") String registryKey,
                       @Param("registryValue") String registryValue);

}
