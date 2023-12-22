package com.organisation.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.organisation.entity.Books;
import com.organisation.entity.Members;
import com.organisation.repositories.BooksRepo;

@Service
public class BookService {
	
	@Autowired
	private BooksRepo repo;
	
	public void addBooks(Books bk)
	{
		repo.save(bk);
	}
	
	public List<Books> getAllBooks()
	{
		List<Books>bks=new ArrayList<>();
		Iterable<Books>ita=repo.findAll();
		Iterator<Books>itr=ita.iterator();
		while(itr.hasNext())
		{
			bks.add(itr.next());
		}
		return bks;
	}
	
	public List<Books> getBooks(List<Long>isbns)
	{
		List<Books>bks=new ArrayList<>();
		for(Long isbn:isbns)
		{
			try {
				if(repo.findById(isbn)!=null && repo.findById(isbn).get().status==true) {
					bks.add(repo.findById(isbn).get());
				}
			}catch(Exception E)
			{
				System.err.println(E.getMessage());
			}
			
		}
		return bks;
	}
	
	public List<Books> getBooksWithSuffix(String suf)
	{
		List<Books>bks=new ArrayList<>();
		Iterable<Books>ita=repo.findAll();
		Iterator<Books>itr=ita.iterator();
		while(itr.hasNext())
		{
			Books t=itr.next();
			if(t.bkname.startsWith(suf))
			{
				bks.add(t);
			}
		}
		return bks;
	}
	
	public boolean removeBooks(long isbn)
	{
		try {
			repo.deleteById(isbn);
			return true;
		}catch(Exception E)
		{
			System.err.println(E.getMessage());
			return false;
		}
	}
	
	public void setBookBorrowed(long isbn, Members member)
	{
		Books bk=repo.findById(isbn).get();
		repo.deleteById(isbn);
		bk.status=true;
		bk.member=member;
		bk.datetaken=LocalDate.now();
		bk.returndate=bk.datetaken.plus(3, ChronoUnit.WEEKS);
		repo.save(bk);
	}
	
	public void returnBorrowedBooks(long isbn)
	{
		try {
			Books bk=repo.findById(isbn).get();
			bk.member=null;
			bk.status=false;
			bk.datetaken=null;
			bk.returndate=null;
			repo.save(bk);
		}catch(Exception E)
		{
			System.err.println(E.getMessage());
		}
		
	}
	
	public List<Books> getCurrentBooks(String genre)
	{
		Iterable<Books>ible = repo.findAll();
		Iterator<Books>itr = ible.iterator();
		List<Books>bks = new ArrayList<>();
		while(itr.hasNext())
		{
			Books u = itr.next();
			if(u.status==false && u.genre.equals(genre))
			{
				bks.add(u);
			}
		}
		return bks;
	}
	
	public List<Books> getCurrentBooks(String genre, String language)
	{
		Iterable<Books>ible = repo.findAll();
		Iterator<Books>itr = ible.iterator();
		List<Books>bks = new ArrayList<>();
		while(itr.hasNext())
		{
			Books u = itr.next();
			if(u.status==false && u.genre.equals(genre) && language.equals(u.language))
			{
				bks.add(u);
			}
		}
		return bks;
	}
	
	

}
