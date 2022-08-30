package com.sparta.ddang.config;

import com.sparta.ddang.jwt.JwtFilter;
import com.sparta.ddang.jwt.TokenProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtSecurityConfiguration
    extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

  private final TokenProvider tokenProvider;

  public JwtSecurityConfiguration(TokenProvider tokenProvider) {
    this.tokenProvider = tokenProvider;
  }

  @Override
  public void configure(HttpSecurity httpSecurity) {
    JwtFilter customJwtFilter = new JwtFilter(tokenProvider);
    httpSecurity.addFilterBefore(customJwtFilter, UsernamePasswordAuthenticationFilter.class);
  }
}
