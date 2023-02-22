package com.co.app.course.springwebflux.repositories.entites.documents;


import jakarta.validation.constraints.NotEmpty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@ToString
@EqualsAndHashCode
@Document(collection = "categories")
public class CategoryEntity {
	
	@Id
	@NotEmpty
	@Setter @Getter
	private String id;

	@Setter @Getter
	@NotEmpty
	private String name;

	public CategoryEntity(){
		//
	}
	public CategoryEntity(String name) {
		this.name = name;
	}
}
