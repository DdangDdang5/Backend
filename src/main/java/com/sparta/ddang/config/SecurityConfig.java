package com.sparta.ddang.config;

import com.sparta.ddang.jwt.AccessDeniedHandlerException;
import com.sparta.ddang.jwt.AuthenticationEntryPointException;
import com.sparta.ddang.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final AuthenticationEntryPointException authenticationEntryPointException;
    private final AccessDeniedHandlerException accessDeniedHandlerException;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // h2-console 사용에 대한 허용 (CSRF, FrameOptions 무시)
        return (web) -> web.ignoring()
                .antMatchers(
                        "/h2-console/**"
                        ,"/favicon.ico"
                        ,"/error"
                        ,"/css/**"
                        ,"/js/**"
                        ,"/img/**"
                        ,"/lib/**"
                        ,"/templates/chat/**"
                );

    }

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors();
        http.headers().frameOptions().disable();

        http.csrf().disable()

                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPointException)
                .accessDeniedHandler(accessDeniedHandlerException)

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/member/**").permitAll()
                .antMatchers("/auction/**").permitAll()
                .antMatchers("/auction/category/**").permitAll()
                .antMatchers("/auction/region/**").permitAll()
                .antMatchers("/category/hit").permitAll()
                .antMatchers("/region/hit").permitAll()
                .antMatchers("/chat/**").permitAll()
                .antMatchers("/pagination/**").permitAll()
                .antMatchers("/ws/**").permitAll()
                .antMatchers("/wss/**").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/topic/**").permitAll()
                .antMatchers("/queue/**").permitAll()
                .antMatchers("/app/**").permitAll()
                .antMatchers("/ono/**").permitAll()
                .antMatchers("/subscribe/**").permitAll()
                .antMatchers("/notification/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .apply(new JwtSecurityConfiguration(tokenProvider));

        return http.build();
    }

}
