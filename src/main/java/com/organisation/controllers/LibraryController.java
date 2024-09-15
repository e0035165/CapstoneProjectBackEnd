package com.organisation.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.operational.EncryptionDataClass;
import com.organisation.entity.AuthRequest;
import com.organisation.entity.Books;
import com.organisation.entity.Members;
import com.organisation.services.BookService;
import com.organisation.services.JwtService;
import com.organisation.services.MembersService;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "https://localhost:3000/", maxAge = 3600)
public class LibraryController {
	@Autowired
	private BookService srvc;
	
	@Autowired
	private EncryptionDataClass edc;
	
	@Autowired
	private MembersService memserv;
	
	
	
	@PostMapping(path="/addBooks")
	public ResponseEntity<String> addBooks(@RequestBody(required=true) Map<String,Object>body)
	{
		ObjectMapper objmap = new ObjectMapper();
		Books bk = objmap.convertValue(body, Books.class);
		srvc.addBooks(bk);
		return new ResponseEntity<>("Successful",HttpStatus.CREATED);
	}
	
	@DeleteMapping(path="/deleteBooks/{pid}")
	public ResponseEntity<String> deleteBook(@PathVariable long pid)
	{
		srvc.removeBooks(pid);
		return new ResponseEntity<>("Book removed successfully",HttpStatus.NO_CONTENT);
	}
	
	@GetMapping(path="/getAllBooks")
	public ResponseEntity<Object> getAllBooks() throws JsonProcessingException
	{
		ObjectMapper objmap = new ObjectMapper();
		List<Books>rate=srvc.getAllBooks();
		objmap.registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		String jsonString = objmap.writeValueAsString(rate);
		return new ResponseEntity<>("Fan",HttpStatus.OK);
	}
	
	@GetMapping(path="/getPassword/{nric}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> getMemberPassword(@PathVariable int nric) {
		Members mbm = memserv.getMember(nric);
		String jwepass = mbm.password;
		String actualPassword = edc.jwedecrypt(jwepass);
		System.out.println(actualPassword);
		return new ResponseEntity<>(actualPassword, HttpStatus.OK);
	}
	
	
	
	

}
