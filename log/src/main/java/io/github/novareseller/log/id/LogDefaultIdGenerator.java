package io.github.novareseller.log.id;


import io.github.novareseller.tool.utils.UniqueIdGenerator;

/**
 *
 * @author bowen
 * @date 2021/05/05
 */
public class LogDefaultIdGenerator implements LogIdGenerator {
    @Override
    public String generateTraceId() {
        return UniqueIdGenerator.generateStringId();
    }
}
