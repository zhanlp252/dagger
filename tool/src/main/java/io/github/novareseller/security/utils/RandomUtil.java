package io.github.novareseller.security.utils;

import io.github.novareseller.security.text.StringPool;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author: Bowen huang
 * @date: 2021/03/18
 */
public class RandomUtil {

    public static String randomUUID() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new UUID(random.nextLong(), random.nextLong()).toString().replace(StringPool.DASH, StringPool.EMPTY);
    }

}