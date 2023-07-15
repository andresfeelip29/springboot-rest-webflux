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


    private final IProductDao productDao;

    private final ICategoryDao categoryDao;

    @Autowired
    public ProductServiceImpl(IProductDao productDao, ICategoryDao categoryDao) {
        this.productDao = productDao;
        this.categoryDao = categoryDao;
    }

    @Override
    public Flux<ProductEntity> findAll() {
        return productDao.findAll();
    }

    @Override
    public Mono<ProductEntity> findById(String id) {
        return productDao.findById(id);
    }

    @Override
    public Mono<ProductEntity> save(ProductEntity productEntity) {
        return productDao.save(productEntity);
    }

    @Override
    public Mono<Void> delete(ProductEntity productEntity) {
        return productDao.delete(productEntity);
    }

    @Override
    public Flux<ProductEntity> findAllByNameUpperCase() {
        return productDao.findAll().map(productEntity -> {
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
        return categoryDao.findAll();
    }

    @Override
    public Mono<CategoryEntity> findCategoryById(String id) {
        return categoryDao.findById(id);
    }

    @Override
    public Mono<CategoryEntity> saveCategory(CategoryEntity categoryEntity) {
        return categoryDao.save(categoryEntity);
    }


}
