package com.co.app.course.springwebflux.handler;

import com.co.app.course.springwebflux.repositories.entites.documents.ProductEntity;
import com.co.app.course.springwebflux.services.constracts.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Date;

@Component
public class ProductHandler {

    @Autowired
    private IProductService productService;

    public Mono<ServerResponse> listProducts(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(productService.findAll(), ProductEntity.class);
    }

    public Mono<ServerResponse> detailProduct(ServerRequest request) {
        String id = request.pathVariable("id");
        return productService.findById(id)
                .flatMap(productEntity -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(productEntity)
                ).switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).build());
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        Mono<ProductEntity> productEntityMono = request.bodyToMono(ProductEntity.class);
        return productEntityMono
                .flatMap(productEntity -> {
                    if (productEntity.getCreateAt() == null) productEntity.setCreateAt(new Date());
                    return productService.save(productEntity);
                }).flatMap(productEntity ->
                        ServerResponse.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).bodyValue(productEntity));
    }

}
