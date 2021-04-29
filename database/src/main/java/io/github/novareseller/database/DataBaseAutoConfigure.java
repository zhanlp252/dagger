package io.github.novareseller.database;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import io.github.novareseller.database.properties.TenantProperties;
import io.github.novareseller.database.support.SqlLogInterceptor;
import io.github.novareseller.security.context.SecurityContext;
import io.github.novareseller.security.utils.Validator;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * @author: Bowen huang
 * @date: 2021/03/18
 */
@EnableTransactionManagement
@Configuration
@AutoConfigureBefore(value={DruidDataSourceAutoConfigure.class, MybatisPlusAutoConfiguration.class})
@MapperScan(value = {"com.valor.panel.*.dao", "io.github.novareseller.*.dao", "xyz.novareseller.*.dao"})
@EnableConfigurationProperties({TenantProperties.class})
public class DataBaseAutoConfigure {

    @Autowired
    private TenantProperties tenantProperties;

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource druidDataSource() {
        return new DruidDataSource();
    }

    /**
     * sql 日志
     *
     * @return SqlLogInterceptor
     */
    @Bean
    @ConditionalOnProperty(value = "spring.dagger.database.sql-log", havingValue = "true")
    public SqlLogInterceptor sqlLogInterceptor() {
        return new SqlLogInterceptor();
    }


    @Bean
    @ConditionalOnClass(MetaObjectHandler.class)
    public DbMetaObjectHandler metaObjectHandler() {
        return new DbMetaObjectHandler();
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                long tenantId = SecurityContext.getLoginUser().getTenantId();
                if (tenantId == 0) {
                    return new NullValue();
                }
                return new LongValue(tenantId);
            }

            @Override
            public boolean ignoreTable(String tableName) {
                if (Validator.isNullOrEmpty(tenantProperties) ||
                        Validator.isNullOrEmpty(tenantProperties.getIgnoreTables())) {
                    return false;
                }
                return tenantProperties.getIgnoreTables().stream().anyMatch((t) -> t.equalsIgnoreCase(tableName));
            }
        }));

        BlockAttackInnerInterceptor blockAttackInnerInterceptor = new BlockAttackInnerInterceptor();
        interceptor.addInnerInterceptor(blockAttackInnerInterceptor);

        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));

        return interceptor;
    }

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> configuration.setUseDeprecatedExecutor(false);
    }



}
