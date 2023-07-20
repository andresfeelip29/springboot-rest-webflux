package com.co.app.course.springwebflux;

import com.co.app.course.springwebflux.repositories.entites.documents.CategoryEntity;
import com.co.app.course.springwebflux.repositories.entites.documents.ProductEntity;
import com.co.app.course.springwebflux.services.ProductServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureWebTestClient
class SpringbootRestWebFluxApplicationTests {

    private final WebTestClient webTestClient;

    private final ProductServiceImpl productService;

    @Value("${config.base.endpoint}")
    private String URL;


    @Autowired
    public SpringbootRestWebFluxApplicationTests(WebTestClient webTestClient, ProductServiceImpl productService) {
        this.webTestClient = webTestClient;
        this.productService = productService;
    }

    @Test
    void listTest() {
        webTestClient.get()
                .uri(URL)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(ProductEntity.class)
                .consumeWith(response -> {
                    List<ProductEntity> productEntityList = response.getResponseBody();
                    productEntityList.forEach(productEntity -> {
                        System.out.println(productEntity);
                    });
                    Assertions.assertTrue(productEntityList.size() > 0);
                });
        //.hasSize(9);
    }

    @Test
    void detailProductTest() {

        //Get product by any name for the get id;
        ProductEntity productEntityMono = this.productService.findByName("TV Sony Bravia OLED 4K Ultra HD").block();

        assert productEntityMono != null;
        webTestClient.get()
                .uri(URL.concat("/{id}"), Collections.singletonMap("id", productEntityMono.getId()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("TV Sony Bravia OLED 4K Ultra HD");
    }

    @Test
    void saveProductEntityTest() {

        CategoryEntity category = this.productService.findCategoryByName("Electrónico").block();
        ProductEntity productCorrect = new ProductEntity("Iphone 14 pro max", 4500000.00, category);

        webTestClient.post()
                .uri(URL.concat("/create"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(productCorrect), ProductEntity.class)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.CREATED)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("Iphone 14 pro max")
                .jsonPath("$.category.name").isEqualTo("Electrónico");
    }

    @Test
    void saveV1ProductEntityTest() {

        CategoryEntity category = this.productService.findCategoryByName("Electrónico").block();
        ProductEntity productCorrect = new ProductEntity("Iphone 14 pro max", 4500000.00, category);

        webTestClient.post()
                .uri(URL.concat("/create"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(productCorrect), ProductEntity.class)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.CREATED)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<LinkedHashMap<String, Object>>() {
                })
                .consumeWith(response -> {
                    Object o = response.getResponseBody().get("data");
                    ProductEntity product = new ObjectMapper().convertValue(o, ProductEntity.class);
                    Assertions.assertNull(product);
                    Assertions.assertEquals(product.getName(), "Iphone 14 pro max");
                    Assertions.assertEquals(product.getCategory().getName(), "Electrónico");
                });

    }

    @Test
    void editProductTest() {

        ProductEntity productEntityMono = this.productService.findByName("Sony Notebook").block();

        CategoryEntity category = this.productService.findCategoryByName("Electrónico").block();
        ProductEntity productEdit = new ProductEntity("Asus Notebook", 10500000.00, category);

        webTestClient.put()
                .uri(URL.concat("/{id}"), Collections.singletonMap("id", productEntityMono.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(productEdit), ProductEntity.class)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.CREATED)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("Asus Notebook");

    }

    @Test
    void deleteProductTest() {

        ProductEntity productEntityMono = this.productService.findByName("Bianchi Bicicleta").block();

        webTestClient.delete()
                .uri(URL.concat("/{id}"), Collections.singletonMap("id", productEntityMono.getId()))
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.NO_CONTENT)
                .expectBody()
                .isEmpty();
    }
}
