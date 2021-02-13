package com.smile.tech.controller;

import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smile.tech.model.ERole;
import com.smile.tech.model.Role;
import com.smile.tech.model.Users;
import com.smile.tech.payload.request.LoginRequest;
import com.smile.tech.payload.request.SignupRequest;
import com.smile.tech.payload.response.JwtResponse;
import com.smile.tech.payload.response.MessageResponse;
import com.smile.tech.repository.RoleRepository;
import com.smile.tech.security.JwtUtils;
import com.smile.tech.service.UsersDetailImpl;

import lombok.val;

import com.smile.tech.repository.UsersRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UsersRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UsersDetailImpl userDetails = (UsersDetailImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(userDetails.getId(), userDetails.getUsername(),
				userDetails.getCreatedDate(), roles, jwt));
	}

	@PreAuthorize("hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
	@PostMapping("/user/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: User Name is already in use!"));
		}

		// Create new user's account
		Users user = new Users(signUpRequest.getUsername(), encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRoles();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		}
//			else {
//			strRoles.forEach(role -> {
//				switch (role) {
//				case "admin":
//					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
//							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//					roles.add(adminRole);
//
//					break;
//				case "mod":
//					Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
//							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//					roles.add(modRole);
//
//					break;
//				default:
//					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
//							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//					roles.add(userRole);
//				}
//			});
//		}

		LocalDateTime date = LocalDateTime.now();

		user.setCreatedDate(date);
		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}

//	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/admin/signup")
	public ResponseEntity<?> registerAdmin(@Valid @RequestBody SignupRequest signUpRequest) {

//    	if(bindingResult.hasErrors()){
//    		return ResponseEntity
//					.badRequest()
//					.body(new MessageResponse("Error: USERNAME && PASSWORD MUST"));
//		}
//    	else
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: User Name is already in use!"));
		}

		// Create new user's account
		Users user = new Users(signUpRequest.getUsername(), encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRoles();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_ADMIN)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		}
//			else {
//			strRoles.forEach(role -> {
//				switch (role) {
//				case "admin":
//					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
//							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//					roles.add(adminRole);
//
//					break;
//				case "mod":
//					Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
//							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//					roles.add(modRole);
//
//					break;
//				default:
//					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
//							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//					roles.add(userRole);
//				}
//			});
//		}

		LocalDateTime date = LocalDateTime.now();

		user.setCreatedDate(date);
		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("Admin registered successfully!"));
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/manager/signup")
	public ResponseEntity<?> registerManager(@Valid @RequestBody SignupRequest signUpRequest) {

		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: User Name is already in use!"));
		}

		// Create new user's account
		Users user = new Users(signUpRequest.getUsername(), encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRoles();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_MANAGER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		}
//			else {
//			strRoles.forEach(role -> {
//				switch (role) {
//				case "admin":
//					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
//							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//					roles.add(adminRole);
//
//					break;
//				case "mod":
//					Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
//							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//					roles.add(modRole);return AService.save(user); 
//
//					break;
//				default:
//					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
//							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//					roles.add(userRole);
//				}
//			});
//		}
		String FORMAT = "yyyy-MM-dd HH:mm:ss";
		LocalDateTime date = LocalDateTime.now();

		user.setCreatedDate(date);
		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("Manager registered successfully!"));
	}
}