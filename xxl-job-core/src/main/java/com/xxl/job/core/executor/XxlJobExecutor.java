package com.xxl.job.core.executor;

import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.impl.ExecutorBizImpl;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.job.core.rpc.netcom.NetComClientProxy;
import com.xxl.job.core.rpc.netcom.NetComServerFactory;
import com.xxl.job.core.thread.JobLogFileCleanThread;
import com.xxl.job.core.thread.JobThread;
import com.xxl.job.core.util.NetUtil;

import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xuxueli on 2016/3/2 21:14.
 */
public class XxlJobExecutor implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(XxlJobExecutor.class);

    private        String                                 adminAddresses;
    private        String                                 appName;
    private        String                                 ip;
    private        int                                    port;
    private        String                                 accessToken;
    private        String                                 logPath;
    private        int                                    logRetentionDays;
    private        NetComServerFactory                    serverFactory        = new NetComServerFactory();
    private static List<AdminBiz>                         adminBizList;
    private static ConcurrentHashMap<String, IJobHandler> jobHandlerRepository = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer, JobThread>  JobThreadRepository  = new ConcurrentHashMap<>();


    /**
     * Sets admin addresses.
     *
     * @param adminAddresses the admin addresses
     */
    public void setAdminAddresses(String adminAddresses) {
        this.adminAddresses = adminAddresses;
    }

    /**
     * Sets app name.
     *
     * @param appName the app name
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * Sets ip.
     *
     * @param ip the ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Sets port.
     *
     * @param port the port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Sets access token.
     *
     * @param accessToken the access token
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Sets log path.
     *
     * @param logPath the log path
     */
    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    /**
     * Sets log retention days.
     *
     * @param logRetentionDays the log retention days
     */
    public void setLogRetentionDays(int logRetentionDays) {
        this.logRetentionDays = logRetentionDays;
    }

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        XxlJobExecutor.applicationContext = applicationContext;
    }

    /**
     * Gets application context.
     *
     * @return the application context
     */
    @Contract(pure = true)
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * Start.
     *
     * @throws Exception the exception
     */
    public void start() throws Exception {
        // init admin-client
        initAdminBizList(adminAddresses, accessToken);

        // init executor-jobHandlerRepository
        initJobHandlerRepository(applicationContext);

        // init logpath
        XxlJobFileAppender.initLogPath(logPath);

        // init executor-server
        initExecutorServer(port, ip, appName, accessToken);

        // init JobLogFileCleanThread
        JobLogFileCleanThread.getInstance().start(logRetentionDays);
    }

    /**
     * Destroy.
     */
    public void destroy() {
        // destory JobThreadRepository
        if (JobThreadRepository.size() > 0) {
            for (Map.Entry<Integer, JobThread> item : JobThreadRepository.entrySet()) {
                removeJobThread(item.getKey(), "web container destroy and kill the job.");
            }
            JobThreadRepository.clear();
        }

        // destory executor-server
        stopExecutorServer();

        // destory JobLogFileCleanThread
        JobLogFileCleanThread.getInstance().toStop();
    }

    /**
     * 解析 admin list
     *
     * @param adminAddresses the admin addresses
     * @param accessToken    the access token
     * @throws Exception the exception
     */
    private static void initAdminBizList(String adminAddresses, String accessToken) throws Exception {
        if (adminAddresses != null && adminAddresses.trim().length() > 0) {
            for (String address : adminAddresses.trim().split(",")) {
                if (address != null && address.trim().length() > 0) {
                    String   addressUrl = address.concat(AdminBiz.MAPPING);
                    AdminBiz adminBiz   = (AdminBiz) new NetComClientProxy(AdminBiz.class, addressUrl, accessToken).getObject();
                    if (adminBizList == null) {
                        adminBizList = new ArrayList<>();
                    }
                    adminBizList.add(adminBiz);
                }
            }
        }
    }

    /**
     * Get admin biz list list.
     *
     * @return the list
     */
    public static List<AdminBiz> getAdminBizList() {
        return adminBizList;
    }

    /**
     * Init executor server.
     *
     * @param port        the port
     * @param ip          the ip
     * @param appName     the app name
     * @param accessToken the access token
     * @throws Exception the exception
     */
    private void initExecutorServer(int port, String ip, String appName, String accessToken) throws Exception {
        // 未配置 port 时, 获取可用的端口, 默认 9999
        port = port > 0 ? port : NetUtil.findAvailablePort(9999);
        // start server, rpc-service, base on jetty
        NetComServerFactory.putService(ExecutorBiz.class, new ExecutorBizImpl());
        NetComServerFactory.setAccessToken(accessToken);
        // jetty + registry
        serverFactory.start(port, ip, appName, jobHandlerRepository);
    }

    private void stopExecutorServer() {
        // jetty + registry + callback
        serverFactory.destroy();
    }

    /**
     * Regist job handler job handler.
     *
     * @param name       the name
     * @param jobHandler the job handler
     * @return the job handler
     */
    public static IJobHandler registJobHandler(String name, IJobHandler jobHandler) {
        logger.info(">>>>>>>>>>> xxl-job register jobhandler success, name:{}, jobHandler:{}", name, jobHandler);
        return jobHandlerRepository.put(name, jobHandler);
    }

    /**
     * Load job handler job handler.
     *
     * @param name the name
     * @return the job handler
     */
    public static IJobHandler loadJobHandler(String name) {
        return jobHandlerRepository.get(name);
    }

    /**
     * 解析被 @JobHandler 标识的任务类
     *
     * @param applicationContext the application context
     */
    private static void initJobHandlerRepository(ApplicationContext applicationContext) {
        if (applicationContext == null) {
            return;
        }

        // init job handler action
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(JobHandler.class);

        if (serviceBeanMap != null && serviceBeanMap.size() > 0) {
            for (Object serviceBean : serviceBeanMap.values()) {
                if (serviceBean instanceof IJobHandler) {
                    String      name    = serviceBean.getClass().getAnnotation(JobHandler.class).value();
                    IJobHandler handler = (IJobHandler) serviceBean;
                    if (loadJobHandler(name) != null) {
                        throw new RuntimeException("xxl-job jobhandler naming conflicts.");
                    }
                    registJobHandler(name, handler);
                }
            }
        }
    }

    /**
     * Regist job thread job thread.
     *
     * @param jobId           the job id
     * @param handler         the handler
     * @param removeOldReason the remove old reason
     * @return the job thread
     */
    public static JobThread registJobThread(int jobId, IJobHandler handler, String removeOldReason) {
        JobThread newJobThread = new JobThread(jobId, handler);
        newJobThread.start();
        logger.info(">>>>>>>>>>> xxl-job regist JobThread success, jobId:{}, handler:{}", new Object[] {jobId, handler});

        JobThread oldJobThread = JobThreadRepository.put(jobId, newJobThread);    // putIfAbsent | oh my god, map's put method return the old value!!!
        if (oldJobThread != null) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();
        }

        return newJobThread;
    }

    /**
     * Remove job thread.
     *
     * @param jobId           the job id
     * @param removeOldReason the remove old reason
     */
    public static void removeJobThread(int jobId, String removeOldReason) {
        JobThread oldJobThread = JobThreadRepository.remove(jobId);
        if (oldJobThread != null) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();
        }
    }

    /**
     * Load job thread job thread.
     *
     * @param jobId the job id
     * @return the job thread
     */
    public static JobThread loadJobThread(int jobId) {
        JobThread jobThread = JobThreadRepository.get(jobId);
        return jobThread;
    }

}
