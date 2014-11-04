package com.covidien.etl.dao;

import java.sql.Connection;

import org.junit.Before;
import org.junit.Test;

import com.covidien.etl.common.EtlType;

public class CustomerDAO2Test {
	CustomerDAO cDao;
	@Before
	public void setUp() throws Exception {
		cDao = new CustomerDAO();
	}

	@Test
	public void test() {
		Connection con = null;
		try {
			con = cDao.getDbConnection().getConnection();
        } catch (Exception e) {
        }
		try {
			cDao.cleanCache(con);
        } catch (Exception e) {
        }
		try {
			cDao.updateNidWithVid(con, 3000, 3000);
        } catch (Exception e) {
        }
		try {
			cDao.updateXrefByPsId(con, EtlType.Customer, 12312321, "12321");
        } catch (Exception e) {
        }
		try {
			cDao.setExpired(con, 3000);
        } catch (Exception e) {
        }
	}

}
