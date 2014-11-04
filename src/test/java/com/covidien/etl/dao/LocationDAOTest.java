package com.covidien.etl.dao;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.covidien.etl.model.Location;

public class LocationDAOTest {
	LocationDAO lDao;
	List<Location> list = new ArrayList<Location>();
	@Before
	public void setUp() throws Exception {
		lDao = new LocationDAO();
		Location location = new Location();
		location.setLOCATION_ID("E1-547140");
		location.setADDRESS_LINE1("7435 W TALCOTT AVE");
		location.setADDRESS_MODIFIER_1("");
		location.setADDRESS_MODIFIER_2("");
		location.setADDRESS_MODIFIER_3("");
		location.setADDRESS_MODIFIER_4("");
		location.setCITY("CHICAGO");
		location.setSTATE_PROVINCE("IL");
		location.setPOSTAL_CODE("60631-3707");
		location.setCOUNTRY_CODE("US");
		location.setLAST_CHANGE_DATE("2013-03-22 01:00:00");
		location.setBATCH_NUMBER(1);
		location.setIS_DELETED(1);
		list.add(location);
	}

	@Test
	public void test() {
		lDao.process(list);
	}

}
