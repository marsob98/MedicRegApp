package pl.Sobiermann.MedicRegApp.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**"),
                                         new AntPathRequestMatcher("/users/register"),
                                         new AntPathRequestMatcher("/swagger-ui/**"),
                                         new AntPathRequestMatcher("/v3/api-docs/**"),
                                         new AntPathRequestMatcher("/v3/api-docs.yaml")).permitAll()

                        .requestMatchers(new AntPathRequestMatcher("/reservations/**")).hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )

                .csrf(csrf -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**"),
                                                           new AntPathRequestMatcher("/users/register"),
                                                           new AntPathRequestMatcher("/reservations"),
                                                           new AntPathRequestMatcher("/swagger-ui/**"),
                                                           new AntPathRequestMatcher("/v3/api-docs/**")))

                .headers(headers -> headers.frameOptions().disable())

                .httpBasic();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
