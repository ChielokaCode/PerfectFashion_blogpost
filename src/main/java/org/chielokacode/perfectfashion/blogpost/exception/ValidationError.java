package org.chielokacode.perfectfashion.blogpost.exception;

public record ValidationError(
        String field,
        String message
) {
}
