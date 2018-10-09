package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobGroup;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by xuxueli on 16/9/30.
 */
public interface XxlJobGroupDao {

    /**
     * Find all list.
     *
     * @return the list
     */
    List<XxlJobGroup> findAll();

    /**
     * Find by address type list.
     *
     * @param addressType the address type
     * @return the list
     */
    List<XxlJobGroup> findByAddressType(@Param("addressType") int addressType);

    /**
     * Save int.
     *
     * @param xxlJobGroup the xxl job group
     * @return the int
     */
    int save(XxlJobGroup xxlJobGroup);

    /**
     * Update int.
     *
     * @param xxlJobGroup the xxl job group
     * @return the int
     */
    int update(XxlJobGroup xxlJobGroup);

    /**
     * Remove int.
     *
     * @param id the id
     * @return the int
     */
    int remove(@Param("id") int id);

    /**
     * Load xxl job group.
     *
     * @param id the id
     * @return the xxl job group
     */
    XxlJobGroup load(@Param("id") int id);
}
