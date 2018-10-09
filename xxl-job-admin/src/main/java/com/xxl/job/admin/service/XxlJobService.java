package com.xxl.job.admin.service;


import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.core.biz.model.ReturnT;

import java.util.Date;
import java.util.Map;

/**
 * core job action for xxl-job
 *
 * @author xuxueli 2016-5-28 15:30:33
 */
public interface XxlJobService {

    /**
     * page list
     *
     * @param start           the start
     * @param length          the length
     * @param jobGroup        the job group
     * @param jobDesc         the job desc
     * @param executorHandler the executor handler
     * @param filterTime      the filter time
     * @return map
     */
    Map<String, Object> pageList(int start, int length, int jobGroup, String jobDesc, String executorHandler, String filterTime);

    /**
     * add job
     *
     * @param jobInfo the job info
     * @return return t
     */
    ReturnT<String> add(XxlJobInfo jobInfo);

    /**
     * update job
     *
     * @param jobInfo the job info
     * @return return t
     */
    ReturnT<String> update(XxlJobInfo jobInfo);

    /**
     * remove job
     *
     * @param id the id
     * @return return t
     */
    ReturnT<String> remove(int id);

    /**
     * pause job
     *
     * @param id the id
     * @return return t
     */
    ReturnT<String> pause(int id);

    /**
     * resume job
     *
     * @param id the id
     * @return return t
     */
    ReturnT<String> resume(int id);

    /**
     * dashboard info
     *
     * @return map
     */
    Map<String, Object> dashboardInfo();

    /**
     * chart info
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return return t
     */
    ReturnT<Map<String, Object>> chartInfo(Date startDate, Date endDate);

}
