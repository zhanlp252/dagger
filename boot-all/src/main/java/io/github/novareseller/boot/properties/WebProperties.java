package io.github.novareseller.boot.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author: Bowen huang
 * @date: 2021/04/28
 */
@Getter
@Setter
@ConfigurationProperties("spring.dagger.web")
public class WebProperties {

    public static final String[] ENDPOINTS = {
            "/**/actuator/**" , "/**/actuator/**/**" ,
            "/v2/api-docs/**", "/swagger-ui.html", "/swagger-resources/**", "/webjars/**" ,
            "/**/v2/api-docs/**", "/**/swagger-ui.html", "/**/swagger-resources/**", "/**/webjars/**",
            "/**/turbine.stream","/**/turbine.stream**/**", "/**/hystrix", "/**/hystrix.stream", "/**/hystrix/**", "/**/hystrix/**/**",	"/**/proxy.stream/**" ,
            "/**/druid/**", "/**/favicon.ico", "/**/prometheus","/favicon.ico"
    };

    private List<String> excludePathPatterns;


    private List<String> filterExcludes;

}
