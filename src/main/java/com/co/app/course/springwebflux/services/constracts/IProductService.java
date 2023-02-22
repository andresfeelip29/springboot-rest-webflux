package com.co.app.course.springwebflux.services.constracts;


import com.co.app.course.springwebflux.repositories.entites.documents.CategoryEntity;
import com.co.app.course.springwebflux.repositories.entites.documents.ProductEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductService {
	
	public Flux<ProductEntity> findAll();
	
	public Flux<ProductEntity> findAllByNameUpperCase();
	
	public Flux<ProductEntity> findAllByNameUpperCaseRepeat();
	
	public Mono<ProductEntity> findById(String id);
	
	public Mono<ProductEntity> save(ProductEntity productEntity);
	
	public Mono<Void> delete(ProductEntity productEntity);
	
	public Flux<CategoryEntity> findAllCategories();

	public Mono<CategoryEntity> findCategoryById(String id);
	
	public Mono<CategoryEntity> saveCategory(CategoryEntity categoryEntity);

}
