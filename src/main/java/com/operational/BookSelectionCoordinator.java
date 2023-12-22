package com.operational;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.organisation.entity.Books;


@Component
public class BookSelectionCoordinator {
	
	public List<List<Books>> getBookCombinations(List<Books>listbks, int current_mass, int no_of_books)
	{
		int size = listbks.size();
		
		int[] booklst = new int[size];
		String[] bookname = new String[size];
		int i=0;
		for(Books bk:listbks)
		{
			booklst[i]=bk.mass;
			bookname[i]=bk.bkname;
			i++;
		}
		return BooksRecursive(booklst, size, current_mass, listbks, no_of_books);
	}

	
	public List<List<Books>> BooksRecursive(int[] bkwgt, int x, int current_mass, List<Books> bknm, int limit)
	{
		if(x==0 || current_mass==0 || limit==0)
		{
			if(current_mass==0 && limit==0)
			{
				return (new ArrayList<>());
			} else {
				return null;
			}
		}
		List<List<Books>>A=null;
		if(bkwgt[x-1]<=current_mass) {
			A=BooksRecursive(bkwgt,x-1,current_mass-bkwgt[x-1],bknm,limit-1);
			if(A!=null) {
				if(A.isEmpty())
				{
					List<Books>temp=new ArrayList<>();
					temp.add(bknm.get(x-1));
					A.add(temp);
				} else {
					for(List<Books>val:A)
					{
						val.add(bknm.get(x-1));
					}
				}
			}
		}
		
		List<List<Books>>B=BooksRecursive(bkwgt,x-1,current_mass,bknm,limit);
		
		if(A!=null && B!=null)
		{
			A.addAll(B);
			return A;
		} else if(A==null && B!=null)
		{
			return B;
		} else if(A!=null && B==null)
		{
			return A;
		} else {
			return null;
		}
		
		
	}
}
