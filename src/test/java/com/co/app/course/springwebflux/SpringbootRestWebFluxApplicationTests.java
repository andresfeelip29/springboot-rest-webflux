package com.co.app.course.springwebflux;

import com.co.app.course.springwebflux.repositories.entites.documents.ProductEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringbootRestWebFluxApplicationTests {

    private final WebTestClient webTestClient;

    @Autowired
    public SpringbootRestWebFluxApplicationTests(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    @Test
    void listTest() {
        webTestClient.get()
                .uri("api/v2/product")
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

}
