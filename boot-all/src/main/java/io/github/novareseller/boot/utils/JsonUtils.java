package io.github.novareseller.boot.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

public class JsonUtils {

    private static final ObjectMapper mapper;

    private static final TypeReference<LinkedHashMap<String, Object>> mapTypeRef = new TypeReference<LinkedHashMap<String, Object>>() {};

    static {
        mapper = createDefault();
    }

    public static ObjectMapper createDefault() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        formatter.setTimeZone(tz);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(formatter);
        mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }

    public static String json(Object object) throws Exception {
        return mapper.writeValueAsString(object);
    }

    public static String jsonWithoutError(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch ( Exception ex ) {
            return null;
        }
    }

    public static <T> T parse(String json, Class<T> type) throws Exception {
        if ( json == null || json.isEmpty() ) {
            return null;
        }

        return mapper.readValue(json, type);
    }

    public static Map<String, Object> parseAsMap(String json) throws Exception {
        if ( json == null || json.isEmpty() ) {
            return null;
        }

        return mapper.readValue(json, mapTypeRef);
    }

    public static <T>T convertBean(Map<String, Object> map, Class<T> clazz) {
        if ( map == null || map.isEmpty() ) {
            return null;
        }

        return mapper.convertValue(map, clazz);
    }

    public static <T>T convertBean(Map<String, Object> map, TypeReference<T> typeReference) {
        if ( map == null || map.isEmpty() ) {
            return null;
        }

        return mapper.convertValue(map, typeReference);
    }

}
