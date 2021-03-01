package com.smile.tech.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.smile.tech.model.ERole;
import com.smile.tech.model.Role;
import com.smile.tech.model.Users;
import com.smile.tech.payload.request.LoginRequest;
import com.smile.tech.payload.request.SignupRequest;
import com.smile.tech.payload.response.JwtResponse;
import com.smile.tech.payload.response.MessageResponse;
import com.smile.tech.repository.DeviceRepository;
import com.smile.tech.repository.RoleRepository;
import com.smile.tech.repository.UsersRepository;
import com.smile.tech.security.JwtUtils;

@Service
public class AuthenticationService {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UsersRepository userRepository;

	@Autowired
	DeviceRepository deviceRepository;
	
	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	public ResponseEntity<?> authenticateUserLogin(@Valid LoginRequest loginRequest) {
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

	public ResponseEntity<?> userRegisterProcess(@Valid SignupRequest signUpRequest) {

		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: User Name is already in use!"));
		}

		Users user = new Users(signUpRequest.getUsername(), encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRoles();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		}

		ZonedDateTime zdtAtET = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        LocalDateTime date = zdtAtET.toLocalDateTime();

		user.setCreatedDate(date);
		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}

	public ResponseEntity<?> adminRegisterProcess(@Valid SignupRequest signUpRequest) {

		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: User Name is already in use!"));
		}
		Users user = new Users(signUpRequest.getUsername(), encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRoles();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_ADMIN)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		}

		ZonedDateTime zdtAtET = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        LocalDateTime date = zdtAtET.toLocalDateTime();

		user.setCreatedDate(date);
		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("Admin registered successfully!"));
	}

	public ResponseEntity<?> managerRegisterProcess(@Valid SignupRequest signUpRequest) {

		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: User Name is already in use!"));
		}

		Users user = new Users(signUpRequest.getUsername(), encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRoles();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_MANAGER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		}
		ZonedDateTime zdtAtET = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        LocalDateTime date = zdtAtET.toLocalDateTime();

		user.setCreatedDate(date);
		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("Manager registered successfully!"));
	}


}
