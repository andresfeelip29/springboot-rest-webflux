package com.co.app.course.springwebflux.repositories.dao.constracts;


import com.co.app.course.springwebflux.repositories.entites.documents.CategoryEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ICategoryDao extends ReactiveMongoRepository<CategoryEntity, String> {
    Mono<CategoryEntity> findByName(String name);
}
