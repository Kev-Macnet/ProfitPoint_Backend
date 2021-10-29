package tw.com.leadtek.nhiwidget.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.async.DeferredResult;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
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
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()))
                .enable(isEnable);
    }

    private ApiInfo innerApiInfo() {
        return new ApiInfoBuilder()
                .title("健保申保最佳化系統 API") //大標題
                //.description("RESTful APIs") //描述
                .version("0.0.2")  //版本
                .build();
    }
    
    private ApiKey apiKey() {
      return new ApiKey("JWT", "Authorization", "header");
    }

    private SecurityContext securityContext() {
      return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }

    private List<SecurityReference> defaultAuth() {
      AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
      AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
      authorizationScopes[0] = authorizationScope;
      return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
    }

    private List<RequestParameter> authorizationParameter() {
      List<RequestParameter> result = new ArrayList<RequestParameter>();
      RequestParameterBuilder tokenBuilder = new RequestParameterBuilder();
      tokenBuilder.name("Authorization").description("JWT").required(false).in("header")
          .accepts(Collections.singleton(MediaType.APPLICATION_JSON)).build();
      result.add(tokenBuilder.build());
      return result;
    }
}

