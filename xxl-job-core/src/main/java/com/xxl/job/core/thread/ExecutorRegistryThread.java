package com.xxl.job.core.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.RegistryParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.RegistryConfig;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.util.IpUtil;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by xuxueli on 17/3/2.
 */
@Slf4j
public class ExecutorRegistryThread extends Thread {

    private static ExecutorRegistryThread instance = new ExecutorRegistryThread();

    /**
     * Get instance executor registry thread.
     *
     * @return the executor registry thread
     */
    public static ExecutorRegistryThread getInstance() {
        return instance;
    }

    private          ExecutorService singleThreadPool;
    private volatile boolean         toStop = false;

    /**
     * client 注册逻辑, 每 30 秒一次心跳检测
     *
     * @param port    the port
     * @param ip      the ip
     * @param appName the app name
     */
    public void start(final int port, final String ip, final String appName, final List<String> jobHandlerRepository) {
        // valid
        if (appName == null || appName.trim().length() == 0) {
            log.warn(">>>>>>>>>>> xxl-job, executor registry config fail, appName is null.");
            return;
        }
        if (XxlJobExecutor.getAdminBizList() == null) {
            log.warn(">>>>>>>>>>> xxl-job, executor registry config fail, adminAddresses is null.");
            return;
        }

        // executor address (generate addredd = ip:port)
        final String executorAddress;
        if (ip != null && ip.trim().length() > 0) {
            executorAddress = ip.trim().concat(":").concat(String.valueOf(port));
        } else {
            executorAddress = IpUtil.getIpPort(port);
        }

        singleThreadPool = new ThreadPoolExecutor(1,
                                                  1,
                                                  0L,
                                                  TimeUnit.MILLISECONDS,
                                                  new LinkedBlockingQueue<Runnable>(1024),
                                                  new ThreadFactoryBuilder()
                                                      .setNameFormat("registry-pool-%d")
                                                      // daemon, service jvm, user thread leave >>> daemon leave >>> jvm leave
                                                      .setDaemon(true)
                                                      .build(),
                                                  new ThreadPoolExecutor.AbortPolicy());
        singleThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                while (!toStop) {
                    try {
                        RegistryParam registryParam = new RegistryParam(RegistryConfig.RegistType.EXECUTOR.name(), appName, executorAddress);
                        registryParam.setJobHandlerRepository(jobHandlerRepository);
                        for (AdminBiz adminBiz : XxlJobExecutor.getAdminBizList()) {
                            try {
                                // 向 admin 发送消息
                                ReturnT<String> registryResult = adminBiz.registry(registryParam);
                                if (registryResult != null && ReturnT.SUCCESS_CODE == registryResult.getCode()) {
                                    registryResult = ReturnT.SUCCESS;
                                    log.info(">>>>>>>>>>> xxl-job registry success, registryParam:{}, registryResult:{}", new Object[] {registryParam, registryResult});
                                    break;
                                } else {
                                    log.info(">>>>>>>>>>> xxl-job registry fail, registryParam:{}, registryResult:{}", new Object[] {registryParam, registryResult});
                                }
                            } catch (Exception e) {
                                log.info(">>>>>>>>>>> xxl-job registry error, registryParam:{}", registryParam, e);
                            }

                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }

                    try {
                        TimeUnit.SECONDS.sleep(RegistryConfig.BEAT_TIMEOUT);
                    } catch (InterruptedException e) {
                        log.warn(">>>>>>>>>>> xxl-job, executor registry thread interrupted, error msg:{}", e.getMessage());
                    }
                }

                // registry remove
                try {
                    RegistryParam registryParam = new RegistryParam(RegistryConfig.RegistType.EXECUTOR.name(), appName, executorAddress);
                    for (AdminBiz adminBiz : XxlJobExecutor.getAdminBizList()) {
                        try {
                            ReturnT<String> registryResult = adminBiz.registryRemove(registryParam);
                            if (registryResult != null && ReturnT.SUCCESS_CODE == registryResult.getCode()) {
                                registryResult = ReturnT.SUCCESS;
                                log.info(">>>>>>>>>>> xxl-job registry-remove success, registryParam:{}, registryResult:{}", new Object[] {registryParam, registryResult});
                                break;
                            } else {
                                log.info(">>>>>>>>>>> xxl-job registry-remove fail, registryParam:{}, registryResult:{}", new Object[] {registryParam, registryResult});
                            }
                        } catch (Exception e) {
                            log.info(">>>>>>>>>>> xxl-job registry-remove error, registryParam:{}", registryParam, e);
                        }

                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                log.info(">>>>>>>>>>> xxl-job, executor registry thread destory.");
            }
        });
    }

    /**
     * To stop.
     */
    public void toStop() {
        toStop = true;
        singleThreadPool.shutdown();
    }

}
