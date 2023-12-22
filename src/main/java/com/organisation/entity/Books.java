package com.organisation.entity;

import java.time.LocalDate;

import com.organisation.entity.*;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.ToString;

@Entity
@Table(name="Books")
public class Books {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long isbn;
	@Column(name="book_name")
	public String bkname;
	@Column(name="author")
	public String author;
	@Column(name="genre")
	public String genre;
	@Column(name="language")
	public String language;
	@Column(name="mass")
	public int mass;
	@Column(name="status")
	public boolean status;
	@Column(name="Taken Date")
	public LocalDate datetaken;
	@ManyToOne
	public Members member;
	
	
	public Books(long isbn, String bkname, String author, String genre, String language, int mass, boolean status) {
		super();
		this.isbn = isbn;
		this.bkname = bkname;
		this.author = author;
		this.genre = genre;
		this.language = language;
		this.mass = mass;
		this.status = status;
	}
	
	public Books(String bkname, String language, int mass, String genre)
	{
		super();
		this.bkname=bkname;
		this.language=language;
		this.mass=mass;
		this.genre=genre;
		this.status=false;
	}
	public Members getMember() {
		return member;
	}

	public void setMember(Members member) {
		this.member = member;
	}
	@Column(name="Return Date")
	public LocalDate returndate;
	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public LocalDate getDatetaken() {
		return datetaken;
	}

	public void setDatetaken(LocalDate datetaken) {
		this.datetaken = datetaken;
	}

	public LocalDate getReturndate() {
		return returndate;
	}

	public void setReturndate(LocalDate returndate) {
		this.returndate = returndate;
	}

	public Books() {
		super();
	}
	
	public int getMass() {
		return mass;
	}

	public void setMass(int mass) {
		this.mass = mass;
	}

	public Books(String bkname, String author, String genre, String language, int mass) {
		super();
		this.bkname = bkname;
		this.author = author;
		this.genre = genre;
		this.language = language;
		this.mass = mass;
	}

	public long getIsbn() {
		return isbn;
	}
	public void setIsbn(long isbn) {
		this.isbn = isbn;
	}
	public String getBkname() {
		return bkname;
	}
	public void setBkname(String bkname) {
		this.bkname = bkname;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}

	@Override
	public String toString() {
		return "Books [isbn=" + isbn + ", bkname=" + bkname + ", author=" + author + ", returndate=" + returndate.toString() + "]";
	}

	

	
	
	
}
