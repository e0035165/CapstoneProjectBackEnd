package com.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.operational.EncryptionDataClass;
import com.organisation.entity.CustomUser;
import com.organisation.services.JwtService;
import com.organisation.services.UserInfoUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtCustomFilter extends OncePerRequestFilter {
	@Autowired
	private JwtService service;
	
	
	@Autowired
	private UserInfoUserDetailsService userservice;
	
	@Autowired
	private EncryptionDataClass rsaService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		final String authHeader = request.getHeader("Authorization");
		String jwt=null;
		String username = null;
		String username_rsa = null;
		if(authHeader!=null && authHeader.startsWith("Bearer ")) {
			jwt=authHeader.substring(7);
			username = service.extractUsername(jwt);
			//username_rsa = rsaService.decryptString(jwt);
		}
		
		//System.out.println(username);
		
		if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null) {
			CustomUser obj=userservice.loadUserByUsername(username);
			
			if(service.validateToken(jwt,obj)) {
				System.out.println("Token validated and passsed");
				UsernamePasswordAuthenticationToken authToken = 
						new UsernamePasswordAuthenticationToken(obj,null,obj.getAuthorities());
				authToken.setDetails(
						new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			} else {
				System.out.println("Failed");
			}
		}
		filterChain.doFilter(request, response);
		
	}

}
