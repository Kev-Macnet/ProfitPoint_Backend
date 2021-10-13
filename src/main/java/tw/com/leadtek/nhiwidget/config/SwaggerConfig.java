package tw.com.leadtek.nhiwidget.config;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResult;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@EnableOpenApi
public class SwaggerConfig<RequestHandler> {
    private boolean isEnable = true;
    
    @Bean
    public Docket createBaseRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("健保申保最佳化系統 API")
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("tw.com.leadtek.nhiwidget.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(innerApiInfo())
                .enable(isEnable);
    }

    private ApiInfo innerApiInfo() {
        return new ApiInfoBuilder()
                .title("健保申保最佳化系統 API") //大標題
                //.description("RESTful APIs") //描述
                .version("0.0.2")  //版本
                .build();
    }
    
}

