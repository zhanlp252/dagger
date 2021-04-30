# Welcome to dagger



## 简介

> dagger项目是一个快速开发spring-boot web项目的脚手架，
>
> 包含bool-all、cache、database、log、oss、security、tool等模块。
>
> 其中database模块设计了支持多租户模式。



## Boot-all模块

用途：是一个父模块，包含整个dagger的其他子模块。只需要引入boot-all模块即可使用到dagger项目其他模块的方法。

使用方法：

```xml
        <dependency>
            <groupId>io.github.novareseller</groupId>
            <artifactId>boot-all</artifactId>
            <version>1.0.1.alpha</version>
        </dependency>
```



## Security模块

用途：使用@EnableDaggerJwt注解，开启用户的鉴权功能。
涉及的技术：JWT 、自定义注解

使用方法：

```java

 
 步骤 1, 在@SpringBootApplication带注释的类上打开@EnableDaggerJwt注释。
  例:
 
  @SpringBootApplication
  @EnableDaggerJwt
  public class ExampleApplication {
 
      public static void main(String[] args) {
          SpringApplication.run(ExampleApplication.class, args);
      }
 
  }
 
 
 步骤 2, 将JwtRegisterBean注入到需要使用的类中。
  例:
 
  @RestController
  @RequestMapping("/security/")
  public class TestController {
 
  	@Autowired
  	private JwtRegisterBean jwtRegisterBean;
 
   @RequestMapping("login")
   public String login(@RequestParam(defaultValue = "101") long tenantId,
   @RequestParam(defaultValue = "100001") long uid){
           String token=jwtRegisterBean.createToken(tenantId,uid);
           return token;
   }
```



## Database模块

用途：自定义自动配置数据库信息，在spring-boot的application.properties/yaml配置文件中配置。

涉及的技术：mybatis-plus，自定义自动配置，mybatis-plus拦截器（多租户）

使用方法：

```yaml
例1.
spring:
  dagger:
    database:
      sql-log: true     #开启打印sql语句日志


例2.
spring:
  dagger:
    database:
      ignoreTables: 表名   #忽略拼接sql语句的表

```





## Tool模块

用途：统一处理集合、字符串、日期等公共工具方法。

涉及的技术：基础的java jdk方法

使用方法：

```
例1
//直接使用StringUtil类下的静态方法humpToUnderline来处理获取数据库的字段

    /**
     * 获取数据库字段
     * 驼峰转下划线
     * @param column  字段名
     * @param keyword 关键字
     * @return String
     */
    private static String getColumn(String column, String keyword) {

        return StringUtil.humpToUnderline(StringUtil.removeSuffix(column, keyword));
    }
```



## cache模块

..............待定.............

## Log模块

..............待定.............


## Oss模块

..............待定.............


