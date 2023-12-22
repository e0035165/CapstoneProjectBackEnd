package com.operational;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.*;
import com.nimbusds.jwt.*;

@Component
@Scope("singleton")
public class EncryptionDataClass {
	private RSAPublicKey pubkey;
	private RSAPrivateKey pvtkey;
	private RSAKey rsaKey;
	private RSAKey jweRSAKey;
	private RSAPublicKey pubkey_jwe;
	private RSAPrivateKey pvtkey_jwe;
	
	public EncryptionDataClass(@Value("${public_cert}") String pubcert, @Value("${private_cert}") String pvtcert, @Value("${public_cert_jwe}") String jwe_pub,
			@Value("${private_cert_jwe}") String jwe_pvt)
	{
		try
		{
			File pubfile=new File(pubcert);
			File pvtfile=new File(pvtcert);
			String pubkey=new String(Files.readAllBytes(pubfile.toPath()), Charset.defaultCharset());
			String pubkeyPEM = pubkey
				      .replace("-----BEGIN PUBLIC KEY-----", "")
				      .replaceAll(System.lineSeparator(), "")
				      .replace("-----END PUBLIC KEY-----", "")
				      .replaceAll("\\r\\n", "")
				      .replaceAll("[\\r\\n]", "");
			byte[] encoded = Base64.getDecoder().decode(pubkeyPEM);

		    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
		    this.pubkey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
		    
		    String pvtkey=new String(Files.readAllBytes(pvtfile.toPath()), Charset.defaultCharset());
		    String pvtkeyPEM = pvtkey
		    		.replaceAll("-----BEGIN PRIVATE KEY-----", "")
		    		.replaceAll(System.lineSeparator(), "")
		    		.replaceAll("-----END PRIVATE KEY-----", "")
				    .replaceAll("\\r\\n", "")
				    .replaceAll("[\\r\\n]", "");
		    byte[] decoded = Base64.getDecoder().decode(pvtkeyPEM);
		    PKCS8EncodedKeySpec keySpecpvt = new PKCS8EncodedKeySpec(decoded);
		    this.pvtkey= (RSAPrivateKey) keyFactory.generatePrivate(keySpecpvt);
		    System.out.println("JWS Cert initialisation success");
		    File pubjwefile = new File(jwe_pub);
		    File pvtjwefile = new File(jwe_pvt);
		    String pubjwekey = new String(Files.readAllBytes(pubjwefile.toPath()), Charset.defaultCharset());
		    String pvtjwekey = new String(Files.readAllBytes(pvtjwefile.toPath()), Charset.defaultCharset());
		    String pubjwepem = pubjwekey
		    		  .replace("-----BEGIN PUBLIC KEY-----", "")
				      .replaceAll(System.lineSeparator(), "")
				      .replace("-----END PUBLIC KEY-----", "")
				      .replaceAll("\\r\\n", "")
				      .replaceAll("[\\r\\n]", "");
		    
		    byte[] encoded_jwe = Base64.getDecoder().decode(pubjwepem);
		    X509EncodedKeySpec keySpecjwe = new X509EncodedKeySpec(encoded_jwe);
		    this.pubkey_jwe = (RSAPublicKey) keyFactory.generatePublic(keySpecjwe);
		    
		    String pvtjwepem = pvtjwekey
		    		.replaceAll("-----BEGIN PRIVATE KEY-----", "")
		    		.replaceAll(System.lineSeparator(), "")
		    		.replaceAll("-----END PRIVATE KEY-----", "")
				    .replaceAll("\\r\\n", "")
				    .replaceAll("[\\r\\n]", "");
		    
		    byte[] decoded_jwe = Base64.getDecoder().decode(pvtjwepem);
		    PKCS8EncodedKeySpec keySpecpvtjwe = new PKCS8EncodedKeySpec(decoded_jwe);
		    this.pvtkey_jwe = (RSAPrivateKey) keyFactory.generatePrivate(keySpecpvtjwe);
		    System.out.println("JWE Cert initialisation success");
		    this.jweRSAKey= new RSAKeyGenerator(2048)
				    .keyID("456")
				    .keyUse(KeyUse.ENCRYPTION)
				    .generate();
		    System.out.println("Cert initialisation success");
		}catch(Exception E)
		{
			System.err.println(E.getLocalizedMessage());
		}
	}
	
	public String encryptedMessage(String password)
	{
		JWSSigner signer = new RSASSASigner(this.pvtkey);
		JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256).build();
		JWSObject jwsObj = new JWSObject(header, new Payload(password));
		JWSVerifier verifier = new RSASSAVerifier(this.pubkey);
		//String encPassword = jwsObj.serialize();
		try {
			//jwsObj = JWSObject.parse(encPassword);
			jwsObj.sign(signer);
			String encPassword = jwsObj.serialize();
			if(jwsObj.verify(verifier)==true)
			{
				System.out.println("JWS Signing is successful");
				return encPassword;
			} else {
				return "Encryption failed";
			}
		} catch (JOSEException e) {
			System.out.println("JWS Signature JOSE error");
			return e.getMessage();
		}
	}
	
	
	public String decryptString(String encPassword)
	{
		JWSObject jwsObj;
		try {
			jwsObj=JWSObject.parse(encPassword);
		} catch(Exception e)
		{
			return e.getMessage();
		}
		JWSVerifier verifier = new RSASSAVerifier(this.pubkey);
		try {
			if(jwsObj.verify(verifier)==true)
			{
				return jwsObj.getPayload().toString();
			} else {
				return "decryption failed";
			}
		} catch (JOSEException e) {
			return e.getMessage();
		}
		
	}
	
	public String jweEncrypt(String password)
	{
		try {
			JWSVerifier verifier = new RSASSAVerifier(this.pubkey);
			JWSSigner signer = new RSASSASigner(this.pvtkey);
			SignedJWT signedjwt = new SignedJWT(
					new JWSHeader.Builder(JWSAlgorithm.RS256).build(),
					new JWTClaimsSet.Builder()
					.subject(password)
					.issueTime(new Date())
					.build()
					);
			signedjwt.sign(signer);
			JWEObject jweObject = new JWEObject(
					new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A256GCM)
					.contentType("JWT")
					.build(),
					new Payload(signedjwt)
					);
			jweObject.encrypt(new RSAEncrypter(this.pubkey_jwe));
			return jweObject.serialize();
		} catch (JOSEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
			
	}
	
	public String jwedecrypt(String encPassword)
	{
		try {
			JWEObject jweObj = JWEObject.parse(encPassword);
			jweObj.decrypt(new RSADecrypter(this.pvtkey_jwe));
			SignedJWT signedjwt = jweObj.getPayload().toSignedJWT();
			JWSVerifier verifier = new RSASSAVerifier(this.pubkey);
			if(signedjwt.verify(verifier))
			{
				System.out.println("JWE Decryption successful");
				return signedjwt.getJWTClaimsSet().getSubject();
			} else {
				System.out.println("JWE/JWS Decryption failed");
				return "JWE/JWS Decryption failed";
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		} catch (JOSEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
		
	}
	

}
