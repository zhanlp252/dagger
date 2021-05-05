package io.github.novareseller.log.id;

/**
 *
 * @author bowen
 * @date 2021/05/05
 */
public class LogIdGeneratorLoader {

    private static LogIdGenerator idGenerator = new LogDefaultIdGenerator();

    public static LogIdGenerator getIdGenerator() {
        return idGenerator;
    }

    public static void setIdGenerator(LogIdGenerator idGenerator) {
        LogIdGeneratorLoader.idGenerator = idGenerator;
    }
}
