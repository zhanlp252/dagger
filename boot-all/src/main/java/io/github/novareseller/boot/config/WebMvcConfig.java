package io.github.novareseller.boot.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import io.github.novareseller.boot.filter.CachingRequestContentFilter;
import io.github.novareseller.boot.interceptor.ClientAuthInterceptor;
import io.github.novareseller.boot.interceptor.LogInterceptor;
import io.github.novareseller.boot.interceptor.UserAuthInterceptor;
import io.github.novareseller.boot.properties.WebProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;

/**
 * @author Bowen Huang
 * @Date 2021/3/19 23:29
 */
@Configuration
@EnableConfigurationProperties({WebProperties.class})
@ConditionalOnClass(WebMvcConfigurer.class)
@EnableWebMvc
@ComponentScan(basePackages = "io.github.novareseller")
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private WebProperties webProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        /**
         * 自定义拦截器，添加拦截路径和排除拦截路径
         * addPathPatterns():添加需要拦截的路径
         * excludePathPatterns():添加不需要拦截的路径
         * 在括号中还可以使用集合的形式，如注释部分代码所示
         */
        registry.addInterceptor(new LogInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns(WebProperties.ENDPOINTS);

        registry.addInterceptor(new UserAuthInterceptor(webProperties.getExcludePathPatterns()))
                .addPathPatterns("/api/**");

        registry.addInterceptor(new ClientAuthInterceptor())
                .addPathPatterns("/api/**");

    }


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        mapper.setDateFormat(new SimpleDateFormat(webProperties.getDateFormatPattern()));
        mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(mapper);
        converters.add(0, converter);
    }

    @Bean("requestFilterRegistrationBean")
    public FilterRegistrationBean<CachingRequestContentFilter> requestFilterRegistrationBean() {
        FilterRegistrationBean<CachingRequestContentFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new CachingRequestContentFilter(webProperties.getExcludePathPatterns()));
        //bean.setOrder(1);
        bean.addUrlPatterns("/api/*");
        return bean;
    }


    @Bean
    @ConditionalOnProperty(value = "spring.dagger.web.enable-kaptcha", havingValue = "true")
    public DefaultKaptcha captchaProducer() {
        DefaultKaptcha captchaProducer = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", "yes");
        properties.setProperty("kaptcha.border.color", "105,179,90");
        properties.setProperty("kaptcha.textproducer.font.color", "red");
        properties.setProperty("kaptcha.image.width", "131");
        properties.setProperty("kaptcha.image.height", "46");
        properties.setProperty("kaptcha.textproducer.font.size", "38");
        properties.setProperty("kaptcha.session.key", "code");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        properties.setProperty("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");
        Config config = new Config(properties);
        captchaProducer.setConfig(config);
        return captchaProducer;
    }


}
