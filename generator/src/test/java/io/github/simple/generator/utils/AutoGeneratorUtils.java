package io.github.simple.generator.utils;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.junit.Test;


import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * @author Simple
 * @date 2021/3/30 9:55
 */
// 代码自动生成器
public class AutoGeneratorUtils {

    @Test
    public void testGenerator() {

        final ResourceBundle rb = ResourceBundle.getBundle("mybatis-plus-generator");
        // 需要构建一个 代码自动生成器 对象
        AutoGenerator mpg = new AutoGenerator();
        // 配置策略

        // 1、全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + rb.getString("OutputDir"));
        gc.setAuthor(rb.getString("Author"));
        gc.setOpen(false);
        gc.setFileOverride(false);  // 是否覆盖
        gc.setServiceName("%sSerive"); // 服务接口，去Service的I前缀
        gc.setIdType(IdType.AUTO); // 主键生成策略
        gc.setDateType(DateType.ONLY_DATE);
        gc.setSwagger2(true);

        // 给代码自动生成器注入配置
        mpg.setGlobalConfig(gc);

        // 2、 设置数据源
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(rb.getString("db.url"));
        dsc.setDriverName(rb.getString("db.drivername"));
        dsc.setUsername(rb.getString("db.username"));
        dsc.setPassword(rb.getString("db.password"));
        dsc.setDbType(DbType.MYSQL);
        mpg.setDataSource(dsc);

        // 3、包的配置

        PackageConfig pc = new PackageConfig();
        pc.setModuleName(rb.getString("ModuleName"));
        pc.setParent(rb.getString("Parent"));
        pc.setEntity("entity");
        pc.setMapper("mapper");
        pc.setService("service");
        pc.setController("controller");

        mpg.setPackageInfo(pc);

        // 4、策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setInclude(rb.getString("Include01"),rb.getString("Include02")); // 设置要映射的表名
        strategy.setNaming(NamingStrategy.underline_to_camel);  // 内置下划线转驼峰命名
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityLombokModel(true);  // 自动Lombok
//        strategy.setLogicDeleteFieldName("deleted");  // 逻辑删除字段

//        // 自动填充策略
//        TableFill gmtCreate = new TableFill("gmt_create", FieldFill.INSERT);
//        TableFill gmtModifid = new TableFill("gmt_modifid", FieldFill.INSERT);
//
//        ArrayList<TableFill> tableFills = new ArrayList<>();
//        tableFills.add(gmtCreate);
//        tableFills.add(gmtModifid);
//        strategy.setTableFillList(tableFills);
//
//        //乐观锁
//        strategy.setVersionFieldName("version");
//        strategy.setRestControllerStyle(true);
//        strategy.setControllerMappingHyphenStyle(true);
        mpg.setStrategy(strategy);

        // 执行
        mpg.execute();
    }
}