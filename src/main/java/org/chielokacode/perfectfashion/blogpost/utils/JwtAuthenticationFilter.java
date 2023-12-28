package org.chielokacode.perfectfashion.blogpost.utils;


    /*
    The purpose of the code is to handle user authentication based on JWT token

         1ST -  CHECK THE Authorization header is not empty and starts With Bearer ,
         then set token and username extracted from token

         2nd  - check if the username extracted from token is not set in SecurityContextHolder, if true
         then set the userDetails(user information) gotten from database for that user to userDetails

         3rd- then check the validity of the token against the username extracted from the user details
        if token is still valid, then create a new UPAToken with the UserDetails, password set as null, and authorities of that user

        4th - set more information such as the remote address and session ID to the UPAToken using the "WebAuthenticationDetailSource"

        5th - finally set the UPAToken with the userDetails and other information to the "SecurityContextHolder"
        so Spring Security knows this user is now Authenticated.

        using filterChain.doFilter(request, response) to continue to next request

     */

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.chielokacode.perfectfashion.blogpost.serviceImpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtUtils utils;
    private UserServiceImpl userService;

    @Autowired
    public JwtAuthenticationFilter(JwtUtils utils, @Lazy UserServiceImpl userService) {
        this.utils = utils;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = null;
        String authorizationHeader = null;
        String username = null;
        UserDetails userDetail = null;

        authorizationHeader = request.getHeader("Authorization");

        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            token = authorizationHeader.substring(7);
            username = utils.extractUsername.apply(token);
        }


        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            userDetail = userService.loadUserByUsername(username);

            if(userDetail != null && utils.isTokenValid.apply(token, userDetail.getUsername())){
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());

                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}