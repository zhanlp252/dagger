package utils;

import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @description:线程traceId封装工具类
 * @author: verity zhan
 * @time: 2021/3/24 19:31
 */
public class ThreadMdcUtil {

    /**
     *
     *
     * @description: 判断当前线程对应MDC的Map是否存在，存在则设置
     * @author: verity zhan
     * @time: 2021/3/30 10:03
     */    

    public static <T> Callable<T> wrap(final Callable<T> callable, final Map<String, String> context) {
        return () -> {
            if (context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            TraceUtil.getTrace();
            try {
                return callable.call();
            } finally {
                MDC.clear();
            }
        };
    }

    /**
     *
     *
     * @description: 设置MDC中的traceId值，不存在则新生成，针对不是子线程的情况，如果是子线程，MDC中traceId不为null
     * 执行run方法 重新返回的是包装后的Runnable 先将主线程的Map设置到当前线程中【 即MDC.setContextMap(context)】，
     * 这样子线程和主线程MDC对应的Map就是一样的了
     * @author: verity zhan
     * @time: 2021/3/30 10:04
     */

    public static Runnable wrap(final Runnable runnable, final Map<String, String> context) {
        return new Runnable() {
            @Override
            public void run() {
                if (context == null) {
                    MDC.clear();
                } else {
                    MDC.setContextMap(context);
                }
                TraceUtil.getTrace();
                try {
                    runnable.run();
                } finally {
                    MDC.clear();
                }
            }
        };
    }
}


