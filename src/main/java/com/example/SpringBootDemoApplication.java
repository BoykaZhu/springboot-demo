package com.example;

import com.example.annotation.EnableStorage;
import com.example.authentication.AuthenticationTokenProvider;
import com.example.bean.Car;
import com.example.bean.House;
import com.example.bean.SimpleObject;
import com.example.bean.User;
import com.example.config.ParentImportConfigure;
import com.example.handler.AccessDeniedHandler;
import com.example.security.UserSecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDateTime;
import java.util.Date;


@SpringBootApplication
@EnableSwagger2
@ComponentScan(basePackages = "com.*")
@EntityScan(basePackages = "com.example.entity")
@EnableJpaRepositories(basePackages = "com.example.repository")
@EnableWebSecurity
@EnableTransactionManagement
/*扫描 @WebServlet @WebFilter @WebListener*/
@ServletComponentScan
@EnableStorage
public class SpringBootDemoApplication extends WebSecurityConfigurerAdapter {

    private static Logger logger = LoggerFactory.getLogger(SpringBootDemoApplication.class);

    @Autowired
    private AuthenticationTokenProvider authenticationTokenProvider;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SpringBootDemoApplication.class, args);

        /*
         * 使用AnnotationConfigApplicationContext可以实现基于Java的配置类加载Spring的应用上下文。
         * 避免使用application.xml进行配置。相比XML配置，更加便捷。
         */
        AnnotationConfigApplicationContext configApplicationContext = new AnnotationConfigApplicationContext(ParentImportConfigure.class);

        /*
         * ApplicationContext容器 使用 ClassPathXmlApplicationContext 类来加载外部 XML 上下文文件加载beans.xml
         */
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");

        //通过容器获取配置的javabean
        User user = (User) context.getBean("user");
        System.out.println(user);
        User user2 = (User) context.getBean("user2");
        System.out.println(user2);

        Car car = (Car) configApplicationContext.getBean("car");
        logger.info("---->车牌号:" + car.getNumber());
        House house = (House) configApplicationContext.getBean("house");
        logger.info("---->房子面积:" + house.getArea());
        SimpleObject simpleObject = (SimpleObject) configApplicationContext.getBean("simpleObject");
        logger.info("---->对象注入数据结果：" + simpleObject.toString());

    }

    /**
     * Swagger组件注册
     *
     * @return
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build()
                .directModelSubstitute(LocalDateTime.class, Date.class);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Spring Boot For JPA API V1.0")
                .description("")
                .version("1.0")
                .build();
    }

    /**
     * .permitAll表示该请求任何人都可以访问，
     * .anyRequest().authenticated(),表示其他的请求都必须要有权限认证。
     *
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .exceptionHandling()
                //处理认证权限异常
                .authenticationEntryPoint(authenticationEntryPoint())
                .and()
                .authorizeRequests() //通过 authorizeRequests() 定义哪些URL需要被保护、哪些不需要被保护
                .antMatchers("/swagger-ui/**").permitAll()
                .antMatchers("/doc.html").permitAll()
                .antMatchers("/api/**").permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserService());
        auth.authenticationProvider(this.authenticationTokenProvider);
    }

    @Bean
    protected UserDetailsService customUserService() {
        return new UserSecurityService();
    }

    @Bean
    protected AuthenticationEntryPoint authenticationEntryPoint() {
        return new AccessDeniedHandler();
    }
}
