package com.covidien.etl.dao;

import java.sql.Connection;

import org.junit.Before;
import org.junit.Test;

import com.covidien.etl.common.EtlType;
import com.covidien.etl.dao.helper.CustomerDAOSQLHelper;
import com.covidien.etl.dbstore.DBConnection;
import com.covidien.etl.dbstore.DBUtiltityFunctions;

public class CustomerDAO2Test {
    CustomerDAO cDao;

    @Before
    public void setUp()
        throws Exception {
        Connection con = DBConnection.getInstance().getConnection();
        DBUtiltityFunctions.init(con);
        CustomerDAOSQLHelper.init(con);
        cDao = new CustomerDAO();
        cDao.init(con);
    }

    @Test
    public void test() {
        try {
            cDao.cleanCache();
        } catch (Exception e) {
        }
        try {
            cDao.updateNidWithVid(3000, 3000);
        } catch (Exception e) {
        }
        try {
            cDao.updateXrefByPsId(EtlType.Customer, 12312321, "12321");
        } catch (Exception e) {
        }
        try {
            cDao.setExpired(3000);
        } catch (Exception e) {
        }
    }

}
