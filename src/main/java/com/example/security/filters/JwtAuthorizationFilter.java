package com.example.security.filters;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.security.jwt.JwtUtils;
import com.example.service.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter{

	@Autowired
	private JwtUtils jwtUtils; //Necesario aqui para validar el token
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService; //Necesario para hacer la consulta del usuario a la BBDD 
	
	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain)
			throws ServletException, IOException {

		String tokenHeader = request.getHeader("Authorization"); //Extracción del token de la petición
		
		if(tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
			String token = tokenHeader.substring(7);
			
			if(jwtUtils.isTokenValid(token)) {
				String username = jwtUtils.getUsernameFromToken(token);
				
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				
				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,null,userDetails.getAuthorities()); //userDetails.getAuthorities(): Permisos-Roles del ususario
				
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				
				
			}
		}
		
		filterChain.doFilter(request, response); //Si no es valido el token, se continuará el filtro de validación y SS denegará el acceso  
	}

}
