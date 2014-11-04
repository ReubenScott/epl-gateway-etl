package com.covidien.etl.dao;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.covidien.etl.model.Customer;

public class CustomerDAOTest {
	CustomerDAO cDao;
	List<Customer> list = new ArrayList<Customer>();;
	@Before
	public void setUp() throws Exception {
		cDao = new CustomerDAO();
		Customer cus = new Customer();
		cus.setCUSTOMER_ID("US-1269421");
		cus.setNAME("Aspen Womens Center");
		cus.setPHONE("(801) 356-0712");
		cus.setFAX("");
		cus.setLAST_CHANGE_DATE("2013-06-14 01:00:00");
		cus.setBATCH_NUMBER(1);
		cus.setIS_DELETED(1);
		list.add(cus);
	}

	@Test
	public void test() {
		cDao.process(list);
	}

}
