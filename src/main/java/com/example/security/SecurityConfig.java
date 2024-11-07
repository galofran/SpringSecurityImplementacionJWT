package com.example.security;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.security.filters.JwtAuthenticationFilter;
import com.example.security.filters.JwtAuthorizationFilter;
import com.example.security.jwt.JwtUtils;
import com.example.service.UserDetailsServiceImpl;

@Configuration
public class SecurityConfig {
	
	@Autowired
	JwtUtils jwtUtils;
	
	@Autowired
	UserDetailsServiceImpl userDetailsService;
	
	@Autowired
	JwtAuthorizationFilter jwtAuthorizationFilter;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, AuthenticationManager authenticationManager) throws Exception { //Configuración del acceso a los endpoints, manejo de la sesión con una autenticación básica hecha con un usuario en memoria
		
		//Filtro que se va a manejar
		JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtils);
		jwtAuthenticationFilter.setAuthenticationManager(authenticationManager);
		
		return httpSecurity
				.csrf(config -> config.disable())
				.authorizeHttpRequests(auth -> {
					auth.requestMatchers("/hello").permitAll();
					auth.anyRequest().authenticated();
				})
				.sessionManagement(session -> {
					session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
					})
				.addFilter(jwtAuthenticationFilter) 
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.build();		
		
		//Lo primero que se validará es el token
		//Si el token es inválido entonces se validará la autorización 
		//Si el usuario no tiene credenciales correctas se denegará el acceso
	}
	
	/*@Bean
	UserDetailsService userDetailsService() {
		
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		
		manager.createUser(User.withUsername("root")
				.password("toor")
				.roles()
				.build());
		return manager;
	}*/
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	/*@Bean
	AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception { //Se encarga de la administración de la autenticación de los usuarios
		
		return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
				.userDetailsService(userDetailsService)
				.passwordEncoder(passwordEncoder())
				.and()
				.build();
	}*/
	@Bean
	AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
	    AuthenticationManagerBuilder authManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
	    
	    // Configuración del servicio de detalles de usuario y codificación de contraseñas
	    authManagerBuilder
	        .userDetailsService(userDetailsService)
	        .passwordEncoder(passwordEncoder());
	    
	    return authManagerBuilder.build();
	}

	
	/*
	public static void main(String [] args) {
		System.out.println(new BCryptPasswordEncoder().encode("1234"));
	}
	*/
}
