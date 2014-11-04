package com.covidien.etl.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.covidien.etl.dao.helper.LocationDAOSQLHelper;
import com.covidien.etl.dbstore.DBConnection;
import com.covidien.etl.dbstore.DBUtiltityFunctions;
import com.covidien.etl.model.LocationRole;

public class LocationRoleDAOTest {
    LocationRoleDAO rDao;
    List<LocationRole> list = new ArrayList<LocationRole>();

    @Before
    public void setUp()
        throws Exception {
        Connection con = DBConnection.getInstance().getConnection();
        DBUtiltityFunctions.init(con);
        LocationDAOSQLHelper.init(con);
        rDao = new LocationRoleDAO();
        rDao.init(con);
        LocationRole role1 = new LocationRole();
        role1.setBatchNumber(1);
        role1.setCustomerId("US-2269421");
        role1.setIsDeleted(0);
        role1.setLastChangeDate("2013-06-16 01:00:00");
        role1.setLocationId("E1-947141");
        role1.setLocationRole("SHIP_FROM");
        list.add(role1);

        LocationRole role2 = new LocationRole();
        role2.setBatchNumber(1);
        role2.setCustomerId("US-2269421");
        role2.setIsDeleted(0);
        role2.setLastChangeDate("2013-06-17 01:00:00");
        role2.setLocationId("E1-947141");
        role2.setLocationRole("SHIP_TO");
        list.add(role2);

        LocationRole role3 = new LocationRole();
        role3.setBatchNumber(1);
        role3.setCustomerId("US-2269421");
        role3.setIsDeleted(1);
        role3.setLastChangeDate("2013-06-18 01:00:00");
        role3.setLocationId("E1-947141");
        role3.setLocationRole("SHIP_TO");
        list.add(role3);

        LocationRole role4 = new LocationRole();
        role4.setBatchNumber(1);
        role4.setCustomerId("US-3269421");
        role4.setIsDeleted(0);
        role4.setLastChangeDate("2013-06-18 01:00:00");
        role4.setLocationId("E1-947141");
        role4.setLocationRole("");
        list.add(role4);

        LocationRole role5 = new LocationRole();
        role5.setBatchNumber(1);
        role5.setCustomerId("");
        role5.setIsDeleted(0);
        role5.setLastChangeDate("2013-06-18 01:00:00");
        role5.setLocationId("E1-947141");
        role5.setLocationRole("SHIP_TO");
        list.add(role5);

        LocationRole role6 = new LocationRole();
        role6.setBatchNumber(1);
        role6.setCustomerId("US-4269421");
        role6.setIsDeleted(0);
        role6.setLastChangeDate("2013- 01:00:00");
        role6.setLocationId("E1-947141");
        role6.setLocationRole("SHIP_TO");
        list.add(role6);
    }

    @Test
    public void test() {
        rDao.process(list);
    }

}
