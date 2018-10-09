package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobInfo;

import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * job info
 *
 * @author xuxueli 2016-1-12 18:03:45
 */
public interface XxlJobInfoDao {

    /**
     * Page list list.
     *
     * @param offset          the offset
     * @param pagesize        the pagesize
     * @param jobGroup        the job group
     * @param jobDesc         the job desc
     * @param executorHandler the executor handler
     * @return the list
     */
    List<XxlJobInfo> pageList(@Param("offset") int offset,
                              @Param("pagesize") int pagesize,
                              @Param("jobGroup") int jobGroup,
                              @Param("jobDesc") String jobDesc,
                              @Param("executorHandler") String executorHandler);

    /**
     * Page list count int.
     *
     * @param offset          the offset
     * @param pagesize        the pagesize
     * @param jobGroup        the job group
     * @param jobDesc         the job desc
     * @param executorHandler the executor handler
     * @return the int
     */
    int pageListCount(@Param("offset") int offset,
                      @Param("pagesize") int pagesize,
                      @Param("jobGroup") int jobGroup,
                      @Param("jobDesc") String jobDesc,
                      @Param("executorHandler") String executorHandler);

    /**
     * Save int.
     *
     * @param info the info
     * @return the int
     */
    int save(XxlJobInfo info);

    /**
     * Load by id xxl job info.
     *
     * @param id the id
     * @return the xxl job info
     */
    XxlJobInfo loadById(@Param("id") int id);

    /**
     * Update int.
     *
     * @param item the item
     * @return the int
     */
    int update(XxlJobInfo item);

    /**
     * Delete int.
     *
     * @param id the id
     * @return the int
     */
    int delete(@Param("id") int id);

    /**
     * Gets jobs by group.
     *
     * @param jobGroup the job group
     * @return the jobs by group
     */
    List<XxlJobInfo> getJobsByGroup(@Param("jobGroup") int jobGroup);

    /**
     * Find all count int.
     *
     * @return the int
     */
    int findAllCount();

}
