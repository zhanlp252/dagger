package io.github.novareseller.boot.wrapper;

import com.google.common.collect.Iterables;
import io.github.novareseller.tool.utils.RandomUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MultipleReadHttpRequestWrapper extends HttpServletRequestWrapper {

    private ByteArrayOutputStream cached = null;
    private Map<String, String[]> parameterMap;
    private final String id;

    public MultipleReadHttpRequestWrapper(HttpServletRequest request) {
        super(request);
        this.id = RandomUtil.getLocalString(12, RandomUtil.DIGIT);
    }

    public String getId() {
        return id;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        if ( parameterMap == null ) {
            Map<String, Set<String>> map = new LinkedHashMap<>();
            decode(getQueryString(), map);
            decode(getPostBodyAsString(), map);

            Map<String, String[]> result = new LinkedHashMap<>();
            map.forEach((key, values) -> {
                ArrayList<String> al = new ArrayList<>(values);
                String[] a = al.toArray(new String[values.size()]);
                result.put(key, a);
            });
            parameterMap = Collections.unmodifiableMap(result);
        }
        return parameterMap;
    }

    @Override
    public String getParameter(String key) {
        Map<String, String[]> parameterMap = getParameterMap();
        String[] values = parameterMap.get(key);
        return values != null && values.length > 0 ? values[0] : null;
    }

    @Override
    public String[] getParameterValues(String key) {
        Map<String, String[]> parameterMap = getParameterMap();
        return parameterMap.get(key);
    }

    private String getPostBodyAsString() {
        try {
            if ( cached == null ) {
                cacheInputStream();
            }
            return cached.toString(getCharacterEncoding());
        } catch ( IOException ex ) {
            throw new RuntimeException(ex);
        }
    }

    private static void fillMap(Iterable<NameValuePair> params, Map<String, Set<String>> map) {
        for ( NameValuePair pair : params ) {
            String key = pair.getName();
            String value = pair.getValue();
            if ( map.containsKey(key) ) {
                Set<String> values = map.get(key);
                values.add(value);
            } else {
                Set<String> values = new LinkedHashSet<>();
                values.add(value);
                map.put(key, values);
            }
        }
    }

    private Iterable<NameValuePair> decodeParams(String body) {
        Iterable<NameValuePair> params = URLEncodedUtils.parse(body, StandardCharsets.UTF_8);
        try {
            String cts = getContentType();
            if ( cts != null ) {
                ContentType ct = ContentType.parse(cts);
                if ( ct.getMimeType().equals(ContentType.APPLICATION_FORM_URLENCODED.getMimeType()) ) {
                    List<NameValuePair> postParams = URLEncodedUtils.parse(IOUtils.toString(getReader()), StandardCharsets.UTF_8);
                    params = Iterables.concat(params, postParams);
                }
            }
        } catch ( IOException ex ) {
            throw new IllegalStateException(ex);
        }
        return params;
    }

    private void decode(String query, Map<String, Set<String>> map) {
        if ( query != null ) {
            fillMap(decodeParams(query), map);
        }
    }

    @Override
    public String getCharacterEncoding() {
        return StandardCharsets.UTF_8.name();
    }

    private void cacheInputStream() throws IOException {
        cached = new ByteArrayOutputStream();
        IOUtils.copy(super.getInputStream(), cached);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if ( cached == null ) {
            cacheInputStream();
        }
        return new MultipleReadInputStream();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }

    private class MultipleReadInputStream extends ServletInputStream {

        private final ByteArrayInputStream input;

        MultipleReadInputStream() {
            input = new ByteArrayInputStream(cached.toByteArray());
        }

        @Override
        public boolean isFinished() {
            return input.available() > 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
        }

        @Override
        public int read() throws IOException {
            return input.read();
        }

        @Override
        public synchronized void reset() throws IOException {
            input.reset();
        }
    }
}
