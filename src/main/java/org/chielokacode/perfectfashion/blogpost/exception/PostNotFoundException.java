package org.chielokacode.perfectfashion.blogpost.exception;

public class PostNotFoundException extends Throwable {
    @Override
    public String getMessage() {
        return "Post Not Found";
    }
}