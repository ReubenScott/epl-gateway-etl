package com.covidien.etl.dao;

import java.sql.Connection;

import org.junit.Test;

import com.covidien.etl.dbstore.DBConnection;

public class TestInsert {

//	private CustomerDAO dao;
	
	@Test
	public void testInsert() throws Exception{
		Connection con = null;		
		DBConnection dbConnection;
		dbConnection = DBConnection.getInstance();
	
		try {
			con = dbConnection.getConnection();
			if (con == null) {
				dbConnection.reconnect();
				con = dbConnection.getConnection();
			}
			con.setAutoCommit(false);
						
		} catch (Exception e) {
			e.printStackTrace();
		}

		
//		dao=new CustomerDAO();
//		System.out.println("insert key is :"+dao.insertNodeRevisions(con, "customer Test7"));
		
				
		con.close();
	}
}
