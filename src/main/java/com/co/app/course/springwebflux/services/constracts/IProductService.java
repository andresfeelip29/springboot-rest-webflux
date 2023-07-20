package com.co.app.course.springwebflux.services.constracts;


import com.co.app.course.springwebflux.repositories.entites.documents.CategoryEntity;
import com.co.app.course.springwebflux.repositories.entites.documents.ProductEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductService {

    Flux<ProductEntity> findAll();

    Flux<ProductEntity> findAllByNameUpperCase();

    Flux<ProductEntity> findAllByNameUpperCaseRepeat();

    Mono<ProductEntity> findById(String id);

    Mono<ProductEntity> save(ProductEntity productEntity);

    Mono<Void> delete(ProductEntity productEntity);

    Flux<CategoryEntity> findAllCategories();

    Mono<CategoryEntity> findCategoryById(String id);

    Mono<CategoryEntity> saveCategory(CategoryEntity categoryEntity);

    Mono<ProductEntity> findByName(String name);

    Mono<CategoryEntity> findCategoryByName(String name);

}
