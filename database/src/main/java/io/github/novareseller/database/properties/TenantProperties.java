package io.github.novareseller.database.properties;

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
@ConfigurationProperties(prefix = "spring.dagger.database")
public class TenantProperties {

    private List<String> ignoreTables;
}
