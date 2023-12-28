package org.chielokacode.perfectfashion.blogpost.serviceImpl;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.coyote.BadRequestException;
import org.chielokacode.perfectfashion.blogpost.config.payload.ApiResponse;
import org.chielokacode.perfectfashion.blogpost.dto.UserDto;
import org.chielokacode.perfectfashion.blogpost.enums.Role;
import org.chielokacode.perfectfashion.blogpost.model.User;
import org.chielokacode.perfectfashion.blogpost.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserDetailsService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not Found"));
    }


    //Method to Save/Register User
    public User saveUser(UserDto userDto) throws BadRequestException {
        //Convert UserDto to User class
        User user = new ObjectMapper().convertValue(userDto, User.class);

        //Before saving User, check if this user Username/Email is already in database
        if (userRepository.existsByUsername(user.getUsername())) {
            ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "Username is already taken, try Logging In");
            throw new BadRequestException(String.valueOf(apiResponse));
        }

        // Assign a default role (ROLE_USER) to the user
        user.setUserRole(Role.ROLE_USER);

        // Encode the user's password
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        //then saves the user to database
        return userRepository.save(user);
    }

    //Method to Save/Register Admin
    public User saveAdmin(UserDto adminDto) throws BadRequestException {
        //Convert UserDto to User class
        User admin = new ObjectMapper().convertValue(adminDto, User.class);

        //Before saving Admin, check if this user Username/Email is already in database
        if (userRepository.existsByUsername(admin.getUsername())) {
            ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "Username is already taken, try Logging In");
            throw new BadRequestException(String.valueOf(apiResponse));
        }

        // Assign a default role (ROLE_USER) to the Admin
        admin.setUserRole(Role.ROLE_ADMIN);

        // Encode the admin's password
        admin.setPassword(passwordEncoder.encode(adminDto.getPassword()));
        //then saves the admin to database
        return userRepository.save(admin);
    }
}