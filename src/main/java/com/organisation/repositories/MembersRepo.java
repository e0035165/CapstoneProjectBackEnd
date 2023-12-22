package com.organisation.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.organisation.entity.Members;

@Repository
public interface MembersRepo extends CrudRepository<Members,Integer> {

}
