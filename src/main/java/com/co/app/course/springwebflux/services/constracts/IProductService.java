package com.co.app.course.springwebflux.services.constracts;


import com.co.app.course.springwebflux.repositories.entites.documents.CategoryEntity;
import com.co.app.course.springwebflux.repositories.entites.documents.ProductEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductService {
	
	public Flux<ProductEntity> findAll();
	
	public Flux<ProductEntity> findAllConNombreUpperCase();
	
	public Flux<ProductEntity> findAllConNombreUpperCaseRepeat();
	
	public Mono<ProductEntity> findById(String id);
	
	public Mono<ProductEntity> save(ProductEntity productEntity);
	
	public Mono<Void> delete(ProductEntity productEntity);
	
	public Flux<CategoryEntity> findAllCategoria();
	
	public Mono<CategoryEntity> findCategoriaById(String id);
	
	public Mono<CategoryEntity> saveCategoria(CategoryEntity categoryEntity);

}
