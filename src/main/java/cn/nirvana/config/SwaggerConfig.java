package cn.nirvana.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashSet;
import java.util.Set;

/**
 * @author
 * @date
 */
@EnableSwagger2
@Configuration
public class SwaggerConfig {

    @Value("${swagger.api.url}")
    private String url;

    @Bean
    public Docket createRestApi() {
        Set<String> produces = new HashSet<>();
        produces.add("application/json");

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .produces(produces)
                .host(url)
                .select()
                .paths(PathSelectors.any())
                .apis(RequestHandlerSelectors.basePackage("cn.nirvana.controller"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("企加云后台api文档")
                .description("企加云后台api文档")
                .license("license by qijiayun.com")
                .version("1.0.0")
                .build();
    }
}
