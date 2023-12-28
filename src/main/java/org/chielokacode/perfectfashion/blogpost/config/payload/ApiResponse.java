package org.chielokacode.perfectfashion.blogpost.config.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "success",
        "message"
})
public class ApiResponse implements Serializable {

    @JsonIgnore
    private static final long serialVersionUID = 7702134516418120340L;

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("message")
    private String message;

    @JsonIgnore
    private HttpStatus status;


    public ApiResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}

/*
the ApiResponse class is designed to represent a standard structure for API responses,
 with fields for success status, a message, and an HTTP status.
 Lombok annotations help reduce boilerplate code, and Jackson annotations
 are used for JSON serialization and deserialization.

 */