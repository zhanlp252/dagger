package io.github.novareseller.boot.filter;


import io.github.novareseller.boot.properties.WebProperties;
import io.github.novareseller.boot.utils.IpUtils;
import io.github.novareseller.boot.utils.SpringUtils;
import io.github.novareseller.boot.wrapper.MultipleReadHttpRequestWrapper;
import io.github.novareseller.tool.utils.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author Bowen
 * @date 2021/04/29
 */
@Slf4j
public class CachingRequestContentFilter extends OncePerRequestFilter {

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    private List<String> excludePathPatterns;

    public CachingRequestContentFilter(List<String> excludePathPatterns) {
        this.excludePathPatterns = excludePathPatterns;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String uri = httpServletRequest.getRequestURI();

        if (!isExcludePath(uri)) {
            httpServletRequest = new MultipleReadHttpRequestWrapper(httpServletRequest);
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private boolean isExcludePath(String uri) {
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
