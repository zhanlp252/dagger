package io.github.novareseller.boot.properties;

import io.github.novareseller.tool.utils.Validator;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.AntPathMatcher;

import java.util.List;

/**
 * @author: Bowen huang
 * @date: 2021/04/28
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.dagger.web")
public class WebProperties {

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    public static final String[] ENDPOINTS = {
            "/**/actuator/**" ,
            "/**/actuator/**/**" ,
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**" ,
            "/**/v2/api-docs/**",
            "/**/swagger-ui.html",
            "/**/swagger-resources/**",
            "/**/webjars/**",
            "/**/proxy.stream/**" ,
            "/**/druid/**",
            "/**/favicon.ico",
            "/**/prometheus",
            "/favicon.ico",
            "/api/ping/v1",
            "/static/*",
            "*.html",
            "*.js",
            "*.ico",
            "*.jpg",
            "*.png",
            "*.css"
    };

    private List<String> excludePathPatterns;

    private String dateFormatPattern = "yyyy-MM-dd HH:mm:ss";


    public boolean isExcludePath(String uri) {
        if (!Validator.isNullOrEmpty(excludePathPatterns)) {
            for (String excludePathPattern : excludePathPatterns) {
                if (MATCHER.match(excludePathPattern, uri)) {
                    return true;
                }
            }
        }
        return false;
    }
}
