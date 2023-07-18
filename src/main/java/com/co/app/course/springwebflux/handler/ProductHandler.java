package com.co.app.course.springwebflux.handler;

import com.co.app.course.springwebflux.repositories.entites.documents.CategoryEntity;
import com.co.app.course.springwebflux.repositories.entites.documents.ProductEntity;
import com.co.app.course.springwebflux.services.constracts.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.UUID;

@Component
public class ProductHandler {


    private final IProductService productService;

    private final Validator validator;

    @Value("${config.uploads.path}")
    private String path;

    @Autowired
    public ProductHandler(IProductService productService, Validator validator) {
        this.productService = productService;
        this.validator = validator;
    }


    public Mono<ServerResponse> createWithPhoto(ServerRequest request) {

        Mono<ProductEntity> productEntityMono = request.multipartData().map(muliPart -> {
            FormFieldPart name = (FormFieldPart) muliPart.toSingleValueMap().get("name");
            FormFieldPart price = (FormFieldPart) muliPart.toSingleValueMap().get("price");
            FormFieldPart categoryName = (FormFieldPart) muliPart.toSingleValueMap().get("category.name");
            FormFieldPart categoryId = (FormFieldPart) muliPart.toSingleValueMap().get("category.id");

            CategoryEntity category = new CategoryEntity(categoryName.value());
            category.setId(categoryId.value());

            return new ProductEntity(name.value(), Double.parseDouble(price.value()), category);
        });

        return request.multipartData()
                .map(partMultiValueMap -> partMultiValueMap.toSingleValueMap().get("file"))
                .cast(FilePart.class)
                .flatMap(filePart -> productEntityMono
                        .flatMap(productEntity -> {
                            productEntity.setPhoto(
                                    UUID.randomUUID().toString()
                                            .concat("-")
                                            .concat(filePart.filename()
                                                    .replace(" ", "")
                                                    .replace(":", "")
                                                    .replace("\\", ""))
                            );
                            productEntity.setCreateAt(new Date());
                            return filePart.transferTo(new File(path.concat(productEntity.getPhoto())))
                                    .then(this.productService.save(productEntity));
                        }))
                .flatMap(productEntity ->
                        ServerResponse.created(URI.create("/api/v2/product/".concat(productEntity.getId())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(BodyInserters.fromValue(productEntity)));
    }

    public Mono<ServerResponse> uploadImage(ServerRequest request) {
        String id = request.pathVariable("id");
        return request.multipartData()
                .map(partMultiValueMap -> partMultiValueMap.toSingleValueMap().get("file"))
                .cast(FilePart.class)
                .flatMap(filePart -> this.productService.findById(id)
                        .flatMap(productEntity -> {
                            productEntity.setPhoto(
                                    UUID.randomUUID().toString()
                                            .concat("-")
                                            .concat(filePart.filename()
                                                    .replace(" ", "")
                                                    .replace(":", "")
                                                    .replace("\\", ""))
                            );
                            return filePart.transferTo(new File(path.concat(productEntity.getPhoto())))
                                    .then(this.productService.save(productEntity));
                        }))
                .flatMap(productEntity ->
                        ServerResponse.created(URI.create("/api/v2/product/".concat(productEntity.getId())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(BodyInserters.fromValue(productEntity)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> listProducts(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(productService.findAll(), ProductEntity.class);
    }

    public Mono<ServerResponse> detailProduct(ServerRequest request) {
        String id = request.pathVariable("id");
        return productService.findById(id)
                .flatMap(productEntity -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(productEntity))
                ).switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).build());
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        Mono<ProductEntity> productEntityMono = request.bodyToMono(ProductEntity.class);
        return productEntityMono
                .flatMap(productEntity -> {
                    Errors errors = new BeanPropertyBindingResult(productEntity, ProductEntity.class.getName());
                    this.validator.validate(productEntity, errors);
                    if (errors.hasErrors()) {
                        return Flux.fromIterable(errors.getFieldErrors())
                                .map(fieldError -> "field".concat(fieldError.getField()).concat(" ").concat(fieldError.getDefaultMessage()))
                                .collectList()
                                .flatMap(list -> ServerResponse.badRequest().body(BodyInserters.fromValue(list)));
                    } else {
                        if (productEntity.getCreateAt() == null) productEntity.setCreateAt(new Date());

                    }
                    return productService.save(productEntity)
                            .flatMap(productEntityPersis ->
                                    ServerResponse.status(HttpStatus.CREATED)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .bodyValue(productEntityPersis));
                });
    }

    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        Mono<ProductEntity> product = serverRequest.bodyToMono(ProductEntity.class);
        String id = serverRequest.pathVariable("id");
        return productService.findById(id)
                .zipWith(product, (productPersis, productRequest) -> {
                    productPersis.setName(productRequest.getName());
                    productPersis.setPrice(productRequest.getPrice());
                    productPersis.setCategory(productPersis.getCategory());
                    return productPersis;
                })
                .flatMap(productEntity -> ServerResponse.created(URI.create("/api/v2/product/".concat(productEntity.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(this.productService.save(productEntity), ProductEntity.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        return productService.findById(serverRequest.pathVariable("id"))
                .flatMap(productEntity -> this.productService.delete(productEntity)
                        .then(ServerResponse.noContent().build()))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
