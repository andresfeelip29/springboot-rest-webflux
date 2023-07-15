package com.co.app.course.springwebflux.controllers;


import com.co.app.course.springwebflux.repositories.entites.documents.ProductEntity;
import com.co.app.course.springwebflux.services.constracts.IProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/product")
public class ProductController {


    private final IProductService productService;

    @Autowired
    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @Value("${config.uploads.path}")
    private String path;

    @PostMapping("/createWithPhoto")
    public Mono<ResponseEntity<ProductEntity>> createProductWithPhoto(ProductEntity product, @RequestPart FilePart file) {
        if (product.getCreateAt() == null) product.setCreateAt(new Date());
        product.setPhoto(UUID.randomUUID().toString() + "-" + file.filename()
                .replace(" ", "")
                .replace(":", "")
                .replace("\\", ""));

        return file.transferTo(new File(path + product.getPhoto()))
                .then(this.productService.save(product))
                .map(productEntity -> ResponseEntity.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(productEntity));
    }

    @PostMapping("/upload/{id}")
    public Mono<ResponseEntity<ProductEntity>> uploadPhoto(@PathVariable String id, @RequestPart FilePart file) {

        return productService.findById(id).flatMap(productEntity -> {
                    productEntity.setPhoto(UUID.randomUUID().toString() + "-" + file.filename()
                            .replace(" ", "")
                            .replace(":", "")
                            .replace("\\", ""));
                    return file.transferTo(new File(path + productEntity.getPhoto()))
                            .then(this.productService.save(productEntity));
                }).map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping
    public Flux<ProductEntity> getAllProducts() {
        return productService.findAll();
    }

    @GetMapping("/Response")
    public Mono<ResponseEntity<Flux<ProductEntity>>> listWithResponsoBody() {
        return Mono.just(
                ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(productService.findAll())
        );
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProductEntity>> getDetailProduct(@PathVariable String id) {
        return productService.findById(id)
                .map(product -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(product))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }

    @PostMapping("/create")
    public Mono<ResponseEntity<Map<String, Object>>> createProduct(@Valid @RequestBody Mono<ProductEntity> productRequest) {

        Map<String, Object> response = new HashMap<>();

        return productRequest.flatMap(product -> {
            if (product.getCreateAt() == null) product.setCreateAt(new Date());
            return productService.save(product).map(productEntity -> {
                response.put("data", product);
                response.put("message", "product successfully created");
                response.put("status", HttpStatus.CREATED.value());
                response.put("timestamp", LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response);
            });

        }).onErrorResume(err -> {
            return Mono.just(err).cast(WebExchangeBindException.class)
                    .flatMap(e -> Mono.just(e.getFieldErrors()))
                    .flatMapMany(errs -> Flux.fromIterable(errs))
                    .map(fieldError -> "The field: " + fieldError.getField() + " " + fieldError.getDefaultMessage())
                    .collectList()
                    .flatMap(rta -> {
                        response.put("erros", rta);
                        response.put("message", "error creating product");
                        response.put("status", HttpStatus.BAD_REQUEST.value());
                        response.put("timestamp", LocalDateTime.now());
                        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(response));
                    });
        });


    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ProductEntity>> updateProduct(@RequestBody ProductEntity productEntity, @PathVariable String id) {
        return productService.findById(id).flatMap(product -> {
                    product.setName(productEntity.getName());
                    product.setPrice(productEntity.getPrice());
                    product.setCategory(productEntity.getCategory());
                    return productService.save(product);
                })
                .map(product -> ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(product))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable String id) {
        return productService.findById(id)
                .flatMap(productEntity -> productService.delete(productEntity).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
                .defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));

    }

}
