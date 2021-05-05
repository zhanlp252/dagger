package io.github.novareseller.log.handler;

import java.io.Serializable;

/**
 * Log的日志标签包装类
 *
 * @author bowen
 * @date 2021/05/05
 */
public class LogLabelBean implements Serializable {

    private static final long serialVersionUID = -4695285303891753977L;
    private String preIvkApp;

    private String preIvkHost;

    private String preIp;

    private String traceId;

    private String spanId;

    public LogLabelBean() {
    }

    public LogLabelBean(String preIvkApp, String preIvkHost, String preIp, String traceId, String spanId) {
        this.preIvkApp = preIvkApp;
        this.preIvkHost = preIvkHost;
        this.preIp = preIp;
        this.traceId = traceId;
        this.spanId = spanId;
    }

    public String getPreIvkApp() {
        return preIvkApp;
    }

    public void setPreIvkApp(String preIvkApp) {
        this.preIvkApp = preIvkApp;
    }

    public String getPreIp() {
        return preIp;
    }

    public void setPreIp(String preIp) {
        this.preIp = preIp;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public String getPreIvkHost() {
        return preIvkHost;
    }

    public void setPreIvkHost(String preIvkHost) {
        this.preIvkHost = preIvkHost;
    }
}
