package org.chielokacode.perfectfashion.blogpost.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.chielokacode.perfectfashion.blogpost.config.payload.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private ApiResponse apiResponse;

    private String message;

    public UnauthorizedException(ApiResponse apiResponse) {
        super();
        this.apiResponse = apiResponse;
    }

    public UnauthorizedException(String message) {
        super(message);
        this.message = message;
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
