package org.chielokacode.perfectfashion.blogpost.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {
    @NotBlank
    @Email
    private String username;
    @NotBlank

    @Size(min = 5, max = 200)
    private String password;
}
