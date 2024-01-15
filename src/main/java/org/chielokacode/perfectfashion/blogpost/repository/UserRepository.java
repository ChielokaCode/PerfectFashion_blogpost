package org.chielokacode.perfectfashion.blogpost.repository;


import org.chielokacode.perfectfashion.blogpost.exception.ResourceNotFoundException;
import org.chielokacode.perfectfashion.blogpost.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<UserDetails> findByUsername(String username);

    default User getUser(User currentUser) {
        return getUserByName(currentUser.getUsername());
    }
    default User getUserByName(String username) {
        return (User) findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with " + username + " does not exist!"));
    }

    boolean existsByUsername(String username);

//    boolean existsByEmail(String username);
//
//    boolean existsByUserRole(Role role);
}