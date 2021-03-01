package com.smile.tech.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smile.tech.payload.request.LoginRequest;
import com.smile.tech.payload.request.SignupRequest;

import com.smile.tech.service.AuthenticationService;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
@Api
public class AuthController {
	
	

	private static Logger logger = LoggerFactory.getLogger(AuthController.class);
	
	@Autowired
	AuthenticationService service;
	
	@ApiOperation(value = "Common Login for All users")
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		logger.info("user signin started.");
		return service.authenticateUserLogin(loginRequest);

	}

	@ApiOperation(value = "User signup performed by Admin or Manager")
	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
	@PostMapping("/user/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		
		logger.info("user signup started.");
		return service.userRegisterProcess(signUpRequest);

	}

	@ApiOperation(value = "Admin signup performed by SuperAdmin")
	@PreAuthorize("hasRole('ROLE_SUPERADMIN')")
	@PostMapping("/admin/signup")
	public ResponseEntity<?> registerAdmin(@Valid @RequestBody SignupRequest signUpRequest) {

		logger.info("admin signup started.");
		return service.adminRegisterProcess(signUpRequest);

	}

	@ApiOperation(value = "Manager signup performed by Admin")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/manager/signup")
	public ResponseEntity<?> registerManager(@Valid @RequestBody SignupRequest signUpRequest) {

		logger.info("manager signup started.");
		return service.managerRegisterProcess(signUpRequest);

	}
	
	

	@RequestMapping("/refresh")
	public String refresh() {
		return "Hello User";
	}
}
