package com.co.app.course.springwebflux.repositories.dao.constracts;


import com.co.app.course.springwebflux.repositories.entites.documents.CategoryEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ICategoryDao extends ReactiveMongoRepository<CategoryEntity, String> {

}
