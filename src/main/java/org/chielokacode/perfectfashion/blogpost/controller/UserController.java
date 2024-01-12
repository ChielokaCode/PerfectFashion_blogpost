package org.chielokacode.perfectfashion.blogpost.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.chielokacode.perfectfashion.blogpost.dto.LoginRequestDto;
import org.chielokacode.perfectfashion.blogpost.dto.UserDto;
import org.chielokacode.perfectfashion.blogpost.model.User;
import org.chielokacode.perfectfashion.blogpost.serviceImpl.UserServiceImpl;
import org.chielokacode.perfectfashion.blogpost.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserServiceImpl userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Autowired
    public UserController(@Lazy UserServiceImpl userService,PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    //Endpoint to register/add the User
    @PostMapping("/sign-up")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDto> signUpUser(@Valid @RequestBody UserDto userDto) throws BadRequestException {
        User user = userService.saveUser(userDto);
        UserDto userDto1 = new ObjectMapper().convertValue(user, UserDto.class);
        return new ResponseEntity<>(userDto1, HttpStatus.CREATED);
    }

    //Endpoint to register/add the Admin
    @PostMapping("/admin/sign-up")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> signUpAdmin(@Valid @RequestBody UserDto adminDto) throws BadRequestException {
        User admin = userService.saveAdmin(adminDto);
        UserDto adminDto1 = new ObjectMapper().convertValue(admin, UserDto.class);
        return new ResponseEntity<>(adminDto1, HttpStatus.CREATED);
    }

    //Endpoint to Log in the User
    @PostMapping("/login")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> logInUser(@Valid @RequestBody LoginRequestDto userDto){
        UserDetails user = userService.loadUserByUsername(userDto.getUsername());
        if(passwordEncoder.matches(userDto.getPassword(), user.getPassword())){
            String jwtToken = jwtUtils.createJwt.apply(user);
            return new ResponseEntity<>(jwtToken, HttpStatus.CREATED);
        }else{
            return new ResponseEntity<>("Username and Password is incorrect",HttpStatus.BAD_REQUEST);
        }
    }

    //Endpoint to Log in the Admin
    @PostMapping("/admin/login")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> logInAdmin(@Valid @RequestBody LoginRequestDto adminDto){
        UserDetails admin = userService.loadUserByUsername(adminDto.getUsername());
        if(passwordEncoder.matches(adminDto.getPassword(), admin.getPassword())){
            String jwtToken = jwtUtils.createJwt.apply(admin);
            return new ResponseEntity<>(jwtToken, HttpStatus.CREATED);
        }else{
            return new ResponseEntity<>("Username and Password is incorrect",HttpStatus.BAD_REQUEST);
        }
    }



    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public String dashBoard(){
        return "Welcome to your DashBoard";
    }


}