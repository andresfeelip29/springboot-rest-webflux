package com.co.app.course.springwebflux.repositories.dao.constracts;


import com.co.app.course.springwebflux.repositories.entites.documents.ProductEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface IProductDao extends ReactiveMongoRepository<ProductEntity, String> {

}
