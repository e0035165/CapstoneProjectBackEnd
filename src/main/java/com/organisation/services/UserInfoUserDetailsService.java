package com.organisation.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.organisation.entity.AuthRequest;
import com.organisation.entity.CustomUser;
import com.organisation.repositories.UserInfoRepository;

@Service
@Lazy
public class UserInfoUserDetailsService implements UserDetailsService{
	
	@Autowired
	private UserInfoRepository repo;
	
//	@Autowired
//	private PasswordEncoder passwordEncoder;
//	
//	@Autowired
//	private AuthenticationManager manager;

	@Override
	public CustomUser loadUserByUsername(String username) throws UsernameNotFoundException {
		List<CustomUser>users = repo.findAll();
		System.out.println(username);
		for(CustomUser user:users) {
			System.out.println(user.getUsername());
		}
		users=users.stream().filter(x->x.getUsername().equals(username)).collect(Collectors.toList());
		System.out.println(users.size());
		if(users.size()==0)
			return null;
		else
			return users.get(0);
	}
	
	public Boolean addRequest(String username, String password) {
		CustomUser user = new CustomUser();
		user.setPassword(password);
		user.setUsername(username);
		repo.save(user);
		return true;
	}

	
	
//	public CustomUser signup(AuthRequest req) {
//		CustomUser user = new CustomUser();
//		user.setUsername(req.username);
//		user.setPassword(passwordEncoder.encode(req.password));
//		return repo.save(user);
//	}

}
