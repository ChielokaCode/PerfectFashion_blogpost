package org.chielokacode.perfectfashion.blogpost.utils;


import com.google.common.base.Objects;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
@Slf4j
public class JwtUtils {


/*
    //1st- For Creating JWT (GetKey, ExpirationDate)
    //2nd - extract username and expirationDate from token - //before that, extract claims
    //3rd -  check if token isValid and isExpired
 */

    //1st- For Creating JWT (GetKey, ExpirationDate)

    private Supplier<SecretKeySpec> getKey = () -> {
        Key key = Keys.hmacShaKeyFor("5627dcc7bb45a7cf9ddd211168fb727a2005338c5fe1e29faa71fc542bcd59e1bf0c604d65578ba9001984bfa0f25e29a509f130db33c51916bf0ebfbb78b645"
                .getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(key.getEncoded(), key.getAlgorithm());
    };

    private Supplier<Date> expirationTime = () ->
            Date.from(LocalDateTime.now()
                    .plusMinutes(10)
                    .atZone(ZoneId.systemDefault())
                    .toInstant());

    //public String createJwt(UserDetails userDetails){}
    public Function<UserDetails, String> createJwt = (userDetails) -> {
        Map<String, Object> claims = new HashMap<>();
      return Jwts.builder()
              .signWith(getKey.get())
              .claims(claims)
              .subject(userDetails.getUsername())
              .issuedAt(new Date(System.currentTimeMillis()))
              .expiration(expirationTime.get())
              .compact();
    };

    //2nd - extract username and expirationDate from token - //before that, extract claim
    public <T> T extractClaims(String token, Function<Claims, T> claimResolver) {
        final Claims claims = Jwts.parser().verifyWith(getKey.get()).build().parseSignedClaims(token).
                getPayload();
        return claimResolver.apply(claims);
    }

    public Function<String, String> extractUsername = (token) -> extractClaims(token, Claims::getSubject);
    public Function<String, Date> extractExpirationTime = (token) -> extractClaims(token, Claims::getExpiration);

    //3rd -  check if token isValid and isExpired

    public Function<String, Boolean> isTokenExpired = (token) ->
        extractExpirationTime.apply(token).after(new Date(System.currentTimeMillis()));

    public BiFunction<String, String, Boolean> isTokenValid = (token, username) -> {
        try {
           boolean b = isTokenExpired.apply(token) && Objects.equal(extractUsername.apply(token), username); //checkError then refactor back to just this line without the variable b
            return b;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    };
}
