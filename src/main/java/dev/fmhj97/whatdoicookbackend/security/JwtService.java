package dev.fmhj97.whatdoicookbackend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}") // From application.properties
    private String secret;

    @Value(("${jwt.expiration}"))
    private Long expiration;

    /**
     * Generates a signing key from the configured secret.
     * The secret is Base64-decoded and used to create an HMAC-SHA key.
     * @return Signing key used to sign and verify JWT tokens.
     */
    private SecretKey getSigningKey() {
        byte[] keyBites = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBites);
    }

    /**
     * Checks whether the given JWT token has expired.
     * @param token JWT token to check.
     * @return True if the token has expired, false otherwise.
     */
    private boolean isTokenExpired(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }

    /**
     * Generates a JWT token for the given user.
     * @param userDetails the user for whom the token is generated.
     * @return Signed JWT token (string).
     */
    public String generateJwtToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts the username from the given JWT token.
     * @param token JWT token.
     * @return Username stored in the JWT token.
     */
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * validates the given JWT token against the provided userDetails.
     * @param token JWT token to validate.
     * @param userDetails User details to validate against.
     * @return True if the token is valid, false otherwise.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }
}
