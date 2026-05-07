package dev.fmhj97.whatdoicookbackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Constructor with args
     * @param jwtService
     * @param userDetailsService
     */
    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Read Authorization header from the request.
        final String authHeader = request.getHeader("Authorization");

        // If there is no token or it doesn't start with "Bearer ", pass the request without authenticating.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the token by removing "Bearer " prefix (7 characters).
        final String token = authHeader.substring(7);

        // Extract the username from the token payload.
        final String username = jwtService.extractUsername(token);

        // If the username is valid and the user is not already authenticated in this request.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load the user from the database using the username;
            UserDetails user = userDetailsService.loadUserByUsername(username);

            // If the token is valid for this user, create Authorization object with user's info.
            if (jwtService.isTokenValid(token, user)) {

                // Create an authorization object with user's details and authorities.
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                user, null, user.getAuthorities()
                        );

                // Attach request details to the new authorization object.
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Register the authorization object in Spring Security's context for the request.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Pass the request to the next filter or controller.
        filterChain.doFilter(request, response);

    }
}
