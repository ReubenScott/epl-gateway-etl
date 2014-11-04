package com.covidien.etl.dbstore;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.Before;
import org.junit.Test;


public class DBUtiltityFunctionsTest {
	Statement stmt;
	@Before
	public void setUp() throws Exception {
		DBConnection dbConnection;
		dbConnection = DBConnection.getInstance();
		Connection con = dbConnection.getConnection();
		stmt = con.createStatement();
	}

	@Test
	public void test(){
		try {
			DBUtiltityFunctions.getLatestNid(stmt);
        } catch (Exception e) {}
		try {
			DBUtiltityFunctions.getSerialNumberValidation(stmt, "SCD700");
        } catch (Exception e) {}
		try {
			DBUtiltityFunctions.checkDuplicateSerialNumber(stmt, "SCD700", "fsdfsdf");
        } catch (Exception e) {}
		try {
			DBUtiltityFunctions.getDeviceTypeSerialNumbers(stmt, "SCD700");
        } catch (Exception e) {}
		try {
			DBUtiltityFunctions.checkAnyCustomerRecordAddedByETL(stmt);
        } catch (Exception e) {}
		try {
			DBUtiltityFunctions.checkAnyLocationRecordAddedByETL(stmt);
        } catch (Exception e) {}
		try {
			DBUtiltityFunctions.checkAnyLocationRoleRecordAddedByETL(stmt);
        } catch (Exception e) {}
	}

}
