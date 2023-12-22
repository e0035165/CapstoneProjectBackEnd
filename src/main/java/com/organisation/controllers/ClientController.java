package com.organisation.controllers;

import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.operational.BookSelectionCoordinator;
import com.operational.EncryptionDataClass;
import com.organisation.entity.Books;
import com.organisation.entity.Members;
import com.organisation.services.BookService;
import com.organisation.services.MembersService;

@RestController
@RequestMapping(path="/open")
@CrossOrigin(origins = "https://localhost:3000/**", maxAge = 3600)
public class ClientController {
	@Autowired
	private BookService bkserv;
	
	@Autowired
	private MembersService srvc;
	
	@Autowired
	private BookSelectionCoordinator coordinator;
	
	@Autowired
	private EncryptionDataClass edc;
	
	@PostMapping(path="/addmember")
	public ResponseEntity<String> addMember(@RequestBody(required=true) Map<String,Object>body)
	{
		ObjectMapper objmap = new ObjectMapper();
		objmap.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		Members mem = objmap.convertValue(body, Members.class);
		System.out.println(mem.toString());
		String testpassword = (String)body.get("password");
		String newans=edc.jweEncrypt(testpassword);
		System.out.println(edc.jwedecrypt(newans));
		mem.password=newans;
		System.out.println("Encrypted password has been set as : "+mem.password);
		srvc.addmember(mem);
		return new ResponseEntity<>("Member created",HttpStatus.CREATED);
	}
	
	@PostMapping(path="/login")
	public ResponseEntity<String> login(@RequestBody(required=true) Map<String,Object>body)
	{
		int user = -1;
		if(body.get("user") instanceof String)
		{
			user = Integer.parseInt((String) body.get("user"));
		} else if(body.get("user") instanceof Integer) {
			user = (Integer) body.get("user");
		}
		String pass = (String) body.get("pass");
		if(srvc.isMemberExists(user)!=null)
		{
			Members mbm = srvc.isMemberExists(user).get();
			String encPass = mbm.getPassword();
//			System.out.println(encPass);
			//System.out.println(edc.jwedecrypt(encPass));
			if(edc.jwedecrypt(encPass).equals(pass))
			{
				return new ResponseEntity<>("Successful",HttpStatus.ACCEPTED);
			} else {
				return new ResponseEntity<>("Invalid Login ID or Password",HttpStatus.BAD_REQUEST);
			}
		} else {
			return new ResponseEntity<>("Member is not registered. Please register", HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@PostMapping(path="/borrow", produces="application/json")
	public ResponseEntity<Object> getBooks(@RequestBody(required=true) Map<String,Object>body)
	{
		int mass = -1;
		int no = -1;
		if(body.get("mass") instanceof String)
		{
			mass = Integer.parseInt((String) body.get("mass"));
		} else if(body.get("mass") instanceof Integer)
		{
			mass = (Integer) body.get("mass");
		}
		
		if(body.get("number") instanceof String)
		{
			no = Integer.parseInt((String) body.get("number"));
		} else if(body.get("number") instanceof Integer)
		{
			no = (Integer) body.get("number");
		}
		String genre = (String) body.get("genre");
		List<Books>bks = bkserv.getCurrentBooks(genre);
		ObjectMapper objmap = new ObjectMapper();
		List<List<Books>>ans=coordinator.getBookCombinations(bks, mass, no);
		JSONArray jsonArray = new JSONArray(ans);
		String arraytojson;
		try {
			arraytojson = objmap.writeValueAsString(ans);
			TypeReference<List<List<Books>>> mapType = new TypeReference<List<List<Books>>>() {};
			List<List<Books>>newans = objmap.readValue(arraytojson, mapType);
			ObjectMapper objectMapper = new ObjectMapper();
			return new ResponseEntity<>(jsonArray.toList(),HttpStatus.OK);
		} catch (JsonProcessingException e) {
			System.err.println(e.getMessage());
			return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
		}
		
		
		
		
	}
	
	@PostMapping(path="/confirmation")
	public ResponseEntity<String> confirmBookSelection(@RequestBody(required=true) Map<String,Object>body)
	{
		int nric = -1;
		String pass = (String) body.get("pass");
		if(body.get("user") instanceof String)
		{
			nric = Integer.parseInt((String) body.get("user"));
		} else if(body.get("user") instanceof Integer)
		{
			nric = (Integer) body.get("user");
		}
		if(srvc.isMemberExists(nric)!=null)
		{
			Members mbm = srvc.isMemberExists(nric).get();
			String encPass = mbm.getPassword();
			if(!edc.jwedecrypt(encPass).equals(pass))
			{
				return new ResponseEntity<>("Invalid Login ID or Password",HttpStatus.BAD_REQUEST);
			}
			List<Books>bks = (List<Books>) body.get("list");
			srvc.addBooks(bks, nric);
			for(Object v:bks)
			{
				ObjectMapper objmap = new ObjectMapper();
				objmap.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				Books bn = objmap.convertValue(v, Books.class);
				bkserv.setBookBorrowed(bn.getIsbn(), mbm);
			}
			return new ResponseEntity<>("Successful Update", HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>("No such user detected", HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@PostMapping(path="/returnbooks")
	public ResponseEntity<String> returnBooks(@RequestBody List<Long>booknumbers)
	{
		List<Books>ant = bkserv.getBooks(booknumbers);
		for(Books bk:ant)
		{
			Members mbm = bk.member;
			bkserv.returnBorrowedBooks(bk.getIsbn());
			mbm.bks.removeIf(x->x.getIsbn()==bk.getIsbn());
		}
		
		return new ResponseEntity<>("Books accounted for",HttpStatus.ACCEPTED);
	}
	
	@PutMapping(path="/editMemberDetails")
	public ResponseEntity<String> editMemberDetails(@RequestBody Map<String,Object>body)
	{
		int nric = -1;
		String pass = (String) body.get("pass");
		if(body.get("user") instanceof String)
		{
			nric = Integer.parseInt((String) body.get("user"));
		} else if(body.get("user") instanceof Integer)
		{
			nric = (Integer) body.get("user");
		}
		if(srvc.isMemberExists(nric)!=null) {
			Members mbm = srvc.isMemberExists(nric).get();
			String encPass = mbm.getPassword();
			srvc.deletemembers(mbm.nric);
			if(!edc.jwedecrypt(encPass).equals(pass))
			{
				return new ResponseEntity<>("Invalid Login ID or Password",HttpStatus.BAD_REQUEST);
			}
			for(Map.Entry<String, Object>entry: body.entrySet())
			{
				if(entry.getKey().equals("new_password"))
				{
					String newPass = (String) entry.getValue();
					mbm.password=edc.jweEncrypt(newPass);
					System.out.println(newPass);
					System.out.println(edc.jwedecrypt(mbm.password));
				} else if(entry.getKey().equals("new_email"))
				{
					String newEmail = (String) entry.getValue();
					mbm.setEmail(newEmail);
				}
			}
			srvc.addmember(mbm);
			return new ResponseEntity<>(mbm.toString(),HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>("User not in system please sign up",HttpStatus.BAD_REQUEST);
		}
	}
	
	@DeleteMapping(path="/deleteAccount/{nric}")
	public ResponseEntity<String> deleteMember(@PathVariable String nric)
	{
		System.out.println(nric);
		try {
			int NRIC = Integer.parseInt(nric);
			srvc.deletemembers(NRIC);
			System.out.println(NRIC);
			return new ResponseEntity<>("Member deleted",HttpStatus.OK);
		}catch(Exception E)
		{
			return new ResponseEntity<>("Member still owes books", HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping(path="/getmember/{nric}")
	public ResponseEntity<String> getMember(@PathVariable String nric)
	{
		int NRIC = Integer.parseInt(nric);
		try {
			Members mbm = srvc.isMemberExists(NRIC).get();
			ObjectMapper mapper = new ObjectMapper();
//			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
//			String json = ow.writeValueAsString(mbm);
			return new ResponseEntity<>(mbm.toString(),HttpStatus.OK);
		}catch(Exception E)
		{
			return new ResponseEntity<>("No member present", HttpStatus.FORBIDDEN);
		}
		
	}
	
	
	
	
	
//	@PutMapping("/editMember/{id}")
//	public ResponseEntity<String> editMember(@PathVariable int id)
//	{
//		
//	}
	
	
	
	
}
