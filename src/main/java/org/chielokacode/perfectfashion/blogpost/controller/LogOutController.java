package org.chielokacode.perfectfashion.blogpost.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.chielokacode.perfectfashion.blogpost.serviceImpl.LogOutServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@SecurityRequirement(name = "Bearer Authentication")
@RestController
@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
@RequestMapping("/api/logout")
@RequiredArgsConstructor
public class LogOutController {

    private final LogOutServiceImpl logoutService;


    @PostMapping
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        logoutService.logout(request, response, authentication);
        return ResponseEntity.ok("Logout successful");
    }
}

