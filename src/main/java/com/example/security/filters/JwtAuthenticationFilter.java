package com.example.security.filters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.models.UserEntity;
import com.example.security.jwt.JwtUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
//UserNamePasswordAuthenticationFiler: Clase que ayuda a autenticarnos en la app
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	
	
	private JwtUtils jwtUtils;
	
	public JwtAuthenticationFilter(JwtUtils jwtUtils) {
		this.jwtUtils = jwtUtils;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) //Método para intentar autenticarse
			throws AuthenticationException {
		
		UserEntity userEntity = null;
		String username = "";
		String password = "";
		
		try {
			userEntity = new ObjectMapper().readValue(request.getInputStream(), UserEntity.class); //Obtiene los parametros y los mapea a la entidad UserEntity
			username = userEntity.getUsername();
			password = userEntity.getPassword();
		}catch(StreamReadException e) {
			throw new RuntimeException(e);
		}catch(DatabindException e) {
			throw new RuntimeException(e);
		}catch (IOException e){
			throw new RuntimeException(e);
		}
		
		
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,password); //Se autenticará en la aplicacion
		
		return getAuthenticationManager().authenticate(authenticationToken);
		
		//AuthenticationManager:Objeto que se encarga de administrar la autenticación 
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, //Si la autenticación es correcta, se generará el token
			Authentication authResult) throws IOException, ServletException {
		
		User user = (User)authResult.getPrincipal();
		String token = jwtUtils.generateAccessToken(user.getUsername());
		
		response.addHeader("Authorization",token); //En el header de la respuesta ira el token
		
		Map<String,Object> httpResponse = new HashMap<>(); //Se mapea la respuesta y se convierte a JSON mediante Jackson
		httpResponse.put("token", token);
		httpResponse.put("Message", "Autenticación correcta");
		httpResponse.put("Username", user.getUsername());
		
		response.getWriter().write(new ObjectMapper().writeValueAsString(httpResponse));
		response.setStatus(HttpStatus.OK.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter().flush(); 
		
		super.successfulAuthentication(request, response, chain, authResult);
		
	}

}
