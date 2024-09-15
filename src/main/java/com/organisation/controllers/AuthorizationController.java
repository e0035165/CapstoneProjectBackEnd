package com.organisation.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.organisation.entity.AuthRequest;
import com.organisation.entity.CustomUser;
import com.organisation.services.JwtService;
import com.organisation.services.UserInfoUserDetailsService;

@RestController
@RequestMapping(path="/authorization")
@CrossOrigin(origins = "https://localhost:3000/**", maxAge = 3600)
public class AuthorizationController {
	
	@Autowired
	private JwtService jwtservice;
	
	
	
	@Autowired
	private AuthenticationManager authManager;
	
	@Autowired
	private UserInfoUserDetailsService userservice;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private AuthenticationProvider priovider;
	
	
	
	@PostMapping("/register")
	public String registration(@RequestBody AuthRequest authRequest) {
		userservice.addRequest(authRequest.username, passwordEncoder.encode(authRequest.password));
		
		return jwtservice.generateToken(authRequest.username);
	}
	
	@PostMapping("/authenticate")
	public ResponseEntity<String> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
		CustomUser user = userservice.loadUserByUsername(authRequest.username);
		
		if(user==null)
			return new ResponseEntity<>("User is not found in Database", HttpStatus.BAD_REQUEST);
		
		Boolean passwordPass = passwordEncoder.matches(authRequest.password, user.getPassword());
		if(passwordPass==false) {
			return new ResponseEntity<>("Password mismatch", HttpStatus.UNAUTHORIZED);
		} else {
			return new ResponseEntity<>("Password matched", HttpStatus.OK);
		}
		
		
	}

}
