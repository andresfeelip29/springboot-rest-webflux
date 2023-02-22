package com.co.app.course.springwebflux.services;

import com.co.app.course.springwebflux.repositories.dao.constracts.ICategoryDao;
import com.co.app.course.springwebflux.repositories.dao.constracts.IProductDao;
import com.co.app.course.springwebflux.repositories.entites.documents.CategoryEntity;
import com.co.app.course.springwebflux.repositories.entites.documents.ProductEntity;
import com.co.app.course.springwebflux.services.constracts.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImpl implements IProductService {

	@Autowired
	private IProductDao dao;
	
	@Autowired
	private ICategoryDao ICategoryDao;
	
	@Override
	public Flux<ProductEntity> findAll() {
		return dao.findAll();
	}

	@Override
	public Mono<ProductEntity> findById(String id) {
		return dao.findById(id);
	}

	@Override
	public Mono<ProductEntity> save(ProductEntity productEntity) {
		return dao.save(productEntity);
	}

	@Override
	public Mono<Void> delete(ProductEntity productEntity) {
		return dao.delete(productEntity);
	}

	@Override
	public Flux<ProductEntity> findAllByNameUpperCase() {
		return dao.findAll().map(productEntity -> {
			productEntity.setName(productEntity.getName().toUpperCase());
			return productEntity;
		});
	}

	@Override
	public Flux<ProductEntity> findAllByNameUpperCaseRepeat() {
		return findAllByNameUpperCase().repeat(5000);
	}

	@Override
	public Flux<CategoryEntity> findAllCategories() {
		return ICategoryDao.findAll();
	}

	@Override
	public Mono<CategoryEntity> findCategoryById(String id) {
		return ICategoryDao.findById(id);
	}

	@Override
	public Mono<CategoryEntity> saveCategory(CategoryEntity categoryEntity) {
		return ICategoryDao.save(categoryEntity);
	}


}
