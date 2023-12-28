package org.chielokacode.perfectfashion.blogpost.config.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {

    @JsonIgnore
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String image;

    @NotBlank
    private String description;

    @NotBlank
    private boolean published;
}

/*
PostResponse - used for representing response data,
PostRequest -  used for representing request data with validation constraints
 */