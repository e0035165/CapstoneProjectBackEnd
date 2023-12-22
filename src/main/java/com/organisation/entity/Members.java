package com.organisation.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="members")
public class Members {
	@Id
	@Column
	@JsonProperty
	public int nric;
	@Column(name="local_date")
	public String dateofjoin;
	@Column(length=40000)
	@JsonProperty
	public String password;
	@OneToMany(mappedBy="member", fetch = FetchType.EAGER)
	public List<Books>bks;
	@Column
	public int current_bks;
	@JsonProperty
	@Column(name="email")
	private String email;
	@JsonProperty
	@Column
	public Float fines = 0.0f;
	
	
	public Members() {
		super();
	}
	
	public Members(int nric, String password, String email)
	{
		super();
		this.nric=nric;
		this.password=password;
		this.email=email;
	}
	
	public Members(int nric, String dateofjoin, String password, List<Books> booksborrowed) {
		super();
		this.nric = nric;
		this.dateofjoin = dateofjoin;
		this.password = password;
		this.bks=booksborrowed;
		this.current_bks=booksborrowed.size();
	}
	public int getNric() {
		return nric;
	}
	public void setNric(int nric) {
		this.nric = nric;
	}
	public List<Books> getBks() {
		return bks;
	}

	public void setBks(List<Books> bks) {
		this.bks = bks;
	}

	public int getCurrent_bks() {
		return current_bks;
	}

	public void setCurrent_bks(int current_bks) {
		this.current_bks = current_bks;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDateofjoin() {
		return dateofjoin;
	}
	public void setDateofjoin(String dateofjoin) {
		this.dateofjoin = dateofjoin;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void addBooks(Books bk)
	{
		this.bks.add(bk);
	}
//	public List<Books> getBooksborrowed() {
//		return booksborrowed;
//	}
//	public void setBooksborrowed(List<Books> booksborrowed) {
//		this.booksborrowed = booksborrowed;
//	}

	@Override
	public String toString() {
		return "Members [nric=" + nric + ", dateofjoin=" + dateofjoin + ", bks=" + bks + ", current_bks=" + current_bks
				+ ", email=" + email + "]";
	}


	
	
	
	
}
