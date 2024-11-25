package com.example.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRolesController {

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/accessAdmin")
	public String accessAdmin() {
		return "Has accedido con el rol de ADMIN";
	}
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@GetMapping("/accessUser")
	public String accessUser() {
		return "Has accedido con el rol de USER";
	}
	
	@PreAuthorize("hasRole('ROLE_INVITED')")
	@GetMapping("/accessInvited")
	public String accessInvited() {
		return "Has accedido con el rol de INVITED";
	}
}
