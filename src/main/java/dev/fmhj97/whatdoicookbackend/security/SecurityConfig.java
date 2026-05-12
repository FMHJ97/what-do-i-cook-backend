package dev.fmhj97.whatdoicookbackend.security;

import dev.fmhj97.whatdoicookbackend.entity.enums.Role;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration class.
 * Defines the security filter chain, access rules for endpoints,
 * password encoding and authentication management.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    /**
     * Constructor with args.
     * @param jwtAuthFilter
     */
    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * Configures the password encoder using BCrypt hashing algorithm.
     * @return a BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the AuthenticationManager as a Spring bean, used for authenticating users during login.
     * @param config the AuthenticationConfiguration provided by Spring
     * @return the configured AuthenticationManager
     * @throws Exception if the AuthenticationManager cannot be created
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configures the security filter chain, defining session policy, public and protected endpoints,
     * authentication error handling, and registers the JWT filter.
     * @param httpSecurity the HttpSecurity object to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if the security configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                // Disable CSRF (Not needed for stateless REST APIs with JWT; No cookies/sessions).
                .csrf(csrf -> csrf.disable())

                // Use stateless sessions (no HTTP session; authentication is per request via JWT).
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Define access rules for each endpoint.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/api/auth/**"
                        ).permitAll()
                        // Ingredient Endpoints
                        .requestMatchers(HttpMethod.GET, "/api/ingredient/**").authenticated()
                        .requestMatchers("/api/ingredient/**").hasRole(Role.ADMIN.name())
                        // Recipe Endpoints
                        .requestMatchers("/api/recipe/**").hasRole(Role.USER.name())
                        .anyRequest().authenticated()
                )

                // Handle authentication and authorization errors with JSON responses
                // instead of Spring's default HTML error pages.
                // 401 (Unauthorized) -> missing or invalid JWT token.
                // 403 (Forbidden) -> authenticated but insufficient permissions.
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Invalid credentials\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Access denied\"}");
                        })
                )

                // Register our JwtAuthFilter before Spring's default authentication filter.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
