package org.chielokacode.perfectfashion.blogpost.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.chielokacode.perfectfashion.blogpost.enums.Role;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String username;
    private String firstName;
    private String lastName;
    private String password;
}