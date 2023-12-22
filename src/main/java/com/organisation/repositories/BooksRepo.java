package com.organisation.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.organisation.entity.Books;

@Repository
public interface BooksRepo extends CrudRepository<Books,Long>{
	

}
