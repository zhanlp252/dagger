package tech.goose.web.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tech.goose.log.constant.TraceConstant;
import tech.goose.tool.text.StringUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Bowen
 * api 经过filter --> interceptor -->aop -->controller 如果某些接口，
 * 比如filter --> userdetail 这种情况，aop mdc设置 后续log输出traceid blog:
 */
public class TraceUtil {

	public static String getTrace() {
		String app_trace_id = "";
		try {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
					.getRequest();

			app_trace_id = request.getHeader(TraceConstant.HTTP_HEADER_TRACE_ID);

			// 未经过HandlerInterceptor的设置
			if (StringUtil.isBlank(MDC.get(TraceConstant.LOG_TRACE_ID))) {
				// 但是有请求头，重新设置
				if (StringUtils.isNotEmpty(app_trace_id)) {
					MDC.put(TraceConstant.LOG_TRACE_ID, app_trace_id);
				}
			}
		} catch (Exception e) {

		}

		return app_trace_id;

	}

}
