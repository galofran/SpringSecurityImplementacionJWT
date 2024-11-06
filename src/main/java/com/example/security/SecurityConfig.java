package com.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception { //Configuración del acceso a los endpoints, manejo de la sesión con una autenticación básica hecha con un usuario en memoria
		return httpSecurity
				.csrf(config -> config.disable())
				.authorizeHttpRequests(auth -> {
					auth.requestMatchers("/hello").permitAll();
					auth.anyRequest().authenticated();
				})
				.sessionManagement(session -> {
					session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
					})
				.httpBasic()
				.and()
				.build();		
	}
	
	@Bean
	UserDetailsService userDetailsService() {
		
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		
		manager.createUser(User.withUsername("root")
				.password("toor")
				.roles()
				.build());
		return manager;
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception { //Se encarga de la administración de la autenticación de los usuarios
		
		return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
				.userDetailsService(userDetailsService())
				.passwordEncoder(passwordEncoder())
				.and()
				.build();
	}
}
