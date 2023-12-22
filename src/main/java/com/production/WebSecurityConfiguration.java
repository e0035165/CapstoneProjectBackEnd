package com.production;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http)
	{
		try {
			http.authorizeHttpRequests(req->req.requestMatchers(HttpMethod.GET,"/admin/**").hasRole("ADMIN")
												.requestMatchers(HttpMethod.POST,"/admin/**").hasRole("ADMIN")
												.requestMatchers(HttpMethod.PUT,"/admin/**").hasRole("ADMIN")
												.requestMatchers(HttpMethod.DELETE,"/admin/**").hasRole("ADMIN")
												.requestMatchers(HttpMethod.GET,"/open/**").permitAll()
												.requestMatchers(HttpMethod.POST,"/open/**").permitAll()
												.requestMatchers(HttpMethod.PUT, "/open/**").permitAll()
												.requestMatchers(HttpMethod.DELETE,"/open/**").permitAll()
												);
			http.httpBasic(Customizer.withDefaults());
			http.csrf(csrf -> csrf.disable());		
											
			return http.build();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
	}
	@Bean
	public UserDetailsService userDetailsService() {
		UserDetails userAdmin = User.builder()
								.username("Sathya")
								.password("{noop}Vignesh$@1995g").roles("ADMIN")
								.username("Benjamin")
								.password("{noop}Benjamin$@1998g").roles("ADMIN").build();

		return new InMemoryUserDetailsManager(userAdmin);
	}
}
