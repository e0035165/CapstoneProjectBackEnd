package com.production;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.filter.JwtCustomFilter;
import com.organisation.services.UserInfoUserDetailsService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {
	private InMemoryUserDetailsManager srvc;
	private UserDetailsManager manager;
	@Autowired
	private UserInfoUserDetailsService userInfoDetailsService;
	@Autowired
	private JwtCustomFilter filter;
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http)
	{
		try {
			http.authorizeHttpRequests(req->req.requestMatchers(HttpMethod.GET,"/admin/**").authenticated()
												.requestMatchers(HttpMethod.POST,"/admin/**").authenticated()
												.requestMatchers(HttpMethod.PUT,"/admin/**").authenticated()
												.requestMatchers(HttpMethod.DELETE,"/admin/**").authenticated()
												.requestMatchers(HttpMethod.POST,"/authorization/**").permitAll()
												.requestMatchers(HttpMethod.GET,"/open/**").permitAll()
												.requestMatchers(HttpMethod.POST,"/open/**").permitAll()
												.requestMatchers(HttpMethod.PUT, "/open/**").permitAll()
												.requestMatchers(HttpMethod.DELETE,"/open/**").permitAll()
												);
			http.httpBasic(Customizer.withDefaults());
			http.csrf(csrf -> csrf.disable());		
			http.sessionManagement(sess->sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));	
			http.authenticationProvider(authenticationProvider()).addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
			return http.build();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
	}
	
	@Bean
	public UserDetailsService userDetailsService() {
		System.out.println(passwordEncoder().encode("Vignesh$@1995g"));
		return userInfoDetailsService;
	}
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userInfoDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
    	//config.authenticationManagerBuilder(this.srvc, null)
        return config.getAuthenticationManager();
    }
	
	
	

}
