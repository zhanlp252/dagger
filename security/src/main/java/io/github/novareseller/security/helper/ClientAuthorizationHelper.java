package io.github.novareseller.security.helper;

import io.github.novareseller.tool.utils.SystemClock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacUtils;

import javax.crypto.Mac;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author Bowen Huang
 * @Date 2021/5/16 23:52
 */
@Slf4j
public class ClientAuthorizationHelper {


    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final byte[] EMPTY_ARRAY = new byte[0];

    private static String urlEncode(String text) {
        try {
            return URLEncoder.encode(text, StandardCharsets.UTF_8.name());
        } catch ( Exception ex ) {
            throw new RuntimeException(ex);
        }
    }

    public static byte[] createFormData(Map<String, String> form) {
        if ( form == null || form.isEmpty() ) {
            return EMPTY_ARRAY;
        }

        TreeMap<String, String> sorted = new TreeMap<>(form);
        String text = sorted.entrySet().stream()
                .map(entry -> String.format("%s=%s", entry.getKey(), urlEncode(entry.getValue())))
                .collect(Collectors.joining("&"));
        return text.getBytes(StandardCharsets.UTF_8);
    }

    public static String createAuthorization(long timestamp, String appId, String secretKey, byte[] data) throws Exception {
        Objects.requireNonNull(appId);
        Objects.requireNonNull(secretKey);
        Objects.requireNonNull(data);

        String algorithm = HMAC_ALGORITHM;
        byte[] sign = sign(timestamp, appId, secretKey, data, algorithm);
        return String.format("%s;%d;%s;%s", algorithm, timestamp, appId, Base64.encodeBase64URLSafeString(sign));
    }

    public static String createAuthorization(long timestamp, String appId, String secretKey, String body) throws Exception {
        byte[] data = body == null ? EMPTY_ARRAY : body.getBytes(StandardCharsets.UTF_8);
        return createAuthorization(timestamp, appId, secretKey, data);
    }

    public static boolean verifyAuthorization(String appId, String secretKey, byte[] data, String authorization) throws Exception {
        if ( appId == null || secretKey == null || data == null || authorization == null ) {
            return false;
        }

        AuthorizationInfo info = parse(authorization);
        if ( info == null ) {
            log.warn("Invalid authorization format: authorization={}", authorization);
            return false;
        }

        if ( !appId.equals(info.appId) ) {
            log.warn("Appid mismatch: authorization={}, appid={}", authorization, appId);
            return false;
        }

        return info.verify(data, secretKey);
    }

    public static boolean verifyAuthorization(String appId, String secretKey, String body, String authorization) throws Exception {
        byte[] data = body == null ? EMPTY_ARRAY : body.getBytes(StandardCharsets.UTF_8);
        return verifyAuthorization(appId, secretKey, data, authorization);
    }

    private static byte[] sign(long timestamp, String appId, String secretKey, byte[] data, String algorithm) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);
        try {
            dout.writeLong(timestamp);
            dout.write(appId.getBytes(StandardCharsets.UTF_8));
            dout.write(data);
            dout.flush();
        } catch ( IOException ex ) {
            throw new RuntimeException(ex);
        }

        Mac mac = HmacUtils.getInitializedMac(algorithm, secretKey.getBytes(StandardCharsets.UTF_8));
        return mac.doFinal(bout.toByteArray());
    }

    public static AuthorizationInfo parse(String authorization) {
        String[] parts = authorization.split(";");
        if ( parts.length != 4 ) {
            return null;
        }

        AuthorizationInfo info = new AuthorizationInfo();
        info.setAuthorization(authorization);
        info.setAlgorithm(parts[0]);
        info.setAppId(parts[2]);
        info.setTimestamp(Long.parseLong(parts[1]));
        info.setSignature(Base64.decodeBase64(parts[3]));
        return info;
    }

    public static class AuthorizationInfo {
        private String appId;
        private long timestamp;
        private byte[] signature;
        private String algorithm;
        private String authorization;

        private AuthorizationInfo() {
        }

        public String getAppId() {
            return appId;
        }

        private void setAppId(String appId) {
            this.appId = appId;
        }

        public long getTimestamp() {
            return timestamp;
        }

        private void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public byte[] getSignature() {
            return signature;
        }

        private void setSignature(byte[] signature) {
            this.signature = signature;
        }

        public String getAlgorithm() {
            return algorithm;
        }

        private void setAlgorithm(String algorithm) {
            this.algorithm = algorithm;
        }

        public String getAuthorization() {
            return authorization;
        }

        private void setAuthorization(String authorization) {
            this.authorization = authorization;
        }

        public boolean verify(byte[] data, String secretKey) {
            long valid = 300 * 1000L;
            if ( SystemClock.now() - timestamp > valid ) {
                log.warn("Authorization expired: authorization={}", authorization);
                return false;
            }

            byte[] s = sign(timestamp, String.valueOf(appId), secretKey, data, algorithm);
            return Arrays.equals(s, signature);
        }
    }

}
