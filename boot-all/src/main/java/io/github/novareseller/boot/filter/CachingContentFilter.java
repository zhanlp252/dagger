package io.github.novareseller.boot.filter;


import io.github.novareseller.boot.utils.IpUtils;
import io.github.novareseller.boot.wrapper.MultipleReadHttpRequestWrapper;
import io.github.novareseller.security.utils.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public class CachingContentFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(CachingContentFilter.class);


    private List<String> filterExcludes;


    public CachingContentFilter(List<String> filterExcludes) {
        this.filterExcludes = filterExcludes;
    }


    @Override
    public void init(FilterConfig config) throws ServletException {
        logger.info("Filter init: {}", config.getServletContext());
    }

    @Override
    public void destroy() {
        logger.info("Filter destroy.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if ( request instanceof HttpServletRequest) {
            HttpServletRequest hr = (HttpServletRequest) request;
            String method = hr.getMethod();
            String remote = IpUtils.getRemoteHost(hr);
            String uri = hr.getRequestURI();
            String query = hr.getQueryString();
            if (!Validator.isNullOrEmpty(filterExcludes) && !filterExcludes.contains(uri)) {
                logger.info("Filter request: method={}, remote={}, uri={}, query={}", method, remote, uri, query);
            }
            request = new MultipleReadHttpRequestWrapper(hr);
        }
        chain.doFilter(request, response);
    }
}
