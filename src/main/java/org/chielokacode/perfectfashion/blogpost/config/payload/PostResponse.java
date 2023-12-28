package org.chielokacode.perfectfashion.blogpost.config.payload;


import lombok.Data;

@Data
public class PostResponse {


    private String title;
    private String image;
    private String description;
    private boolean published;
}

/*
PostResponse - used for representing response data,
PostRequest -  used for representing request data with validation constraints
 */