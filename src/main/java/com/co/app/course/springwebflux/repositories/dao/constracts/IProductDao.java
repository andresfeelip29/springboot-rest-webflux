package com.co.app.course.springwebflux.repositories.dao.constracts;


import com.co.app.course.springwebflux.repositories.entites.documents.ProductEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Mono;

public interface IProductDao extends ReactiveMongoRepository<ProductEntity, String> {
    Mono<ProductEntity> findByName(String name);

    @Query("{ 'name': name }")
    Mono<ProductEntity> getProductEntityByName(@Param("name") String name);

}
