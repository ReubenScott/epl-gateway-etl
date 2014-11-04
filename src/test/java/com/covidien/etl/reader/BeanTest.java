package com.covidien.etl.reader;

import org.junit.Test;

import com.covidien.etl.dbstore.DBConnection;

public class BeanTest {

	@Test
	public void testDBParameter() throws Exception{
		DBConnection.getInstance();
	}
}
