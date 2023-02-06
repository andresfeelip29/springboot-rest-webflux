package com.co.app.course.springwebflux.repositories.entites.documents;

import java.util.Date;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

@ToString
@EqualsAndHashCode
@Document(collection = "products")
public class ProductEntity {

    @Id
    @Setter @Getter
    private String id;

    @NotEmpty
    @Setter @Getter
    private String name;

    @NotNull
    @Setter @Getter
    private Double price;


    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Setter @Getter
    private Date createAt;

    @Valid
    @Setter @Getter
    private CategoryEntity category;

    @Setter @Getter
    private String photo;

    public ProductEntity(){
        //
    }

    public ProductEntity(String name, Double price) {
        this.name = name;
        this.price = price;
    }

    public ProductEntity(String name, Double price, CategoryEntity category) {
        this.name = name;
        this.price = price;
        this.category = category;
    }
}
