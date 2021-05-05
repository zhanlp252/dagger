package io.github.novareseller.log.configuration;

import io.github.novareseller.log.context.LogContext;
import io.github.novareseller.log.context.LogLabelGenerator;
import io.github.novareseller.log.id.LogIdGenerator;
import io.github.novareseller.log.id.LogIdGeneratorLoader;
import io.github.novareseller.tool.text.StringUtil;
import io.github.novareseller.tool.utils.Validator;
import org.springframework.beans.factory.InitializingBean;

/**
 * Log参数初始化类，适用于springboot和spring
 *
 * @author bowen
 * @date 2021/05/05
 */
public class LogPropertyInit implements InitializingBean {

    private String pattern;

    private Boolean enableInvokeTimePrint;

    private String idGenerator;

    private Boolean mdcEnable;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtil.isNotBlank(pattern)){
            LogLabelGenerator.setLabelPattern(pattern);
        }

        if (!Validator.isNullOrEmpty(enableInvokeTimePrint)){
            LogContext.setEnableInvokeTimePrint(enableInvokeTimePrint);
        }

        if (StringUtil.isNotBlank(idGenerator)){
            try{
                LogIdGenerator logIdGenerator = (LogIdGenerator) LogSpringAware.registerBean(Class.forName(idGenerator));
                LogIdGeneratorLoader.setIdGenerator(logIdGenerator);
            }catch (Exception e){
                throw new RuntimeException("Id generator package path is incorrect");
            }
        }
        //If and only if the user is set to true,
        // modify the mdc attribute in the context does not affect the original AspectLogbackMDCConverter,
        // when the custom pattern exists tl, open MDC
        if (Boolean.TRUE.equals(mdcEnable)) {
            LogContext.setHasLogMDC(true);
        }
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Boolean getEnableInvokeTimePrint() {
        return enableInvokeTimePrint;
    }

    public void setEnableInvokeTimePrint(Boolean enableInvokeTimePrint) {
        this.enableInvokeTimePrint = enableInvokeTimePrint;
    }

    public String getIdGenerator() {
        return idGenerator;
    }

    public void setIdGenerator(String idGenerator) {
        this.idGenerator = idGenerator;
    }

    public Boolean getMdcEnable() {
        return mdcEnable;
    }

    public void setMdcEnable(Boolean mdcEnable) {
        this.mdcEnable = mdcEnable;
    }
}
