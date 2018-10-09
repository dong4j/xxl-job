package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobLogGlue;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * job log for glue
 *
 * @author xuxueli 2016-5-19 18:04:56
 */
public interface XxlJobLogGlueDao {

    /**
     * Save int.
     *
     * @param xxlJobLogGlue the xxl job log glue
     * @return the int
     */
    int save(XxlJobLogGlue xxlJobLogGlue);

    /**
     * Find by job id list.
     *
     * @param jobId the job id
     * @return the list
     */
    List<XxlJobLogGlue> findByJobId(@Param("jobId") int jobId);

    /**
     * Remove old int.
     *
     * @param jobId the job id
     * @param limit the limit
     * @return the int
     */
    int removeOld(@Param("jobId") int jobId, @Param("limit") int limit);

    /**
     * Delete by job id int.
     *
     * @param jobId the job id
     * @return the int
     */
    int deleteByJobId(@Param("jobId") int jobId);

}
