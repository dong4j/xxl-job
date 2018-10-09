package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobLog;

import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * job log
 *
 * @author xuxueli 2016-1-12 18:03:06
 */
public interface XxlJobLogDao {

    /**
     * Page list list.
     *
     * @param offset           the offset
     * @param pagesize         the pagesize
     * @param jobGroup         the job group
     * @param jobId            the job id
     * @param triggerTimeStart the trigger time start
     * @param triggerTimeEnd   the trigger time end
     * @param logStatus        the log status
     * @return the list
     */
    List<XxlJobLog> pageList(@Param("offset") int offset,
                             @Param("pagesize") int pagesize,
                             @Param("jobGroup") int jobGroup,
                             @Param("jobId") int jobId,
                             @Param("triggerTimeStart") Date triggerTimeStart,
                             @Param("triggerTimeEnd") Date triggerTimeEnd,
                             @Param("logStatus") int logStatus);

    /**
     * Page list count int.
     *
     * @param offset           the offset
     * @param pagesize         the pagesize
     * @param jobGroup         the job group
     * @param jobId            the job id
     * @param triggerTimeStart the trigger time start
     * @param triggerTimeEnd   the trigger time end
     * @param logStatus        the log status
     * @return the int
     */
    int pageListCount(@Param("offset") int offset,
                      @Param("pagesize") int pagesize,
                      @Param("jobGroup") int jobGroup,
                      @Param("jobId") int jobId,
                      @Param("triggerTimeStart") Date triggerTimeStart,
                      @Param("triggerTimeEnd") Date triggerTimeEnd,
                      @Param("logStatus") int logStatus);

    /**
     * Load xxl job log.
     *
     * @param id the id
     * @return the xxl job log
     */
    XxlJobLog load(@Param("id") int id);

    /**
     * Save int.
     *
     * @param xxlJobLog the xxl job log
     * @return the int
     */
    int save(XxlJobLog xxlJobLog);

    /**
     * Update trigger info int.
     *
     * @param xxlJobLog the xxl job log
     * @return the int
     */
    int updateTriggerInfo(XxlJobLog xxlJobLog);

    /**
     * Update handle info int.
     *
     * @param xxlJobLog the xxl job log
     * @return the int
     */
    int updateHandleInfo(XxlJobLog xxlJobLog);

    /**
     * Delete int.
     *
     * @param jobId the job id
     * @return the int
     */
    int delete(@Param("jobId") int jobId);

    /**
     * Trigger count by handle code int.
     *
     * @param handleCode the handle code
     * @return the int
     */
    int triggerCountByHandleCode(@Param("handleCode") int handleCode);

    /**
     * Trigger count by day list.
     *
     * @param from the from
     * @param to   the to
     * @return the list
     */
    List<Map<String, Object>> triggerCountByDay(@Param("from") Date from,
                                                @Param("to") Date to);

    /**
     * Clear log int.
     *
     * @param jobGroup        the job group
     * @param jobId           the job id
     * @param clearBeforeTime the clear before time
     * @param clearBeforeNum  the clear before num
     * @return the int
     */
    int clearLog(@Param("jobGroup") int jobGroup,
                 @Param("jobId") int jobId,
                 @Param("clearBeforeTime") Date clearBeforeTime,
                 @Param("clearBeforeNum") int clearBeforeNum);

}
