package io.github.novareseller.boot.filter;


import io.github.novareseller.boot.utils.IpUtils;
import io.github.novareseller.boot.wrapper.MultipleReadHttpRequestWrapper;
import io.github.novareseller.security.utils.Validator;
import lombok.extern.slf4j.Slf4j;
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


    private List<String> filterExcludes;

    public CachingRequestContentFilter(List<String> filterExcludes) {
        this.filterExcludes = filterExcludes;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String method = httpServletRequest.getMethod();
        String remote = IpUtils.getRemoteHost(httpServletRequest);
        String uri = httpServletRequest.getRequestURI();
        String query = httpServletRequest.getQueryString();
        if (!Validator.isNullOrEmpty(filterExcludes) && !filterExcludes.contains(uri)) {
            log.info("Filter request: method={}, remote={}, uri={}, query={}", method, remote, uri, query);
        }
        httpServletRequest = new MultipleReadHttpRequestWrapper(httpServletRequest);

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
