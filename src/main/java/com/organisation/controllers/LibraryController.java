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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.operational.EncryptionDataClass;
import com.organisation.entity.Books;
import com.organisation.entity.Members;
import com.organisation.services.BookService;
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
		return new ResponseEntity<>("Book removed successfully",HttpStatus.OK);
	}
	
	@GetMapping(path="/getAllBooks")
	public ResponseEntity<Object> getAllBooks()
	{
		ObjectMapper objmap = new ObjectMapper();
		List<Books>rate=srvc.getAllBooks();
		return new ResponseEntity<>(rate,HttpStatus.OK);
	}
	
	@GetMapping(path="/getPassword/{nric}")
	public ResponseEntity<String> getMemberPassword(@PathVariable int nric) {
		Members mbm = memserv.getMember(nric);
		String jwepass = mbm.password;
		String actualPassword = edc.jwedecrypt(jwepass);
		System.out.println(actualPassword);
		return new ResponseEntity<>(actualPassword, HttpStatus.OK);
	}
	

}
