package com.co.app.course.springwebflux;


import com.co.app.course.springwebflux.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


@Configuration
public class RouterFunctionConfig {

    @Bean
    public RouterFunction<ServerResponse> routes(ProductHandler handler) {
        return RouterFunctions.route(RequestPredicates.GET("api/v2/product")
                        .or(RequestPredicates.GET("api/v3/product")), handler::listProducts)
                .andRoute(RequestPredicates.GET("api/v2/product/{id}"), handler::detailProduct)
                .andRoute(RequestPredicates.POST("api/v2/product/create"), handler::create)
                .andRoute(RequestPredicates.PUT("api/v2/product/{id}"), handler::update)
                .andRoute(RequestPredicates.DELETE("api/v2/product/{id}"), handler::delete)
                .andRoute(RequestPredicates.POST("api/v2/product/upload/{id}"), handler::uploadImage)
                .andRoute(RequestPredicates.POST("api/v2/product/createWithPhoto"), handler::createWithPhoto);
    }
}
