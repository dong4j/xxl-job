package com.xxl.job.core.rpc.netcom.jetty.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import com.xxl.job.core.thread.ExecutorRegistryThread;
import com.xxl.job.core.thread.TriggerCallbackThread;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * rpc jetty server
 *
 * @author xuxueli 2015-11-19 22:29:03
 */
public class JettyServer {
    private static final Logger logger = LoggerFactory.getLogger(JettyServer.class);

    private Server          server;
    private ExecutorService singleThreadPool;

    /**
     * 启动 jetty 服务
     *
     * @param port    the port
     * @param ip      the ip
     * @param appName the app name
     */
    public void start(final int port, final String ip, final String appName, final List<String> jobHandlerRepository) {
        singleThreadPool = new ThreadPoolExecutor(1,
                                                  1,
                                                  0L,
                                                  TimeUnit.MILLISECONDS,
                                                  new LinkedBlockingQueue<Runnable>(1024),
                                                  new ThreadFactoryBuilder()
                                                      .setNameFormat("jetty-pool-%d")
                                                      // daemon, service jvm, user thread leave >>> daemon leave >>> jvm leave
                                                      .setDaemon(true)
                                                      .build(),
                                                  new ThreadPoolExecutor.AbortPolicy());
        singleThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                // The Server
                server = new Server(new ExecutorThreadPool(1000));

                // HTTP connector
                ServerConnector connector = new ServerConnector(server);
                if (ip != null && ip.trim().length() > 0) {
                    // The network interface this connector binds to as an IP address or a hostname.  If null or 0.0.0.0, then bind to all interfaces.
                    // connector.setHost(ip);
                }
                connector.setPort(port);
                server.setConnectors(new Connector[] {connector});

                // Set a handler
                HandlerCollection handlerc = new HandlerCollection();
                handlerc.setHandlers(new Handler[] {new JettyServerHandler()});
                server.setHandler(handlerc);

                try {
                    // Start server
                    server.start();
                    logger.info(">>>>>>>>>>> xxl-job jetty server start success at port:{}.", port);

                    // Start Registry-Server
                    ExecutorRegistryThread.getInstance().start(port, ip, appName, jobHandlerRepository);

                    // Start Callback-Server
                    TriggerCallbackThread.getInstance().start();

                    server.join();    // block until thread stopped
                    logger.info(">>>>>>>>>>> xxl-rpc server join success, netcon={}, port={}", JettyServer.class.getName(), port);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
    }

    /**
     * Destroy.
     */
    public void destroy() {
        // destroy Registry-Server
        ExecutorRegistryThread.getInstance().toStop();

        // destroy Callback-Server
        TriggerCallbackThread.getInstance().toStop();

        // destroy server
        if (server != null) {
            try {
                server.stop();
                server.destroy();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        logger.info(">>>>>>>>>>> xxl-rpc server destroy success, netcon={}", JettyServer.class.getName());
        // 关闭线程池
        singleThreadPool.shutdown();
    }

}
