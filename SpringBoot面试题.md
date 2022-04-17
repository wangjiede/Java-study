1. 什么是Springboot?

   Springboot是Spring开源组织下的子项目，是spring一站式解决方案，主要是简化了使用spring的难度，简省了繁重的xml配置，提供了各种启动器，在运行过程中自定配置，开发者能快速上手。

2. 为什么要用SpringBoot？

   SpringBoot有点比较多，如：简化配置、独立运行、自动配置、无需部署war文件。

3. SpringBoot核心配置文件有几个？他们的区别是什么？

   - application配置文件主要用于Springboot项目的自动化配置。
   - bootstrap配置文件使用场景：
     1. 使用SpringCloudConfig配置中心时，需要通过bootstrap配置文件中配置连接配置中心的属性来连接到配置中心，来加载外部配置信息
     2. 一些固定不能被覆盖的属性
     3. 一些加密/解密的场景

4. SpringBoot配置文件有哪几种格式？

   主要有yml和properties，yml格式的层级比较分明，并且不能支持@PropertySource注解导入。

5. SpringBoot核心注解是哪个？主要由那几个注解构成？

   核心注解是启动类上面的@SpringBootApplication，主要包含以下三个注解：

   - SpringBootConfiguration:组合了 @Configuration 注解，实现配置文件的功能
   - @EnableAutoConfiguration：打开自动配置的功能，也可以关闭某个自动配置的选项，如关闭数据源自动配置功能
   - ComponentScan：Spring组件烧苗

6. 开启SpringBoot特性有哪几种方式？

   - 继承spring-boot-starter-parent

     ```java
     <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.5.6.RELEASE</version>
     </parent>
     ```

   - 导入spring-boot-dependencies项目依赖

     ```java
     <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>1.5.6.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
     </dependencyManagement>
     ```

7. Springboot需要独立的容器运行吗？

   不需要，Springboot内嵌了tomcat、jetty等容器。、

8. 运行Springboot有哪几种方式？

   - 打包用命令或者放到容器中运行
   - 使用maven/gradle运行
   - 直接运行SpringBoot启动类的main方法

9. SpringBoot自动配置原理是什么？

   注解 @EnableAutoConfiguration, @Configuration, @ConditionalOnClass 就是自动配置的核心，首先它得是一个配置文件，其次根据类路径下是否有这个类去自动配置。@EnableAutoConfiguration注解会自动加载类路径及所有jar包下的spring.factories配置中映射的自动配置类。

10. 如何理解SpringBoot中starters?

    Starters可以理解为启动器，它包含了一系列可以集成到应用里面的依赖包，你可以一站式集成 Spring 及其他技术，而不需要到处找示例代码和依赖包。如你想使用 Spring JPA 访问数据库，只要加入 spring-boot-starter-data-jpa 启动器依赖就能使用了。

11. 如何在SpringBoot启动时运行一些特定的代码？

    可以实现接口 ApplicationRunner 或者 CommandLineRunner，这两个接口实现方式一样，它们都只提供了一个 run 方法。

12. Springboot有哪几种读取配置文件的方式？

    @PropertySource、@Value、@ConfigurationProperties、Environment类对象。

13. Springboot支持哪些日志框架？推荐和默认的日志框架是哪个？

    springboot支持logging、log4j、logback日志框架，默认使用logback框架，spring有日志启动器spring-boot-starter-logging启动器，默认集成了log4j和logback框架。

14. springboot如何定义多套不同的环境？

    application-dev.properties
    application-test.properties
    application-prod.properties

15. Springboot可以兼容老Spring项目吗？

    可以的，老的Spring项目配置文件可以使用@ImportResource注解加载。