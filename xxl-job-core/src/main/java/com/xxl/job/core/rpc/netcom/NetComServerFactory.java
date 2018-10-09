package com.xxl.job.core.rpc.netcom;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.rpc.codec.RpcRequest;
import com.xxl.job.core.rpc.codec.RpcResponse;
import com.xxl.job.core.rpc.netcom.jetty.server.JettyServer;

import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * netcom init
 *
 * @author xuxueli 2015-10-31 22:54:27
 */
public class NetComServerFactory {
    private JettyServer server = new JettyServer();

    /**
     * client 向 admin 注册
     *
     * @param port    the port
     * @param ip      the ip
     * @param appName the app name
     */
    public void start(int port, String ip, String appName, List<String> jobHandlerRepository) {
        server.start(port, ip, appName, jobHandlerRepository);
    }

    /**
     * Destroy.
     */
    public void destroy() {
        server.destroy();
    }

    /**
     * init local rpc service map
     */
    private static Map<String, Object> serviceMap = new HashMap<>();
    private static String              accessToken;

    /**
     * Put service.
     *
     * @param iface       the iface
     * @param serviceBean the service bean
     */
    public static void putService(Class<?> iface, Object serviceBean) {
        serviceMap.put(iface.getName(), serviceBean);
    }

    /**
     * Sets access token.
     *
     * @param accessToken the access token
     */
    public static void setAccessToken(String accessToken) {
        NetComServerFactory.accessToken = accessToken;
    }

    /**
     * Invoke service rpc response.
     *
     * @param request     the request
     * @param serviceBean the service bean
     * @return the rpc response
     */
    public static RpcResponse invokeService(RpcRequest request, Object serviceBean) {
        if (serviceBean == null) {
            serviceBean = serviceMap.get(request.getClassName());
        }
        if (serviceBean == null) {
            // TODO
        }

        RpcResponse response = new RpcResponse();

        if (System.currentTimeMillis() - request.getCreateMillisTime() > 180000) {
            response.setResult(new ReturnT<String>(ReturnT.FAIL_CODE, "The timestamp difference between admin and executor exceeds the limit."));
            return response;
        }
        if (accessToken != null && accessToken.trim().length() > 0 && !accessToken.trim().equals(request.getAccessToken())) {
            response.setResult(new ReturnT<String>(ReturnT.FAIL_CODE, "The access token[" + request.getAccessToken() + "] is wrong."));
            return response;
        }

        try {
            Class<?>   serviceClass   = Objects.requireNonNull(serviceBean).getClass();
            String     methodName     = request.getMethodName();
            Class<?>[] parameterTypes = request.getParameterTypes();
            Object[]   parameters     = request.getParameters();

            FastClass  serviceFastClass  = FastClass.create(serviceClass);
            FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);

            Object result = serviceFastMethod.invoke(serviceBean, parameters);

            response.setResult(result);
        } catch (Throwable t) {
            t.printStackTrace();
            response.setError(t.getMessage());
        }

        return response;
    }

}
