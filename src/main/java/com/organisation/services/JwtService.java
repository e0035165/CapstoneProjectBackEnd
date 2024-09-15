package com.organisation.services;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.organisation.entity.CustomUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	
	private static String SECRET = "1q3wmD0sLJL7LR7mnmVEH1mQGRiZvISkRa6WGFvddjk=";
	
	public String generateToken(String username) {
		Map<String,Object>claims = new HashMap<>();
		return createToken(claims,username);
	}
	
	public String generateToken(String username, String password) {
		Map<String,Object>claims = new HashMap<>();
		claims.put("password", password);
		return createToken(claims,username);
	}
	
	public String extractUsername(String token) {
		final Claims claims = extractAllClaims(token);
		System.out.println(claims.getSubject());
		return claims.getSubject();
		
	}
	
	public Date extractExpirationDate(String token) {
		final Claims claims = extractAllClaims(token);
		return claims.getExpiration();
		
	}
	
	
	public <T> T extractClaims(String token, Function<Claims,T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
	
	public Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSignKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
	
	
	private String createToken(Map<String,Object>claims, String userName) {
		return Jwts.builder().setClaims(claims).setSubject(userName)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis()+1000*60*60*24))
				.signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
	}
	
	
	private Key getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET);
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	private Boolean isTokenExpired(String token) {
		Date exp = extractExpirationDate(token);
		Date now = new Date(System.currentTimeMillis());
		if(exp.before(now)) {
			return true;
		} else {
			return false;
		}
	}
	
	
	public Boolean validateToken(String token, CustomUser userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
	
	
	public static void main(String...strings) throws NoSuchAlgorithmException {
		JwtService serv = new JwtService();
		System.out.println(serv.generateToken("Suresh"));
	}
}
