package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRolesController {

	@GetMapping("/accessAdmin")
	public String accessAdmin() {
		return "Has accedido con el rol de ADMIN";
	}
	
	@GetMapping("/accessUser")
	public String accessUser() {
		return "Has accedido con el rol de USER";
	}
	
	@GetMapping("/accessInvited")
	public String accessInvited() {
		return "Has accedido con el rol de INVITED";
	}
}
