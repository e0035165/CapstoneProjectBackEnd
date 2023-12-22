package com.organisation.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.organisation.entity.Books;
import com.organisation.entity.Members;
import com.organisation.repositories.MembersRepo;

@Service
@EnableScheduling
public class MembersService {
	@Autowired
	private MembersRepo repo;
	
	
	
	public void addmember(Members mbm)
	{
		repo.save(mbm);
	}
	
	public void deletemembers(int nric)
	{
		if(repo.findById(nric)!=null) {
			repo.deleteById(nric);
		}
	}
	
	public void updatePassword(int nric, String newpassword)
	{
		if(repo.findById(nric)!=null)
		{
			Members mbm = repo.findById(nric).get();
			mbm.password=newpassword;
			repo.save(mbm);
		}
	}
	
	public Optional<Members> isMemberExists(int user)
	{
		return repo.findById(user);
	}
	
	public void addBooks(List<Books>bks, int id)
	{
		Members mbm = repo.findById(id).get();
		if(mbm!=null) {
			for(Object b:bks)
			{
				ObjectMapper objmap = new ObjectMapper();
				objmap.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				Books bn = objmap.convertValue(b, Books.class);
				mbm.addBooks(bn);
			}
		}
	}
	
	public Members getMember(int nric)
	{
		return repo.findById(nric).get();
	}
	
	@Scheduled(cron="${cron.expression}")
	public void updateAllMembers() {
		Iterable<Members>allMembers = repo.findAll();
		Iterator<Members>itr = allMembers.iterator();
		while(itr.hasNext())
		{
			Members mbm = itr.next();
			mbm.fines=0.00f;
			if(mbm.bks.size()>0)
			{
				for(Books b:mbm.bks)
				{
					if(b.returndate.isBefore(LocalDate.now()))
					{
						long days = ChronoUnit.DAYS.between(b.returndate, LocalDate.now());
						mbm.fines = mbm.fines + (Float)(days*0.10f);
						
					}
				}
				repo.save(mbm);
				System.out.println("For member "+mbm.nric+ " fine has been updated to "+mbm.fines);
			}
		}
	}
	
	
	

}
