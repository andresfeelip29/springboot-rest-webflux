package com.co.app.course.springwebflux;

import com.co.app.course.springwebflux.repositories.entites.documents.CategoryEntity;
import com.co.app.course.springwebflux.repositories.entites.documents.ProductEntity;
import com.co.app.course.springwebflux.services.constracts.IProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;

import java.util.Date;

@SpringBootApplication
public class SpringbootRestWebFluxApplication implements CommandLineRunner {

	@Autowired
	private IProductService service;

	@Autowired
	private ReactiveMongoTemplate mongoTemplate;

	private static final Logger log = LoggerFactory.getLogger(SpringbootRestWebFluxApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringbootRestWebFluxApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		mongoTemplate.dropCollection("products").subscribe();
		mongoTemplate.dropCollection("categories").subscribe();

		CategoryEntity electronico = new CategoryEntity("Electrónico");
		CategoryEntity deporte = new CategoryEntity("Deporte");
		CategoryEntity computacion = new CategoryEntity("Computación");
		CategoryEntity muebles = new CategoryEntity("Muebles");

		Flux.just(electronico, deporte, computacion, muebles)
				.flatMap(service::saveCategoria)
				.doOnNext(c ->{
					log.info("Categoria creada: " + c.getName() + ", Id: " + c.getId());
				}).thenMany(
						Flux.just(new ProductEntity("TV Panasonic Pantalla LCD", 456.89, electronico),
										new ProductEntity("Sony Camara HD Digital", 177.89, electronico),
										new ProductEntity("Apple iPod", 46.89, electronico),
										new ProductEntity("Sony Notebook", 846.89, computacion),
										new ProductEntity("Hewlett Packard Multifuncional", 200.89, computacion),
										new ProductEntity("Bianchi Bicicleta", 70.89, deporte),
										new ProductEntity("HP Notebook Omen 17", 2500.89, computacion),
										new ProductEntity("Mica Cómoda 5 Cajones", 150.89, muebles),
										new ProductEntity("TV Sony Bravia OLED 4K Ultra HD", 2255.89, electronico)
								)
								.flatMap(productEntity -> {
									productEntity.setCreateAt(new Date());
									return service.save(productEntity);
								})
				)
				.subscribe(productEntity -> log.info("Insert: " + productEntity.getId() + " " + productEntity.getName()));

	}
}
