package org.chielokacode.perfectfashion.blogpost.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class BlogApiException extends RuntimeException {

    private static final long serialVersionUID = -6593330219878485669L;

    private final HttpStatus status;
    private final String message;

    public BlogApiException(HttpStatus status, String message) {
        super();
        this.status = status;
        this.message = message;
    }

    public BlogApiException(HttpStatus status, String message, Throwable exception) {
        super(exception);
        this.status = status;
        this.message = message;
    }
}
